package com.pasha.edit.internal.compose.screen

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pasha.android_core.presentation.TodoItemViewModel
import com.pasha.edit.internal.views.EditTaskViewModel
import java.time.LocalDateTime
import com.pasha.core_ui.R


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditTaskScreen(
    viewModel: EditTaskViewModel,
    todoItemViewModel: TodoItemViewModel,
    navigateBackAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var taskText by rememberSaveable {
        val text = todoItemViewModel.sharedTodoItem?.text

        mutableStateOf(
            if (text != null) text else ""
        )
    }

    val editState = viewModel.editState.collectAsStateWithLifecycle()
    val changeableTaskPart = remember(key1 = editState.value) {
        mutableStateOf(
            ChangeableTaskPart(
                text = taskText,
                priority = editState.value.priority,
                deadline = editState.value.deadline
            )
        )
    }

    val scrollState: ScrollState = rememberScrollState()
    Scaffold(
        topBar = {
            EditAppBar(
                navigateBackAction = navigateBackAction,
                saveAction = {
                    if (todoItemViewModel.sharedTodoItem == null) {
                        viewModel.createTask(
                            text = changeableTaskPart.value.text,
                            currentTime = LocalDateTime.now()
                        )
                    } else {
                        viewModel.changeTask(
                            oldTask = todoItemViewModel.sharedTodoItem!!,
                            newText = changeableTaskPart.value.text
                        )
                    }

                    navigateBackAction()
                },
                scrollOffsetCallback = { scrollState.value },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = colorResource(id = R.color.color_blue)
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            EditScreenContent(
                taskPart = changeableTaskPart.value,
                onTextChangedCallback = { newText ->
                    taskText = newText
                    changeableTaskPart.value = changeableTaskPart.value.copy(
                        text = taskText
                    )
                },
                onPriorityChangedCallback = { newPriority ->
                    viewModel.changePriority(newPriority)
                },
                onDeadlineChangedCallback = { newDate ->
                    viewModel.changeDeadline(newDate)
                },
                onDeleteAction = {
                    if (todoItemViewModel.sharedTodoItem != null) {
                        todoItemViewModel.removeTask(todoItemViewModel.sharedTodoItem!!)

                        todoItemViewModel.clearSharedItem()
                    }
                    navigateBackAction()
                }
            )
        }
    }
}

package com.pasha.ytodo.presentation.edit.compose

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pasha.ytodo.data.repositories.TodoItemsRepositoryTestImpl
import com.pasha.ytodo.presentation.TodoItemViewModel
import com.pasha.ytodo.presentation.edit.views.EditTaskViewModel
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime


@Composable
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

    val editState = viewModel.editState.collectAsState()
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
                scrollOffsetCallback = { scrollState.value }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            val context = LocalContext.current
            EditScreenContent(
                taskPart = changeableTaskPart.value,
                onTextChangedCallback = { newText ->
                    taskText = newText
                    changeableTaskPart.value = changeableTaskPart.value.copy(
                        text = taskText
                    )
                },
                onPriorityChangedCallback = { newPriority ->
                    Toast.makeText(context, newPriority.name, Toast.LENGTH_SHORT).show()
                    viewModel.changePriority(newPriority)
                },
                onDeadlineChangedCallback = { newDate ->
                    viewModel.changeDeadline(newDate)
                },
                onDeleteAction = {
                    if (todoItemViewModel.sharedTodoItem != null) {
                        viewModel.removeTask(todoItemViewModel.sharedTodoItem!!)

                        todoItemViewModel.clearSharedItem()
                    }
                    navigateBackAction()
                }
            )
        }
    }
}

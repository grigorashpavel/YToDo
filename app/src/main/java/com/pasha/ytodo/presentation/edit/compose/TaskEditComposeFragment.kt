package com.pasha.ytodo.presentation.edit.compose

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.pasha.ytodo.presentation.TodoItemViewModel
import com.pasha.ytodo.presentation.edit.compose.screen.EditTaskScreen
import com.pasha.ytodo.presentation.edit.compose.theme.TodoTheme
import com.pasha.ytodo.presentation.edit.views.EditTaskViewModel


class TaskEditComposeFragment : Fragment() {
    private val todoItemViewModel by activityViewModels<TodoItemViewModel>()
    private val viewModel by viewModels<EditTaskViewModel> {
        EditTaskViewModel.provideFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val task = todoItemViewModel.sharedTodoItem
        val isFirstCreation = savedInstanceState == null
        if (isFirstCreation && task != null) {
            viewModel.changePriority(task.priority)
            viewModel.changeDeadline(task.deadline)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                var navigationStarted by rememberSaveable { mutableStateOf(false) }
                TodoTheme(useDynamicColors = false) {
                    EditTaskScreen(
                        viewModel,
                        todoItemViewModel,
                        navigateBackAction = {
                            if (!navigationStarted) {
                                navigationStarted = true

                                todoItemViewModel.clearSharedItem()
                                findNavController().navigateUp()
                            }
                        }
                    )
                }
            }
        }
    }
}
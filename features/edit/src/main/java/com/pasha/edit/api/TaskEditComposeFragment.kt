package com.pasha.edit.api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pasha.android_core.di.findDependencies
import com.pasha.android_core.presentation.TodoItemViewModel
import com.pasha.android_core.presentation.activityLazyViewModel
import com.pasha.android_core.presentation.fragmentLazyViewModel
import com.pasha.edit.internal.compose.screen.EditTaskScreen
import com.pasha.edit.internal.compose.theme.TodoTheme
import com.pasha.edit.internal.di.DaggerEditComponent
import com.pasha.edit.internal.views.EditTaskViewModel
import javax.inject.Inject


class TaskEditComposeFragment : Fragment() {
//    private val todoItemViewModel by activityViewModels<TodoItemViewModel>()

    @Inject
    internal lateinit var viewModelFactory: EditTaskViewModel.Factory
    private val viewModel by fragmentLazyViewModel {
        viewModelFactory.create()
    }

    @Inject
    internal lateinit var todoViewModelFactory: TodoItemViewModel.Factory
    private val todoItemViewModel: TodoItemViewModel by activityLazyViewModel {
        todoViewModelFactory.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val task = todoItemViewModel.sharedTodoItem
        val isFirstCreation = savedInstanceState == null

        if (isFirstCreation) {
            DaggerEditComponent.factory()
                .create(findDependencies())
                .inject(this)
        }

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

                                exitBack()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isFirstCreation = savedInstanceState == null
        if (isFirstCreation.not()) {
            DaggerEditComponent.factory()
                .create(findDependencies())
                .inject(this)
        }

        configureBackPressedAction()
    }

    private fun configureBackPressedAction() {
        requireActivity().onBackPressedDispatcher.addCallback(owner = viewLifecycleOwner) {
            exitBack()
        }
    }

    private fun exitBack() {
        todoItemViewModel.clearSharedItem()
        findNavController().navigateUp()

    }
}
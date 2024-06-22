package com.pasha.ytodo.presentation.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasha.ytodo.R
import com.pasha.ytodo.databinding.FragmentTasksBinding
import com.pasha.ytodo.domain.models.TaskProgress
import com.pasha.ytodo.domain.models.TodoItem
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.presentation.TodoItemViewModel
import com.pasha.ytodo.presentation.tasks.adapters.ActionsListener
import com.pasha.ytodo.presentation.tasks.adapters.TasksRecyclerViewAdapter
import kotlinx.coroutines.launch


class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val todoItemViewModel: TodoItemViewModel by activityViewModels<TodoItemViewModel>()
    private val viewModel: TasksViewModel by viewModels { TasksViewModel.provideFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val rootView = binding.root

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureTasksListRecyclerView()
        configureFinishedTasksCounter()
        configureCreateFabListener()
        configureTasksVisibilityButtonListener()
        configureVisibilityIcon()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configureTasksListRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTasksList.layoutManager = layoutManager

        binding.rvTasksList.adapter = createTasksAdapter()
    }

    private fun createTasksAdapter(): TasksRecyclerViewAdapter {
        val tasksAdapter = TasksRecyclerViewAdapter(createActionsListener())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.items.collect { tasks ->
                    tasksAdapter.setTodoTasks(tasks.toList())
                }
            }
        }

        return tasksAdapter
    }

    private fun configureCreateFabListener() {
        binding.fabCrateTask.setOnClickListener {
            navigateToTaskCreation()
        }
    }

    private fun configureFinishedTasksCounter() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.finishedTasksCounter.collect { completedTasksNum ->
                    binding.tvFinishedTasks.text = requireContext().getString(
                        R.string.tasks_toolbar_finished_todo_subtitle,
                        completedTasksNum
                    )
                }
            }
        }
    }

    private fun navigateToTaskCreation() {
        findNavController().navigate(R.id.action_tasksFragment_to_taskEditFragment)
    }

    private fun navigateToTaskEditing(task: TodoItem) {
        todoItemViewModel.passTodoItem(item = task)
        findNavController().navigate(R.id.action_tasksFragment_to_taskEditFragment)
    }

    private fun createActionsListener() = object : ActionsListener {
        override fun onClickItem(item: TodoItem) {
            navigateToTaskEditing(item)
        }

        override fun onClickProgressCheckbox(item: TodoItem, newProgressState: Boolean) {
            val progressState = when (newProgressState) {
                true -> TaskProgress.DONE
                false -> TaskProgress.TODO
            }

            viewModel.changeTaskProgress(item, newProgress = progressState)
        }

        override fun onClickInfoButton(item: TodoItem) {
            navigateToTaskEditing(task = item)
        }
    }

    private fun configureTasksVisibilityButtonListener() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.actionVisible) {
                viewModel.changeDoneTasksVisibility()
            }
            true
        }
    }

    private fun configureVisibilityIcon() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksVisibility.collect { isAllTaskShown ->
                    val visibilityItem = binding.toolbar.menu.findItem(R.id.actionVisible)
                    if (isAllTaskShown) {
                        visibilityItem.setIcon(R.drawable.ic_visible_24)
                    } else {
                        visibilityItem.setIcon(R.drawable.ic_invisible_24)
                    }
                }
            }
        }
    }
}
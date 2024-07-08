package com.pasha.ytodo.presentation.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pasha.ytodo.R
import com.pasha.ytodo.databinding.FragmentTasksBinding
import com.pasha.ytodo.domain.entities.TaskProgress
import com.pasha.ytodo.domain.entities.TodoItem
import com.pasha.ytodo.presentation.TodoItemViewModel
import com.pasha.ytodo.presentation.tasks.adapters.ActionsListener
import com.pasha.ytodo.presentation.tasks.adapters.TasksRecyclerViewAdapter
import com.pasha.ytodo.presentation.tasks.adapters.ItemSwipeHelperCallback
import kotlinx.coroutines.delay
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
        configureSwipeRefreshBehaviourAndStyles()
        configureSwipeRefreshListener()
        setupErrorListener()
        configureProgressIndication()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configureTasksListRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTasksList.layoutManager = layoutManager

        val adapter = createTasksAdapter()

        val callback = ItemSwipeHelperCallback(adapter)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(binding.rvTasksList)
        binding.rvTasksList.addItemDecoration(helper)

        binding.rvTasksList.adapter = adapter
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
        if (checkNavigationActionToPossible() != true) return

        findNavController().navigate(R.id.action_tasksFragment_to_taskEditComposeFragment)
    }

    private fun navigateToTaskEditing(task: TodoItem) {
        if (checkNavigationActionToPossible() != true) return

        todoItemViewModel.passTodoItem(item = task)
        findNavController().navigate(R.id.action_tasksFragment_to_taskEditComposeFragment)
    }

    @SuppressLint("RestrictedApi")
    private fun checkNavigationActionToPossible(): Boolean {
        val destinationName = findNavController().currentBackStackEntry?.destination?.displayName
        if (destinationName == null) return false


        val curFragmentName = TasksFragment::class.simpleName
        val (destPrefix, destFragmentName) = destinationName
            .split('/')


        return curFragmentName?.lowercase() == destFragmentName.lowercase()
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

        override fun onDelete(item: TodoItem) {
            viewModel.removeItem(item)
        }

        override fun onAddItem() {
            navigateToTaskCreation()
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

    private fun configureProgressIndication() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isListRefreshing.collect { isRefreshing ->
                    binding.swipeRefreshLayout.isRefreshing = isRefreshing
                    if (isRefreshing) {
                        binding.linearProgressIndicator.visibility = View.VISIBLE
                    } else {
                        viewLifecycleOwner.lifecycleScope.launch {
                            delay(1000)
                            binding.linearProgressIndicator.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun configureSwipeRefreshBehaviourAndStyles() {
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(
            R.color.back_secondary_elevated
        )
        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(R.color.color_blue, requireContext().theme)
        )

        binding.appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            if (binding.swipeRefreshLayout.isRefreshing.not()) {
                binding.swipeRefreshLayout.isEnabled = verticalOffset >= 0
            }
        }
    }

    private fun configureSwipeRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshTodoList()
        }
    }

    private fun setupErrorListener() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errors.collect { message ->
                    message?.let { showErrorMessage(it) }
                    viewModel.errorMessageShown()
                }
            }
        }
    }

    private fun showErrorMessage(message: String) {
        val snackbar = Snackbar.make(binding.coordinator, message, Snackbar.LENGTH_LONG)
            .setTextMaxLines(6)
            .setBackgroundTint(resources.getColor(R.color.back_secondary, requireContext().theme))
            .setTextColor(resources.getColor(R.color.label_primary, requireContext().theme))
        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snackbar.show()
    }
}
package com.pasha.all_tasks.api

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pasha.all_tasks.databinding.FragmentTasksBinding
import com.pasha.all_tasks.internal.TasksViewModel
import com.pasha.all_tasks.internal.adapters.ActionsListener
import com.pasha.all_tasks.internal.adapters.ItemSwipeHelperCallback
import com.pasha.all_tasks.internal.adapters.TasksRecyclerViewAdapter
import com.pasha.all_tasks.internal.di.DaggerTasksComponent
import com.pasha.android_core.di.findDependencies
import com.pasha.android_core.presentation.TodoItemViewModel
import com.pasha.android_core.presentation.lazyViewModel
import com.pasha.core_ui.R
import com.pasha.domain.entities.TaskProgress
import com.pasha.domain.entities.TodoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val todoItemViewModel: TodoItemViewModel by activityViewModels<TodoItemViewModel>()

    @Inject
    internal lateinit var viewModelFactory: TasksViewModel.Factory
    private val viewModel: TasksViewModel by lazyViewModel {
        viewModelFactory.create()
    }

    @Inject
    internal lateinit var navigationProvider: TasksNavCommandsProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val rootView = binding.root

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaggerTasksComponent.factory()
            .create(findDependencies<TasksDeps>(), findDependencies<TasksNavDeps>())
            .inject(this)

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

        findNavController().navigate(navigationProvider.toTaskEdit.action)
    }

    private fun navigateToTaskEditing(task: TodoItem) {
        if (checkNavigationActionToPossible() != true) return

        todoItemViewModel.passTodoItem(item = task)
        findNavController().navigate(navigationProvider.toTaskEdit.action)
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
            if (item.itemId == com.pasha.all_tasks.R.id.actionVisible) {
                viewModel.changeDoneTasksVisibility()
            }
            true
        }
    }

    private fun configureVisibilityIcon() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksVisibility.collect { isAllTaskShown ->
                    val visibilityItem =
                        binding.toolbar.menu.findItem(com.pasha.all_tasks.R.id.actionVisible)
                    if (isAllTaskShown) {
                        visibilityItem.setIcon(com.pasha.all_tasks.R.drawable.ic_visible_24)
                    } else {
                        visibilityItem.setIcon(com.pasha.all_tasks.R.drawable.ic_invisible_24)
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
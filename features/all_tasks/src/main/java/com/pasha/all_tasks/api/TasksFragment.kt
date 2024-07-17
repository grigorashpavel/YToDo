package com.pasha.all_tasks.api

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.pasha.all_tasks.databinding.DeletionSnackbackLayoutBinding
import com.pasha.all_tasks.databinding.FragmentTasksBinding
import com.pasha.all_tasks.internal.TasksViewModel
import com.pasha.all_tasks.internal.adapters.ActionsListener
import com.pasha.all_tasks.internal.adapters.ItemSwipeHelperCallback
import com.pasha.all_tasks.internal.adapters.TasksRecyclerViewAdapter
import com.pasha.all_tasks.internal.di.DaggerTasksComponent
import com.pasha.android_core.di.findDependencies
import com.pasha.android_core.presentation.TodoItemViewModel
import com.pasha.android_core.presentation.activityLazyViewModel
import com.pasha.android_core.presentation.fragmentLazyViewModel
import com.pasha.core_ui.R
import com.pasha.domain.entities.TaskProgress
import com.pasha.domain.entities.TodoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    @Inject
    internal lateinit var todoViewModelFactory: TodoItemViewModel.Factory

    private val todoItemViewModel: TodoItemViewModel by activityLazyViewModel {
        todoViewModelFactory.create()
    }

    @Inject
    internal lateinit var viewModelFactory: TasksViewModel.Factory
    private val viewModel: TasksViewModel by fragmentLazyViewModel {
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
        configureTasksMenuListener()
        configureVisibilityIcon()
        configureSwipeRefreshBehaviourAndStyles()
        configureAppBarOffsetListener()
        configureSwipeRefreshListener()
        setupErrorListener()
        configureProgressIndication()
        configureCancellationSnackbar()
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

        findNavController().navigate(
            navigationProvider.toTaskEdit.action,
            navigationProvider.toTaskEdit.args,
            navigationProvider.toTaskEdit.navOptions
        )
    }

    private fun navigateToTaskEditing(task: TodoItem) {
        if (checkNavigationActionToPossible() != true) return

        todoItemViewModel.passTodoItem(item = task)
        findNavController().navigate(
            navigationProvider.toTaskEdit.action,
            navigationProvider.toTaskEdit.args,
            navigationProvider.toTaskEdit.navOptions
        )
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
            todoItemViewModel.removeTask(item)
        }

        override fun onAddItem() {
            navigateToTaskCreation()
        }
    }

    private fun configureTasksMenuListener() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == com.pasha.all_tasks.R.id.actionVisible) {
                viewModel.changeDoneTasksVisibility()
            } else if (item.itemId == com.pasha.all_tasks.R.id.settings) {
                findNavController().navigate(
                    navigationProvider.toSettings.action,
                    navigationProvider.toSettings.args,
                    navigationProvider.toSettings.navOptions
                )
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

    private fun configureAppBarOffsetListener() {
        binding.appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            val isRefreshPossible = verticalOffset >= 0
            setSwipeRefreshEnableState(isRefreshPossible)

            val isOtherMenuOptionsVisible = verticalOffset >= 0
            setOtherOptionsMenuVisibility(isOtherMenuOptionsVisible)
        }
    }

    private fun setOtherOptionsMenuVisibility(isVisible: Boolean) {
        binding.toolbar.menu.findItem(com.pasha.all_tasks.R.id.settings).isVisible = isVisible

    }

    private fun configureSwipeRefreshBehaviourAndStyles() {
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(
            R.color.back_secondary_elevated
        )
        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(R.color.color_blue, requireContext().theme)
        )
    }

    private fun setSwipeRefreshEnableState(isEnable: Boolean) {
        if (binding.swipeRefreshLayout.isRefreshing.not()) {
            binding.swipeRefreshLayout.isEnabled = isEnable
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

    @SuppressLint("RestrictedApi")
    private fun configureCancellationSnackbar() {
        val customView = layoutInflater
            .inflate(com.pasha.all_tasks.R.layout.deletion_snackback_layout, null, false)

        val snackbar = createCustomSnackbar(customView)

        val snackBinding = DeletionSnackbackLayoutBinding.bind(customView)
        snackBinding.btnCancel.setOnClickListener {
            todoItemViewModel.cancelDeleteTaskOperation()
            snackbar.dismiss()
        }

        setDeletionSnackbarProgressIndication(snackBinding)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                todoItemViewModel.isDeletionStart.collect { isStarted ->
                    if (isStarted) {
                        snackBinding.tvTaskText.text = getString(
                            com.pasha.all_tasks.R.string.task_name_param_label,
                            todoItemViewModel.taskToDeletionName
                        )
                        snackbar.show()
                    } else snackbar.dismiss()
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun createCustomSnackbar(newView: View): Snackbar {
        val snackbar = Snackbar.make(binding.coordinator, "", Snackbar.LENGTH_INDEFINITE)
        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE

        val snackbarLayout = snackbar.view as SnackbarLayout
        snackbarLayout.setPadding(0)
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
        snackbarLayout.addView(newView, 0)

        return snackbar
    }

    @SuppressLint("RestrictedApi")
    private fun setDeletionSnackbarProgressIndication(binding: DeletionSnackbackLayoutBinding) {
        val startBgColor = resources.getColor(R.color.back_primary, requireActivity().theme)
        val endBgColor = resources.getColor(R.color.color_red, requireActivity().theme)
        val evaluator = ArgbEvaluator()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                todoItemViewModel.timerFlow.collect { curSec ->
                    binding.btnCancel.text =
                        getString(com.pasha.all_tasks.R.string.action_cancel_param_label, curSec)

                    val fraction = curSec / todoItemViewModel.totalCancellationTimeSec.toFloat()
                    val color = evaluator.evaluate(fraction, endBgColor, startBgColor) as Int
                    binding.root.setBackgroundColor(
                        color
                    )
                }
            }
        }
    }
}
package com.pasha.ytodo.presentation.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pasha.ytodo.R
import com.pasha.ytodo.databinding.FragmentTaskEditBinding
import com.pasha.ytodo.domain.models.TaskPriority
import com.pasha.ytodo.presentation.TodoItemViewModel
import com.pasha.ytodo.presentation.edit.adapters.DropDownMenuPriorityArrayAdapter
import java.time.LocalDateTime
import java.util.Calendar


class TaskEditFragment : Fragment() {
    private var _binding: FragmentTaskEditBinding? = null
    private val binding get() = _binding!!

    private var months: Array<String>? = null
    private val todoItemViewModel by activityViewModels<TodoItemViewModel>()
    private val viewModel: EditTaskViewModel by viewModels {
        EditTaskViewModel.provideFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        months = resources.getStringArray(R.array.deadline_formatted_months)

        val task = todoItemViewModel.sharedTodoItem
        if (task != null) {
            viewModel.changePriority(task.priority)
            viewModel.changeDeadline(task.deadline)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureTaskTextField()
        configureTaskDeadlineWidget()
        configureNavigationBackToolbarButton()
        configureBackPressedAction()
        configureSaveButtonListener()
        configureRemoveButton()
    }

    override fun onResume() {
        super.onResume()

        configurePriorityMenu()
    }

    private fun configureTaskDeadlineWidget() {
        setDeadlineIfEnabled()

        binding.deadlineOptionSwitch.setOnCheckedChangeListener { _, isDeadlineEnabled ->
            binding.calendarTaskDeadlineLayout.isEnabled = isDeadlineEnabled

            if (isDeadlineEnabled.not()) {
                clearDeadlineField()
                viewModel.changeDeadline(null)
            }
        }

        binding.tvDeadlineDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setDeadlineIfEnabled() {
        val isDeadlineEnabled = viewModel.editState.deadline != null
        binding.calendarTaskDeadlineLayout.isEnabled = isDeadlineEnabled
        binding.deadlineOptionSwitch.isChecked = isDeadlineEnabled

        if (isDeadlineEnabled) {
            val deadline = viewModel.editState.deadline!!
            val day = deadline.dayOfMonth
            val month = deadline.month.value - 1
            val year = deadline.year

            val formattedDate = getFormattedDeadline(day, month, year)
            binding.tvDeadlineDate.setText(formattedDate)
        }
    }

    private fun clearDeadlineField() {
        binding.tvDeadlineDate.text.clear()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth: Int = calendar.get(Calendar.MONTH)
        val currentDayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(), R.style.Theme_Pasha_DatePicker, { _, year, month, dayOfMonth ->
                val deadline = getFormattedDeadline(dayOfMonth, month, year)
                binding.tvDeadlineDate.setText(deadline)

                viewModel.changeDeadline(createDeadlineDate(dayOfMonth, month, year))
            }, currentYear, currentMonth, currentDayOfMonth
        )

        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, getReadyButtonText(), datePicker)
        datePicker.datePicker.minDate = calendar.timeInMillis
        datePicker.show()
    }

    private fun getReadyButtonText() = requireContext().getString(R.string.ready_text)

    private fun getFormattedDeadline(day: Int, month: Int, year: Int): String {
        val formattedMonth = if (months != null) months!![month] else month.toString()

        return "$day ${formattedMonth.lowercase()} $year"
    }

    private fun configureNavigationBackToolbarButton() {
        binding.editToolbar.setNavigationOnClickListener {
            exitBack()
        }
    }

    private fun exitBack() {
        todoItemViewModel.clearSharedItem()
        findNavController().navigateUp()

    }

    private fun configureBackPressedAction() {
        requireActivity().onBackPressedDispatcher.addCallback(owner = viewLifecycleOwner) {
            exitBack()
        }
    }

    private fun configureTaskTextField() {
        val task = todoItemViewModel.sharedTodoItem
        if (task == null) return

        binding.etTaskText.setText(task.text)
    }

    private fun configurePriorityMenu() {
        setDefaultPriorityMenuItem()
        setPriorityMenuAdapter()
        setPriorityMenuListener()
    }

    private fun setDefaultPriorityMenuItem() {
        val task = todoItemViewModel.sharedTodoItem
        val isPriorityEmpty = binding.tvDropdownMenu.text.isEmpty()

        if (task == null) {
            binding.tvDropdownMenu.setText(TaskPriority.NORMAL.stringId)
        } else if (isPriorityEmpty) {
            binding.tvDropdownMenu.setText(task.priority.stringId)

        }
    }

    private fun setPriorityMenuAdapter() {
        val priorities = TaskPriority.entries
        val prioritiesText = priorities.map {
            requireContext().getString(it.stringId)
        }.toTypedArray()
        val menuAdapter = DropDownMenuPriorityArrayAdapter(
            requireContext(), R.layout.item_priority_dropdown_menu, prioritiesText
        )

        binding.tvDropdownMenu.setAdapter(menuAdapter)
    }

    private fun setPriorityMenuListener() {
        val priorities = TaskPriority.entries
        binding.tvDropdownMenu.setOnItemClickListener { _, _, position, _ ->
            viewModel.changePriority(priorities[position])
        }
    }

    private fun configureSaveButtonListener() {
        binding.editToolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.actionSave) {
                val isOpenedExistTask = todoItemViewModel.sharedTodoItem != null

                if (isOpenedExistTask) {
                    val oldTask = todoItemViewModel.sharedTodoItem
                    viewModel.changeTask(oldTask!!, binding.etTaskText.text.toString())
                } else {
                    viewModel.createTask(binding.etTaskText.text.toString(), LocalDateTime.now())
                }

                exitBack()
            }
            true
        }
    }

    private fun createDeadlineDate(day: Int, month: Int, year: Int): LocalDateTime {
        return LocalDateTime.of(year, month, day, 0, 0)
    }

    private fun configureRemoveButton() {
        val isEditMode = todoItemViewModel.sharedTodoItem != null
            binding.btnDeleteTask.isEnabled = isEditMode

        binding.btnDeleteTask.setOnClickListener {
            val taskToRemove = todoItemViewModel.sharedTodoItem
            if (taskToRemove != null)
                viewModel.removeTask(taskToRemove)

            exitBack()
        }
    }
}
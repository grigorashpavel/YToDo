package com.pasha.all_tasks.internal.adapters

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pasha.core_ui.R
import com.pasha.all_tasks.databinding.ItemTaskBinding
import com.pasha.domain.entities.TaskPriority
import com.pasha.domain.entities.TaskProgress
import com.pasha.domain.entities.TodoItem
import java.time.format.DateTimeFormatter

class TasksViewHolder(
    private val binding: ItemTaskBinding,
    private val actionsClickListener: ActionsListener
) : RecyclerView.ViewHolder(binding.root) {
    private var currentItem: TodoItem? = null

    init {
        configureListeners()
    }

    fun bind(task: TodoItem) {
        currentItem = task

        configureTaskProgressCheckbox()
        configureTaskText()
        configureTasksDeadline()
    }

    private fun configureTaskProgressCheckbox() {
        val isImportant = currentItem!!.priority == TaskPriority.HIGH
        binding.checkboxTaskProgress.buttonTintList = if (isImportant) {
            getImportantColorStates()
        } else {
            getUsualColorStates()
        }

        val isTaskCompleted = currentItem!!.progress == TaskProgress.DONE
        binding.checkboxTaskProgress.isChecked = isTaskCompleted
    }

    private fun configureTaskText() {
        when {
            currentItem!!.priority == TaskPriority.HIGH -> {
                val importantText = getHighImportantEmoji() + currentItem!!.text
                binding.tvTaskText.text = importantText
            }

            currentItem!!.priority == TaskPriority.LOW -> {
                val importantText = getLowImportantEmoji() + " " + currentItem!!.text
                binding.tvTaskText.text = importantText
            }

            else -> binding.tvTaskText.text = currentItem!!.text
        }

        if (currentItem!!.progress == TaskProgress.DONE) {
            makeTextStrikeTrough()
        } else {
            makeTextNormal()
        }
    }

    private fun makeTextStrikeTrough() {
        binding.tvTaskText.paintFlags =
            binding.tvTaskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    private fun makeTextNormal() {
        binding.tvTaskText.paintFlags =
            binding.tvTaskText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    private fun configureListeners() {
        configureItemClickListener()
        configureCheckboxListener()
        configureInfoButtonListener()
    }

    private fun configureInfoButtonListener() {
        binding.btnTaskInfo.setOnClickListener {
            actionsClickListener.onClickInfoButton(currentItem!!)
        }
    }

    private fun configureItemClickListener() {
        binding.root.setOnClickListener {
            actionsClickListener.onClickItem(currentItem!!)
        }
    }

    private fun configureCheckboxListener() {
        binding.checkboxTaskProgress.setOnClickListener {
            val isTaskCompleted = binding.checkboxTaskProgress.isChecked

            actionsClickListener.onClickProgressCheckbox(currentItem!!, isTaskCompleted)
        }
    }

    fun startActionChangeProgress() {
        actionsClickListener.onClickProgressCheckbox(currentItem!!, true)
    }

    fun startActionDeleteItem() {
        actionsClickListener.onDelete(currentItem!!)
    }

    private fun getLowImportantEmoji(): String {
        val emoji = binding.root.context.getText(R.string.item_task_low_important_emoji_code)
        return emoji.toString()
    }

    private fun getHighImportantEmoji(): String {
        val emoji = binding.root.context.getText(R.string.item_task_high_important_emoji_code)
        return emoji.toString()
    }

    private fun getImportantColorStates(): ColorStateList {
        return binding.root.context.getColorStateList(R.color.item_task_checkbox_important_color_states)
    }

    private fun getUsualColorStates(): ColorStateList {
        return binding.root.context.getColorStateList(R.color.item_task_checkbox_usual_color_states)
    }

    private fun configureTasksDeadline() {
        val deadline = currentItem?.deadline
        if (deadline == null) {
            binding.deadlineBody.visibility = View.GONE
            return
        }

        binding.deadlineBody.visibility = View.VISIBLE

        val deadlineFormattedString =
            deadline.format(DateTimeFormatter.ofPattern("dd:MM:yyyy")).toString()
        binding.deadlineBody.text = "Дедлайн: $deadlineFormattedString"
    }
}
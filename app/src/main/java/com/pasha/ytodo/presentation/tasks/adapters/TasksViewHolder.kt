package com.pasha.ytodo.presentation.tasks.adapters

import android.content.res.ColorStateList
import android.graphics.Paint
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pasha.ytodo.R
import com.pasha.ytodo.databinding.ItemTaskBinding
import com.pasha.ytodo.domain.models.TaskPriority
import com.pasha.ytodo.domain.models.TaskProgress
import com.pasha.ytodo.domain.models.TodoItem
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
        if (currentItem!!.priority == TaskPriority.HIGH) {
            binding.checkboxTaskProgress.buttonTintList = getImportantColorStates()
        }

        val isTaskCompleted = currentItem!!.progress == TaskProgress.DONE
        binding.checkboxTaskProgress.isChecked = isTaskCompleted
    }

    private fun configureTaskText() {
        if (currentItem!!.priority == TaskPriority.HIGH) {
            val importantText = getImportantEmoji() + currentItem!!.text
            binding.tvTaskText.text = importantText
        } else if (currentItem != null) {
            binding.tvTaskText.text = currentItem!!.text
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

    private fun getImportantEmoji(): String {
        val emoji = binding.root.context.getText(R.string.item_task_important_emoji_binary_code)
        return emoji.toString()
    }

    private fun getImportantColorStates(): ColorStateList {
        return binding.root.context.getColorStateList(R.color.item_task_checkbox_important_color_states)
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
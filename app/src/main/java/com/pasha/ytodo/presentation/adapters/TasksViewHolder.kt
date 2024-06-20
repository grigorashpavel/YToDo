package com.pasha.ytodo.presentation.adapters

import android.content.res.ColorStateList
import android.graphics.Paint
import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import com.pasha.ytodo.R
import com.pasha.ytodo.databinding.ItemTaskBinding
import com.pasha.ytodo.domain.models.TaskPriority
import com.pasha.ytodo.domain.models.TaskProgress
import com.pasha.ytodo.domain.models.TodoItem

class TasksViewHolder(private val binding: ItemTaskBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(task: TodoItem) {
        configureTaskProgressCheckbox(task)
        configureTaskText(task)
    }

    private fun configureTaskProgressCheckbox(task: TodoItem) {
        if (task.priority == TaskPriority.HIGH) {
            binding.checkboxTaskProgress.buttonTintList = getImportantColorStates()
        }

        val isTaskCompleted = task.progress == TaskProgress.DONE
        binding.checkboxTaskProgress.isChecked = isTaskCompleted
    }

    private fun configureTaskText(task: TodoItem) {
        if (task.priority == TaskPriority.HIGH) {
            val importantText = getImportantEmoji() + task.text
            binding.tvTaskText.text = importantText
        } else {
            binding.tvTaskText.text = task.text
        }

        if (task.progress == TaskProgress.DONE) {
            makeTextStrikeTrough()
        }
    }

    private fun makeTextStrikeTrough() {
        binding.tvTaskText.paintFlags =
            binding.tvTaskText.paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG
    }

    private fun makeTextNormal() {
        binding.tvTaskText.paintFlags =
            binding.tvTaskText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    fun configureListeners() {
        binding.checkboxTaskProgress.setOnClickListener {
            val isTaskCompleted = binding.checkboxTaskProgress.isChecked

            if (isTaskCompleted) {
                makeTextStrikeTrough()
            } else {
                makeTextNormal()
            }
        }

        binding.btnTaskInfo.setOnClickListener {

        }
    }

    private fun getImportantEmoji(): String {
        val emoji = binding.root.context.getText(R.string.item_task_important_emoji_binary_code)
        return emoji.toString()
    }

    private fun getImportantColorStates(): ColorStateList {
        return binding.root.context.getColorStateList(R.color.item_task_checkbox_important_color_states)
    }
}
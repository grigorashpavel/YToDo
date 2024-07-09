package com.pasha.ytodo.presentation.tasks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pasha.ytodo.databinding.ItemAddActionBinding
import com.pasha.ytodo.databinding.ItemTaskBinding
import com.pasha.ytodo.domain.entities.TodoItem

class TasksRecyclerViewAdapter(
    private val actionsListener: ActionsListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<TodoItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            TASK_VIEW_TYPE -> ItemTaskBinding.inflate(inflater, parent, false)
            else -> ItemAddActionBinding.inflate(inflater, parent, false)
        }

        return when (viewType) {
            TASK_VIEW_TYPE -> TasksViewHolder(binding as ItemTaskBinding, actionsListener)
            else -> AddActionViewHolder(
                binding as ItemAddActionBinding,
                actionsListener::onAddItem
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < items.size) {
            val item = items[position]
            (holder as TasksViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun getItemViewType(position: Int): Int {
        return when {
            position < items.size -> TASK_VIEW_TYPE
            else -> ADD_VIEW_TYPE
        }
    }

    fun setTodoTasks(tasks: List<TodoItem>) {
        val diffCallback = TasksDiffCallback(items, tasks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(tasks)
        diffResult.dispatchUpdatesTo(this)
    }

    companion object {
        private const val TASK_VIEW_TYPE = 1
        private const val ADD_VIEW_TYPE = 2
    }
}
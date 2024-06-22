package com.pasha.ytodo.presentation.tasks.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pasha.ytodo.databinding.ItemTaskBinding
import com.pasha.ytodo.domain.models.TodoItem

class TasksRecyclerViewAdapter(
    private val actionsListener: ActionsListener
) : RecyclerView.Adapter<TasksViewHolder>() {
    private val items = mutableListOf<TodoItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)

        return TasksViewHolder(binding, actionsListener)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun setTodoTasks(tasks: List<TodoItem>) {
        val diffCallback = TasksDiffCallback(items, tasks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(tasks)
        diffResult.dispatchUpdatesTo(this)
    }
}
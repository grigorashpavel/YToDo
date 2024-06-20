package com.pasha.ytodo.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasha.ytodo.databinding.ItemTaskBinding
import com.pasha.ytodo.domain.models.TodoItem

class TasksRecyclerViewAdapter : RecyclerView.Adapter<TasksViewHolder>() {
    private val items = mutableListOf<TodoItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)

        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task = items[position]
        holder.bind(task)
        holder.configureListeners()
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun setTodoTasks(tasks: List<TodoItem>) {
        items.clear()
        items.addAll(tasks)
        notifyDataSetChanged()
    }
}
package com.pasha.all_tasks.internal.adapters

import com.pasha.domain.entities.TodoItem


interface ActionsListener {
    fun onClickItem(item: TodoItem)
    fun onClickProgressCheckbox(item: TodoItem, newProgressState: Boolean)
    fun onClickInfoButton(item: TodoItem)
    fun onDelete(item: TodoItem)
    fun onAddItem()
}
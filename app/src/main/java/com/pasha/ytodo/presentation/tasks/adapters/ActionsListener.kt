package com.pasha.ytodo.presentation.tasks.adapters

import com.pasha.ytodo.domain.entities.TodoItem

interface ActionsListener {
    fun onClickItem(item: TodoItem)
    fun onClickProgressCheckbox(item: TodoItem, newProgressState: Boolean)
    fun onClickInfoButton(item: TodoItem)
}
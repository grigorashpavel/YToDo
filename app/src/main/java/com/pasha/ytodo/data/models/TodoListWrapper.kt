package com.pasha.ytodo.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoListWrapper(
    @SerialName("status")
    val status: String,
    @SerialName("list")
    val todos: List<TodoDto>,
    @SerialName("revision")
    val revision: Int? = null
)

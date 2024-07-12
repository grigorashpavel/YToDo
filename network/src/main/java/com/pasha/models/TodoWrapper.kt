package com.pasha.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TodoWrapper(
    @SerialName("status")
    val status: String,
    @SerialName("element")
    val item: TodoDto,
    @SerialName("revision")
    val revision: Int? = null
)

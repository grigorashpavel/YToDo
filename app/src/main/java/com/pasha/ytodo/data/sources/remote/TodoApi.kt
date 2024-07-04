package com.pasha.ytodo.data.sources.remote

import com.pasha.ytodo.data.models.TodoListWrapper
import com.pasha.ytodo.data.models.TodoWrapper
import com.pasha.ytodo.network.Headers
import com.pasha.ytodo.network.Paths
import com.pasha.ytodo.network.Service
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface TodoApi {
    @GET(Paths.GetTodoList)
    suspend fun getTodoList(): Response<TodoListWrapper>

    @PATCH(Paths.UpdateTodoList)
    suspend fun updateTodoList(
        @Header(Headers.LastKnownRevision) revision: Int,
        @Body content: TodoListWrapper
    ): Response<TodoListWrapper>

    @GET("${Paths.GetTodoItemById}/{${Paths.ItemIdPath}}")
    suspend fun getTodoItemById(
        @Path(Paths.ItemIdPath) todoId: String
    ): Response<TodoWrapper>

    @POST(Paths.AddTodoItem)
    suspend fun addTodoItem(
        @Header(Headers.LastKnownRevision) revision: Int,
        @Body content: TodoWrapper
    ): Response<TodoWrapper>

    @PUT("${Paths.UpdateTodoItemById}/{${Paths.ItemIdPath}}")
    suspend fun updateTodoItem(
        @Path(Paths.ItemIdPath) todoId: String,
        @Body content: TodoWrapper
    ): Response<TodoWrapper>

    @DELETE("${Paths.DeleteTodoItemById}/{${Paths.ItemIdPath}}")
    suspend fun deleteTodoItem(
        @Path(Paths.ItemIdPath) todoId: String
    ): Response<TodoWrapper>
}
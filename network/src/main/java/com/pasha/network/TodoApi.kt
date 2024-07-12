package com.pasha.network

import com.pasha.models.TodoListWrapper
import com.pasha.models.TodoWrapper
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
    @GET(com.pasha.network.Paths.GetTodoList)
    suspend fun getTodoList(): Response<TodoListWrapper>

    @PATCH(com.pasha.network.Paths.UpdateTodoList)
    suspend fun updateTodoList(
        @Header(com.pasha.network.Headers.LastKnownRevision) revision: Int,
        @Body content: TodoListWrapper
    ): Response<TodoListWrapper>

    @GET("${com.pasha.network.Paths.GetTodoItemById}/{${com.pasha.network.Paths.ItemIdPath}}")
    suspend fun getTodoItemById(
        @Path(com.pasha.network.Paths.ItemIdPath) todoId: String
    ): Response<TodoWrapper>

    @POST(com.pasha.network.Paths.AddTodoItem)
    suspend fun addTodoItem(
        @Header(com.pasha.network.Headers.LastKnownRevision) revision: Int,
        @Body content: TodoWrapper
    ): Response<TodoWrapper>

    @PUT("${com.pasha.network.Paths.UpdateTodoItemById}/{${com.pasha.network.Paths.ItemIdPath}}")
    suspend fun updateTodoItem(
        @Header(com.pasha.network.Headers.LastKnownRevision) revision: Int,
        @Path(com.pasha.network.Paths.ItemIdPath) todoId: String,
        @Body content: TodoWrapper
    ): Response<TodoWrapper>

    @DELETE("${com.pasha.network.Paths.DeleteTodoItemById}/{${com.pasha.network.Paths.ItemIdPath}}")
    suspend fun deleteTodoItem(
        @Header(com.pasha.network.Headers.LastKnownRevision) revision: Int,
        @Path(com.pasha.network.Paths.ItemIdPath) todoId: String
    ): Response<TodoWrapper>
}
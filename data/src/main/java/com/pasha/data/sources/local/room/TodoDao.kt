package com.pasha.data.sources.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pasha.data.sources.local.room.models.TodoRoomEntity
import com.pasha.domain.entities.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getTodoListFlow(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todos")
    fun getTodoList(): List<TodoItem>

    @Insert
    fun addItem(entity: TodoRoomEntity)

    @Update
    fun updateItem(entity: TodoRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAllItems(list: List<TodoRoomEntity>)

    @Query("DELETE FROM todos")
    fun deleteAllItems()

    @Query("DELETE FROM todos WHERE id = :itemId")
    fun deleteItemById(itemId: String)

    @Query("SELECT * FROM todos WHERE id = :itemId LIMIT 1")
    fun getItemById(itemId: String): TodoItem
}
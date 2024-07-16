package com.pasha.data.sources.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pasha.data.sources.local.room.models.SynchronizeRoomEntity
import com.pasha.data.sources.local.room.models.TodoRoomEntity
import javax.inject.Inject


@Database(
    entities = [TodoRoomEntity::class, SynchronizeRoomEntity::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(Converters::class)
abstract class TodoRoomDatabase: RoomDatabase() {
    abstract fun TodoDao(): TodoDao
    abstract fun SynchronizeDao(): SynchronizeDao

    companion object {
        @Volatile
        private var Instance: TodoRoomDatabase? = null

        fun getInstance(context: Context): TodoRoomDatabase {
            return Instance ?: synchronized(this) {
                Instance ?: buildDatabase(context).also {
                    Instance = it
                }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TodoRoomDatabase::class.java,
                "todo.db"
            ).build()
    }
}
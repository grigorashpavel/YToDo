package com.pasha.ytodo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "revision")
data class RevisionRoomEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "revision")
    val revision: Int
)

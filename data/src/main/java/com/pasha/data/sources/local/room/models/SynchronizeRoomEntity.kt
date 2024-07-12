package com.pasha.data.sources.local.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "synchronize")
data class SynchronizeRoomEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "revision")
    val revision: Int?,
    @ColumnInfo(name = "no_connection_modified")
    val isModifiedWithoutConnection: Boolean,
)

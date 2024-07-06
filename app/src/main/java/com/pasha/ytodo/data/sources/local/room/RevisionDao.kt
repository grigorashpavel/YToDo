package com.pasha.ytodo.data.sources.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pasha.ytodo.data.models.RevisionRoomEntity


@Dao
interface RevisionDao {
    @Query("SELECT revision from revision")
    fun getRevision(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateRevision(newRevision: RevisionRoomEntity)
}
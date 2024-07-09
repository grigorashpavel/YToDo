package com.pasha.ytodo.data.sources.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pasha.ytodo.data.models.SynchronizeRoomEntity


@Dao
interface SynchronizeDao {
    @Query("SELECT revision FROM synchronize")
    fun getRevision(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateSynchronizeInfo(newSyncInfo: SynchronizeRoomEntity)

    @Query("SELECT no_connection_modified FROM synchronize")
    fun isModifiedWithoutConnection(): Boolean
}
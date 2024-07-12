package com.pasha.data.repositories

import android.util.Log
import com.pasha.android_core.di.RemoteDataSource
import com.pasha.domain.DataSource
import com.pasha.domain.LocalDataSource
import com.pasha.domain.repositories.SynchronizeRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class SynchronizeRepositoryImpl @Inject constructor(
    @com.pasha.android_core.di.LocalDataSource private val localSource: DataSource,
    @RemoteDataSource private val remoteSource: DataSource
) : SynchronizeRepository {
    override suspend fun synchronizeItemsIfNeed() {
        coroutineScope {
            val remoteList = remoteSource.getTodoList()

            val remoteRevision = remoteSource.getRevision()!!
            val localRevision = localSource.getRevision()

            val isUpdatedWithoutConnection =
                (localSource as LocalDataSource).isModifiedWithoutConnection()

            Log.e("Sync", "remoteRevision=$remoteRevision, localRevision=$localRevision")
            Log.e("Sync", "isUpdatedWithoutConnection=$isUpdatedWithoutConnection")
            if (localRevision == null || remoteRevision > localRevision) {
                Log.e("Synchronize Local", remoteList.toString())

                localSource.updateTodoList(newList = remoteList)
                localSource.setNewRevision(remoteRevision)
            } else if (isUpdatedWithoutConnection) {
                val localList = localSource.getTodoList()
                Log.e("Synchronize Remote", localList.toString())

                remoteSource.updateTodoList(localList)
                remoteSource.getTodoList()
                val revision = remoteSource.getRevision()!!
                localSource.setNewRevision(revision)
                (localSource as LocalDataSource).setNoConnectionUpdate(false)
            } else { }
        }
    }
}
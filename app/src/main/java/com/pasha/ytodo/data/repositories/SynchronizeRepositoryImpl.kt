package com.pasha.ytodo.data.repositories

import android.util.Log
import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.domain.repositories.LocalDataSource
import com.pasha.ytodo.domain.repositories.SynchronizeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


class SynchronizeRepositoryImpl(
    private val localSource: DataSource,
    private val remoteSource: DataSource
) : SynchronizeRepository {
    override suspend fun synchronizeItemsIfNeed() {
        coroutineScope {
            val remoteList = remoteSource.getTodoList()

            val remoteRevision = remoteSource.getRevision()!!
            val localRevision = localSource.getRevision()

            val isUpdatedWithoutConnection =
                (localSource as LocalDataSource).isModifiedWithoutConnection()

            if (localRevision == null || remoteRevision > localRevision) {
                Log.e("Synchronize Local", remoteList.toString())

                localSource.updateTodoList(newList = remoteList)
                localSource.setNewRevision(remoteRevision)
            } else if (isUpdatedWithoutConnection) {
                val localList = localSource.getTodoList()
                Log.e("Synchronize Remote", localList.toString())

                remoteSource.updateTodoList(localList)
                (localSource as LocalDataSource).setNoConnectionUpdate(false)
            } else { }
        }
    }
}
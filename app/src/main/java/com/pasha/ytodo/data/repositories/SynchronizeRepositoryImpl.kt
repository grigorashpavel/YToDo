package com.pasha.ytodo.data.repositories

import android.util.Log
import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.domain.entities.TodoItem
import com.pasha.ytodo.domain.repositories.SynchronizeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class SynchronizeRepositoryImpl(
    private val localSource: DataSource,
    private val remoteSource: DataSource
) : SynchronizeRepository {
    override suspend fun synchronizeItems() {
        coroutineScope {
            val localList = async { localSource.getTodoList() }.await()
            val remoteList = async { remoteSource.getTodoList() }.await()

            val remoteRevision = remoteSource.getRevision()!!
            val localRevision = localSource.getRevision()!!

            if (remoteRevision > localRevision) {
                updateLocal(remoteList)
            } else if (remoteRevision < localRevision) {
                updateRemote(localList)
            }

            localSource.setNewRevision(remoteSource.getRevision()!!)
        }
    }

    private suspend fun updateLocal(newItems: List<TodoItem>) {
        localSource.updateTodoList(newItems)
    }

    private suspend fun updateRemote(newItems: List<TodoItem>) {
        remoteSource.updateTodoList(emptyList())
        remoteSource.updateTodoList(newItems)
    }

    override suspend fun isSynchronizeNeed(): Boolean {
        val remoteRevision = if (remoteSource.getRevision() == null) {
            remoteSource.getTodoList()
            remoteSource.getRevision()
        } else remoteSource.getRevision()

        val localRevision = localSource.getRevision()

        Log.e("SynchronizeRepositoryImpl", "localRev = $localRevision, remoteRev = $remoteRevision")
        return remoteRevision != localRevision
    }
}
package com.pasha.ytodo.domain.repositories

interface SynchronizeRepository {
    suspend fun synchronizeItems()
    suspend fun isSynchronizeNeed(): Boolean
}
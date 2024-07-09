package com.pasha.ytodo.domain.repositories

interface SynchronizeRepository {
    suspend fun synchronizeItemsIfNeed()
}
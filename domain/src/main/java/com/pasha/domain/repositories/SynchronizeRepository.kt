package com.pasha.domain.repositories

interface SynchronizeRepository {
    suspend fun synchronizeItemsIfNeed()
}
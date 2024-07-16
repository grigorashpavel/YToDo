package com.pasha.data.repositories

import com.pasha.android_core.identification.DeviceIdentificationManager
import com.pasha.domain.repositories.IdentificationRepository
import javax.inject.Inject

class IdentificationRepositoryImpl @Inject constructor(
    private val identificationManager: DeviceIdentificationManager
): IdentificationRepository {
    override fun getDeviceId(): String {
        return identificationManager.getAndroidDeviceId()
    }
}
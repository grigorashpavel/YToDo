package com.pasha.data.identification

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.pasha.android_core.identification.DeviceIdentificationManager
import javax.inject.Inject

class DeviceIdentificationManagerImpl @Inject constructor(
    private val context: Context
) : DeviceIdentificationManager {
    @SuppressLint("HardwareIds")
    override fun getAndroidDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun getDeviceName(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }
}
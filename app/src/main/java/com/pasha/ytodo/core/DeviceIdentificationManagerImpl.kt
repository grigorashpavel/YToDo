package com.pasha.ytodo.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings

class DeviceIdentificationManagerImpl(
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
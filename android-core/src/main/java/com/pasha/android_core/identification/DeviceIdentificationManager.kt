package com.pasha.android_core.identification

interface DeviceIdentificationManager{
    fun getAndroidDeviceId(): String
    fun getDeviceName(): String
}
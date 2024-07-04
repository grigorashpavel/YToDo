package com.pasha.ytodo.core

interface DeviceIdentificationManager{
    fun getAndroidDeviceId(): String
    fun getDeviceName(): String
}
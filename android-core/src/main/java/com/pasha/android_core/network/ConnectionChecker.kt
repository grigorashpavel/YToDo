package com.pasha.android_core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log


class ConnectionChecker(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    fun registerNetworkCallback(callback: NetworkCallback) {
        connectivityManager.registerNetworkCallback(networkRequest, callback)
    }

    fun unregisterNetworkCallback(callback: NetworkCallback) {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}
package com.pasha.ytodo.network

import android.content.Context
import android.net.ConnectivityManager


object ConnectionChecker {
    fun addDefaultNetworkAvailableListener(
        context: Context,
        listener: ConnectivityManager.OnNetworkActiveListener
    ) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.addDefaultNetworkActiveListener(listener)
    }

    fun removeDefaultNetworkAvailableListener(
        context: Context,
        listener: ConnectivityManager.OnNetworkActiveListener
    ) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.removeDefaultNetworkActiveListener(listener)
    }
}
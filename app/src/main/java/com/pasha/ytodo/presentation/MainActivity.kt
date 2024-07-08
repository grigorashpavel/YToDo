package com.pasha.ytodo.presentation

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pasha.ytodo.R
import com.pasha.ytodo.core.NetworkAvailableWorker
import com.pasha.ytodo.core.SynchronizeWorker
import com.pasha.ytodo.network.ConnectionChecker

class MainActivity : AppCompatActivity() {
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createNetworkCallback()

        val isFirstCreation = savedInstanceState == null
        if (isFirstCreation) {
            SynchronizeWorker.registerPeriodSynchronizeWork(this)
        }
    }

    override fun onStart() {
        super.onStart()

        networkCallback?.let { ConnectionChecker.registerNetworkCallback(this, it) }
    }

    override fun onStop() {
        super.onStop()

        networkCallback?.let { ConnectionChecker.unregisterNetworkCallback(this, it) }
    }

    private fun createNetworkCallback() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                Log.e("MainActivity", "onAvailable()")

                startSynchronizeIfNeed()
            }
        }
    }

    private fun startSynchronizeIfNeed() {
        SynchronizeWorker.startSynchronizeIfNeed(this)
    }
}

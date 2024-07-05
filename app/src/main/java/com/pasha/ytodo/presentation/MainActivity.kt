package com.pasha.ytodo.presentation

import android.net.ConnectivityManager
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
    private var networkCallback: ConnectivityManager.OnNetworkActiveListener? = null

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

    override fun onResume() {
        super.onResume()

        networkCallback?.let { ConnectionChecker.addDefaultNetworkAvailableListener(this, it) }
    }

    override fun onPause() {
        super.onPause()

        networkCallback?.let { ConnectionChecker.removeDefaultNetworkAvailableListener(this, it) }
    }

    private fun createNetworkCallback() {
        networkCallback = ConnectivityManager.OnNetworkActiveListener {
            NetworkAvailableWorker.startWorkerForOneRequest(this)
        }
    }
}

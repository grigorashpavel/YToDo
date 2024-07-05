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
    private var isFirstCreation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        isFirstCreation = savedInstanceState == null

        createNetworkCallback()

        if (isFirstCreation) {
            registerPeriodSynchronizeWork()
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
            startWorkerForOneRequest()
            startSynchronizeOnNetworkAvailable()
        }
    }

    private fun startWorkerForOneRequest() {
        Log.e("MainActivity", "startWorkerForOneRequest()")

        val workRequest = OneTimeWorkRequestBuilder<NetworkAvailableWorker>().build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            NetworkAvailableWorker.TAG,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun startSynchronizeOnNetworkAvailable() {
        Log.e("MainActivity", "startSynchronizedOnNetworkAvailable()")
        val request = SynchronizeWorker.createOneTimeRequest()
        WorkManager.getInstance(this).enqueueUniqueWork(
            SynchronizeWorker.ONE_TIME_TAG,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    private fun registerPeriodSynchronizeWork() {
        Log.e("MainActivity", "registerPeriodSynchronizeWork()")

        val request = SynchronizeWorker.createPeriodRequest()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            SynchronizeWorker.PERIOD_TAG,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }
}

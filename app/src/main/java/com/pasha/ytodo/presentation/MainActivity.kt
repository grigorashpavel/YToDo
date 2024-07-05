package com.pasha.ytodo.presentation

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pasha.ytodo.R
import com.pasha.ytodo.core.NetworkAvailableReceiver
import com.pasha.ytodo.core.NetworkAvailableWorker
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import com.pasha.ytodo.network.ConnectionChecker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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
        }
    }

    private fun startWorkerForOneRequest() {
        val workRequest = OneTimeWorkRequestBuilder<NetworkAvailableWorker>().build()

        WorkManager.getInstance(this@MainActivity).enqueueUniqueWork(
            NetworkAvailableWorker.TAG,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}

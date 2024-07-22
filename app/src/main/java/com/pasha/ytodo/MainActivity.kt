package com.pasha.ytodo

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.pasha.android_core.di.DepsMap
import com.pasha.android_core.di.HasDependencies
import com.pasha.android_core.network.ConnectionChecker
import com.pasha.android_core.synchronize.SynchronizeWorker
import com.pasha.ytodo.di.DaggerMainActivityComponent
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasDependencies {
    @Inject
    override lateinit var depsMap: DepsMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerMainActivityComponent.factory()
            .create(this)
            .inject(this)

        setContentView(R.layout.activity_main)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

package com.pasha.ytodo.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pasha.ytodo.R
import com.pasha.ytodo.data.repositories.TodoItemsRepositoryTestImpl
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        (applicationContext as TodoItemRepositoryProvider).todoItemsRepository.setupErrorListener()
    }

    private fun TodoItemsRepository.setupErrorListener() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                errors.onEach { throwable ->
                    //when + type check = profit
                    throwable.message?.let { showErrorMessage(it) }
                }.collect()
            }
        }
    }

    private fun showErrorMessage(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("TEMP ERROR MESSAGE")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->

            }
            .show()
    }
}

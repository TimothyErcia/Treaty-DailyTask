package com.treaty.dailytask

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.treaty.dailytask.utility.NetworkUtility
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val networkUtility: NetworkUtility by inject()
    private val taskGroupViewModel: TaskGroupViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        Log.d("NETWORK", "onCreate: ${networkUtility.isOnline()}")
    }
}
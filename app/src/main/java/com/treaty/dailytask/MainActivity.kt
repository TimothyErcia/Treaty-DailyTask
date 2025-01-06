package com.treaty.dailytask

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.treaty.dailytask.utility.NetworkUtility
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {
    private val networkUtility: NetworkUtility by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        Log.d("NETWORK", "onCreate: ${networkUtility.isOnline()}")
    }
}
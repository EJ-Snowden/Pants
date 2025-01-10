package com.example.pants.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pants.R
import com.example.pants.presentation.ui.GameFragment
import com.example.pants.utils.SystemUiManager


class MainActivity : AppCompatActivity() {

    private lateinit var systemUiManager: SystemUiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        systemUiManager = SystemUiManager(this)
        systemUiManager.hideSystemBars()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, GameFragment())
            .commit()
    }
}

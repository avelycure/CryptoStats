package com.avelycure.cryptostats.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.avelycure.cryptostats.R
import com.avelycure.cryptostats.presentation.home.CryptoInfoFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, CryptoInfoFragment())
                .commit()
    }
}
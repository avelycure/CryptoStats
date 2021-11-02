package com.avelycure.cryptostats.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.avelycure.cryptostats.R
import io.reactivex.rxjava3.plugins.RxJavaPlugins

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
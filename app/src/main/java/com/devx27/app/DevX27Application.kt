package com.devx27.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * DevX27Application — Hilt entry point.
 * All Hilt component generation is anchored here.
 */
@HiltAndroidApp
class DevX27Application : Application() {

    override fun onCreate() {
        super.onCreate()
        // Additional global setup (logging, crash reporting, etc.) goes here.
    }
}

package com.renatovaler.globantchallenge.di

import android.app.Application
import com.renatovaler.globantchallenge.core.network.NetworkHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobantChallenge: Application() {

    override fun onCreate() {
        super.onCreate()
        NetworkHelper.init(this)
    }
}
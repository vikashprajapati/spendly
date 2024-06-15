package dev.nomadicprogrammer.spendly

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dev.nomadicprogrammer.spendly.notification.NotificationChannelManager

@HiltAndroidApp
class MainApp : Application() {
    val context : Context = this
    override fun onCreate() {
        super.onCreate()
    }
}
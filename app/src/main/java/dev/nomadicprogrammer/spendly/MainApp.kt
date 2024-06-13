package dev.nomadicprogrammer.spendly

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApp : Application() {
    val context : Context = this
}
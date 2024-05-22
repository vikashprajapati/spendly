package dev.nomadicprogrammer.spendly.base

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.appSettings : DataStore<Preferences> by preferencesDataStore("app_settings")

val LAST_PROCESSED_SMS = longPreferencesKey("last_processed_sms")

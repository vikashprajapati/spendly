package dev.nomadicprogrammer.spendly.smsparser

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

object Util {
    fun smsReadPermissionAvailable(context: Context): Boolean {
        val isReadSmsPermissionAvailable = context.checkSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
        Log.d("Util", "Read SMS Permission: $isReadSmsPermissionAvailable")
        return isReadSmsPermissionAvailable
    }
}
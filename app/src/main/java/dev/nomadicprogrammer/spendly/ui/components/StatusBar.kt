package dev.nomadicprogrammer.spendly.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun StatusBarColor(surfaceBgColor: Color) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            (context as Activity).window.statusBarColor = surfaceBgColor.toArgb()
        }
    }
}
package dev.nomadicprogrammer.spendly

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.nomadicprogrammer.spendly.home.presentation.Home
import dev.nomadicprogrammer.spendly.smsparser.common.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.ui.theme.SpendlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpendlyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    var isPermissionAvailable by remember { mutableStateOf(false) }

                    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                        isPermissionAvailable = it
                    }

                    Home(name = "Vikash", isPermissionAvailable)

                    LaunchedEffect(key1 = true){
                        checkAndRequestSmsPermission(context, launcher) {
                            isPermissionAvailable = true
                        }
                    }
                }
            }
        }
    }
}

fun checkAndRequestSmsPermission(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    onPermissionAvailable: () -> Unit,
){
    if(!smsReadPermissionAvailable(context)){
        launcher.launch(android.Manifest.permission.READ_SMS)
    }else{
        onPermissionAvailable.invoke()
    }
}
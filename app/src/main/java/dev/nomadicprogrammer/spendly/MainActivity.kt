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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.nomadicprogrammer.spendly.home.presentation.Home
import dev.nomadicprogrammer.spendly.navigation.AppNavigator
import dev.nomadicprogrammer.spendly.smsparser.common.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.ui.theme.SpendlyTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpendlyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }
}

fun checkAndRequestPermission(
    context: Context,
    permission : String,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    onPermissionAvailable: () -> Unit,
){
    if(!smsReadPermissionAvailable(context)){
        launcher.launch(permission)
    }else{
        onPermissionAvailable.invoke()
    }
}
package dev.nomadicprogrammer.spendly

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.nomadicprogrammer.spendly.smsparser.SpendAnalyserController
import dev.nomadicprogrammer.spendly.smsparser.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.ui.theme.SpendlyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpendlyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val coroutineScope = rememberCoroutineScope()
                    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                        if (it) {
                            coroutineScope.launch {
                                SpendAnalyserController(context).launchTransactionalSmsClassifier()
                            }
                        } else {
                            Log.d("MainActivity", "Permission denied")
                        }
                    }
                    Column {
                        Button(
                            onClick = { launchSpendAnalyser(context, coroutineScope, launcher) }
                        ) {
                            Text(text = "Click me")
                        }
                    }
                }
            }
        }
    }
}

private fun launchSpendAnalyser(context: Context, coroutineScope: CoroutineScope, launcher: ManagedActivityResultLauncher<String, Boolean>) {
    checkAndRequestSmsPermission(context, launcher) {
        coroutineScope.launch {
            SpendAnalyserController(context).launchTransactionalSmsClassifier()
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpendlyTheme {
        Greeting("Android")
    }
}
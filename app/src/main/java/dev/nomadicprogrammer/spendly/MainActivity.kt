package dev.nomadicprogrammer.spendly

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.nomadicprogrammer.spendly.screen.Home
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController
import dev.nomadicprogrammer.spendly.smsparser.common.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
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
                    val recentTransaction = remember { mutableStateOf(listOf<TransactionalSms>()) }
                    Home(name = "Vikash", income = 340000.0 , spent = 100324.4 , recentTransactions = recentTransaction)
                    LaunchedEffect(key1 = true){
                        launchSpendAnalyser(context, coroutineScope, launcher){
                            recentTransaction.value = it
                        }
                    }
                }
            }
        }
    }
}

private fun launchSpendAnalyser(
    context: Context,
    coroutineScope: CoroutineScope,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    onReportGenerated: (transactionalSms : List<TransactionalSms>) -> Unit
) {
    checkAndRequestSmsPermission(context, launcher) {
        coroutineScope.launch {
            val spendAnalyserController = SpendAnalyserController(context)
            spendAnalyserController.launchTransactionalSmsClassifier()
            val data = spendAnalyserController.generateReport()
            onReportGenerated.invoke(data)
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
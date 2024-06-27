package dev.nomadicprogrammer.spendly.navigation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.nomadicprogrammer.spendly.checkAndRequestPermission
import dev.nomadicprogrammer.spendly.transaction.AllTransactions
import dev.nomadicprogrammer.spendly.home.presentation.Home
import dev.nomadicprogrammer.spendly.home.presentation.HomeViewModel
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.transaction.create.CreateTransaction

@Composable
fun AppNavigator(){
    val navigationController = rememberNavController()
    val homeViewModel : HomeViewModel = hiltViewModel<HomeViewModel>()

    NavHost(navController = navigationController, startDestination = Screen.Home.route){
        homeNavGraph(navigationController, homeViewModel)
        seeAllTransactions(homeViewModel)
        newTransaction(navigationController)
    }
}

fun NavGraphBuilder.seeAllTransactions(homeViewModel: HomeViewModel){
    composable(Screen.SeeAllTransaction.route){
        AllTransactions(homeViewModel)
    }
}

fun NavGraphBuilder.newTransaction(navController: NavController){
    composable(Screen.NewTransaction.route){ backstackEntry ->
        val transactionToCreateType = backstackEntry.arguments?.getString(Screen.NewTransaction.Args.TRANSACTION_TYPE)?.run {
            if (this == "Expense") TransactionType.DEBIT else TransactionType.CREDIT
        }
        CreateTransaction(navController, transactionToCreateType ?: TransactionType.DEBIT)
    }
}

fun NavGraphBuilder.homeNavGraph(navController: NavController, homeViewModel: HomeViewModel){
    composable(Screen.Home.route){
        var isPermissionAvailable by remember { mutableStateOf(false) }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            isPermissionAvailable = it
        }

        Home(navController, name = "Vikash", isPermissionAvailable, homeViewModel)
        val context = LocalContext.current.applicationContext
        LaunchedEffect(key1 = true){
            checkAndRequestPermission(
                context,
                Manifest.permission.READ_SMS,
                launcher
            ) {
                isPermissionAvailable = true
            }
        }
    }
}
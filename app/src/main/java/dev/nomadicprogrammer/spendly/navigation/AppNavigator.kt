package dev.nomadicprogrammer.spendly.navigation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import dev.nomadicprogrammer.spendly.base.TransactionStateHolder
import dev.nomadicprogrammer.spendly.checkAndRequestPermission
import dev.nomadicprogrammer.spendly.home.presentation.Home
import dev.nomadicprogrammer.spendly.home.presentation.HomeEvent
import dev.nomadicprogrammer.spendly.home.presentation.HomeViewModel
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.transaction.presentation.view.AllTransactions
import dev.nomadicprogrammer.spendly.transaction.presentation.create.CreateTransaction
import dev.nomadicprogrammer.spendly.transaction.presentation.view.TransactionDetails

@Composable
fun AppNavigator(){
    val navigationController = rememberNavController()
    val homeViewModel : HomeViewModel = hiltViewModel<HomeViewModel>()

    NavHost(navController = navigationController, startDestination = Home.route){
        homeNavGraph(navigationController, homeViewModel)
        seeAllTransactions(navigationController, homeViewModel)
        newTransaction(navigationController)
        transactionDetails(navigationController, homeViewModel)
    }
}

fun NavGraphBuilder.transactionDetails(navController: NavController, homeViewModel: HomeViewModel){
    composable(TransactionDetail.route){
        val transactionStateHolder = homeViewModel.state.collectAsState().value.selectedTransactionalSms?:return@composable

        TransactionDetails(
            navController = navController,
            transactionStateHolder = transactionStateHolder,
            onBackPressed = {
                homeViewModel.onEvent(HomeEvent.TransactionViewPageDismissed)
                navController.popBackStack()
            }
        )
    }
}

fun NavGraphBuilder.seeAllTransactions(navController: NavController, homeViewModel: HomeViewModel){
    composable(SeeAllTransaction.route){
        AllTransactions(navController, homeViewModel)
    }
}

fun NavGraphBuilder.newTransaction(navController: NavController){
    composable(NewTransaction.route){ backstackEntry ->
        val transactionToCreateType = backstackEntry.arguments?.getString(NewTransaction.Args.TRANSACTION_TYPE)?.run {
            if (this == "Expense") TransactionType.DEBIT else TransactionType.CREDIT
        }
        CreateTransaction(navController, transactionToCreateType ?: TransactionType.DEBIT)
    }
}

fun NavGraphBuilder.homeNavGraph(navController: NavController, homeViewModel: HomeViewModel){
    composable(Home.route){
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
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.nomadicprogrammer.spendly.checkAndRequestPermission
import dev.nomadicprogrammer.spendly.home.presentation.AllTransactions
import dev.nomadicprogrammer.spendly.home.presentation.Home
import dev.nomadicprogrammer.spendly.home.presentation.HomeEvent
import dev.nomadicprogrammer.spendly.home.presentation.HomeViewModel

@Composable
fun AppNavigator(){
    val navigationController = rememberNavController()

    NavHost(navController = navigationController, startDestination = Screen.Home.route){
        homeNavGraph(navigationController)
        seeAllTransactions(navigationController)
    }
}

fun NavGraphBuilder.seeAllTransactions(navController: NavController){
    composable(Screen.SeeAllTransaction.route){
        AllTransactions(navController)
    }
}

fun NavGraphBuilder.homeNavGraph(navController: NavController){
    composable(Screen.Home.route){
        var isPermissionAvailable by remember { mutableStateOf(false) }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            isPermissionAvailable = it
        }
        val homeViewModel : HomeViewModel = hiltViewModel<HomeViewModel>()

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
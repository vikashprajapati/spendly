package dev.nomadicprogrammer.spendly.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.navigation.Screen
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController
import dev.nomadicprogrammer.spendly.ui.components.TransactionItemCard
import dev.nomadicprogrammer.spendly.ui.utils.ViewBy

@Composable
fun AllTransactions(navController: NavController){
    val backStackEntry = remember(navController.currentBackStackEntryAsState()) {
        navController.getBackStackEntry(Screen.Home.route)
    }
    val homeViewModel : HomeViewModel = viewModel(
        viewModelStoreOwner = backStackEntry,
        key = "HomeViewModel",
        factory = HomeViewModelFactory(SpendAnalyserController(LocalContext.current.applicationContext))

    )
    val customFilter = stringResource(id = ViewBy.entries.toTypedArray()[homeViewModel.selectedTabIndex].resId)
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.custom_transactions, customFilter),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.padding(8.dp))

        LazyColumn() {
            items(homeViewModel.transactionsViewBy) { transaction ->
                TransactionItemCard(transaction){
                    navController.navigate(Screen.TransactionDetail.route)
                }
            }
        }
    }
}
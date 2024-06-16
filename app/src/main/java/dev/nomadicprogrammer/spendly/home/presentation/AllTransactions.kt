package dev.nomadicprogrammer.spendly.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.navigation.Screen
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserUseCase
import dev.nomadicprogrammer.spendly.transactiondetails.TransactionDetails
import dev.nomadicprogrammer.spendly.ui.components.TransactionItemCard
import dev.nomadicprogrammer.spendly.ui.utils.ViewBy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactions(homeViewModel: HomeViewModel){
    val uiState by homeViewModel.state.collectAsState()
    val customFilter = stringResource(id = ViewBy.entries.toTypedArray()[uiState.selectedTabIndex].resId)
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.custom_transactions, customFilter),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.padding(8.dp))
        val transactionsToShow = uiState.currentViewTransactions
        LazyColumn() {
            items(transactionsToShow) { transaction ->
                TransactionItemCard(transaction){
                    homeViewModel.onEvent(HomeEvent.TransactionSelected(transaction))
                }
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()
    if (uiState.dialogTransactionSms != null) {
        TransactionDetails(
            uiState.dialogTransactionSms!!,
            sheetState = sheetState,
            onDismiss = {
                homeViewModel.onEvent(HomeEvent.TransactionDialogDismissed)
            }
        )
    }
}
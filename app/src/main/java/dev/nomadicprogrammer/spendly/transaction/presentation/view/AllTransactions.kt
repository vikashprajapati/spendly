package dev.nomadicprogrammer.spendly.transaction.presentation.view

import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.home.presentation.HomeEvent
import dev.nomadicprogrammer.spendly.home.presentation.HomeViewModel
import dev.nomadicprogrammer.spendly.ui.components.TransactionItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactions(homeViewModel: HomeViewModel){
    val uiState by homeViewModel.state.collectAsState()
    val customFilter = stringResource(id = uiState.currentViewBy.resId)
    
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
        val context = LocalContext.current
        TransactionDetails(
            homeViewModel = homeViewModel,
            sheetState = sheetState,
            onDismiss = {
                homeViewModel.onEvent(HomeEvent.TransactionDialogDismissed)
            },
            onUpdateClick = {
                homeViewModel.onEvent(HomeEvent.TransactionUpdate(it))
                Toast.makeText(context, "Transaction updated", Toast.LENGTH_SHORT).show()
                // TODO: show a snackbar using proper state management
            }
        )
    }
}
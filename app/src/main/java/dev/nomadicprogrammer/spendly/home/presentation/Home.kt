package dev.nomadicprogrammer.spendly.home.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.base.TransactionCategory
import dev.nomadicprogrammer.spendly.navigation.Screen
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.transaction.transactiondetails.TransactionDetails
import dev.nomadicprogrammer.spendly.ui.components.CircularLoading
import dev.nomadicprogrammer.spendly.ui.components.TabButton
import dev.nomadicprogrammer.spendly.ui.components.TransactionItemCard
import dev.nomadicprogrammer.spendly.ui.components.TransactionSummaryChart
import dev.nomadicprogrammer.spendly.ui.utils.ViewBy
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController : NavController,
    name: String,
    readSmsPermissionAvailable: Boolean,
    viewModel : HomeViewModel
) {
    if (!readSmsPermissionAvailable) {
        return
    }

    LaunchedEffect(key1 = readSmsPermissionAvailable) {
        viewModel.onEvent(HomeEvent.ReadSmsPermissionGranted)
    }

    LaunchedEffect(null){
        viewModel.onEvent(HomeEvent.PageLoad)
    }
    val uiState by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = {
            navController.navigate(Screen.NewTransaction.route)
        }, shape = MaterialTheme.shapes.large) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add new transaction"
            )
        } }
    ) { padding ->
        Column(
            modifier = Modifier
            .padding(16.dp)
        ) {
            if (uiState.progress >= 0 && uiState.progress < 100) {
                CircularLoading(uiState.progress)
            }

            /*Text(
                text = stringResource(id = R.string.greeting_hello),
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = name,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight(800)
            )

            Spacer(modifier = Modifier.height(24.dp))*/

            val selectedTab = uiState.selectedTabIndex
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(ViewBy.entries.toTypedArray().indices.last){ index ->
                    val tab = ViewBy.entries[index]
                    TabButton(
                        isSelected = index == selectedTab, text = stringResource(id = tab.resId),
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        viewModel.onEvent(HomeEvent.ViewBySelected(tab, index))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val income by remember(uiState.currentViewTransactions) {
                derivedStateOf {
                    uiState.currentViewTransactions
                        .filter { it.type == TransactionType.CREDIT }
                        .sumOf { it.currencyAmount.amount }
                        .toFloat()
                }
            }
            val expense by remember(uiState.currentViewTransactions) {
                derivedStateOf {
                    uiState.currentViewTransactions
                        .filter { it.type == TransactionType.DEBIT }
                        .sumOf { it.currencyAmount.amount }
                        .toFloat()
                }
            }
            TransactionSummaryChart(income, expense, onChartClick = {})

            Spacer(modifier = Modifier.height(24.dp))

            val sheetState = rememberModalBottomSheetState()
            val context = LocalContext.current
            if (uiState.dialogTransactionSms != null) {
                TransactionDetails(
                    homeViewModel = viewModel,
                    sheetState = sheetState,
                    onDismiss = {
                        viewModel.onEvent(HomeEvent.TransactionDialogDismissed)
                    },
                    onUpdateClick = {
                        Log.d("Home", "Transaction updated: $it")
                        viewModel.onEvent(HomeEvent.TransactionUpdate(it))
                        Toast.makeText(context, "Transaction updated", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            RecentTransactions(
                uiState.recentTransactions,
                onTransactionSmsClick = {
                    viewModel.onEvent(HomeEvent.TransactionSelected(it))
                },
                onSeeAllClick = {
                    navController.navigate(Screen.SeeAllTransaction.route)
                }
            )
        }
    }
}

@Composable
fun RecentTransactions(
    recentTransactions: List<Transaction>,
    onTransactionSmsClick : (Transaction) -> Unit,
    onSeeAllClick : () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.recent_transactions),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                modifier = Modifier
                    .height(32.dp),
                contentPadding = PaddingValues(8.dp),
                onClick = onSeeAllClick,
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Text(
                    text = stringResource(id = R.string.see_all),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        LazyColumn {
            items(recentTransactions, key = { it.id ?: Random.nextInt() }) { transaction ->
                TransactionItemCard(transaction, onTransactionSmsClick)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable fun RecentPreview(){
    val transactions= listOf(
        Transaction.create(
            id = "dfd",
            TransactionType.DEBIT,
            Sms("id", "", "", System.currentTimeMillis()),
            CurrencyAmount("INR", 100.0),
            "Amazon",
            "12-12-2021",
            "Amit",
            "Sbi",
            TransactionCategory.TRAVEL
        ),

        Transaction.create(
            "idfd",
            TransactionType.CREDIT,
            Sms("id", "", "", System.currentTimeMillis()),
            CurrencyAmount("INR", 200.0),
            "Amazon",
            "12-12-2021",
            "Amit",
            "Sbi",
            TransactionCategory.TRANSFER
        )
    )
    Column(modifier = Modifier.padding(16.dp)) {
        RecentTransactions(
            recentTransactions = transactions,
            onTransactionSmsClick = {},
            onSeeAllClick = {}
        )
    }
}
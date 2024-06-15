package dev.nomadicprogrammer.spendly.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.navigation.Screen
import dev.nomadicprogrammer.spendly.notification.categories.TransactionNotification
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.transactiondetails.TransactionDetails
import dev.nomadicprogrammer.spendly.ui.components.TabButton
import dev.nomadicprogrammer.spendly.ui.components.TransactionItemCard
import dev.nomadicprogrammer.spendly.ui.components.TransactionSummaryChart
import dev.nomadicprogrammer.spendly.ui.utils.ViewBy

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

    LaunchedEffect(null){
        viewModel.onEvent(HomeEvent.PageLoad)
    }
    val uiState by viewModel.state.collectAsState()

    Column(modifier = Modifier
        .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.greeting_hello),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = name,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight(800)
        )

        Spacer(modifier = Modifier.height(24.dp))

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

        val income = uiState.currentViewTransactions
            .filter { it.type == TransactionType.CREDIT }
            .sumOf { it.currencyAmount.amount }
            .toFloat()
        val expense = uiState.currentViewTransactions
            .filter { it.type == TransactionType.DEBIT }
            .sumOf { it.currencyAmount.amount }
            .toFloat()
        TransactionSummaryChart(income, expense, onChartClick = {})

        Spacer(modifier = Modifier.height(24.dp))

        val sheetState = rememberModalBottomSheetState()
        if (uiState.dialogTransactionSms != null) {
            TransactionDetails(
                uiState.dialogTransactionSms!!,
                sheetState = sheetState,
                onDismiss = {
                    viewModel.onEvent(HomeEvent.TransactionDialogDismissed)
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
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = onSeeAllClick,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.see_all),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        modifier = Modifier.size(16.dp),
                        contentDescription = "See all transactions"
                    )
                }
            }
        }
        LazyColumn {
            items(recentTransactions) { transaction ->
                TransactionItemCard(transaction, onTransactionSmsClick)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable fun RecentPreview(){
    val transactions= listOf(
        Transaction.create(
            TransactionType.DEBIT,
            Sms("id", "", "", System.currentTimeMillis()),
            CurrencyAmount("INR", 100.0),
            "Amazon",
            "12-12-2021",
            "Amit",
            "Sbi",
            "Food"
        ),

        Transaction.create(
            TransactionType.CREDIT,
            Sms("id", "", "", System.currentTimeMillis()),
            CurrencyAmount("INR", 200.0),
            "Amazon",
            "12-12-2021",
            "Amit",
            "Sbi",
            "Food"
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
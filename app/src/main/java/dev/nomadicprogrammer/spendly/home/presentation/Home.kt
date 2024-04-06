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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.navigation.Screen
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import dev.nomadicprogrammer.spendly.ui.components.TabButton
import dev.nomadicprogrammer.spendly.ui.components.TransactionItemCard
import dev.nomadicprogrammer.spendly.ui.components.TransactionSummaryChart
import dev.nomadicprogrammer.spendly.ui.utils.ViewBy

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

    LaunchedEffect(key1 = true){
        viewModel.onEvent(HomeEvent.PageLoad)
    }

    val recentTransactions = viewModel.recentTransactions

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

        val selectedTab = viewModel.selectedTabIndex
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

        TransactionSummaryChart(viewModel.income, viewModel.expense, onChartClick = {})

        Spacer(modifier = Modifier.height(24.dp))

        RecentTransactions(
            recentTransactions,
            onTransactionSmsClick = {
                viewModel.onEvent(HomeEvent.TransactionSelected(it))
                navController.navigate(Screen.TransactionDetail.route)
            },
            onSeeAllClick = {
                navController.navigate(Screen.SeeAllTransaction.route)
            }
        )
    }
}

@Composable
fun RecentTransactions(
    recentTransactions: MutableState<List<TransactionalSms>>,
    onTransactionSmsClick : (TransactionalSms) -> Unit,
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
        LazyColumn() {
            items(recentTransactions.value) { transaction ->
                TransactionItemCard(transaction, onTransactionSmsClick)
            }
        }
    }
}

@Preview(wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE, showBackground = true, showSystemUi = true)
@Composable fun HomePreview() {
    Home(
        rememberNavController(),
        name = "John Doe", readSmsPermissionAvailable = true,
        HomeViewModel(SpendAnalyserController(LocalContext.current.applicationContext))
    )
}

@Preview(showBackground = true, showSystemUi = false)
@Composable fun RecentPreview(){
    val transactions : MutableState<List<TransactionalSms>> = remember {
        mutableStateOf(
            listOf(
                TransactionalSms.create(
                    "debit", Sms("dfd", "Amazon", "Hello", System.currentTimeMillis()), CurrencyAmount("INR", 100.0), "", ""
                )!!
            )
        )
    }
    Column(modifier = Modifier.padding(16.dp)) {
        RecentTransactions(
            recentTransactions = transactions,
            onTransactionSmsClick = {},
            onSeeAllClick = {}
        )
    }
}
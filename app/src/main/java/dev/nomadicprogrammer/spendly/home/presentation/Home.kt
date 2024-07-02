package dev.nomadicprogrammer.spendly.home.presentation

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.base.TransactionCategoryResource
import dev.nomadicprogrammer.spendly.base.TransactionStateHolder
import dev.nomadicprogrammer.spendly.navigation.NewTransaction
import dev.nomadicprogrammer.spendly.navigation.SeeAllTransaction
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.TransactionCategory
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import dev.nomadicprogrammer.spendly.transaction.presentation.view.TransactionDetails
import dev.nomadicprogrammer.spendly.ui.components.CircularLoading
import dev.nomadicprogrammer.spendly.ui.components.FabActionItem
import dev.nomadicprogrammer.spendly.ui.components.FabMainItem
import dev.nomadicprogrammer.spendly.ui.components.MultipleFloatingActionButton
import dev.nomadicprogrammer.spendly.ui.components.TabButton
import dev.nomadicprogrammer.spendly.ui.components.TransactionItemCard
import dev.nomadicprogrammer.spendly.ui.components.TransactionSummaryChart
import kotlinx.coroutines.launch
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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val statusBarColor = MaterialTheme.colorScheme.primary
    LaunchedEffect(key1 = readSmsPermissionAvailable) {
        viewModel.onEvent(HomeEvent.ReadSmsPermissionGranted)
    }

    LaunchedEffect(null){
        viewModel.onEvent(HomeEvent.PageLoad)
        coroutineScope.launch {
            (context as Activity).window.statusBarColor = statusBarColor.toArgb()
        }
    }
    val uiState by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            HomeMainFab(navController)
        }
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

            val income by remember(uiState.currentViewTransactionalSms) {
                derivedStateOf {
                    uiState.currentViewTransactionalSms
                        .filter { it.transactionalSms.type == TransactionType.CREDIT }
                        .sumOf { it.transactionalSms.currencyAmount.amount }
                        .toFloat()
                }
            }
            val expense by remember(uiState.currentViewTransactionalSms) {
                derivedStateOf {
                    uiState.currentViewTransactionalSms
                        .filter { it.transactionalSms.type == TransactionType.DEBIT }
                        .sumOf { it.transactionalSms.currencyAmount.amount }
                        .toFloat()
                }
            }
            TransactionSummaryChart(income, expense, onChartClick = {})

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.dialogTransactionalSmsSms != null) {
                val sheetState = rememberModalBottomSheetState()
                TransactionDetails(
                    homeViewModel = viewModel,
                    sheetState = sheetState,
                    onDismiss = {
                        viewModel.onEvent(HomeEvent.TransactionDialogDismissed)
                    },
                    onUpdateClick = {
                        Log.d("Home", "Transaction updated: $it")
                        val newStateHolder = uiState.dialogTransactionalSmsSms?.copy(transactionalSms = it)
                        viewModel.onEvent(HomeEvent.TransactionUpdate(newStateHolder!!))
                    }
                )
            }

            val toastMessage = viewModel.toastMessage.collectAsState(null)
            if (!toastMessage.value.isNullOrEmpty()) {
                Toast.makeText(context, toastMessage.value, Toast.LENGTH_SHORT).show()
                viewModel.onEvent(HomeEvent.ClearToastMessage)
            }

            RecentTransactions(
                uiState.recentTransactionalSms,
                onTransactionSmsClick = {
                    viewModel.onEvent(HomeEvent.TransactionSelected(it))
                },
                onSeeAllClick = {
                    navController.navigate(SeeAllTransaction.route)
                }
            )
        }
    }
}

@Composable
private fun HomeMainFab(navController: NavController) {
    var isFabOpen by remember { mutableStateOf(false) }
    val mainFab by remember {
        derivedStateOf {
            if (isFabOpen) {
                FabMainItem(Icons.Outlined.Close, "Close")
            } else {
                FabMainItem(Icons.Outlined.Add, "Add Transaction")
            }
        }
    }

    val fabActionItems = listOf(
        FabActionItem(
            icon = { Image(painter = painterResource(id = R.drawable.income), contentDescription = "Income") },
            contentDescription = "Income",
            onClick = { navController.navigate(NewTransaction.withArgs(NewTransaction.Args.TRANSACTION_TYPE to "Income")) }
        ),
        FabActionItem(
            icon = { Image(painter = painterResource(id = R.drawable.expense), contentDescription = "Expense") },
            contentDescription = "Expense",
            onClick = { navController.navigate(NewTransaction.withArgs(NewTransaction.Args.TRANSACTION_TYPE to "Expense")) }
        )
    )

    MultipleFloatingActionButton(
        mainFab = mainFab,
        isFabOpen = isFabOpen,
        fabActionItems = fabActionItems
    ) {
        isFabOpen = !isFabOpen
    }
}

@Composable
fun RecentTransactions(
    recentTransactionalSms: List<TransactionStateHolder>,
    onTransactionSmsClick : (TransactionStateHolder) -> Unit,
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
            items(recentTransactionalSms, key = { it.transactionalSms.id ?: Random.nextInt() }) { transaction ->
                TransactionItemCard(transaction, onTransactionSmsClick)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable fun RecentPreview(){
    val transactionalSms= listOf(
        TransactionalSms.create(
            id = "dfd",
            TransactionType.DEBIT,
            Sms("id", "", "", System.currentTimeMillis()),
            CurrencyAmount("INR", 100.0),
            "Amazon",
            "12-12-2021",
            "Amit",
            "Sbi",
            TransactionCategory.CashOutflow("Grocery")
        ),

        TransactionalSms.create(
            "idfd",
            TransactionType.CREDIT,
            Sms("id", "", "", System.currentTimeMillis()),
            CurrencyAmount("INR", 200.0),
            "Amazon",
            "12-12-2021",
            "Amit",
            "Sbi",
            TransactionCategory.Other
        )
    )
    val stateHolder = transactionalSms.map {
        TransactionStateHolder(it, TransactionCategoryResource(TransactionCategory.Other, R.drawable.bus_icon, MaterialTheme.colorScheme.primary))
    }
    Column(modifier = Modifier.padding(16.dp)) {
        RecentTransactions(
            recentTransactionalSms = stateHolder,
            onTransactionSmsClick = {},
            onSeeAllClick = {}
        )
    }
}
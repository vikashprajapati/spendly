package dev.nomadicprogrammer.spendly.transactiondetails

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.nomadicprogrammer.spendly.home.presentation.HomeEvent
import dev.nomadicprogrammer.spendly.home.presentation.HomeViewModel
import dev.nomadicprogrammer.spendly.home.presentation.HomeViewModelFactory
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

@Composable
fun TransactionDetails(navController: NavController) {
    val homeViewModel : HomeViewModel = viewModel(
        key = "HomeViewModel",
        factory = HomeViewModelFactory(SpendAnalyserController(LocalContext.current.applicationContext))
    )
    val transactionalSms = homeViewModel.dialogTransactionSms.value

    if (transactionalSms == null) {
        navController.popBackStack()
        return
    }

    Dialog(
        onDismissRequest = {
            navController.popBackStack()
            homeViewModel.onEvent(HomeEvent.TransactionDialogDismissed)
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Column {
            Text(text = "Transaction Details")
            Text(text = "Amount: ${transactionalSms.currencyAmount}")
            Text(text = "Date: ${transactionalSms.transactionDate}")
            Text(text = "Bank: ${transactionalSms.bankName}")
            Text(text = "Type: ${if(transactionalSms is TransactionalSms.Debit) "Debit" else "Credit"}")
        }
    }
}
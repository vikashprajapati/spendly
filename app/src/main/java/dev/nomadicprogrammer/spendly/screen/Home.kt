package dev.nomadicprogrammer.spendly.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

@Composable
fun Home(
    name: String,
    income: Double,
    spent: Double,
    recentTransactions: MutableState<List<TransactionalSms>>
) {
    Log.d("Home", "Recent transactions: $recentTransactions")
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Hello, $name",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Income",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$income",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Spent",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "-$spent",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Recent transactions",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(recentTransactions.value) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionalSms) {
    Log.d("TransactionItem", "Transaction: $transaction")
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = transaction.bankName ?: transaction.originalSms.senderId,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${transaction.currencyAmount}",
            style = MaterialTheme.typography.bodySmall,
            color = if (transaction is TransactionalSms.Debit) MaterialTheme.colorScheme.error else Color.Green
        )
        Text(
            text = transaction.transactionDate ?: transaction.originalSms.date.toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

package dev.nomadicprogrammer.spendly.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import dev.nomadicprogrammer.spendly.ui.components.Account
import dev.nomadicprogrammer.spendly.ui.components.TransactionSummaryChart

@Composable
fun Home(
    name: String,
    recentTransactions: MutableState<List<TransactionalSms>>
) {
    Log.d("Home", "Recent transactions: $recentTransactions")
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surface)
        .padding(16.dp)
    ) {
        Text(
            text = "Hello,",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = name,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        val income = remember { Account.Income(1000f) }
        val spent = remember { Account.Expense(100f) }
        TransactionSummaryChart(income, spent, onChartClick = {})

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recent transactions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            OutlinedButton(onClick = { /*TODO*/ }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "See all",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(imageVector = Icons.Outlined.KeyboardArrowRight, contentDescription = "See all transactions")
                }
            }
        }
        LazyColumn() {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.inversePrimary, MaterialTheme.shapes.large)
                .padding(12.dp),
            contentDescription = "Shopping",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.bankName ?: transaction.originalSms.senderId,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${transaction.currencyAmount}",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (transaction is TransactionalSms.Debit) MaterialTheme.colorScheme.error else Color.Green
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.originalSms.senderId,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = transaction.transactionDate ?: transaction.originalSms.date.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

package dev.nomadicprogrammer.spendly.ui.components

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import java.util.UUID

@Composable
fun TransactionItemCard(transaction: TransactionalSms) {
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
                .size(60.dp)
                .background(MaterialTheme.colorScheme.inversePrimary, MaterialTheme.shapes.large)
                .padding(20.dp),
            contentDescription = "Shopping",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.bankName ?: transaction.originalSms.senderId,
                    style = MaterialTheme.typography.bodyMedium
                )
                val transactionSymbol = if (transaction is TransactionalSms.Debit) "-" else "+"
                Text(
                    text = "$transactionSymbol ${transaction.currencyAmount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (transaction is TransactionalSms.Debit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.originalSms.senderId,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = transaction.transactionDate ?: transaction.originalSms.date.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false
)
@Composable fun TransactionItemCardPreview() {
    TransactionItemCard(
        transaction = TransactionalSms.Debit(
            originalSms = Sms(
                id = UUID.randomUUID().toString(),
                senderId = "Amazon",
                date = System.currentTimeMillis(),
                msgBody = "You've spent 1000 INR on Amazon"
            ),
            currencyAmount = CurrencyAmount(
                amount = 1000.0,
                currency = "INR"
            ),
            bankName = "ICICI Bank",
            transactionDate = "2021-09-01",
            transferredTo = ""
        ))

}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false
)
@Composable fun CreditTransactionItemCardPreview() {
    TransactionItemCard(
        transaction = TransactionalSms.Credit(
            originalSms = Sms(
                id = UUID.randomUUID().toString(),
                senderId = "Amazon",
                date = System.currentTimeMillis(),
                msgBody = "You've spent 1000 INR on Amazon"
            ),
            currencyAmount = CurrencyAmount(
                amount = 1000.0,
                currency = "INR"
            ),
            bankName = "ICICI Bank",
            transactionDate = "2021-09-01",
            receivedFrom = ""
        ))

}
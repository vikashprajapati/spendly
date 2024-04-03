package dev.nomadicprogrammer.spendly.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import dev.nomadicprogrammer.spendly.ui.components.Account
import dev.nomadicprogrammer.spendly.ui.components.TransactionItemCard
import dev.nomadicprogrammer.spendly.ui.components.TransactionSummaryChart
import java.util.UUID

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
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight(800)
        )

        Spacer(modifier = Modifier.height(24.dp))
        val income = remember{ mutableStateOf(Account.Income(0f)) }
        val spent = remember { mutableStateOf(Account.Expense(0f)) }

        val creditedIncome = recentTransactions.value
            .filterIsInstance<TransactionalSms.Credit>()
            .sumOf { it.currencyAmount.amount }
            .toFloat()
        income.value = income.value.copy(balance = creditedIncome)

        val debitedIncome = recentTransactions.value
            .filterIsInstance<TransactionalSms.Debit>()
            .sumOf { it.currencyAmount.amount }
            .toFloat()
        spent.value = spent.value.copy(balance = debitedIncome)
        Log.d("Home", "Credited income: $creditedIncome, Debited income: $debitedIncome")


        TransactionSummaryChart(income, spent, onChartClick = {})

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recent transactions",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            OutlinedButton(
                onClick = { /*TODO*/ },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
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
                TransactionItemCard(transaction)
            }
        }
    }
}

@Preview()
@Composable fun HomePreview() {
    Home(
        name = "John Doe",
        recentTransactions = remember { mutableStateOf(listOf(
            TransactionalSms.Debit(
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
            ),
            TransactionalSms.Credit(
                originalSms = Sms(
                    id = UUID.randomUUID().toString(),
                    senderId = "Google",
                    date = System.currentTimeMillis(),
                    msgBody = "You've received 1000 INR from Google"
                ),
                currencyAmount = CurrencyAmount(
                    amount = 1000.0,
                    currency = "INR"
                ),
                bankName = "ICICI Bank",
                transactionDate = "2021-09-01",
                receivedFrom = ""
            ),
            TransactionalSms.Debit(
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
            ),
        )) }
    )
}
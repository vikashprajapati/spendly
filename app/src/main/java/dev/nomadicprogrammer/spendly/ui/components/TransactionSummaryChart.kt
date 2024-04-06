package dev.nomadicprogrammer.spendly.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSummaryChart(
    income: MutableState<Account.Income>,
    spent: MutableState<Account.Expense>,
    onChartClick: () -> Unit
) {
    Log.d("TransactionSummaryChart", "Income: ${income.value.balance}, Spent: ${spent.value.balance}")
    val incomeColor = MaterialTheme.colorScheme.primary
    val expenseColor = MaterialTheme.colorScheme.error
    ElevatedCard(
        onClick = onChartClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .fillMaxHeight(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.5f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .fillMaxHeight()
                                .background(incomeColor, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Income", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "₹ ${income.value.balance}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .fillMaxHeight()
                                .background(expenseColor, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Spent", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "₹ ${spent.value.balance}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp),
            ) {
                val total = remember { mutableFloatStateOf(income.value.balance + spent.value.balance) }
                val arcRatioIncome = remember { income.value.balance / total.floatValue }
                val arcRatioSpent = remember { spent.value.balance / total.floatValue }
                val slices = listOf(
                    Slice(arcRatioIncome*360, incomeColor),
                    Slice(arcRatioSpent*360, expenseColor),
                )

                PieChart(slices, useCenter = true)
            }
        }
    }
}

sealed class Account(
    open val balance: Float
){
    data class Income(override val balance: Float) : Account(balance)
    data class Expense(override val balance: Float) : Account(balance)
}

@Preview
@Composable
fun CardWithChartPreview() {
    val income = remember { mutableStateOf(Account.Income(1000f)) }
    val spent = remember { mutableStateOf(Account.Expense(100f)) }
    TransactionSummaryChart(income, spent = spent) {}
}
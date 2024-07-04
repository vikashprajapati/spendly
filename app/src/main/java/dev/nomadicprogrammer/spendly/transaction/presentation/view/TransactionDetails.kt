package dev.nomadicprogrammer.spendly.transaction.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.home.presentation.HomeEvent
import dev.nomadicprogrammer.spendly.home.presentation.HomeViewModel
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Credit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Debit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.ui.components.TransactionCategoriesGrid

@Composable
fun TransactionDetails(
    homeViewModel: HomeViewModel,
    navController: NavController,
    transactionId : String
) {
    val state = homeViewModel.state.collectAsState()
    val transactionalSms = state.value.allTransactionalSms.first { it.transactionalSms.id == transactionId }.transactionalSms
    val transactionCategoryResource = homeViewModel.transactionCategoryResource.first { it.transactionCategory.name == transactionalSms.category.name }

    SideEffect {
        homeViewModel.onEvent(HomeEvent.TransactionDetailsDialogLoaded(transactionalSms))
    }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transaction Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = transactionalSms.type.name,
                color = if (transactionalSms.type == TransactionType.DEBIT) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .background(
                        color = if (transactionalSms.type == TransactionType.DEBIT) MaterialTheme.colorScheme.error.copy(
                            alpha = 0.1f
                        ) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "From: ${transactionalSms.originalSms?.senderId}", style = MaterialTheme.typography.titleSmall)
            Text(text = DateUtils.Local.formattedDateFromTimestamp(transactionalSms.originalSms?.date?:System.currentTimeMillis()), style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            Text(
                text = transactionalSms.originalSms?.msgBody ?:"Body not found",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Text(text = "Tag Category", style = MaterialTheme.typography.titleSmall)
        val selectedCategory = remember { mutableStateOf(transactionCategoryResource) }
        val categories = homeViewModel.transactionCategoryResource
        TransactionCategoriesGrid(
            selectedCategory = selectedCategory.value,
            tagCategories = categories
        ){
            selectedCategory.value = it
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Button(
            onClick = {
                val updatedTransaction = if(transactionalSms is Debit) transactionalSms.copy(category = selectedCategory.value.transactionCategory) else (transactionalSms as Credit).copy(category = selectedCategory.value.transactionCategory)
//                onUpdateClick(updatedTransaction)
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = "Update",
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium),
            )
        }
    }
}
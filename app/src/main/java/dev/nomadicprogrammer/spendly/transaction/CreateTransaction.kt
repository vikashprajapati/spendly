package dev.nomadicprogrammer.spendly.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.nomadicprogrammer.spendly.ui.components.TransactionCategoriesGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransaction() {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(24.dp)
    ) {
        Text(
            text = "Create Transaction",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            var amount by rememberSaveable { mutableStateOf("") }
            TextField(
                value = amount,
                onValueChange = { amount = it },
                textStyle = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .width(240.dp)
                    .padding(vertical = 16.dp),
                label = { Text("Amount", style = MaterialTheme.typography.bodyLarge) },
                prefix = { Text(text = "Rs ", style = MaterialTheme.typography.titleLarge) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        var selectedTransactionType by rememberSaveable { mutableStateOf("expense") }
        SpendSelector(selectedTransactionType){
            selectedTransactionType = it
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            val secondPartyLabel = if (selectedTransactionType == "expense") "Transferred To" else "Received From"
            var secondParty by rememberSaveable { mutableStateOf("") }
            TextField(
                value = secondParty,
                onValueChange = { secondParty = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp),
                label = { Text(secondPartyLabel) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Select Category",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        val selectedCategory = remember { mutableStateOf("") }
        TransactionCategoriesGrid(
            selectedCategory = selectedCategory.value,
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ){
            selectedCategory.value = it
        }

        val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Enter Transaction Date",
            style = MaterialTheme.typography.bodyMedium
        )
        DatePicker(
            modifier = Modifier
                .padding(0.dp),
            state = datePickerState,
            showModeToggle = false,
            title = null,
            headline = null
        )
    }
}

@Composable
private fun SpendSelector(
    selectedTransactionType: String,
    onTransactionTypeSelected: (String) -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .align(Alignment.CenterVertically),
            onClick = {  onTransactionTypeSelected("income") },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            border = BorderStroke(
                if (selectedTransactionType == "income") 2.dp else 0.dp,
                MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = "Income",
                modifier = Modifier
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(0.dp)
                .width(8.dp)
        )

        Button(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .align(Alignment.CenterVertically),
            onClick = { onTransactionTypeSelected("expense") },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            border = BorderStroke(
                if (selectedTransactionType == "expense") 2.dp else 0.dp,
                MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = "Expense",
                modifier = Modifier
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun previewCreateTransaction() {
    CreateTransaction()
}
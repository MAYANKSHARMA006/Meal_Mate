package com.example.meal_mate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.meal_mate.viewmodel.ExpenseViewModel
import com.example.meal_mate.model.Expense
import com.example.meal_mate.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    expenseViewModel: ExpenseViewModel = viewModel(),
    budgetViewModel: BudgetViewModel = viewModel()
) {

    // Collecting State
    val expenseList by expenseViewModel.expenses
    val weekly by expenseViewModel.weeklyTotal
    val monthly by expenseViewModel.monthlyTotal
    val dailyBudget by budgetViewModel.dailyBudget

    // Dialog States
    var showDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    var foodName by remember { mutableStateOf("") }
    var foodAmount by remember { mutableStateOf("") }
    var newBudget by remember { mutableStateOf("") }

    // Progress Bar Logic
    val totalSpentToday = expenseList.sumOf { it.amount }
    val progress = (totalSpentToday.toFloat() / dailyBudget.coerceAtLeast(1).toFloat())
        .coerceIn(0f, 1f)

    val percentage = (progress * 100).toInt()

    // ---------------- MAIN SCREEN ----------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "MealMate",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // EXPENSE SUMMARY CARD
        SummaryCard(weekly, monthly)

        Spacer(modifier = Modifier.height(20.dp))

        // DAILY BUDGET BAR
        DailyBudgetBar(
            dailyBudget = dailyBudget,
            spent = totalSpentToday,
            progress = progress,
            percentage = percentage
        )

        Spacer(modifier = Modifier.height(12.dp))

        // SET BUDGET BUTTON
        Button(
            onClick = { showBudgetDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Daily Budget")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ADD EXPENSE BUTTON
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Expense")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // EXPENSE LIST TITLE
        Text("Your Expenses", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(10.dp))

        // EXPENSE LIST
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(expenseList) { exp ->
                ExpenseItem(
                    expense = exp,
                    onDelete = { expenseViewModel.deleteExpense(exp.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    // ---------------- ADD EXPENSE DIALOG ----------------
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Expense") },
            text = {
                Column {

                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = foodAmount,
                        onValueChange = { foodAmount = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (foodName.isNotEmpty() && foodAmount.isNotEmpty()) {
                        expenseViewModel.addExpense(foodName, foodAmount.toInt(), 0) {}
                    }
                    foodName = ""
                    foodAmount = ""
                    showDialog = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ---------------- SET DAILY BUDGET DIALOG ----------------
    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text("Set Daily Budget") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newBudget,
                        onValueChange = { newBudget = it },
                        label = { Text("Enter Daily Budget (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newBudget.isNotEmpty()) {
                        budgetViewModel.updateBudget(newBudget.toInt())
                    }
                    newBudget = ""
                    showBudgetDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    newBudget = ""
                    showBudgetDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ------------------- COMPONENTS ------------------------

@Composable
fun SummaryCard(weekly: Int, monthly: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weekly Expense: ₹$weekly", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Monthly Expense: ₹$monthly", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun DailyBudgetBar(
    dailyBudget: Int,
    spent: Int,
    progress: Float,
    percentage: Int
) {
    Column {
        Text("Daily Budget", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(6.dp))

        Text("₹$spent / ₹$dailyBudget")
        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = if (progress >= 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text("$percentage% used", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ExpenseItem(expense: Expense, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(expense.name, style = MaterialTheme.typography.bodyLarge)
                Text("₹${expense.amount}", style = MaterialTheme.typography.bodyMedium)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

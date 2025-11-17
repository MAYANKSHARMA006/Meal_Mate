package com.example.meal_mate.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.meal_mate.model.Expense
import com.example.meal_mate.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(expenseViewModel: ExpenseViewModel = viewModel()) {

    val allExpenses = expenseViewModel.expenses.value

    // FILTER STATE
    var selectedFilter by remember { mutableStateOf("Today") }

    val filteredExpenses = filterExpenses(allExpenses, selectedFilter)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Expense History", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        // FILTER BUTTONS
        FilterRow(
            selected = selectedFilter,
            onSelected = { selectedFilter = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            items(filteredExpenses) { exp ->
                HistoryItem(expense = exp)
            }
        }
    }
}

// ---------------- FILTER ROW UI ----------------

@Composable
fun FilterRow(selected: String, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterChipUI("Today", selected, onSelected)
        FilterChipUI("This Week", selected, onSelected)
        FilterChipUI("This Month", selected, onSelected)
    }
}

@Composable
fun FilterChipUI(label: String, selected: String, onSelected: (String) -> Unit) {
    FilterChip(
        selected = selected == label,
        onClick = { onSelected(label) },
        label = { Text(label) }
    )
}

// ---------------- FILTER LOGIC ----------------

fun filterExpenses(list: List<Expense>, filter: String): List<Expense> {

    val cal = Calendar.getInstance()
    val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

    return when (filter) {

        "Today" -> {
            list.filter {
                SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                    .format(Date(it.timestamp)) == today
            }
        }

        "This Week" -> {
            val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)
            val year = cal.get(Calendar.YEAR)

            list.filter {
                cal.timeInMillis = it.timestamp
                cal.get(Calendar.WEEK_OF_YEAR) == currentWeek &&
                        cal.get(Calendar.YEAR) == year
            }
        }

        "This Month" -> {
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)

            list.filter {
                cal.timeInMillis = it.timestamp
                cal.get(Calendar.MONTH) == month &&
                        cal.get(Calendar.YEAR) == year
            }
        }

        else -> list
    }
}

// ---------------- LIST ITEM ----------------

@Composable
fun HistoryItem(expense: Expense) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(expense.name, style = MaterialTheme.typography.bodyLarge)
            Text("₹${expense.amount}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

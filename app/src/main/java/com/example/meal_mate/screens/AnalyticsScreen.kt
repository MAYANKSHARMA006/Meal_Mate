package com.example.meal_mate.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.meal_mate.viewmodel.ExpenseViewModel
import java.util.*

@Composable
fun AnalyticsScreen(expenseViewModel: ExpenseViewModel = viewModel()) {

    val expenses = expenseViewModel.expenses.value
    val calendar = Calendar.getInstance()

    // ---------------------------------------------
    //   WEEKLY EXPENSE GRAPH (₹)
    // ---------------------------------------------
    val expenseTotals = MutableList(7) { 0 }

    expenses.forEach {
        calendar.timeInMillis = it.timestamp
        val day = calendar.get(Calendar.DAY_OF_WEEK)        // 1 = Sun
        val index = (day + 5) % 7                           // Convert to Mon=0..Sun=6
        expenseTotals[index] += it.amount
    }

    val maxExpenseValue = (expenseTotals.maxOrNull() ?: 1).toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Weekly Expense Analytics",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            expenseTotals.forEachIndexed { index, amount ->
                BarItem(
                    day = days[index],
                    amount = amount,
                    maxValue = maxExpenseValue,
                    barColor = MaterialTheme.colorScheme.primary // Blue bars
                )
            }
        }

        // ---------------------------------------------
        //   WEEKLY CALORIES GRAPH (kcal)
        // ---------------------------------------------
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Weekly Calorie Analytics",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        val calorieTotals = MutableList(7) { 0 }

        expenses.forEach {
            calendar.timeInMillis = it.timestamp
            val day = calendar.get(Calendar.DAY_OF_WEEK)
            val index = (day + 5) % 7
            calorieTotals[index] += it.calories
        }

        val maxCalValue = (calorieTotals.maxOrNull() ?: 1).toFloat()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            calorieTotals.forEachIndexed { index, amount ->
                BarItem(
                    day = days[index],
                    amount = amount,
                    maxValue = maxCalValue,
                    barColor = Color(0xFFFF7043) // Orange bars for calories
                )
            }
        }
    }
}

@Composable
fun BarItem(day: String, amount: Int, maxValue: Float, barColor: Color) {

    val ratio = if (maxValue == 0f) 0f else amount.toFloat() / maxValue
    val barHeight = (180.dp * ratio).coerceAtLeast(12.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(240.dp)
    ) {

        // BAR
        Canvas(
            modifier = Modifier
                .width(26.dp)
                .height(barHeight)
        ) {
            drawRect(color = barColor)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(amount.toString(), style = MaterialTheme.typography.bodySmall)
        Text(day, style = MaterialTheme.typography.bodyMedium)
    }
}

package com.example.meal_mate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.meal_mate.model.Expense
import com.example.meal_mate.ui.components.GlassSurface
import com.example.meal_mate.ui.components.StatBadge
import com.example.meal_mate.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(expenseViewModel: ExpenseViewModel = viewModel()) {

    val allExpenses = expenseViewModel.expenses.value

    var selectedFilter by remember { mutableStateOf("Today") }

    val filteredExpenses = filterExpenses(allExpenses, selectedFilter)
    val totalSpent = filteredExpenses.sumOf { it.amount }
    val totalCalories = filteredExpenses.sumOf { it.calories }
    val avgSpend = if (filteredExpenses.isEmpty()) 0 else totalSpent / max(filteredExpenses.size, 1)
    val topMeal = filteredExpenses.maxByOrNull { it.amount }?.name ?: "—"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {

        Text("Spend history", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(18.dp))

        FilterRow(
            selected = selectedFilter,
            onSelected = { selectedFilter = it }
        )

        Spacer(modifier = Modifier.height(22.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            StatBadge(
                label = "Total spend",
                value = "₹$totalSpent",
                modifier = Modifier.fillMaxWidth(0.5f),
                icon = Icons.Rounded.Bolt,
                valueColor = MaterialTheme.colorScheme.primary
            )
            StatBadge(
                label = "Avg ticket",
                value = "₹$avgSpend",
                modifier = Modifier.fillMaxWidth(0.5f),
                icon = Icons.Rounded.TrendingUp,
                valueColor = MaterialTheme.colorScheme.tertiary
            )
            StatBadge(
                label = "Calories logged",
                value = "$totalCalories kcal",
                modifier = Modifier.fillMaxWidth(0.5f),
                icon = Icons.Rounded.Fastfood
            )
            StatBadge(
                label = "Top pick",
                value = topMeal,
                modifier = Modifier.fillMaxWidth(0.5f),
                icon = Icons.Rounded.History
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        if (filteredExpenses.isEmpty()) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("No entries yet", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Log a meal to start building your beautiful timeline.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredExpenses) { exp ->
                    HistoryItem(expense = exp)
                }
            }
        }
    }
}

@Composable
private fun FilterRow(selected: String, onSelected: (String) -> Unit) {
    val filters = listOf("Today", "This Week", "This Month")
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        filters.forEach { label ->
            FilterChip(
                selected = selected == label,
                onClick = { onSelected(label) },
                label = { Text(label) },
                leadingIcon = if (selected == label) {
                    {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = null
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                )
            )
        }
    }
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
    val formatter = remember {
        SimpleDateFormat("MMM d, yyyy • hh:mm a", Locale.getDefault())
    }

    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(expense.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    formatter.format(Date(expense.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${expense.amount}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${expense.calories} kcal",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

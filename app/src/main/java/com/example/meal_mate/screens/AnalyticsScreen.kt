package com.example.meal_mate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun AnalyticsScreen(expenseViewModel: ExpenseViewModel = viewModel()) {

    val expenses = expenseViewModel.expenses.value
    val calendar = remember { Calendar.getInstance() }
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val weeklySpend = MutableList(7) { 0 }
    val weeklyCalories = MutableList(7) { 0 }

    expenses.forEach {
        calendar.timeInMillis = it.timestamp
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        val index = (day + 5) % 7
        weeklySpend[index] += it.amount
        weeklyCalories[index] += it.calories
    }

    val totalSpend = weeklySpend.sum()
    val totalCalories = weeklyCalories.sum()
    val avgMeal = if (expenses.isEmpty()) 0 else totalSpend / max(expenses.size, 1)
    val peakDayIndex = weeklySpend.indexOf(weeklySpend.maxOrNull() ?: 0).coerceAtLeast(0)
    val trendDescriptor = days.getOrElse(peakDayIndex) { "—" }

    val groupedMeals = expenses.groupBy { it.name }
    val topMeals = groupedMeals.entries
        .sortedByDescending { entry -> entry.value.sumOf { it.amount } }
        .take(3)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Text("Intelligent insights", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(18.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            StatBadge(
                label = "Weekly spend",
                value = "₹$totalSpend",
                modifier = Modifier.fillMaxWidth(0.5f),
                icon = Icons.Rounded.Bolt,
                valueColor = MaterialTheme.colorScheme.primary
            )
            StatBadge(
                label = "Avg per meal",
                value = "₹$avgMeal",
                modifier = Modifier.fillMaxWidth(0.5f),
                icon = Icons.Rounded.TrendingUp,
                valueColor = MaterialTheme.colorScheme.tertiary
            )
            StatBadge(
                label = "Calorie burn",
                value = "$totalCalories kcal",
                modifier = Modifier.fillMaxWidth(0.5f),
                icon = Icons.Rounded.Fastfood
            )
            StatBadge(
                label = "Peak day",
                value = trendDescriptor,
                modifier = Modifier.fillMaxWidth(0.5f),
                icon = Icons.Rounded.Insights
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        InsightChartCard(
            title = "Spending pulse",
            subtitle = "₹${weeklySpend.maxOrNull() ?: 0} max day",
            values = weeklySpend,
            labels = days,
            gradient = Brush.verticalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                )
            ),
            valueFormatter = { "₹$it" }
        )

        Spacer(modifier = Modifier.height(20.dp))

        InsightChartCard(
            title = "Calorie rhythm",
            subtitle = "${weeklyCalories.maxOrNull() ?: 0} kcal peak",
            values = weeklyCalories,
            labels = days,
            gradient = Brush.verticalGradient(
                listOf(
                    Color(0xFFFF9D6C),
                    Color(0xFFFF6F91)
                )
            ),
            valueFormatter = { "$it kcal" }
        )

        Spacer(modifier = Modifier.height(24.dp))

        TopMealsCard(topMeals = topMeals)

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun InsightChartCard(
    title: String,
    subtitle: String,
    values: List<Int>,
    labels: List<String>,
    gradient: Brush,
    valueFormatter: (Int) -> String
) {
    val maxValue = (values.maxOrNull() ?: 1).coerceAtLeast(1)

    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { index, amount ->
                    PremiumBar(
                        label = labels.getOrElse(index) { "--" },
                        amount = amount,
                        maxValue = maxValue.toFloat(),
                        gradient = gradient,
                        valueFormatter = valueFormatter
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumBar(
    label: String,
    amount: Int,
    maxValue: Float,
    gradient: Brush,
    valueFormatter: (Int) -> String
) {
    val ratio = if (maxValue == 0f) 0f else amount / maxValue
    val targetHeight = (ratio * 180f).coerceAtLeast(8f)

    Column(
        modifier = Modifier.height(220.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valueFormatter(amount),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .width(28.dp)
                .height(targetHeight.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(gradient)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun TopMealsCard(topMeals: List<Map.Entry<String, List<Expense>>>) {
    if (topMeals.isEmpty()) return

    val formatter = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }

    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Signature bites", style = MaterialTheme.typography.titleLarge)
            Text(
                "Your most indulgent picks this week",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            topMeals.forEach { entry ->
                val spend = entry.value.sumOf { it.amount }
                val lastDate = entry.value.maxByOrNull { it.timestamp }?.timestamp ?: 0L
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(entry.key, style = MaterialTheme.typography.titleMedium)
                        Text(
                            formatter.format(Date(lastDate)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "₹$spend",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

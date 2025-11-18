package com.example.meal_mate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.meal_mate.model.Expense
import com.example.meal_mate.ui.components.GlassSurface
import com.example.meal_mate.ui.components.StatBadge
import com.example.meal_mate.viewmodel.BudgetViewModel
import com.example.meal_mate.viewmodel.ExpenseViewModel
import kotlin.math.abs
import com.example.meal_mate.ui.theme.AuroraEnd
import com.example.meal_mate.ui.theme.AuroraMid
import com.example.meal_mate.ui.theme.AuroraStart
import com.example.meal_mate.ui.theme.EmeraldEnd
import com.example.meal_mate.ui.theme.EmeraldStart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    expenseViewModel: ExpenseViewModel = viewModel(),
    budgetViewModel: BudgetViewModel = viewModel()
) {
    val expenseList by expenseViewModel.expenses
    val weekly by expenseViewModel.weeklyTotal
    val monthly by expenseViewModel.monthlyTotal
    val dailyBudget by budgetViewModel.dailyBudget

    var showDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var foodName by remember { mutableStateOf("") }
    var foodAmount by remember { mutableStateOf("") }
    var newBudget by remember { mutableStateOf("") }

    val totalSpentToday = expenseList.sumOf { it.amount }
    val progress = (totalSpentToday.toFloat() / dailyBudget.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 28.dp)
        ) {
            item {
                GreetingHeader(
                    mealsLogged = expenseList.size,
                    remaining = (dailyBudget - totalSpentToday).coerceAtLeast(0)
                )
            }

            item {
                HeroSummary(
                    spent = totalSpentToday,
                    budget = dailyBudget,
                    weekly = weekly,
                    monthly = monthly,
                    progress = progress
                )
            }

            item {
                InsightRow(
                    spent = totalSpentToday,
                    remaining = (dailyBudget - totalSpentToday).coerceAtLeast(0),
                    weekly = weekly,
                    monthly = monthly,
                    entries = expenseList.size
                )
            }

            item {
                DailyBudgetBar(
                    dailyBudget = dailyBudget,
                    spent = totalSpentToday,
                    progress = progress,
                    percentage = percentage
                )
            }

            item {
                ActionRow(
                    onAddExpense = { showDialog = true },
                    onSetBudget = { showBudgetDialog = true }
                )
            }

            item {
                SectionHeader(
                    title = "Today’s log",
                    subtitle = if (expenseList.isEmpty()) "Track every bite" else "You logged ${expenseList.size} meals"
                )
            }

            if (expenseList.isEmpty()) {
                item {
                    EmptyExpenseState(onAdd = { showDialog = true })
                }
            } else {
                items(expenseList, key = { it.id }) { exp ->
                    ExpenseItem(
                        expense = exp,
                        onDelete = { expenseViewModel.deleteExpense(exp.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }
    }

    if (showDialog) {
        AddExpenseDialog(
            foodName = foodName,
            onNameChange = { foodName = it },
            amount = foodAmount,
            onAmountChange = { foodAmount = it },
            onDismiss = { showDialog = false },
            onConfirm = {
                if (foodName.isNotEmpty() && foodAmount.isNotEmpty()) {
                    expenseViewModel.addExpense(foodName, foodAmount.toInt(), 0) {}
                }
                foodName = ""
                foodAmount = ""
                showDialog = false
            }
        )
    }

    if (showBudgetDialog) {
        BudgetDialog(
            newBudget = newBudget,
            onValueChange = { newBudget = it },
            onDismiss = {
                newBudget = ""
                showBudgetDialog = false
            },
            onConfirm = {
                if (newBudget.isNotEmpty()) {
                    budgetViewModel.updateBudget(newBudget.toInt())
                }
                newBudget = ""
                showBudgetDialog = false
            }
        )
    }
}

@Composable
private fun GreetingHeader(
    mealsLogged: Int,
    remaining: Int
) {
    val dateStamp = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Curated for you",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "MealMate concierge",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$dateStamp · ${if (remaining > 0) "₹$remaining left to indulge" else "Over by ₹${abs(remaining)}"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        GlassSurface(
            modifier = Modifier.size(62.dp),
            shape = CircleShape,
            borderColor = Color.Transparent,
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = mealsLogged.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "meals",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightRow(
    spent: Int,
    remaining: Int,
    weekly: Int,
    monthly: Int,
    entries: Int
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 2
    ) {
        StatBadge(
            label = "Spent today",
            value = "₹$spent",
            modifier = Modifier.fillMaxWidth(0.5f),
            icon = Icons.Rounded.Bolt,
            valueColor = MaterialTheme.colorScheme.primary
        )
        StatBadge(
            label = "Balance",
            value = "₹$remaining",
            modifier = Modifier.fillMaxWidth(0.5f),
            icon = Icons.Rounded.PieChart,
            valueColor = MaterialTheme.colorScheme.tertiary
        )
        StatBadge(
            label = "Weekly total",
            value = "₹$weekly",
            modifier = Modifier.fillMaxWidth(0.5f),
            icon = Icons.Rounded.Insights
        )
        StatBadge(
            label = "Monthly trend",
            value = "₹$monthly",
            modifier = Modifier.fillMaxWidth(0.5f),
            icon = Icons.Rounded.Fastfood
        )
        StatBadge(
            label = "Logs today",
            value = "$entries entries",
            modifier = Modifier.fillMaxWidth(0.5f),
            icon = Icons.Rounded.AddCircle
        )
    }
}

@Composable
private fun HeroSummary(
    spent: Int,
    budget: Int,
    weekly: Int,
    monthly: Int,
    progress: Float
) {
    val gradientBrush = Brush.linearGradient(listOf(AuroraStart, AuroraMid, AuroraEnd))
    val remaining = (budget - spent).coerceAtLeast(0)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text(
                    text = "MealMate",
                    style = MaterialTheme.typography.headlineLarge.copy(color = Color.White)
                )

                Text(
                    text = "Today's balance ₹$remaining / ₹$budget",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White.copy(alpha = 0.92f))
                )

                ProgressPill(progress = progress)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryChip(label = "This week", value = "₹$weekly")
                    SummaryChip(label = "This month", value = "₹$monthly")
                }
            }
        }
    }
}

@Composable
private fun ProgressPill(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0.08f, 1f))
                .clip(RoundedCornerShape(50))
                .background(Color.White),
        )
    }
}

@Composable
private fun SummaryChip(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun DailyBudgetBar(
    dailyBudget: Int,
    spent: Int,
    progress: Float,
    percentage: Int
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            SectionHeader(title = "Daily budget tracker", subtitle = "Keep it under control")
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "₹$spent of ₹$dailyBudget used",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Brush.horizontalGradient(listOf(AuroraStart, AuroraEnd)))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$percentage% spent",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                ElevatedAssistChip(
                    onClick = {},
                    label = { Text(if (progress > 1f) "Over budget" else "On track") }
                )
            }
        }
    }
}

@Composable
private fun ActionRow(
    onAddExpense: () -> Unit,
    onSetBudget: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GradientActionButton(
            modifier = Modifier.weight(1f),
            title = "Add expense",
            subtitle = "Capture meals instantly",
            icon = Icons.Rounded.AddCircle,
            gradient = Brush.linearGradient(listOf(EmeraldStart, EmeraldEnd)),
            onClick = onAddExpense
        )

        GradientOutlineButton(
            modifier = Modifier.weight(1f),
            title = "Set budget",
            subtitle = "Define your limit",
            onClick = onSetBudget
        )
    }
}

@Composable
private fun GradientActionButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(24.dp)),
        onClick = onClick,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


@Composable
private fun GradientOutlineButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    GlassSurface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        subtitle?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyExpenseState(onAdd: () -> Unit) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No expenses yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Log your first meal and watch insights come alive.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(onClick = onAdd) {
                Text("Add expense")
            }
        }
    }
}

@Composable
private fun ExpenseItem(expense: Expense, onDelete: () -> Unit) {
    val dateFormatter = remember {
        SimpleDateFormat("MMM d • hh:mm a", Locale.getDefault())
    }

    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Fastfood,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(expense.name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dateFormatter.format(Date(expense.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("₹${expense.amount}", style = MaterialTheme.typography.titleMedium)
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun AddExpenseDialog(
    foodName: String,
    onNameChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) { Text("Add expense") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = foodName,
                    onValueChange = onNameChange,
                    label = { Text("Food name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
private fun BudgetDialog(
    newBudget: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) { Text("Save") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Set daily budget") },
        text = {
            OutlinedTextField(
                value = newBudget,
                onValueChange = onValueChange,
                label = { Text("Enter daily budget (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

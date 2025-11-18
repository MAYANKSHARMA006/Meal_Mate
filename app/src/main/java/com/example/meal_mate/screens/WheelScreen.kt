package com.example.meal_mate.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.withSave
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.meal_mate.ui.theme.DarkBackground
import com.example.meal_mate.ui.theme.DarkCard
import com.example.meal_mate.ui.theme.LightCard
import com.example.meal_mate.viewmodel.ExpenseViewModel
import com.example.meal_mate.viewmodel.FoodViewModel
import com.example.meal_mate.viewmodel.MealViewModel
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// ---------------- DATA CLASS ----------------
data class MealResult(
    val name: String,
    val calories: Int,
    val price: Int,
    val reason: String
)

// ---------------- MAIN SCREEN ----------------
@Composable
fun WheelScreen(
    expenseViewModel: ExpenseViewModel = viewModel(),
    mealViewModel: MealViewModel = viewModel(),
    foodViewModel: FoodViewModel = viewModel()
) {

    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val foodList = foodViewModel.foods.value

    if (foodList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var selectedTime by remember { mutableStateOf("Breakfast") }
    var selectedMood by remember { mutableStateOf("Light Mood") }
    var budget by remember { mutableFloatStateOf(100f) }
    var mealResult by remember { mutableStateOf<MealResult?>(null) }

    val filteredMeals = foodList.filter {
        it.category.equals(selectedTime, true) &&
                it.mood.equals(selectedMood, true) &&
                it.price <= budget.toInt()
    }

    val wheelItems = filteredMeals.map { it.name }

    if (wheelItems.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No meals match your filters", color = Color.Red)
        }
        return
    }

    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {

        Text("Meal Decision Wheel", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        // TIME
        Text("Choose Time", style = MaterialTheme.typography.titleMedium)
        ChipRow(listOf("Breakfast", "Lunch", "Dinner"), selectedTime) { selectedTime = it }
        Spacer(modifier = Modifier.height(20.dp))

        // MOOD
        Text("Choose Mood", style = MaterialTheme.typography.titleMedium)
        ChipRow(listOf("Light Mood", "Healthy Mood", "Stress Mood"), selectedMood) { selectedMood = it }
        Spacer(modifier = Modifier.height(20.dp))

        // BUDGET
        Text("Select Budget (₹${budget.toInt()})", style = MaterialTheme.typography.titleMedium)
        Slider(value = budget, onValueChange = { budget = it }, valueRange = 50f..500f)
        Spacer(modifier = Modifier.height(30.dp))

        // WHEEL + POINTER
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▼",
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(bottom = 230.dp)
            )

            SpinWheel(angle = rotation.value, items = wheelItems)

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text("●", fontSize = 38.sp, color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // SPIN BUTTON
        Button(
            onClick = {
                if (!isSpinning) {
                    isSpinning = true
                    val randomSpin = (900..1800).random().toFloat()

                    scope.launch {
                        rotation.animateTo(
                            targetValue = rotation.value + randomSpin,
                            animationSpec = tween(3000, easing = LinearOutSlowInEasing)
                        )

                        isSpinning = false

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
                        else @Suppress("DEPRECATION") vibrator.vibrate(150)

                        val index = ((rotation.value % 360) / (360f / wheelItems.size)).toInt()
                        val selectedFood = filteredMeals.first { it.name == wheelItems[index] }

                        mealResult = MealResult(
                            selectedFood.name,
                            selectedFood.calories,
                            selectedFood.price,
                            selectedFood.reason
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Spin the Wheel 🎡")
        }

        Spacer(modifier = Modifier.height(30.dp))

        mealResult?.let { meal ->
            ResultCard(meal)
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    mealViewModel.saveMeal(meal.name, meal.calories, meal.price)
                    expenseViewModel.addExpense(meal.name, meal.price, meal.calories, onDone = {})
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add as Expense")
            }
        }
    }
}

// ---------------- UPDATED BEIGE WHEEL COLORS ----------------

@Composable
fun SpinWheel(angle: Float, items: List<String>) {

    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    val light1 = Color(0xFFE8DCC2)
    val light2 = Color(0xFFC7B89D)

    val dark1 = Color(0xFF3A3A3A)
    val dark2 = Color(0xFF2C2C2C)

    Canvas(
        modifier = Modifier.size(260.dp).clip(CircleShape)
    ) {
        val sliceAngle = 360f / items.size
        val radius = size.minDimension / 2

        rotate(angle) {
            items.forEachIndexed { index, label ->

                drawArc(
                    color = if (isDark)
                        if (index % 2 == 0) dark1 else dark2
                    else
                        if (index % 2 == 0) light1 else light2,
                    startAngle = sliceAngle * index,
                    sweepAngle = sliceAngle,
                    useCenter = true
                )

                // Label position
                val midAngle = sliceAngle * index + sliceAngle / 2
                val rad = Math.toRadians(midAngle.toDouble())

                val textX = center.x + (radius * 0.55f) * cos(rad).toFloat()
                val textY = center.y + (radius * 0.55f) * sin(rad).toFloat()

                drawContext.canvas.nativeCanvas.withSave {
                    rotate(midAngle, textX, textY)

                    drawText(
                        label,
                        textX,
                        textY,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 26f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}

// ---------------- CHIP ROW ----------------
@Composable
fun ChipRow(options: List<String>, selectedOption: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
            FilterChip(
                selected = option == selectedOption,
                onClick = { onSelect(option) },
                label = { Text(option) }
            )
        }
    }
}

// ---------------- RESULT CARD ----------------
@Composable
fun ResultCard(meal: MealResult) {

    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    val cardColor = if (isDark) DarkCard else LightCard
    val textColor = MaterialTheme.colorScheme.onSurface

    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text("Your Meal:", style = MaterialTheme.typography.titleLarge, color = textColor)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Name: ${meal.name}", color = textColor)
            Text("Calories: ${meal.calories} kcal", color = textColor)
            Text("Price: ₹${meal.price}", color = textColor)
            Text("Why: ${meal.reason}", color = textColor)
        }
    }
}

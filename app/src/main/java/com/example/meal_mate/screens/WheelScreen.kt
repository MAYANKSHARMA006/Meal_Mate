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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.withSave
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.meal_mate.viewmodel.ExpenseViewModel
import com.example.meal_mate.viewmodel.FoodViewModel
import com.example.meal_mate.viewmodel.MealViewModel
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// ------------------ DATA CLASS ------------------

data class MealResult(
    val name: String,
    val calories: Int,
    val price: Int,
    val reason: String
)

// ------------------ MAIN WHEEL SCREEN ------------------

@Composable
fun WheelScreen(
    expenseViewModel: ExpenseViewModel = viewModel(),
    mealViewModel: MealViewModel = viewModel(),
    foodViewModel: FoodViewModel = viewModel()
) {

    val context = LocalContext.current

    // VIBRATOR
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // FIRESTORE MEALS
    val foodList = foodViewModel.foods.value

    if (foodList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // FILTER STATES
    var selectedTime by remember { mutableStateOf("Breakfast") }
    var selectedMood by remember { mutableStateOf("Light Mood") }
    var budget by remember { mutableFloatStateOf(100f) }

    var mealResult by remember { mutableStateOf<MealResult?>(null) }

    // ------------- FIRESTORE FILTERS -------------
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
            Text("Try changing mood or budget")
        }
        return
    }

    // WHEEL ANIMATION
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {

        Text("Meal Decision Wheel", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        // -------- TIME SELECT --------
        Text("Choose Time", style = MaterialTheme.typography.titleMedium)
        ChipRow(listOf("Breakfast", "Lunch", "Dinner"), selectedTime) { selectedTime = it }
        Spacer(modifier = Modifier.height(20.dp))

        // -------- MOOD SELECT --------
        Text("Choose Mood", style = MaterialTheme.typography.titleMedium)
        ChipRow(listOf("Light Mood", "Healthy Mood", "Stress Mood"), selectedMood) { selectedMood = it }
        Spacer(modifier = Modifier.height(20.dp))

        // -------- BUDGET SELECT --------
        Text("Select Budget (₹${budget.toInt()})", style = MaterialTheme.typography.titleMedium)
        Slider(value = budget, onValueChange = { budget = it }, valueRange = 50f..500f)
        Spacer(modifier = Modifier.height(30.dp))

        // ---------------- WHEEL + POINTER + CENTER BUTTON ----------------
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            // ▼ POINTER ARROW
            Text(
                text = "▼",
                fontSize = 32.sp,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(bottom = 230.dp)
            )

            // ROTATING WHEEL
            SpinWheel(angle = rotation.value, items = wheelItems)

            // CENTER BUTTON / KNOB
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text("●", fontSize = 38.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // ---------------- SPIN BUTTON ----------------
        Button(
            onClick = {
                if (!isSpinning) {
                    isSpinning = true
                    val randomSpin = (900..1800).random().toFloat() // 2.5–5 rotations

                    scope.launch {
                        rotation.animateTo(
                            targetValue = rotation.value + randomSpin,
                            animationSpec = tween(3000, easing = LinearOutSlowInEasing)
                        )

                        isSpinning = false

                        // VIBRATION ON STOP
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                    150,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(150)
                        }

                        val index = ((rotation.value % 360) /
                                (360f / wheelItems.size)).toInt()

                        val chosenMeal = wheelItems[index]
                        val selectedFood = filteredMeals.first { it.name == chosenMeal }

                        mealResult = MealResult(
                            name = selectedFood.name,
                            calories = selectedFood.calories,
                            price = selectedFood.price,
                            reason = selectedFood.reason
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

        // ---------------- RESULT CARD ----------------
        mealResult?.let { meal ->
            ResultCard(meal)
            Spacer(modifier = Modifier.height(20.dp))

            // SAVE MEAL + ADD EXPENSE
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

// ------------------ UPDATED SPINWHEEL() WITH GRADIENT + LABELS ------------------

@Composable
fun SpinWheel(angle: Float, items: List<String>) {
    Canvas(
        modifier = Modifier.size(260.dp).clip(CircleShape)
    ) {
        val sliceAngle = 360f / items.size
        val radius = size.minDimension / 2

        rotate(angle) {
            items.forEachIndexed { index, label ->

                // GRADIENT BRUSH
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF42A5F5),
                        Color(0xFF1E88E5),
                        Color(0xFF0D47A1)
                    )
                )

                // Draw gradient slice
                drawArc(
                    brush = gradientBrush,
                    startAngle = sliceAngle * index,
                    sweepAngle = sliceAngle,
                    useCenter = true
                )

                // ---- TEXT LABEL --------
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

// ------------------ CHIP ROW ------------------

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

// ------------------ RESULT CARD ------------------

@Composable
fun ResultCard(meal: MealResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Your Meal:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Name: ${meal.name}")
            Text("Calories: ${meal.calories} kcal")
            Text("Price: ₹${meal.price}")
            Text("Why: ${meal.reason}")
        }
    }
}

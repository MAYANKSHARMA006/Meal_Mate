package com.example.meal_mate.model

data class Expense(
    val id: String = "",
    val name: String = "",
    val amount: Int = 0,
    val calories: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

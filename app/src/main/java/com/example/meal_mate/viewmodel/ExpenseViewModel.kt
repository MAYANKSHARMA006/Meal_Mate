package com.example.meal_mate.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.meal_mate.model.Expense
import com.example.meal_mate.repository.ExpenseRepository

class ExpenseViewModel : ViewModel() {

    private val repo = ExpenseRepository()

    var expenses = mutableStateOf<List<Expense>>(emptyList())
    var weeklyTotal = mutableIntStateOf(0)
    var monthlyTotal = mutableIntStateOf(0)

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        repo.getExpenses { list ->
            expenses.value = list
            weeklyTotal.intValue = list.sumOf { it.amount }
            monthlyTotal.intValue = list.sumOf { it.amount }
        }
    }

    fun addExpense(name: String, amount: Int, calories: Int = 0, onDone: () -> Unit) {
        repo.addExpense(
            Expense(
                id = "",            // repo will assign real ID
                name = name,
                amount = amount,
                calories = calories,
                timestamp = System.currentTimeMillis()
            )
        ) {
            loadExpenses()
            onDone()
        }
    }

    fun deleteExpense(id: String) {
        repo.deleteExpense(id)
        loadExpenses()   // refresh after delete
    }
}

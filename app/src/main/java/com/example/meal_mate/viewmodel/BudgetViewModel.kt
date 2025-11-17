package com.example.meal_mate.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class BudgetViewModel : ViewModel() {

    var dailyBudget = mutableStateOf(300)  // default ₹300

    fun updateBudget(amount: Int) {
        dailyBudget.value = amount
    }
}

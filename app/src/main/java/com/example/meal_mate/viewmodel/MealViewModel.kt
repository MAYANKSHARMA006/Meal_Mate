package com.example.meal_mate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.meal_mate.datastore.MealDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel(application: Application) : AndroidViewModel(application) {

    private val mealStore = MealDataStore(application)

    private val _meal = MutableStateFlow(Triple("", 0, 0))
    val meal: StateFlow<Triple<String, Int, Int>> = _meal

    init {
        viewModelScope.launch {
            mealStore.getMeal.collect { _meal.value = it }
        }
    }

    fun saveMeal(name: String, cal: Int, price: Int) {
        viewModelScope.launch {
            mealStore.saveMeal(name, cal, price)
        }
    }
}

package com.example.meal_mate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.example.meal_mate.Repository.FoodRepository
import com.example.meal_mate.model.Food

class FoodViewModel : ViewModel() {

    private val repo = FoodRepository()

    var foods = mutableStateOf<List<Food>>(emptyList())

    init {
        loadFoods()
    }

    fun loadFoods() {
        repo.getFoods { list ->
            foods.value = list
        }
    }
}


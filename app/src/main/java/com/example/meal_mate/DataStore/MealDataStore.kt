package com.example.meal_mate.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.mealDataStore by preferencesDataStore("meal_data")

class MealDataStore(private val context: Context) {

    companion object {
        val MEAL_NAME = stringPreferencesKey("meal_name")
        val MEAL_CAL = intPreferencesKey("meal_calories")
        val MEAL_PRICE = intPreferencesKey("meal_price")
    }

    val getMeal = context.mealDataStore.data.map { prefs ->
        Triple(
            prefs[MEAL_NAME] ?: "",
            prefs[MEAL_CAL] ?: 0,
            prefs[MEAL_PRICE] ?: 0
        )
    }

    suspend fun saveMeal(name: String, cal: Int, price: Int) {
        context.mealDataStore.edit { prefs ->
            prefs[MEAL_NAME] = name
            prefs[MEAL_CAL] = cal
            prefs[MEAL_PRICE] = price
        }
    }
}

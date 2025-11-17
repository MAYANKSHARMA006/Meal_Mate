package com.example.meal_mate.Repository

import com.example.meal_mate.model.Food
import com.google.firebase.firestore.FirebaseFirestore

class FoodRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getFoods(onResult: (List<Food>) -> Unit) {
        db.collection("foods")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Food::class.java) }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}

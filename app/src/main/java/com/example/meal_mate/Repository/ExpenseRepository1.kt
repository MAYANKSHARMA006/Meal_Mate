package com.example.meal_mate.repository

import com.example.meal_mate.model.Expense
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ExpenseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val userExpenses = db
        .collection("users")
        .document("user1")
        .collection("expenses")

    fun addExpense(expense: Expense, onDone: () -> Unit) {
        val doc = userExpenses.document()
        val updated = expense.copy(id = doc.id)
        doc.set(updated).addOnSuccessListener { onDone() }
    }

    fun getExpenses(onData: (List<Expense>) -> Unit) {
        userExpenses
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.toObjects(Expense::class.java)
                    onData(list)
                }
            }
    }

    fun deleteExpense(id: String) {
        userExpenses.document(id).delete()
    }
}

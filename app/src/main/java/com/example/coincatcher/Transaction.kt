package com.example.coincatcher

data class Transaction (
    val id: String,
    var title: String,
    var amount: Double,
    var category: String,
    var date: String,
    var type: String
)
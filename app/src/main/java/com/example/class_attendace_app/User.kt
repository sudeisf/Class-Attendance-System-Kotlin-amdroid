package com.example.class_attendace_app

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String, // Consider hashing passwords before storing them
    val phone: String? // Nullable if the database allows NULL for this field
)



package com.example.class_attendace_app

data class ClassModel(
    val id: Int,
    val name: String,
    val teacherId: String,
    val courseCode: String,
    val startDate: String,
    val description: CharSequence?
)

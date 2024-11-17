package com.example.class_attendace_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ClassDetailActivity : AppCompatActivity() {

    private lateinit var studentAdapter: StudentAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_detail_activity)

        val classId = intent.getIntExtra("CLASS_ID", -1) // Get class ID from intent

        if (classId == -1) {
            Toast.makeText(this, "Class ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val recyclerView: RecyclerView = findViewById(R.id.studList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list and a click handler
        studentAdapter = StudentAdapter(emptyList()) { student ->
            // Handle item click
            Toast.makeText(this, "Clicked: ${student.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = studentAdapter

        // Fetch students based on the class ID from the database and update the adapter
        fetchStudentsFromDb(classId)
    }

    // Fetch students from the database by class ID
    private fun fetchStudentsFromDb(classId: Int) {
        val dbHelper = DatabaseHelper(this)
        val students = dbHelper.getStudentsByClassId(classId)

        // Update the RecyclerView with the fetched students
        studentAdapter.updateStudents(students)
    }
}


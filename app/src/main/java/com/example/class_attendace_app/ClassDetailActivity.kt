package com.example.class_attendace_app

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class ClassDetailActivity : AppCompatActivity() {

    private lateinit var studentAdapter: StudentAdapter
    private lateinit var db: DatabaseHelper  // Declare db object

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_detail_activity)


        db = DatabaseHelper(this)

        val classId = intent.getIntExtra("CLASS_ID", -1)

        if (classId == -1) {
            Toast.makeText(this, "Class ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val recyclerView: RecyclerView = findViewById(R.id.studList)
        recyclerView.layoutManager = LinearLayoutManager(this)


        studentAdapter = StudentAdapter(emptyList()) { student ->

            Toast.makeText(this, "Clicked: ${student.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = studentAdapter

        // Correctly reference a FloatingActionButton
        val fab = findViewById<FloatingActionButton>(R.id.add_stud)

        // Set an onClickListener for the FloatingActionButton
        fab.setOnClickListener {
            showCreateClassDialog()
        }


        fetchStudentsFromDb(classId)
    }

    private fun showCreateClassDialog() {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_student_dialog, null)


        val studentNameInput = dialogView.findViewById<EditText>(R.id.Student_name_1)
        val idNumber = dialogView.findViewById<EditText>(R.id.id_number)
        val createButton = dialogView.findViewById<Button>(R.id.create_class_button)
        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)


        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Create a New Student")
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()

        createButton.setOnClickListener {
            val name = studentNameInput.text.toString().trim()
            val id = idNumber.text.toString().trim()

            if (name.isNotEmpty() && id.isNotEmpty()) {
                val result = db.insertStudent(name, id)
                if (result > 0) {
                    val updatedClassList = db.getAllStudents()
                    studentAdapter.updateStudents(updatedClassList)
                    Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Failed to add student", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun fetchStudentsFromDb(classId: Int) {
        val students = db.getStudentsByClassId(classId)
        studentAdapter.updateStudents(students)
    }
}



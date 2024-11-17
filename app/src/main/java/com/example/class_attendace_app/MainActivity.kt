package com.example.class_attendace_app

import ClassAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var classAdapter: ClassAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actitvity_home) // Fixed typo in layout file name

        databaseHelper = DatabaseHelper(this)

        val createClassButton: FloatingActionButton = findViewById(R.id.fab_create_class)
        insertDefaultStudents()

        // Set a click listener to show the dialog
        createClassButton.setOnClickListener {
            showCreateClassDialog()
        }

        recyclerView = findViewById(R.id.recycler_view_classes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch data and set adapter
        val classList = databaseHelper.getAllClasses()
        classAdapter = ClassAdapter(classList) { selectedClass ->
            // Handle item click
            println("Clicked class: ${selectedClass.name}")
        }
        recyclerView.adapter = classAdapter
    }

    private fun insertDefaultStudents() {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_STUDENT}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) {
            val students = listOf(
                "Alice Johnson" to "ROLL001",
                "Bob Smith" to "ROLL002",
                "Charlie Brown" to "ROLL003",
                // Add more students as needed
            )
            for ((name, rollNumber) in students) {
                val result = databaseHelper.insertStudent(name, rollNumber)
                if (result > 0) {
                    println("Inserted student: $name with Roll Number: $rollNumber")
                } else {
                    println("Failed to insert student: $name")
                }
            }
        } else {
            println("Students are already populated in the database.")
        }
    }


    private fun showCreateClassDialog() {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_class, null)

        // Get references to the input fields
        val classNameInput = dialogView.findViewById<EditText>(R.id.class_name)
        val courseCodeInput = dialogView.findViewById<EditText>(R.id.class_code)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.desicription_input)
        val createButton = dialogView.findViewById<Button>(R.id.create_class_button)
        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)
        val dateButton = dialogView.findViewById<Button>(R.id.date) // Button for date picker

        // Create an AlertDialog
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Create a New Class")
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()
        var selectedStartDate: String? = null

        // Handle Date Button Click to show DatePickerDialog
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    // Format the selected date as dd/MM/yyyy
                    selectedStartDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        createButton.setOnClickListener {
            val className = classNameInput.text.toString().trim()
            val courseCode = courseCodeInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val teacherID = intent.getIntExtra("TEACHER_ID", -1)

            // Validate inputs before proceeding
            if (className.isNotEmpty() && courseCode.isNotEmpty() && description.isNotEmpty() && selectedStartDate != null) {
                // Insert class into the database
                val result = databaseHelper.insertClass(className, teacherID, courseCode, selectedStartDate!!, description)
                if (result > 0) {
                    // Update RecyclerView with the new class list
                    val updatedClassList = databaseHelper.getAllClasses()
                    classAdapter.updateClassList(updatedClassList) // Update adapter with new data
                    Toast.makeText(this, "Class Created Successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Failed to insert class", Toast.LENGTH_SHORT).show()
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
}

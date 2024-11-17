package com.example.class_attendace_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity: AppCompatActivity() {

    private lateinit var signIn: TextView
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var phone: EditText

    private lateinit var dataHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.register_page)
        dataHelper = DatabaseHelper(this)

        name = findViewById(R.id.teacher_full_name)
        email = findViewById(R.id.email2)
        password = findViewById(R.id.password2)
        phone = findViewById(R.id.phoneNumber)

        signIn = findViewById(R.id.login_link)
        signIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val registerButton: TextView = findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            val userName = name.text.toString()
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()
            val userPhone = phone.text.toString()

            // Log inputs for debugging
            Log.d("RegisterActivity", "Registering teacher: Name=$userName, Email=$userEmail")

            val result = dataHelper.insertTeacher(userName, userEmail, userPassword, userPhone)
            Log.d("RegisterActivity", "Insert Result: $result")

            if (result != -1L) {
                Log.d("RegisterActivity", "Teacher registered successfully!")
                Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("RegisterActivity", "Error inserting user.")
                Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

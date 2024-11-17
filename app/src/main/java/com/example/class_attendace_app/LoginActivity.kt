package com.example.class_attendace_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var register: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var db: DatabaseHelper

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)


        email = findViewById(R.id.email_input)
        password = findViewById(R.id.password_input)
        db = DatabaseHelper(this)

        loginButton = findViewById(R.id.log_btn)
        register = findViewById(R.id.register)

        loginButton.setOnClickListener {
            val emailValue = email.text.toString().trim()
            val passwordValue = password.text.toString().trim()

            if (emailValue.isNotEmpty() && passwordValue.isNotEmpty()) {
                val teacherId = db.validateTeacherLogin(emailValue, passwordValue)
                if (teacherId != null) {
                    // Successfully logged in, pass the teacher ID to the next activity
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("TEACHER_ID", teacherId)
                    startActivity(intent)
                    finish()
                } else {
                    // Incorrect email or password
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show an error if fields are empty
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Handle register button click
        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

//        db.deleteAllTeachers()

    }


}









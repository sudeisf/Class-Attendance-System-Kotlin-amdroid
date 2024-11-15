package com.example.class_attendace_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val loginButton = findViewById<Button>(R.id.log_btn)

        loginButton.setOnClickListener{
            val  intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}






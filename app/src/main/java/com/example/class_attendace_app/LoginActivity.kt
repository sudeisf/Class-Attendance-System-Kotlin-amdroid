package com.example.class_attendace_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    private  lateinit var loginButton:Button
    private lateinit var register: TextView

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

         loginButton = findViewById(R.id.log_btn)
         register = findViewById(R.id.register)

        loginButton.setOnClickListener{
            val  intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        register.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java )
            startActivity(intent)
            finish()
        }
    }
}






package com.example.class_attendace_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity: AppCompatActivity() {


    private  lateinit var signIn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)


        signIn  = findViewById(R.id.login_link)
        signIn.setOnClickListener{
            val intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



    }


}
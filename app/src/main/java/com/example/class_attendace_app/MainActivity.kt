package com.example.class_attendace_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.class_attendace_app.ui.theme.ClassattendaceappTheme
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actitvity_home)

        val createClassButton: FloatingActionButton = findViewById(R.id.fab_create_class)

        // Set a click listener to show the dialog
        createClassButton.setOnClickListener {
            showCreateClassDialog()}
    }

    private fun showCreateClassDialog() {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_class, null)
        val dateView = LayoutInflater.from(this).inflate(R.layout.date, null)

        val dateButton: Button = dialogView.findViewById(R.id.date)

        dateButton.setOnClickListener{
            val dateBuilder = AlertDialog.Builder(this)
                .setTitle("Enter Date")
                .setView(dateView)
                .setCancelable(true)

            val date = dateBuilder.create()

            val closeButton: Button = dateView.findViewById(R.id.Enter)
            closeButton.setOnClickListener {
                date.dismiss()
            }

            date.show()
        }

        // Create an AlertDialog
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Create a New Class")
            .setView(dialogView)  // Set the custom dialog layout
            .setCancelable(true)  // Allow the dialog to be dismissed

        // Set the dialog button actions
        val dialog = dialogBuilder.create()

        val closeButton: Button = dialogView.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
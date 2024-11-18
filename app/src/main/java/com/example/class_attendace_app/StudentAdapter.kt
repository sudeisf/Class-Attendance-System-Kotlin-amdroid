package com.example.class_attendace_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.compose.material.ToggleButton

class StudentAdapter(
    private var students: List<StudentModel>,
    private val onClick: (StudentModel) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.Student_name)
        val studentId: TextView = itemView.findViewById(R.id.student_id)
        val attendanceToggle: ToggleButton = itemView.findViewById(R.id.attendance_toggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_card_layout, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.studentName.text = student.name
        holder.studentId.text = student.rollNumber
        holder.attendanceToggle.isChecked = student.isPresent

        holder.attendanceToggle.setOnCheckedChangeListener { _, isChecked ->
            student.isPresent = isChecked
        }

        holder.itemView.setOnClickListener {
            onClick(student)
        }
    }

    override fun getItemCount(): Int = students.size

    // Update the list of students and notify the adapter
    fun updateStudents(newStudents: List<StudentModel>) {
        students = newStudents
        notifyDataSetChanged()
    }
}

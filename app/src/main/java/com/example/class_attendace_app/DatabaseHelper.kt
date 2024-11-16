package com.example.class_attendace_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TEACHER_TABLE)
        db?.execSQL(CREATE_STUDENT_TABLE)
        db?.execSQL(CREATE_CLASS_TABLE)
        db?.execSQL(CREATE_ATTENDANCE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TEACHER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENT")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLASS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ATTENDANCE")
        onCreate(db)
    }

    // Insert Teacher
    fun insertTeacher(name: String, email: String, phone: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("phone", phone)
        }
        return db.insert(TABLE_TEACHER, null, values)
    }

    // Insert Student
    fun insertStudent(name: String, rollNumber: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("roll_number", rollNumber)
        }
        return db.insert(TABLE_STUDENT, null, values)
    }

    // Insert Class
    fun insertClass(name: String, teacherId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("teacher_id", teacherId)
        }
        return db.insert(TABLE_CLASS, null, values)
    }

    // Insert Attendance
    fun markAttendance(studentId: Int, classId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("student_id", studentId)
            put("class_id", classId)
        }
        return db.insert(TABLE_ATTENDANCE, null, values)
    }

    companion object {
        const val DATABASE_NAME = "AttendanceDB"
        const val DATABASE_VERSION = 1

        // Teacher Table
        const val TABLE_TEACHER = "teacher"
        const val CREATE_TEACHER_TABLE = """
            CREATE TABLE $TABLE_TEACHER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                email TEXT,
                phone TEXT
            )
        """

        // Student Table
        const val TABLE_STUDENT = "student"
        const val CREATE_STUDENT_TABLE = """
            CREATE TABLE $TABLE_STUDENT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                roll_number TEXT
            )
        """

        // Class Table
        const val TABLE_CLASS = "class"
        const val CREATE_CLASS_TABLE = """
            CREATE TABLE $TABLE_CLASS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                teacher_id INTEGER,
                FOREIGN KEY(teacher_id) REFERENCES $TABLE_TEACHER(id)
            )
        """

        // Attendance Table
        const val TABLE_ATTENDANCE = "attendance"
        const val CREATE_ATTENDANCE_TABLE = """
            CREATE TABLE $TABLE_ATTENDANCE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER,
                class_id INTEGER,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(student_id) REFERENCES $TABLE_STUDENT(id),
                FOREIGN KEY(class_id) REFERENCES $TABLE_CLASS(id)
            )
        """
    }
}

package com.example.class_attendace_app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db?.execSQL(CREATE_TEACHER_TABLE)
            db?.execSQL(CREATE_STUDENT_TABLE)
            db?.execSQL(CREATE_CLASS_TABLE)
            db?.execSQL(CREATE_ATTENDANCE_TABLE)
            Log.d("DatabaseDebug", "Tables created successfully.")
        } catch (e: Exception) {
            Log.e("DatabaseDebug", "Error creating tables: ", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_TEACHER")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENT")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLASS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_ATTENDANCE")
            onCreate(db)
            Log.d("DatabaseDebug", "Database upgraded to version $newVersion.")
        } catch (e: Exception) {
            Log.e("DatabaseDebug", "Error during database upgrade: ", e)
        }
    }

    // Insert Teacher
    fun insertTeacher(name: String, email: String, password: String, phone: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("password", password)
            put("phone", phone)
        }

        // Log the data being inserted
        Log.d("DatabaseDebug", "Inserting Teacher: Name=$name, Email=$email, Phone=$phone")

        // Insert the data into the teacher table
        return db.insert("teacher", null, values) // Replace "teacher" with your actual table name
    }

    // Insert Class
    fun insertClass(name: String, teacherId: Int, courseCode: String, startDate: String, description: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("teacher_id", teacherId)
            put("course_code", courseCode)
            put("start_date", startDate)
            put("discription", description)
        }

        return db.insert(TABLE_CLASS, null, values) // Ensure TABLE_CLASS is defined correctly
    }

    // Mark Attendance
    fun markAttendance(studentId: Int, classId: Int): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("student_id", studentId)
                put("class_id", classId)
            }
            db.insertOrThrow(TABLE_ATTENDANCE, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseDebug", "Error marking attendance: ", e)
            -1L
        }
    }

    // Validate Login
    @SuppressLint("Range")
    fun validateTeacherLogin(email: String, password: String): String? {
        val db = readableDatabase
        val query = "SELECT id, password FROM $TABLE_TEACHER WHERE email = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        try {
            if (cursor.moveToFirst()) {
                val storedPassword = cursor.getString(cursor.getColumnIndex("password"))
                return if (password == storedPassword) {
                    cursor.getString(cursor.getColumnIndex("id"))
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseDebug", "Error during login validation: ", e)
        } finally {
            cursor.close()
        }
        return null
    }

    // Get all classes
    @SuppressLint("Range")
    fun getAllClasses(): List<ClassModel> {
        val classList = mutableListOf<ClassModel>()
        val db = this.readableDatabase

        // Query all classes from the table
        val query = "SELECT * FROM $TABLE_CLASS"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val teacherId = cursor.getInt(cursor.getColumnIndex("teacher_id"))
                val courseCode = cursor.getString(cursor.getColumnIndex("course_code"))
                val startDate = cursor.getString(cursor.getColumnIndex("start_date"))
                val description = cursor.getString(cursor.getColumnIndex("discription"))

                // Create ClassModel object
                val classModel = ClassModel(id, name, teacherId.toString(), courseCode, startDate, description)
                classList.add(classModel)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return classList
    }


    fun insertStudent(name: String, rollNumber: String): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", name)
                put("roll_number", rollNumber)
            }

            // Log the data being inserted
            Log.d("DatabaseDebug", "Inserting Student: Name=$name, Roll Number=$rollNumber")

            // Insert the data into the student table
            db.insertOrThrow(TABLE_STUDENT, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseDebug", "Error inserting student: ", e)
            -1L // Return -1 if there is an error
        }
    }

    companion object {
        const val DATABASE_NAME = "ClassAttendanceDB"
        const val DATABASE_VERSION = 1

        // Table Names
        const val TABLE_TEACHER = "teacher"
        const val TABLE_STUDENT = "student"
        const val TABLE_CLASS = "class"
        const val TABLE_ATTENDANCE = "attendance"

        // SQL Queries
        const val CREATE_TEACHER_TABLE = """
            CREATE TABLE $TABLE_TEACHER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                phone TEXT
            )
        """
        const val CREATE_STUDENT_TABLE = """
            CREATE TABLE $TABLE_STUDENT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                roll_number TEXT UNIQUE NOT NULL
            )
        """
        const val CREATE_CLASS_TABLE = """
            CREATE TABLE $TABLE_CLASS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                teacher_id INTEGER NOT NULL,
                course_code TEXT NOT NULL,
                start_date TEXT NOT NULL,
                discription TEXT NOT NULL,
                FOREIGN KEY(teacher_id) REFERENCES $TABLE_TEACHER(id)
            )
        """
        const val CREATE_ATTENDANCE_TABLE = """
            CREATE TABLE $TABLE_ATTENDANCE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                class_id INTEGER NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(student_id) REFERENCES $TABLE_STUDENT(id),
                FOREIGN KEY(class_id) REFERENCES $TABLE_CLASS(id)
            )
        """
    }
}

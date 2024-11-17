package com.example.class_attendace_app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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

        return db.insert(TABLE_TEACHER, null, values) // Use constants for table names
    }

    // Insert Class
    fun insertClass(name: String, teacherId: Int, courseCode: String, startDate: String, description: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("teacher_id", teacherId)
            put("course_code", courseCode)
            put("start_date", startDate)
            put("description", description)  // Fixed typo here
        }

        return db.insert(TABLE_CLASS, null, values) // Ensure TABLE_CLASS is defined correctly
    }

    // Mark Attendance
    fun markAttendance(studentId: Int, classId: Int, status: Int): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("student_id", studentId)
                put("class_id", classId)
                put("status", status)  // 1 for present, 0 for absent
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
        val query = "SELECT * FROM $TABLE_CLASS"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val teacherId = cursor.getInt(cursor.getColumnIndex("teacher_id"))
                val courseCode = cursor.getString(cursor.getColumnIndex("course_code"))
                val startDate = cursor.getString(cursor.getColumnIndex("start_date"))
                val description = cursor.getString(cursor.getColumnIndex("description"))

                val classModel = ClassModel(id, name, teacherId.toString(), courseCode, startDate, description)
                classList.add(classModel)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return classList
    }

    // Insert Student
    fun insertStudent(name: String, rollNumber: String): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", name)
                put("roll_number", rollNumber)
            }

            Log.d("DatabaseDebug", "Inserting Student: Name=$name, Roll Number=$rollNumber")

            db.insertOrThrow(TABLE_STUDENT, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseDebug", "Error inserting student: ", e)
            -1L
        }
    }

    // Get all students
    @SuppressLint("Range")
    fun getAllStudents(): List<StudentModel> {
        val studentList = mutableListOf<StudentModel>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_STUDENT"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val rollNumber = cursor.getString(cursor.getColumnIndex("roll_number"))

                val studentModel = StudentModel(name, rollNumber, id, isPresent = false)
                studentList.add(studentModel)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return studentList
    }

    // Get student by roll number
    @SuppressLint("Range")
    fun getStudentByRollNumber(rollNumber: String): StudentModel? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_STUDENT WHERE roll_number = ?"
        val cursor = db.rawQuery(query, arrayOf(rollNumber))

        var student: StudentModel? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val roll = cursor.getString(cursor.getColumnIndex("roll_number"))

            student = StudentModel(name, roll, id, isPresent = false)
        }

        cursor.close()
        db.close()

        return student
    }

    // Get students by class ID (with attendance status)
    @SuppressLint("Range")
    fun getStudentsByClassId(classId: Int): List<StudentModel> {
        val studentList = mutableListOf<StudentModel>()
        val db = this.readableDatabase
        val query = """
            SELECT s.id, s.name, s.roll_number, a.status 
            FROM $TABLE_STUDENT s
            LEFT JOIN $TABLE_ATTENDANCE a ON s.id = a.student_id AND a.class_id = ?
        """
        val cursor = db.rawQuery(query, arrayOf(classId.toString()))

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val rollNumber = cursor.getString(cursor.getColumnIndex("roll_number"))
            val status = cursor.getInt(cursor.getColumnIndex("status"))
            val isPresent = status == 1

            studentList.add(StudentModel(name, rollNumber, id, isPresent))
        }

        cursor.close()
        db.close()
        return studentList
    }

    companion object {
        const val DATABASE_NAME = "CADB"
        const val DATABASE_VERSION = 1
        const val TABLE_TEACHER = "teachers"
        const val TABLE_STUDENT = "students"
        const val TABLE_CLASS = "classes"
        const val TABLE_ATTENDANCE = "attendance"

        const val CREATE_TEACHER_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_TEACHER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                email TEXT,
                password TEXT,
                phone TEXT
            )
        """
        const val CREATE_STUDENT_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_STUDENT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                roll_number TEXT
            )
        """
        const val CREATE_CLASS_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_CLASS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                teacher_id INTEGER,
                course_code TEXT,
                start_date TEXT,
                description TEXT
            )
        """
        const val CREATE_ATTENDANCE_TABLE = """
            CREATE TABLE IF NOT EXISTS $TABLE_ATTENDANCE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER,
                class_id INTEGER,
                status INTEGER
            )
        """
    }
}


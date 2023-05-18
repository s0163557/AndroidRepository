package com.example.app2.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.app2.data.Faculty
import com.example.app2.data.Group
import com.example.app2.data.Plane
import com.example.app2.data.Seat
import com.example.app2.data.Student
import java.util.*
import kotlin.collections.ArrayList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SHARED_PREFERENCES_NAME = "UniversityAppPrefs"

class FacultyRepository private constructor() {
    var university: MutableLiveData<List<Faculty>> = MutableLiveData()

    companion object {
        private var INSTANCE: FacultyRepository? = null
        fun newInstance() {
            if (INSTANCE == null) {
                INSTANCE = FacultyRepository()
            }
        }

        fun get(): FacultyRepository {
            return INSTANCE ?: throw IllegalStateException("Репозиторий не инициализирован")
        }
    }

    private fun getRowLetter(row: Int): String {
        val alphabet = ('A'..'Z').joinToString("")
        var result = ""
        var num = row
        while (num > 0) {
            val remainder = (num - 1) % 26
            result = alphabet[remainder] + result
            num = (num - 1) / 26
        }
        return result
    }

    fun addSeats(plane: Plane) {
        for (row in 1..plane.amRows) {
            for (col in 1..plane.amCols) {
                val rowToLetter = getRowLetter(row)
                val seat = Seat(name = "$rowToLetter$col", position = plane.seats.size+1)
                plane.seats += seat
            }
        }
    }

    fun saveData(context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val jsonUniversity = Gson().toJson(university.value)
        sharedPreferences.edit().putString(SHARED_PREFERENCES_NAME, jsonUniversity).apply()
    }

    fun loadData(context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(SHARED_PREFERENCES_NAME, null)
        if (jsonString != null) {
            val listType = object : TypeToken<List<Faculty>>() {}.type
            val faculties = Gson().fromJson<List<Faculty>>(jsonString, listType)
            university.value = faculties
        } else {
            university.value = arrayListOf()
        }
    }

    fun newFaculty(faculty: Faculty) {
        val list: MutableList<Faculty>
        if (university.value != null) {
            list = (university.value as ArrayList<Faculty>)
        } else {
            list = ArrayList<Faculty>()
        }
        list.add(faculty)
        university.postValue(list)
    }

    fun deleteFaculty(facultyID: UUID) {
        val u = university.value ?: return
        val faculty = u.find {  it.id == facultyID } ?: return
        val list: MutableList<Faculty>
        list = (university.value as ArrayList<Faculty>)
        list.remove(faculty)
        university.postValue(list)
    }

    fun editFaculty(name: String, cd : Date, _faculty: Faculty) {
        val faculty = Faculty(name = name, yearOfCreate = cd)
        val list: MutableList<Faculty>
        if (university.value != null) {
            list = (university.value as ArrayList<Faculty>)
        } else {
            list = ArrayList<Faculty>()
        }
        list.remove(_faculty)
        list.add(faculty)
        university.postValue(list)
    }

    fun newGroup(facultyID: UUID, name: String) {
        if (university.value == null) return
        val u = university.value!!
        val faculty = u.find { it.id == facultyID }
        if (faculty == null) return
        val group = Group(name = name)
        val list: ArrayList<Group>
        if (faculty.groups != null) {
            list = (faculty.groups as ArrayList<Group>)
        } else
            list = ArrayList<Group>()
        list.add(group)
        faculty.groups = list
        university.postValue(u)
    }

    fun newStudent(groupID: UUID, student: Student) {
        val u = university.value ?: return

        val faculty = u.find { it.groups?.find { it.id == groupID } != null } ?: return
        val group = faculty.groups?.find { it.id == groupID } ?: return
        val list: ArrayList<Student> = if ((group.students?.isEmpty() ?: true) == true)
            ArrayList()
        else {
            group.students as ArrayList<Student>
        }
        list.add(student)
        group.students = list
        university.postValue(u)
    }

    fun editGroup(group: Group, name: String) {
        val u = university.value ?: return
        val faculty = u.find { it.groups?.find { it.id == group.id } != null } ?: return
        var _group = faculty.groups?.find { it.id == group.id } ?: return
        _group = group
       _group.name = name
        faculty.groups!!.remove(group)
        faculty.groups!!.add(_group)
        university.postValue(u)
    }

    fun deleteStudent(groupID: UUID, student: Student) {
        val u = university.value ?: return
        val faculty = u.find { it.groups?.find { it.id == groupID } != null } ?: return
        val group = faculty.groups?.find { it.id == groupID }
        if (group!!.students?.isEmpty() == true) return
        val list = group.students as ArrayList<Student>
        list.remove(student)
        group.students = list
        university.postValue(u)
    }

    fun editStudent(groupID: UUID, student: Student) {
        val u = university.value ?: return
        val faculty = u.find { it.groups?.find { it.id == groupID } != null } ?: return
        val group = faculty.groups?.find { it.id == groupID } ?: return
        val _student = group.students?.find { it.id == student.id }
        if (_student == null) {
            newStudent(groupID, student)
            return
        }
        val list = group.students as ArrayList<Student>
        val i = list.indexOf(_student)
        list.remove(_student)
        list.add(i, student)
        group.students = list
        university.postValue(u)

    }

}
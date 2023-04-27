package com.example.app2.repository

import androidx.lifecycle.MutableLiveData
import com.example.app2.data.Faculty
import com.example.app2.data.Group
import com.example.app2.data.Student
import java.util.*
import kotlin.collections.ArrayList

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

    fun newFaculty(name: String, cd : Int) {
        val faculty = Faculty(name = name, yearOfCreate = cd)
        val list: MutableList<Faculty>
        if (university.value != null) {
            list = (university.value as ArrayList<Faculty>)
        } else {
            list = ArrayList<Faculty>()
        }
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
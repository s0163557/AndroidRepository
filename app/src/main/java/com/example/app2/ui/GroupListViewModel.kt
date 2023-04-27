package com.example.app2.ui

import androidx.lifecycle.ViewModel
import com.example.app2.data.Student
import com.example.app2.repository.FacultyRepository
import java.util.UUID

class GroupListViewModel : ViewModel() {
    fun deleteStudent(groupID: UUID, student: Student) =
        FacultyRepository.get().deleteStudent(groupID, student)
}
package com.example.app2.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.data.Faculty
import com.example.app2.data.Student
import com.example.app2.repository.FacultyRepository
import java.util.*
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app2.databinding.FragmentFacultyBinding

class StudentViewModel : ViewModel() {

    fun newStudent(groupID: UUID, student: Student) =
        FacultyRepository.get().newStudent(groupID, student)
    fun editStudent(groupID: UUID, student: Student) =
        FacultyRepository.get().editStudent(groupID, student)
}
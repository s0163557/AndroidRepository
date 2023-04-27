package com.example.app2.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app2.data.Faculty
import com.example.app2.repository.FacultyRepository
import java.util.UUID

class GroupViewModel : ViewModel() {
    var faculty: MutableLiveData<Faculty> = MutableLiveData()
    private lateinit var _facultyID : UUID

    fun setFaculty(facultyID : UUID){
        _facultyID = facultyID

        FacultyRepository.get().university.observeForever{
            faculty.postValue(it.find {it.id == _facultyID})
        }
    }
}
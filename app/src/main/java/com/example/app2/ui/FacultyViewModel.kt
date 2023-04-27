package com.example.app2.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app2.data.Faculty
import com.example.app2.repository.FacultyRepository

class FacultyViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var university : MutableLiveData<List<Faculty>> = MutableLiveData()

    init {
        FacultyRepository.get().university.observeForever{
            university.postValue(it)
        }
    }
}
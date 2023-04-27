package com.example.app2

import android.app.Application
import android.content.Context
import com.example.app2.repository.FacultyRepository

class App2Application : Application() {

    override fun onCreate() {
        super.onCreate()
        FacultyRepository.newInstance()
    }

    init{
        instance = this
    }

    companion object {
        private var instance : App2Application? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}
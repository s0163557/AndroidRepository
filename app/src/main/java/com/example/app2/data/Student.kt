package com.example.app2.data
import android.provider.ContactsContract.CommonDataKinds.Phone
import java.util.*
import kotlin.collections.ArrayList

data class Student(
    val id : UUID = UUID.randomUUID(),
    var townName : String = "",
    var seatsNumber : Int = 0,
    var sale : Int = 0,
    var departureDate : Date = Date(),
    var arrivalDate : Date = Date(),
    var occupiedSeats : ArrayList<Boolean> ?= ArrayList()
)

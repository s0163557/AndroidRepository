package com.example.app2.data
import android.provider.ContactsContract.CommonDataKinds.Phone
import java.time.DayOfWeek
import java.util.*
import kotlin.collections.ArrayList

data class Student(
    val id : UUID = UUID.randomUUID(),
    var ArrivalCity: String="",
    var NameOfPlane: String="",
    var DepartureJour: Int = 0,
    var DepartureMinute: Int = 0,
    var Arrivalhour: Int = 0,
    var ArrivalMinute: Int = 0,
    var DayOfWeek: java.time.DayOfWeek = java.time.DayOfWeek.MONDAY,
    var Sale : Int = 0,
    var planes: List<Plane> = emptyList()
)

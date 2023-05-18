package com.example.app2.data
import java.util.*
import kotlin.collections.ArrayList

data class Plane(
    val id : UUID = UUID.randomUUID(),
    var name : String="",
    val date: String = "",
    var amRows : Int = 0,
    var amCols : Int = 0,
    var seats: List<Seat> = emptyList()
)

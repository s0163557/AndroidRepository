package com.example.app2.data
import java.util.*

data class Group(
    val id : UUID = UUID.randomUUID(),
    var name : String=""){
    constructor() : this(UUID.randomUUID())
    var students : MutableList<Student>? = null
}

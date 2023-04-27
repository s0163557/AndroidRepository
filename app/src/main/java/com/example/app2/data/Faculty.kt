package com.example.app2.data

import java.util.*

data class Faculty(
    val id : UUID = UUID.randomUUID(),
    var name : String="",
    var yearOfCreate : Int = 2023){
    constructor(name: String, yearOfCreate: Int) : this(UUID.randomUUID(), name=name, yearOfCreate = yearOfCreate)
    var groups : MutableList<Group>? = null
}

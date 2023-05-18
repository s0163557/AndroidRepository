package com.example.app2.data

import java.util.UUID

data class Seat(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    var isBooked : Boolean = false,
    val position : Int
)

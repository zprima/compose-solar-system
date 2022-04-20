package com.example.solarsystem.data

import androidx.compose.runtime.Immutable

@Immutable
data class Planet(
    val name:String = "Planet X",
    val rotationDays:Int,
    val mass:Int,
    val representationColorHex: String,
    val distanceFromSun: Float
)
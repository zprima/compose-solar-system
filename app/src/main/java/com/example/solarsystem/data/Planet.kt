package com.example.solarsystem.data

import androidx.compose.runtime.Immutable

@Immutable
data class Planet(
    val name:String = "Planet X",
    val rotationDays:Int,
    val representationColorHex: String,
    val distanceFromSun: Float,
    val posX: Int = 0,
    val posY: Int = 0,
    val radius: Float = 20f
)
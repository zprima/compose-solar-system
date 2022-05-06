package com.example.solarsystem.ui.screen

import com.example.solarsystem.data.Planet
import com.example.solarsystem.data.planetList

data class SolarSystemUiState(
    val planets: List<Planet> = planetList,
    val zoomLevel: Int = planetList.size
)
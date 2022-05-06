package com.example.solarsystem.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.solarsystem.data.planetList

class SolarSystemViewModel: ViewModel() {
    var uiState by mutableStateOf(SolarSystemUiState())
        private set

    fun zoomIn(){
        if(uiState.zoomLevel == 1) return

        val newPlanetList = uiState.planets.toMutableList()
        newPlanetList.removeAt(newPlanetList.size - 1)

        uiState = uiState.copy(
            planets = newPlanetList,
            zoomLevel = uiState.zoomLevel - 1
        )
    }

    fun zoomOut(){
        if(uiState.zoomLevel == planetList.size) return

        val newPlanetList = uiState.planets.toMutableList()
        val newPlanet = planetList[newPlanetList.size]
        newPlanetList.add(newPlanet)

        uiState = uiState.copy(
            planets = newPlanetList,
            zoomLevel = uiState.zoomLevel + 1
        )
    }
}
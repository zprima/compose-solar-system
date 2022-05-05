package com.example.solarsystem

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.solarsystem.data.planetList
import com.example.solarsystem.ui.screen.SolarSystemScreen
import com.example.solarsystem.ui.theme.SolarSystemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val selectedPlanetList = remember { mutableStateListOf(planetList) }

            SolarSystemTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    val currentPlanets = selectedPlanetList[0]

                                    if (currentPlanets.size < 2) {
                                        selectedPlanetList[0] = planetList
                                    } else {
                                        val newPlanets = currentPlanets.toMutableList()
                                            newPlanets.removeAt(currentPlanets.size -1)
                                        selectedPlanetList[0] = newPlanets
                                    }
                                }
                            )
                        },
                    color = MaterialTheme.colors.background
                ) {
                    Log.d("APP", selectedPlanetList.toString())
                    SolarSystemScreen(selectedPlanetList)
                }
            }
        }
    }
}

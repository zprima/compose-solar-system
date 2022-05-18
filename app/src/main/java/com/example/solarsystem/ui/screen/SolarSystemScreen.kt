package com.example.solarsystem.ui.screen

import android.graphics.Paint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.solarsystem.data.Planet
import com.example.solarsystem.util.radians
import kotlin.math.*

@Composable
fun SolarSystemScreen(
    solarSystemViewModel: SolarSystemViewModel = viewModel()
) {
    val uiState = solarSystemViewModel.uiState
    val planetList = uiState.planets

    val planetMaxDistance = remember(planetList) { planetList.maxOf { it.distanceFromSun } }
    val planetMinDistance = 0

    val infiniteTransition = rememberInfiniteTransition()
    val planetRotationAnimations = planetList.mapIndexed { _, planet ->
        val rotationNormalization =
            ((planet.rotationDays / 365f) * 10000).roundToInt()

        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = radians(360f).toFloat(),
            animationSpec = infiniteRepeatable(
                tween(
                    durationMillis = rotationNormalization,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    var scale by remember { mutableStateOf(1f) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        val newScale = scale * zoomChange

        if(newScale > (scale + (newScale * 0.1f))){
            solarSystemViewModel.zoomIn()
        }
        else if(newScale < (scale - (newScale * 0.1f))){
            solarSystemViewModel.zoomOut()
        }

        scale = newScale


//
//        val newScale =  floor(scale * zoomChange * 100)
//        val fScale = floor(scale * 100)
//
//        Log.d("APPX", "prevScale $scale")
//        Log.d("APPX", "newScale $newScale")
//        Log.d("APPX", "fScale $fScale")
//
//        if(newScale >= (fScale+15f)){
//            Log.d("APPX", "zoom in")
//            solarSystemViewModel.zoomIn()
//        } else if(newScale <= (fScale)){
//            Log.d("APPX", "zoom out")
//            solarSystemViewModel.zoomOut()
//        }
//
//        var nextScale = scale * zoomChange
//        if(nextScale < 1f) nextScale = 1f
//        Log.d("APPX", "nextScale $nextScale")
//        scale = nextScale
    }

    val configuration = LocalConfiguration.current
    val width = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val height = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }

    val starAngle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = radians(360f).toFloat(),
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 280000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    val starPositions = remember(configuration) {
        (1..100)
            .map {
                Offset(
                    (0..width.toInt()).random().toFloat(),
                    (0..height.toInt()).random().toFloat(),
                )
            }
            .toMutableStateList()
    }


    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val center = remember(
            key1 = constraints.maxWidth,
            key2 = constraints.maxHeight
        ) {
            Offset(
                x = constraints.maxWidth / 2f,
                y = constraints.maxHeight / 2f
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = state)
        ) {

            drawStars(starPositions, starAngle.value, center)

            drawSun(center)

            planetList.forEachIndexed { index, planet ->
                val angle = planetRotationAnimations[index].value

                val normalizeDistance =
                    (planet.distanceFromSun - planetMinDistance) / (planetMaxDistance - planetMinDistance)

                val normalizeX = (normalizeDistance * center.x) + center.x
//                val normalizeY = (normalizeDistance * center.y) + center.y

                val planetPosition = Offset(
                    x = normalizeX,
                    y = center.y
                )

                val planetPositionAngle = Offset(
                    x = cos(angle) * (planetPosition.x - center.x),
                    y = sin(angle) * (planetPosition.x - center.x)
                ) + center

                drawPlanet(
                    planetPositionAngle,
                    planet,
                )

                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    center = center,
                    radius = planetPosition.x - center.x,
                    style = Stroke(width = 10f)
                )

//                drawPlanet(
//                    planetPosition,
//                    planet,
//                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(onClick = { solarSystemViewModel.zoomIn() }) {
                Text("+", color = Color.White, fontSize = 18.sp)
            }

            Button(onClick = { solarSystemViewModel.zoomOut() }) {
                Text("-", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

fun DrawScope.drawStars(starPositions: List<Offset>, starAngle: Float, center: Offset){
    starPositions.forEach {

        val cosAngle = cos(starAngle)
        val sinAngle = sin(starAngle)

        val dx = it.x - center.x
        val dy = it.y - center.y

        var rx = (dx * cosAngle - dy * sinAngle)
        var ry = (dx * sinAngle + dy * cosAngle)

        rx += center.x
        ry += center.y

        drawCircle(
            color = Color.White,
            radius = listOf(1f, 2f).random(),
            center = Offset(rx, ry)
        )
    }

}

fun DrawScope.drawSun(center: Offset) {
    drawCircle(
        color = Color.Yellow,
        center = center,
        radius = 30f
    )
}

fun DrawScope.drawPlanet(
    planetPosition: Offset,
    planet: Planet
) {
    drawCircle(
        color = Color(planet.representationColorHex.toColorInt()),
        center = planetPosition,
        radius = planet.radius
    )

    val paint = Paint()
    paint.color = Color.White.toArgb()
    paint.textSize = 36f

    drawContext.canvas.nativeCanvas.drawText(
        planet.name,
        planetPosition.x,
        planetPosition.y,
        paint
    )
}
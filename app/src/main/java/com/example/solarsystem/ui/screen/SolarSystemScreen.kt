package com.example.solarsystem.ui.screen

import android.graphics.Paint
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.solarsystem.data.Planet
import com.example.solarsystem.data.planetList
import com.example.solarsystem.util.radians
import kotlin.math.*

@Composable
fun SolarSystemScreen(){
    val planetMaxDistance = remember { planetList.maxOf { it.distanceFromSun } }
    val planetMinDistance = remember { planetList.minOf { it.distanceFromSun } }

    val infiniteTransition = rememberInfiniteTransition()

    val planetRotationAnimations = planetList.mapIndexed { index, planet ->
        val rotationNormalization =
            ((planet.rotationDays / 365f) * 10000).roundToInt()

        Log.d("App", "${planet.name}: $rotationNormalization")

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

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val center = remember(
            key1 = constraints.maxWidth,
            key2 = constraints.maxHeight
        ) {
            Offset(
                x = constraints.maxWidth / 2f,
                y = constraints.maxHeight / 2f
            )
        }

        Canvas(modifier = Modifier
            .fillMaxSize()){

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

//                drawPlanet(
//                    planetPosition,
//                    planet,
//                )
            }
        }
    }
}

fun DrawScope.drawSun(center: Offset){
    drawCircle(
        color = Color.Yellow,
        center = center,
        radius = 30f
    )
}

fun DrawScope.drawPlanet(
    planetPosition: Offset,
    planet: Planet
){
    drawCircle(
        color = Color(planet.representationColorHex.toColorInt()),
        center = planetPosition,
        radius = 20f
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

@Preview(showBackground = true)
@Composable
fun SolarSystemScreenPreview(){
    SolarSystemScreen()
}
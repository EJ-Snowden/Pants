package com.example.pants.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun animatedGradientTransition(color: Color): Pair<Color, Brush> {
    val transition = updateTransition(color, label = "color state")
    val animatedColor by transition.animateColor(
        transitionSpec = { spring(stiffness = Spring.StiffnessVeryLow) },
        label = "color preview",
    ) { it }

    val gradientStops = remember(animatedColor) {
        arrayOf(
            0.0f to transition.currentState,
            0.5f to animatedColor,
            1.0f to transition.targetState
        )
    }
    val animatedGradient = remember(gradientStops) {
        Brush.linearGradient(
            colorStops = gradientStops,
            start = Offset(0f, 1000f),
            end = Offset(1000f, 0f)
        )
    }

    return Pair(animatedColor, animatedGradient)
}

package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ActiveGold
import com.example.ui.theme.GoldGlowColor
import com.example.ui.theme.InactiveDotColor

@Composable
fun ProgressDots(
    totalDots: Int,
    currentIndex: Int, // 1-based index (1..totalDots)
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "DotPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotScale"
    )

    // Divide into two even rows for visual balance (e.g. 17 + 17 = 34, 17 + 16 = 33)
    val firstRowCount = (totalDots + 1) / 2
    val secondRowCount = totalDots - firstRowCount

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Row 1
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..firstRowCount) {
                DotItem(
                    isActive = (i == currentIndex),
                    pulseScale = pulseScale
                )
            }
        }

        // Row 2
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in (firstRowCount + 1)..totalDots) {
                DotItem(
                    isActive = (i == currentIndex),
                    pulseScale = pulseScale
                )
            }
        }
    }
}

@Composable
private fun DotItem(
    isActive: Boolean,
    pulseScale: Float
) {
    val targetColor = if (isActive) ActiveGold else InactiveDotColor
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 350),
        label = "DotColor"
    )

    if (isActive) {
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .size(8.dp)
                .shadow(elevation = 6.dp, shape = CircleShape, spotColor = GoldGlowColor, ambientColor = ActiveGold)
                .clip(CircleShape)
                .background(animatedColor)
        )
    } else {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(width = 1.dp, color = animatedColor, shape = CircleShape)
        )
    }
}

package com.example.swapcampus_isaaclee.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

fun Modifier.auditTouchTarget(enabled: Boolean): Modifier = composed {
    if (!enabled) return@composed this
    this.drawWithContent {
        drawContent()
        val meetsW3C = size.width >= 48.dp.toPx() && size.height >= 48.dp.toPx()
        val strokeColor = if (meetsW3C) Color(0xFF10B981) else Color(0xFFEF4444)
        drawRoundRect(
            color = strokeColor,
            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            )
        )
    }
}

fun Modifier.thumbZoneArcOverlay(enabled: Boolean): Modifier = composed {
    if (!enabled) return@composed this
    this.drawBehind {
        val arcHeight = size.height * 0.36f
        val topY = size.height - arcHeight
        drawArc(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF10B981).copy(alpha = 0.22f),
                    Color(0xFF10B981).copy(alpha = 0.08f)
                ),
                startY = topY,
                endY = size.height
            ),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(-size.width * 0.15f, topY),
            size = Size(size.width * 1.3f, arcHeight * 2f)
        )
    }
}


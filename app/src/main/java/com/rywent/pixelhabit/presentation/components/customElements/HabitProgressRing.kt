package com.rywent.pixelhabit.presentation.components.customElements

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    strokeWidth: Dp = 16.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    centerTextColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val targetProgress = progress.coerceIn(0f, 1f)
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(targetProgress) {
        animatedProgress.animateTo(
            targetValue = targetProgress,
            animationSpec = tween(
                durationMillis = 1100,
                easing = FastOutSlowInEasing
            )
        )
    }

    val stroke = strokeWidth.coerceAtMost(size / 6)
    val textSize = (size.value * 0.24f).coerceIn(14f, 26f)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val innerSize = size.toPx() - stroke.toPx()

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round),
                topLeft = androidx.compose.ui.geometry.Offset(stroke.toPx() / 2, stroke.toPx() / 2),
                size = androidx.compose.ui.geometry.Size(innerSize, innerSize)
            )

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = animatedProgress.value * 360f,
                useCenter = false,
                style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round),
                topLeft = androidx.compose.ui.geometry.Offset(stroke.toPx() / 2, stroke.toPx() / 2),
                size = androidx.compose.ui.geometry.Size(innerSize, innerSize)
            )
        }

        Text(
            text = "${(animatedProgress.value * 100).toInt()}%",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = textSize.sp
            ),
            color = centerTextColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
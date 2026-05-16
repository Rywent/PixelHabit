package com.rywent.pixelhabit.presentation.screens.home.components

import android.service.autofill.OnClickAction
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun StreakCard(
    currentStreak: Int,
    modifier: Modifier = Modifier,
    onStreakClick: () -> Unit = {}
) {

    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .size(width = 290.dp, height = 240.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .width(270.dp)
                .height(170.dp)
                .graphicsLayer {
                    rotationZ = -36f
                }
                 .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(110.dp)
                )
                .clip(RoundedCornerShape(70.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            scheme.primary.copy(alpha = 0.15f),
                            scheme.surfaceContainerHigh,
                            scheme.surfaceContainerHighest
                        ),
                        start = Offset(Float.POSITIVE_INFINITY, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
        )

        Icon(
            imageVector = Icons.Rounded.LocalFireDepartment,
            contentDescription = null,
            tint = scheme.primary,
            modifier = Modifier
                .size(115.dp)
                .offset(
                    x = (-35).dp,
                    y = 28.dp
                )
        )

        // NUMBER
        Text(
            text = currentStreak.toString(),
            color = scheme.primary,
            fontSize = 65.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-4).sp,
            textAlign = TextAlign.Right,
            softWrap = false,
            modifier = Modifier
                .width(130.dp)
                .offset(
                    x = 40.dp,
                    y = (-20).dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}


package com.rywent.pixelhabit.data.mapper

import androidx.compose.ui.graphics.vector.ImageVector

fun ImageVector.toPath(): String {
    return AppIcon.fromIcon(this)
}

fun String.toIcon(): ImageVector {
    return AppIcon.fromKey(this)
}
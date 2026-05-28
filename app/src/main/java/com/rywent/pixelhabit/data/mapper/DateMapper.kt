package com.rywent.pixelhabit.data.mapper

import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormattedDate(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(this))
}
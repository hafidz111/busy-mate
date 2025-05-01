package com.example.busymate.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    val date = Date(timestamp)
    return sdf.format(date)
}

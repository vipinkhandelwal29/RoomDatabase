package com.example.roomdatabase.database.util

import java.text.SimpleDateFormat
import java.util.*

fun timeStampToDate(timestamp: Long) =
    SimpleDateFormat("MM dd, yyyy", Locale.US).format(Date(timestamp))
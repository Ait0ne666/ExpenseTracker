package com.ait0ne.expensetracker.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        val months = listOf("январь","февраль","март", "апрель","май","июнь","июль","август","сентябрь", "октябрь","ноябрь","декабрь",)
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")



        fun isSameDay(a: Date, b: Date) :Boolean {
            return a.date == b.date && a.month == b.month && a.year == b.year
        }


    }
}
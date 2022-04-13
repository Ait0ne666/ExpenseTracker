package com.ait0ne.expensetracker.utils

import java.util.*

class DateUtils {
    companion object {
        val months = listOf("январь","февраль","март", "апрель","май","июнь","июль","август","сентябрь", "октябрь","ноябрь","декабрь",)

        fun isSameDay(a: Date, b: Date) :Boolean {
            return a.date == b.date && a.month == b.month && a.year == b.year
        }


    }
}
package com.ait0ne.expensetracker.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class DateUtils {
    companion object {
        val months = listOf("январь","февраль","март", "апрель","май","июнь","июль","август","сентябрь", "октябрь","ноябрь","декабрь",)
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")



        fun isSameDay(a: Date, b: Date) :Boolean {
            return a.date == b.date && a.month == b.month && a.year == b.year
        }



        fun localToUTC(date: Date): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.timeZone = TimeZone.getTimeZone("UTC")

            return calendar.time
        }



        fun localToUTCString(date: Date): String {

            val formatter = isoFormatter

            val localString = formatter.format(date)

            formatter.timeZone = TimeZone.getTimeZone("UTC")

            val utcDate = formatter.parse(localString)

            return formatter.format(utcDate)
        }

    }
}
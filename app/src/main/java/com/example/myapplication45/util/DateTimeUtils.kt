package com.example.myapplication45.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object DateTimeUtils {
    private const val DEFAULT_FORMAT_DATE = "dd-MM-yyyy"
    private const val DEFAULT_FORMAT_DATE_2 = "dd/MM/yyyy, hh:mm a"

    private fun getDateToday(): String? {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat(DEFAULT_FORMAT_DATE, Locale.ENGLISH)
        return df.format(c.time)
    }

    fun getLongCurrentTimeStamp(): Long {
        return convertDateToTimeStamp(getDateToday())
    }

    fun convertDateToTimeStamp(strDate: String?): Long {
        var result = "0"
        if (strDate != null) {
            try {
                val format = SimpleDateFormat(DEFAULT_FORMAT_DATE, Locale.ENGLISH)
                val date = format.parse(strDate)
                if (date != null) {
                    val timestamp = date.time / 1000
                    result = timestamp.toString()
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return result.toLong()
    }

    fun convertTimeStampToDate(strTimeStamp: String?): String {
        var result = ""
        if (strTimeStamp != null) {
            try {
                val floatTimestamp = strTimeStamp.toFloat()
                val timestamp = floatTimestamp.toLong()
                val sdf = SimpleDateFormat(DEFAULT_FORMAT_DATE_2, Locale.ENGLISH)
                val date = Date(timestamp)
                result = sdf.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }
}
package com.example.myapplication45.util

import android.util.Patterns


object StringUtil {

    //CharSequence: thao tac voi cac kieu du lieu khac nhau khong can chuyen doi kieu
    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) false else Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
    fun isEmpty(input: String?): Boolean {
        return input.isNullOrEmpty() || "" == input.trim { it <= ' ' }
    }
    fun getDoubleNumber(number: Int): String {
        return if (number < 10) {
            "0$number"
        } else "" + number
    }
}
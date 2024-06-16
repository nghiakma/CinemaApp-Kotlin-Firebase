package com.example.myapplication45.model

import java.util.ArrayList

class Revenue {
    var movieId: Long = 0
    var movieName: String? = null
    private var histories: MutableList<BookingHistory?>? = null

    fun getHistories(): MutableList<BookingHistory?> {
        if (histories == null) {
            histories = ArrayList()
        }
        return histories!!
    }

    fun setHistories(histories: MutableList<BookingHistory?>?) {
        this.histories = histories
    }

    val quantity: Int
        get() {
            if (histories == null || histories!!.isEmpty()) {
                return 0
            }
            var result = 0
            for (history in histories!!) {
                result += history?.count!!.toInt()
            }
            return result
        }
    val totalPrice: Int
        get() {
            if (histories == null || histories!!.isEmpty()) {
                return 0
            }
            var result = 0
            for (history in histories!!) {
                result += history?.total!!
            }
            return result
        }
}
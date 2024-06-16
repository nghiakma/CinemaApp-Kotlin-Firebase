package com.example.myapplication45.model
class BookingHistory {
    var id: Long = 0
    var movieId: Long = 0
    var name: String? = null
    var date: String? = null
    var room: String? = null
    var time: String? = null
    var count: String? = null
    var seats: String? = null
    var foods: String? = null
    var payment: String? = null
    var total = 0
    var user: String? = null
    var isUsed = false

    constructor() {}
    constructor(id: Long, movieId: Long, name: String?, date: String?, room: String?, time: String?,
                count: String?, seats: String?, foods: String?, payment: String?,
                total: Int, user: String?, used: Boolean) {
        this.id = id
        this.movieId = movieId
        this.name = name
        this.date = date
        this.room = room
        this.time = time
        this.count = count
        this.seats = seats
        this.foods = foods
        this.payment = payment
        this.total = total
        this.user = user
        isUsed = used
    }
}
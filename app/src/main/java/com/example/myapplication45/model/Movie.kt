package com.example.myapplication45.model

import java.io.Serializable

class Movie : Serializable {
    var id: Long = 0
    var name: String? = null
    var description: String? = null
    var price = 0
    var date: String? = null
    var image: String? = null
    var imageBanner: String? = null
    var url: String? = null
    var rooms: List<RoomFirebase?>? = null
    var categoryId: Long = 0
    var categoryName: String? = null
    var booked = 0

    constructor() {}
    constructor(id: Long, name: String?, description: String?, price: Int, date: String?,
                image: String?, imageBanner: String?, url: String?, rooms: List<RoomFirebase?>?,
                categoryId: Long, categoryName: String?, booked: Int) {
        this.id = id
        this.name = name
        this.description = description
        this.price = price
        this.date = date
        this.image = image
        this.imageBanner = imageBanner
        this.url = url
        this.rooms = rooms
        this.categoryId = categoryId
        this.categoryName = categoryName
        this.booked = booked
    }


}
package com.example.myapplication45.model

import java.io.Serializable

class Food : Serializable {
    var id: Long = 0
    var name: String? = null
    var price = 0
    var count = 0
    var quantity = 0

    constructor() {}
    constructor(id: Long, name: String?, price: Int, quantity: Int) {
        this.id = id
        this.name = name
        this.price = price
        this.quantity = quantity
    }
}
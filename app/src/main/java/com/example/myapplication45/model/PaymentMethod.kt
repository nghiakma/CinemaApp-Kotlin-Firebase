package com.example.myapplication45.model

class PaymentMethod {
    var type = 0
    var name: String? = null

    constructor() {}
    constructor(type: Int, name: String?) {
        this.type = type
        this.name = name
    }
}
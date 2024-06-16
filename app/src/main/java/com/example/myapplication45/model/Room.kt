package com.example.myapplication45.model

class Room {
    var id = 0
    var title: String? = null
    var isSelected = false

    constructor() {}
    constructor(id: Int, title: String?, isSelected: Boolean) {
        this.id = id
        this.title = title
        this.isSelected = isSelected
    }
}
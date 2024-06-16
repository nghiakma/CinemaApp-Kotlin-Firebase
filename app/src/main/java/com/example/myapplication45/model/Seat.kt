package com.example.myapplication45.model

import java.io.Serializable

class  Seat : Serializable {
    var id = 0
    var title: String? = null
    var isSelected = false

    constructor() {}
    constructor(id: Int, title: String?, selected: Boolean) {
        this.id = id
        this.title = title
        isSelected = selected
    }
}
package com.example.myapplication45.model

import java.io.Serializable


class SeatLocal : Serializable {
    var id = 0
    var title: String? = null
    var isSelected = false
    var isChecked = false
    var roomId = 0
    var timeId = 0

    constructor() {}
    constructor(id: Int, title: String?, selected: Boolean, roomId: Int, timeId: Int) {
        this.id = id
        this.title = title
        isSelected = selected
        this.roomId = roomId
        this.timeId = timeId
    }
}
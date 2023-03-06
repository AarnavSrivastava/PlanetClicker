package com.example.planetclicker

import kotlin.math.floor
import kotlin.math.pow

class UpgradeItem(name: String, image: Int, cost: Int) {
    val image: Int = image
    val name: String = name
    var cost: Int = cost
    private val startCost: Int = cost
    private var count: Int = 0

    fun buyItem() {
        count++
        this.cost = floor(startCost * 1.15.pow(count.toDouble()) + 0.5).toInt()
    }
}
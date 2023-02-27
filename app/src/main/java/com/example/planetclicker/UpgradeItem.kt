package com.example.planetclicker

import kotlin.math.floor
import kotlin.math.pow

class UpgradeItem(name: String, image: Int, startCost: Int, description: String) {
    private val startCost: Int = startCost

    companion object {
        var cost: Int = 0
        var count: Int = 0
    }

    init {
        cost = startCost
    }

    fun buyItem() {
        count++;
        cost = floor(startCost * 1.15.pow(count.toDouble()) + 0.5).toInt()
    }
}
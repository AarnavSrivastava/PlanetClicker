package com.example.planetclicker

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.floor
import kotlin.math.pow

class UpgradeItem(name: String, image: Int, cost: AtomicInteger, cooldown: AtomicInteger) {
    val image: Int = image
    val name: String = name
    val income: AtomicInteger = AtomicInteger(0)

    var cost: AtomicInteger = cost
    private var startCost: Int = cost.get()

    var enabled: AtomicBoolean = AtomicBoolean(false)

    val cooldown: AtomicInteger = cooldown

    var count: AtomicInteger = AtomicInteger(0)

    fun buyItem() {
        count.incrementAndGet()
        this.cost.set(floor(startCost * 1.15.pow(count.toDouble()) + 0.5).toInt())
        income.set(floor(count.get() * 1.5 - 0.5).toInt())
    }
}
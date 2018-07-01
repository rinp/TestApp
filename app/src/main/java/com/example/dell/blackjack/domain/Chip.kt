package com.example.dell.blackjack.domain

import java.io.Serializable

class Chip(var num: Int) : Serializable {

    operator fun compareTo(betChip: Chip): Int {
        return this.num.compareTo(betChip.num)
    }

    fun isEmpty(): Boolean = this.num < 0
    fun notEmpty(): Boolean = !isEmpty()

    operator fun plusAssign(chip: Chip) {
        this.num += chip.num
    }

    operator fun times(times: Double): Chip {
        return (this.num * times).toInt().toChip()
    }

    operator fun times(times: Int): Chip {
        return (this.num * times).toChip()
    }

    override fun toString(): String {
        return this.num.toString()
    }

}


fun Int.toChip(): Chip {
    return Chip(this)
}


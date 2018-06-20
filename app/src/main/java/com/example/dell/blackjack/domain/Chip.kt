package com.example.dell.blackjack.domain

class Chip(var num: Int) {

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

}


fun Int.toChip(): Chip {
    return Chip(this)
}


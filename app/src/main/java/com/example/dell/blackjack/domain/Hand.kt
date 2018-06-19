package com.example.dell.blackjack.domain

/**
 * カード
 * @card カードを表示するView
 * @Trump カードの情報
 * @isHide 裏表
 */
class Hand(val trump: Trump, var isHide: Boolean) : Trump by trump {

    fun open() {
        this.isHide = false
    }

    fun point(): List<Int> {
        if (isHide) {
            return listOf(0)
        }
        return when (trump.num) {
            1 -> listOf(1, 11)
            in 11..13 -> listOf(10)
            else -> listOf(trump.num)
        }
    }

}
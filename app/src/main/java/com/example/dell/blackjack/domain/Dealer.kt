package com.example.dell.blackjack.domain

import android.util.Log

class Dealer : Player {
    override var hand = mutableListOf<Hand>() //手札

    val firstScore: Score
        get() = calcScore(hand.map { it.copy().apply { this.open() } })

    fun addCard(trump: Trump) {
        addCard(hand, trump)
        Log.d("game", "ディーラースコア${this.score}")
    }

    fun makeHand(deck: Deck) {
        makeHand(hand, false, deck)
    }

    fun openHand() {
        hand.forEach { it.open() }
    }


    fun isDealerStopScore(): Boolean {
        return DEALER_STOP_SCR <= this.score.num
    }

}

////カードルール
private const val DEALER_STOP_SCR: Int = 17 //ディーラーがこれ以上カードを引かなくなる数

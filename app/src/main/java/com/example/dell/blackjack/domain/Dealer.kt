package com.example.dell.blackjack.domain

import android.util.Log

class Dealer : Player {
    var hand = mutableListOf<Hand>() //手札
    lateinit var score: Score
        private set

    fun addCard(trump: Trump): Hand {
        val addCard = addCard(hand, trump)
        score = calcScore(hand)
        Log.d("game", "ディーラースコア${this.score}")
        return addCard
    }

    fun makeHand(deck: Deck): MutableList<Hand> {
        makeHand(hand, false, deck)
        score = calcScore(hand)
        return hand
    }

    fun openHand(): MutableList<Hand> {
        hand.forEach { it.open() }
        score = calcScore(hand)
        return hand
    }
}
package com.example.dell.blackjack.domain

import android.util.Log

class Deck(deckCount: Int = 2) {
    companion object {
        private val suits = listOf("dia", "heart", "spade", "club")
    }

    private var cards: List<Trump> = (1..deckCount).flatMap {
        Log.d("deck", "decks$it")
        suits.flatMap { suit ->
            (1..13).map { num ->
                TrumpImpl(suit = suit, num = num)
            }
        }
    }.shuffled().toList()

    private var i = 0

    private fun reset() {
        cards.shuffled()
        i = 0
    }

    private fun isEmpty(): Boolean {
        return i >= cards.count()
    }

    fun dealCard(): Trump {
        if (isEmpty()) {
            reset()
        }
        return cards[i++]
    }

    fun remainingCardCount() = cards.count() - i

}
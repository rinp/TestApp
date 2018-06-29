package com.example.dell.blackjack.domain

class Deck(deckCount: Int = 2) {
    private var cards: List<Trump> = (1..deckCount).flatMap {
        suits.flatMap { suit ->
            (1..13).map { num ->
                when (num) {
                    1 -> TrumpImpl(suit = suit, num = num)
                    in (11..13) -> TrumpImpl(suit = suit, num = num)
                    else -> TrumpImpl(suit = suit, num = num)
                }
            }
        }
    }.shuffled()

    private var i = 0
    private val suits = listOf("dia", "heart", "spade", "club")

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
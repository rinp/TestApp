package com.example.dell.blackjack.domain

data class Deck(private val deckCount: Int = 2) {
    private lateinit var cards: List<Trump>
    private var i = 0
    private val suits = listOf("dia", "heart", "spade", "club")

    //トランプデッキの生成
    fun init() {
        this.cards = (1..deckCount).flatMap {
            suits.flatMap { suit ->
                (1..13).map { num ->
                    when (num) {
                        1 -> TrumpImpl(suit = suit, num = num)
                        in (11..13) -> TrumpImpl(suit = suit, num = num)
                        else -> TrumpImpl(suit = suit, num = num)
                    }
                }
            }
        }
        reset()
    }

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

    fun remainingCardCount() = "count:${cards.count() - i}"

}
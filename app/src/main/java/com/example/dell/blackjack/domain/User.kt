package com.example.dell.blackjack.domain

class User : Player {

    override var hand = mutableListOf<Hand>()

    lateinit var chip: Chip

    fun addCard(trump: Trump) {
        addCard(hand, trump)
    }

    fun makeHand(deck: Deck) {
        makeHand(hand, true, deck)
    }

}
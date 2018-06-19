package com.example.dell.blackjack.domain

class User(playerChip: Int) : Player {
    var hand = mutableListOf<Hand>() //手札(プレイヤー)
    lateinit var score: Score
        private set

    var chip = playerChip
        internal set

    fun addCard(trump: Trump): Hand {
        val handCard = addCard(hand, trump)
        score = calcScore(hand)
        return handCard
    }

    fun makeHand(deck: Deck): MutableList<Hand> {
        makeHand(hand, true, deck)
        score = calcScore(hand)
        return hand
    }

}
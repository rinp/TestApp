package com.example.dell.blackjack.userCase

import com.example.dell.blackjack.UserChipPref
import com.example.dell.blackjack.domain.*

class MainUseCase(
        private val userChip: UserChipPref,
        val betChip: Chip,
        private val deck: Deck,
        private val dealer: Dealer,
        private val user: User
) {

    fun remainingCardCount(): Int {
        return deck.remainingCardCount()
    }

    fun playerDrawCard() {
        val card = deck.dealCard()
        user.addCard(card)

        //22以上<Bust>で敗北
        if (user.score is Score.Bust) {
            dealerPlayTurn()
        }
    }

    fun dealerPlayTurn() {
        dealer.openHand()
        while (dealer.isDealerStopScore()) {
            val card = deck.dealCard()
            dealer.addCard(card)
        }
        turnEnd()
    }


    private fun turnEnd() {
        moveChip(issue)
    }

    val issue: Judge
        get() {
            val playerScr = user.score
            val dealerScr = dealer.score
            val bustFlgP = playerScr is Score.Bust
            val bustFlgD = dealerScr is Score.Bust
            val bjFlgP = playerScr === Score.BlackJack
            return when {
                bustFlgP -> Judge.LOSE
                bustFlgD -> {
                    if (bjFlgP) {
                        Judge.BJ_WIN
                    }
                    Judge.WIN
                }
                playerScr > dealerScr -> {
                    if (bjFlgP) {
                        Judge.BJ_WIN
                    }
                    Judge.WIN
                }
                playerScr < dealerScr -> Judge.LOSE
                else -> Judge.PUSH
            }
        }

    private fun moveChip(judge: Judge) {
        userChip.saveChip(betChip * judge.dividendPercent)
    }

    fun initHand() {
        user.makeHand(deck)
        dealer.makeHand(deck)
    }

    val lackChip: Boolean
        get() = userChip.loadChip() < betChip


    val loadPlayerChip: Chip
        get() = userChip.loadChip()

    val playerHand: MutableList<Hand>
        get() = user.hand
    val dealerHand: MutableList<Hand>
        get() = dealer.hand

    val playerScore: Score
        get() = user.score
    val dealerScore: Score
        get() = dealer.score
    val dealerFirstScore: Score
        get() = dealer.firstScore

}


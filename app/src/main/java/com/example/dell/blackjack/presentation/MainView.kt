package com.example.dell.blackjack.presentation

import com.example.dell.blackjack.domain.Chip
import com.example.dell.blackjack.domain.Hand
import com.example.dell.blackjack.domain.Judge
import com.example.dell.blackjack.domain.Score

interface MainView {
    //    fun countCards(deck: Deck)
    fun disabledHit()

    fun enableHit()
    fun renameHitBtn(text: String)
    fun renameBetChip(chip: Chip)
    fun setUserChip(chip: Chip)
    fun setCaption()
    fun disabledStand()
    fun enableStand()
    fun setDeckCount(count: Int)
    fun setResult(judge: Judge)
    fun setPlayerScore(score: Score)
    fun setDealerScore(score: Score)
    fun hideNextGame()
    fun showNextGame()
    fun hideBackTop()
    fun showBackTop()
    fun showSocView()
    fun removeAllCardZone()
    fun addDealerCard(hand: Hand)
    fun addPlayerCard(hand: Hand)
    fun addDealerCards(hands: List<Hand>)
    fun addPlayerCards(hands: List<Hand>)
    fun showAllDealerHand(dealerHands: MutableList<Hand>)
    fun showUserHand(hands: List<Hand>)
    fun showDealerHand(hands: List<Hand>)
    fun removeResult()

}
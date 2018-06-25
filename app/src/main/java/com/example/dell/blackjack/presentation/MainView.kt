package com.example.dell.blackjack.presentation

import com.example.dell.blackjack.domain.Score

interface MainView {
    //    fun countCards(deck: Deck)
    fun disabledHit()

    fun enableHit()
    fun renameHitBtn(text: String)
    fun renameBetChip(text: String)
    fun renameOwnChip(text: String)
    fun setCaption()
    fun disabledStand()
    fun enableStand()
    fun setDeckCount(count: Int)
    fun setResult(text: String)
    fun setPlayerScore(score: Score)
    fun setDealerScore(score: Score)
    fun hideNextGame()
    fun showNextGame()
    fun hideBackTop()
    fun showBackTop()

}
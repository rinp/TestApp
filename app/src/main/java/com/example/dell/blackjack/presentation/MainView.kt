package com.example.dell.blackjack.presentation

import com.example.dell.blackjack.domain.Deck

interface MainView {
    fun countCards(deck: Deck)
    fun disabledHit()
    fun enableHit()
    fun renameHitBtn(text: String)
}
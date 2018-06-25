package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dell.blackjack.domain.*
import com.example.dell.blackjack.presentation.MainView
import org.jetbrains.anko.*

// 最終的にはMainActivityに戻ることになる。
// BlackJackの一定量のデータ変化に連動する形で各々の値が変わる形にする。
// RxAndroidの利用？

data class GameLayout(
        val view: MainView,
        val userZone: LinearLayout,
        val dealerZone: LinearLayout
) {

    /**
     * 次のゲームを始める
     */
    @SuppressLint("SetTextI18n")
    fun nextSet(playerChip: Chip) {
        view.renameOwnChip("chip: $playerChip")
        view.showBackTop()
        view.showNextGame()
    }

    @SuppressLint("SetTextI18n")
    fun showUserHand(trump: Hand) {
        showHand(trump, userZone)
    }

    fun showUserHands(userHands: MutableList<Hand>) {
        userHands.forEach { showUserHand(it) }
    }

    fun showDealerHands(userHands: MutableList<Hand>) {
        userHands.forEach { showDealerHand(it) }
    }

    @SuppressLint("SetTextI18n")
    fun showDealerHand(trump: Hand) {
        showHand(trump, dealerZone)
    }

    private fun showHand(trump: Hand, zone: LinearLayout) {
    }

    fun resetShowDealerHands(dealerHands: MutableList<Hand>) {
        dealerZone.removeAllViewsInLayout()
        showDealerHands(dealerHands)
    }

}
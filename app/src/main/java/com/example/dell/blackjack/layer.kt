package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

data class GameLayout(
        val hit: Button,
        val playerCS: TextView,
        val dealerCS: TextView,
        val handZone: LinearLayout,
        val dealerZone: LinearLayout,
        val stand: Button,
        val result: TextView,
        val ownChip: TextView,
        val nextGame: Button,
        val backTop1: Button,
        val applicationContext: Context,
        val endsCards: TextView,
        val socView: LinearLayout,
        val caption00: TextView,
        val bet: TextView
) {
    /**
     * 次のゲームを始める
     */
    @SuppressLint("SetTextI18n")
    fun nextSet() {
        ownChip.text = "chip: ${player.ownChip}"
        setChip(applicationContext, player.ownChip)
        nextGame.visibility = View.VISIBLE
        backTop1.visibility = View.VISIBLE
    }

    //トランプセットの残り枚数を更新する
    @SuppressLint("SetTextI18n")
    fun countCards(view: TextView, deck: Deck) {
        if (view.id == R.id.endsCards) {
            view.text = deck.remainingCardCount()
        }
    }


}
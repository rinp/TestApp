package com.example.dell.blackjack.domain

import android.annotation.SuppressLint
import com.example.dell.blackjack.BLACK_JACK_NUM


interface Player {
    companion object {
        private const val HAND_NUM: Int = 2 //初回の手札の数
    }

    //手札にカードを追加する
    @SuppressLint("SetTextI18n", "RtlHardcoded")
    fun addCard(hands: MutableList<Hand>, trump: Trump) {
        val card = Hand(trump = trump, isHide = false)
        hands += card
    }

    var hand: MutableList<Hand>

    val score: Score
        get() = calcScore(hand)

    //スコア計算
    fun calcScore(hands: List<Hand>): Score {

        val reduce: List<Int> = hands
                .map { it.point() }
                .reduceRight { list, acc ->
                    list.flatMap { num ->
                        acc.map { it + num }
                    }
                }

        val minScore = reduce.min()!!
        if (BLACK_JACK_NUM < minScore) {
            return Score.Bust(minScore)
        }

        if (hands.size == 2 && reduce.any { it == BLACK_JACK_NUM }) {
            return Score.BlackJack
        }

        return Score.Point(reduce.filter { it <= BLACK_JACK_NUM }.max()!!)
    }

    /**
     *初回の手札を引く
     * @userZone 手札を表示するView
     * @user 手札を格納する変数
     */
    @SuppressLint("SetTextI18n", "RtlHardcoded")
    fun makeHand(user: MutableList<Hand>, playerFlg: Boolean, deck: Deck) {
        user.clear()
        for (i in 1..HAND_NUM) {
            val trump = deck.dealCard()
            user += Hand(trump = trump, isHide = i > 1 && !playerFlg)
        }
    }
}
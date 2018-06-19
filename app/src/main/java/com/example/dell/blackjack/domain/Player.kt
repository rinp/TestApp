package com.example.dell.blackjack.domain

import android.annotation.SuppressLint

private const val HAND_NUM: Int = 2 //初回の手札の数

interface Player {

    //手札にカードを追加する
    @SuppressLint("SetTextI18n", "RtlHardcoded")
    fun addCard(user: MutableList<Hand>, trump: Trump): Hand {
        val card = Hand(trump = trump, isHide = false)
        user += card
        return card
    }

    //スコア計算
    fun calcScore(hands: List<Hand>): Score {
        /*定数*/

        val reduce: List<Int> = hands.map { it.point() }.reduceRight { list, acc -> list.flatMap { num -> acc.map { it + num } } }

        if (BLACK_JACK_NUM < reduce.min()!!) {
            return Score.Bust(reduce.min()!!)
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
        var score = 0
        for (i in 1..HAND_NUM) {
            val trump = deck.dealCard()
            score += trump.num
            user += Hand(trump = trump, isHide = i > 1 && !playerFlg)
        }
    }


}
package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.widget.TextView

private const val HAND_NUM: Int = 2 //初回の手札の数

class Player(playerChip: Int) {
    private var hand = mutableListOf<Hand>() //手札(プレイヤー)
    var score: Int = 0
        private set

    var chip = playerChip
        internal set

    fun addCard(trump: Trump): Hand {
        val handCard = addCard(trump)
        score = calcScore(hand)
        return handCard
    }

    fun makeHand(deck: Deck): MutableList<Hand> {
        makeHand(hand, true, deck)
        score = calcScore(hand)
        return hand
    }

    fun printScore(playerCS: TextView): Int {
        return calcCardScore(hand, playerCS, true)
    }
}

class Dealer {
    private var hand = mutableListOf<Hand>() //手札
    var score: Int = 0
        private set

    fun addCard(trump: Trump): Hand {
        val addCard = addCard(hand, trump)
        score = calcScore(hand)
        return addCard
    }

    fun makeHand(deck: Deck): MutableList<Hand> {
        makeHand(hand, false, deck)
        score = calcScore(hand)
        return hand
    }

    fun printScore(playerCS: TextView): Int {
        return calcCardScore(hand, playerCS, false)
    }

    fun calcScore(): Int {
        return calcScore(hand)
    }

    @SuppressLint("SetTextI18n")
    fun openHand(): MutableList<Hand> {
        hand.forEach { it.open() }
        score = calcScore()
        return hand
    }
}

//手札にカードを追加する
@SuppressLint("SetTextI18n", "RtlHardcoded")
private fun addCard(user: MutableList<Hand>, trump: Trump): Hand {
    val card = Hand(trump = trump, isHide = false)
    user += card
    return card
}


//スコアに対しての画面書き込みを行う
@SuppressLint("SetTextI18n")
private fun calcCardScore(user: MutableList<Hand>, write: TextView, playerFlg: Boolean): Int {
    val cc = calcScore(user)
    if (write.text.indexOf("Player") != -1) {
        write.text = "Player:$cc"
        if (cc > BLACKJACK) {
            write.text = "Player:$cc <Bust>"
        } else if (cc == BLACKJACK) {
            write.text = "Player:$cc <BJ>"
        }
    } else {
        write.text = "Dealer:$cc"
        if (cc > BLACKJACK) {
            write.text = "Dealer:$cc <Bust>"
        } else if (cc == BLACKJACK) {
            write.text = "Dealer:$cc <BJ>"
        }
    }
    // 用途不明
//    if (playerFlg) {
//        dpVs[PLAYER] = cc
//    } else {
//        dpVs[DEALER] = cc
//    }
    return cc
}

//スコア計算
fun calcScore(hands: List<Hand>): Int {

    val reduce: List<Int> = hands.map { it.point() }.reduceRight { list, acc -> list.flatMap { num -> acc.map { it + num } } }

    if (BLACKJACK < reduce.min()!!) {
        return reduce.min()!!
    }

    return reduce.filter { it <= BLACKJACK }.max()!!

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

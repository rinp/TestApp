package com.example.dell.blackjack

import android.annotation.SuppressLint

private const val HAND_NUM: Int = 2 //初回の手札の数

class Player(playerChip: Int) {
    var hand = mutableListOf<Hand>() //手札(プレイヤー)
    lateinit var score: Score
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

}

class Dealer {
    var hand = mutableListOf<Hand>() //手札
    lateinit var score: Score
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

    @SuppressLint("SetTextI18n")
    fun openHand(): MutableList<Hand> {
        hand.forEach { it.open() }
        score = calcScore(hand)
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

sealed class Score {
    operator fun compareTo(other: Score): Int {
        return when {
            this === Score.BlackJack -> {
                if (other === Score.BlackJack) {
                    0
                } else {
                    1
                }

            }
            this is Score.Bust -> {
                if (other is Score.Bust) {
                    0
                } else {
                    -1
                }
            }
            this is Score.Point -> {
                return when {
                    other === Score.BlackJack -> -1
                    other is Score.Bust -> 1
                    other is Score.Point -> this.num.compareTo(other.num)
                    else -> throw IllegalArgumentException()
                }
            }
            else -> throw IllegalArgumentException()
        }
    }

    abstract val num: Int


    object BlackJack : Score() {
        override val num: Int
            get() = 21
    }

    class Bust(override val num: Int) : Score()

    class Point(override val num: Int) : Score()

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

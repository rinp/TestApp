package com.example.dell.blackjack

import android.widget.LinearLayout

/*定数*/
const val BLACKJACK: Int = 21 //BLACKJACKの値
////所持金関係
const val FIRSTCHIP: Int = 3000 //初回起動時チップ
const val DEBAGMANYCHIP: Int = 1000000 //テスト用
////Bet
const val BET1: Int = 500 //ベット1
const val BET2: Int = 1000 //ベット2
const val BET3: Int = 5000 //ベット3

data class Deck(private val deckCount: Int = 2) {
    private lateinit var cards: List<Trump>
    private var i = 0
    private val suits = listOf("dia", "heart", "spade", "club")

    //トランプデッキの生成
    fun init() {
        this.cards = (1..deckCount).flatMap {
            suits.flatMap { suit ->
                (1..13).map { num ->
                    when (num) {
                        1 -> Trump(suit = suit, num = num)
                        in (11..13) -> Trump(suit = suit, num = num)
                        else -> Trump(suit = suit, num = num)
                    }
                }
            }
        }
        reset()
    }

    private fun reset() {
        cards.shuffled()
        i = 0

    }

    private fun isEmpty(): Boolean {
        return i >= cards.count()
    }

    fun dealCard(): Trump {
        if (isEmpty()) {
            reset()
        }
        return cards[i++]
    }

    fun remainingCardCount() = "count:${cards.count() - i}"

}

val dpVs = mutableMapOf(PLAYER to 0, DEALER to 0) //1ゲームの結果
val player = Wager(0, 0)//ステータス

/**
 * 山札
 * @suit 絵札
 * @num カード番号
 * @isFace 表裏 初回は裏
 * @id 手札処理用
 */
open class Trump(val suit: String, val num: Int)

/**
 * カード
 * @card カードを表示するView
 * @Trump カードの情報
 * @isHide 裏表
 */
//class Hand(val card: View, val trump: Trump)
class Hand(val card: LinearLayout, val trump: Trump, var isHide: Boolean) {
    fun open() {
        this.isHide = false
    }

    fun point(): List<Int> {
        if (isHide) {
            return listOf(0)
        }
        return when (trump.num) {
            1 -> listOf(1, 11)
            in 11..13 -> listOf(10)
            else -> listOf(trump.num)
        }
    }
}

//ゲーム時のチップの流れ
class Wager(var ownChip: Int, var betChip: Int = 0) {
    fun setBet(bet: Int) {
        this.betChip = bet
    }

    fun resetBet() {
        this.betChip = 0
    }

    //勝負前チップ判定
    fun callChip(): Boolean {
        return this.ownChip >= this.betChip
    }

    //勝負後のチップ処理
    fun resultChip(dividend: Double) {
        val rst: Double = (this.ownChip - this.betChip) + (this.betChip * dividend)
        this.ownChip = rst.toInt()
    }
}


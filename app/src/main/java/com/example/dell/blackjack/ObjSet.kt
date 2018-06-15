package com.example.dell.blackjack

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
                        1 -> TrumpImpl(suit = suit, num = num)
                        in (11..13) -> TrumpImpl(suit = suit, num = num)
                        else -> TrumpImpl(suit = suit, num = num)
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

// TODO 利用している？
// val dpVs = mutableMapOf(PLAYER to 0, DEALER to 0) //1ゲームの結果

/**
 * 山札
 * @suit 絵札
 * @num カード番号
 * @isFace 表裏 初回は裏
 * @id 手札処理用
 */
class TrumpImpl(override val suit: String, override val num: Int) : Trump

interface Trump {
    val suit: String
    val num: Int
}

/**
 * カード
 * @card カードを表示するView
 * @Trump カードの情報
 * @isHide 裏表
 */
class Hand(val trump: Trump, var isHide: Boolean) : Trump by trump {

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

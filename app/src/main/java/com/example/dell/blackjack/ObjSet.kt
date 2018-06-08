package com.example.dell.blackjack

import android.widget.LinearLayout
import android.content.Context
import android.widget.TextView

/*定数*/
////カードルール
const val DEALSTOPSCR: Int = 17 //ディーラーがこれ以上カードを引かなくなる数
const val ACEHIGH: Int = 11 //ACEを高い値で数える
const val ACELOW: Int = 1 //ACEを低い値で数える
const val JQK: Int = 10 //絵札の値
const val BLACKJACK: Int = 21 //BLACKJACKの値
////プリファレンス関連
val PREFILENAME = "ownChip" //プリファレンスファイル名
val PLAYERMONEY = "chip" //操作側の所持金
////所持金関係
const val FIRSTCHIP: Int = 3000 //初回起動時チップ
const val DEBAGMANYCHIP: Int = 1000000 //テスト用
////Bet
const val BET1: Int = 500 //ベット1
const val BET2: Int = 1000 //ベット2
const val BET3: Int = 5000 //ベット3
const val LOSEOS: Double = 0.0 //敗北時配当
const val WINOS: Double = 3.0 //勝利時配当
const val BJOS: Double = 3.5 //BJ勝利時配当
val DUELRESLT = listOf("BJWIN", "WIN", "LOSE", "PUSH") //画面書き込み用


const val HANDNUM: Int = 2 //初回の手札の数

data class Deck(private val deckCount: Int = 2) {
    private lateinit var cards: List<Trump>
    private var i = 0
    private val suits = listOf("dia", "heart", "spade", "club")

    //トランプデッキの生成
    fun init() {
        this.cards = (1..deckCount).flatMap {
            (1..13).flatMap { num ->
                suits.map { suit ->
                    Trump(suit = suit, num = num)
                }
            }
        }.shuffled()
        i = 0
    }

    private fun isEmpty(): Boolean {
        return i >= cards.count()
    }

    fun dealCard(): Trump {
        if (isEmpty()) {
            init()
        }
        return cards[i++]
    }

    fun remainingCardCount() = "count:${cards.count() - i}"

}

val you = Player()

class Player {
    private var hand = mutableListOf<Hand>() //手札(プレイヤー)
    fun addCard(userZone: LinearLayout) {
        addCard(hand, userZone)
    }

    fun makeHand(handZone: LinearLayout) {
        makeHand(handZone, hand, true)
    }

    fun printScore(playerCS: TextView): Int {
        return calcCardScore(hand, playerCS, true)
    }

    fun calcScore(): Int {
        return calcpt(hand, true)
    }
}

val dealer = mutableListOf<Hand>() //手札(ディーラー)


val calcLi = mutableListOf<Hand>() //スコア計算用
val dpVs = mutableMapOf(PLAYER to 0, DEALER to 0) //1ゲームの結果
val player = Wager(0, 0)//ステータス

/*
* プリファレンス関連
* 現状は、プリファレンスファイルが1つの想定
* 大規模なものを作るときは引数にプリファレンスファイル名を追加する
*/
/** 入力された「キー：数値」をプリファレンスに保存する、存在していれば上書きする */
fun setChip(context: Context, key: String, num: Int) {
    // プリファレンスの準備 //
    val pref = context.getSharedPreferences(PREFILENAME, Context.MODE_PRIVATE)
    // プリファレンスに書き込むためのEditorオブジェクト取得 //
    val editor = pref.edit()
    editor.putInt(key, num)
    // 書き込みの確定（実際にファイルに書き込む）
    editor.commit()
}

/** プリファレンスから「キーに一致する値」を取り出す。登録されていなければ -1 を返す  */
fun loadChip(context: Context, key: String): Int {
    // プリファレンスの準備 //
    val pref = context.getSharedPreferences(PREFILENAME, Context.MODE_PRIVATE)
    // "user_age" というキーで保存されている値を読み出す
    return pref.getInt(key, -1)
}

/** プリファレンスからキーを削除 */
fun removePref(context: Context, key: String) {
    // プリファレンスの準備 //
    val pref = context.getSharedPreferences(PREFILENAME, Context.MODE_PRIVATE)
    // "user_age" というキーで保存されている値を読み出す
    if (pref.getInt(key, -1) == -1) {
        return
    }
    val editor = pref.edit()
    editor.remove(key)
    editor.commit()
}

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
 * @hidFlg 裏表
 */
//class Hand(val card: View, val trump: Trump)
class Hand(val card: LinearLayout, val trump: Trump, var hidFlg: Boolean) {
    fun open(hidFlg: Boolean) {
        this.hidFlg = !hidFlg
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


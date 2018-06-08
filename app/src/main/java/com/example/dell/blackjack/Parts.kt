@file:Suppress("UNREACHABLE_CODE", "NAME_SHADOWING")

package com.example.dell.blackjack

import android.annotation.SuppressLint

import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dell.blackjack.R.id.*
import org.jetbrains.anko.*


var deck = Deck()

/*処理*/
//キャプションのセット
@SuppressLint("SetTextI18n")
fun setCaption(text: TextView) {
    text.text =
            """
    |【RANK】
    | Ace :1or11
    | Jack,Queen,King:10
    | else:ownNumber
    |
    |【Rate】
    | Win(BJ):×2.5
    | Win:×2
    | PUSH:×1
    | LOSE:×0
""".trimMargin()
}

//トランプセットの残り枚数を更新する
@SuppressLint("SetTextI18n")
fun countCards(view: TextView) {
    if (view.id == endsCards) {
        view.text = deck.remainingCardCount()
    }
}

/**
 *初回の手札を引く
 * @userZone 手札を表示するView
 * @user 手札を格納する変数
 */
@SuppressLint("SetTextI18n", "RtlHardcoded")
fun makeHand(userZone: LinearLayout, user: MutableList<Hand>, playerFlg: Boolean) {
    user.clear()
    var score = 0
    for (i in 1..HANDNUM) {
        val trump = deck.dealCard()
        score += trump.num
        user += Hand(userZone.linearLayout {
            textView {
                backgroundColor = Color.parseColor(CARDF)
                text = "${trump.suit}\n${trump.num}"
                if (i > 1 && !playerFlg) {
                    //2枚目以降ディーラー
                    backgroundColor = Color.parseColor(CARDB)
                    text = ""
                }
                gravity = Gravity.LEFT
            }.lparams(width = userZone.width) {
                width = dip(CARDW)
                height = dip(CARDH)
                gravity = Gravity.LEFT
                horizontalMargin = dip(5)
                verticalMargin = dip(5)
            }
        }, trump, i > 1 && !playerFlg)
    }
    if (playerFlg) {
        dpVs[PLAYER] = score
    } else {
        dpVs[DEALER] = score
    }

}

/**
 * 勝負判定
 * BJWIN 0
 * WIN 1
 * LOSE 2
 * PUSH 3
 */
fun cmpScore(playerScr: Int, dealerScr: Int, rst: Wager): Int {
    val BustFlgP = playerScr > BLACKJACK
    val BustFlgD = dealerScr > BLACKJACK
    val BJFlgP = playerScr == BLACKJACK
    when {
        BustFlgP -> {
            //LOSE
            rst.resultChip(LOSEOS)
            return 2
        }
        BustFlgD -> {
            if (BJFlgP) {
                //BJ WIN
                rst.resultChip(BJOS)
                return 0
            }
            //WIN
            rst.resultChip(WINOS)
            return 1
        }
        playerScr > dealerScr -> {
            if (BJFlgP) {
                //BJ WIN
                rst.resultChip(BJOS)
                return 0
            }
            //WIN
            rst.resultChip(WINOS)
            return 1
        }
        playerScr < dealerScr -> {
            //LOSE
            rst.resultChip(LOSEOS)
            return 2
        }
        else ->
            //PUSH
            return 3
    }
}

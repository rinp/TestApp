@file:Suppress("UNREACHABLE_CODE", "NAME_SHADOWING")

package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.widget.TextView

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

@file:Suppress("UNREACHABLE_CODE", "NAME_SHADOWING")

package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.widget.TextView

private const val LOSEOS: Double = 0.0 //敗北時配当
private const val WINOS: Double = 3.0 //勝利時配当
private const val BJOS: Double = 3.5 //BJ勝利時配当

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

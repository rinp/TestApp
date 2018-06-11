@file:Suppress("UNREACHABLE_CODE", "NAME_SHADOWING")

package com.example.dell.blackjack

private const val LOSEOS: Double = 0.0 //敗北時配当
private const val WINOS: Double = 3.0 //勝利時配当
private const val BJOS: Double = 3.5 //BJ勝利時配当

enum class Judge(val output: String) {
    BJ_WIN("BJ WIN"),
    WIN("BJ WIN"),
    LOSE("BJ WIN"),
    PUSH("BJ WIN")
}


/**
 * 勝負判定
 * BJWIN 0
 * WIN 1
 * LOSE 2
 * PUSH 3
 */
fun cmpScore(playerScr: Int, dealerScr: Int, rst: Wager): Judge {
    val bustFlgP = playerScr > BLACKJACK
    val bustFlgD = dealerScr > BLACKJACK
    val bjFlgP = playerScr == BLACKJACK
    when {
        bustFlgP -> {
            //LOSE
            rst.resultChip(LOSEOS)
            return Judge.LOSE
        }
        bustFlgD -> {
            if (bjFlgP) {
                //BJ WIN
                rst.resultChip(BJOS)
                return Judge.BJ_WIN
            }
            //WIN
            rst.resultChip(WINOS)
            return Judge.WIN
        }
        playerScr > dealerScr -> {
            if (bjFlgP) {
                //BJ WIN
                rst.resultChip(BJOS)
                return Judge.BJ_WIN
            }
            //WIN
            rst.resultChip(WINOS)
            return Judge.WIN
        }
        playerScr < dealerScr -> {
            //LOSE
            rst.resultChip(LOSEOS)
            return Judge.LOSE
        }
        else ->
            //PUSH
            return Judge.PUSH
    }
}

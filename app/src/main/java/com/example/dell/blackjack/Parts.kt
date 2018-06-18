@file:Suppress("UNREACHABLE_CODE", "NAME_SHADOWING")

package com.example.dell.blackjack

enum class Judge(val output: String, val dividendPercent: Double) {
    BJ_WIN("BJ WIN", 2.5),
    WIN("WIN", 2.0),
    LOSE("LOSE", 0.0),
    PUSH("PUSH", 1.0)
}

const val BLACK_JACK_NUM: Int = 21 //BLACKJACKの値

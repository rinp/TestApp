@file:Suppress("UNREACHABLE_CODE", "NAME_SHADOWING")

package com.example.dell.blackjack

enum class Judge(val output: String, val dividendPercent: Double) {
    BJ_WIN("BJ WIN", 3.5),
    WIN("WIN", 3.0),
    LOSE("LOSE", 0.0),
    PUSH("PUSH", 0.0)
}

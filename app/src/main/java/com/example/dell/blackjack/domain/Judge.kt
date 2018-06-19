package com.example.dell.blackjack.domain

enum class Judge(val output: String, val dividendPercent: Double) {
    BJ_WIN("BJ WIN", 2.5),
    WIN("WIN", 2.0),
    LOSE("LOSE", 0.0),
    PUSH("PUSH", 1.0)
}

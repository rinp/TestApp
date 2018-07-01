package com.example.dell.blackjack.domain

interface UserChip {
    fun loadChip(): Chip

    fun saveChip(chip: Chip)
}


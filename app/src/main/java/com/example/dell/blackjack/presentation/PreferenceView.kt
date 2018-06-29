package com.example.dell.blackjack.presentation

import com.example.dell.blackjack.domain.Chip

interface PreferenceView {
    fun loadChip(): Chip
    fun setChip(chip: Chip)
    fun setChip(chip: Int)

}
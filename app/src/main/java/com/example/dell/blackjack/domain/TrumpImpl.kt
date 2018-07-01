package com.example.dell.blackjack.domain

/**
 * 山札
 * @suit 絵札
 * @num カード番号
 * @isFace 表裏 初回は裏
 * @id 手札処理用
 */
data class TrumpImpl(override val suit: String, override val num: Int) : Trump
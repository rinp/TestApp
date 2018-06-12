package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*

private const val HAND_NUM: Int = 2 //初回の手札の数

class Player {
    private var hand = mutableListOf<Hand>() //手札(プレイヤー)
    var score: Int = 0
        private set

    fun addCard(userZone: LinearLayout, trump: Trump) {
        addCard(hand, userZone, trump)
        score = calcScore(hand)
    }

    fun makeHand(handZone: LinearLayout, deck: Deck) {
        makeHand(handZone, hand, true, deck)
        score = calcScore(hand)
    }

    fun printScore(playerCS: TextView): Int {
        return calcCardScore(hand, playerCS, true)
    }
}

class Dealer {
    private var hand = mutableListOf<Hand>() //手札
    var score: Int = 0
        private set

    fun addCard(userZone: LinearLayout, trump: Trump) {
        addCard(hand, userZone, trump)
        score = calcScore(hand)
    }

    fun makeHand(handZone: LinearLayout, deck: Deck) {
        makeHand(handZone, hand, false, deck)
        score = calcScore(hand)
    }

    fun printScore(playerCS: TextView): Int {
        return calcCardScore(hand, playerCS, false)
    }

    fun calcScore(): Int {
        return calcScore(hand)
    }

    @SuppressLint("SetTextI18n")
    fun openHand(userZone: LinearLayout) {
        for (d in hand) {
            if (!d.isHide) {
                continue
            }
            d.card.linearLayout {
                textView {
                    text = "${d.trump.suit}\n${d.trump.num}"
                    backgroundColor = Color.parseColor(CARDF)
                }.lparams(width = userZone.width) {
                    width = dip(CARDW)
                    height = dip(CARDH)
                    gravity = Gravity.START
                    horizontalMargin = dip(5)
                    verticalMargin = dip(5)
                }
            }
            //カードをオープン状態にする(スコアに含める)
            d.open()
            //裏のカードとして使用していた空のテキストビューを削除するindex2: 配列:0~3 count:3なので裏は配列:1(count-2)
            d.card.removeView(d.card.getChildAt(d.card.childCount - 2))
        }
    }
}

//手札にカードを追加する
@SuppressLint("SetTextI18n", "RtlHardcoded")
private fun addCard(user: MutableList<Hand>, userZone: LinearLayout, trump: Trump) {
    user += Hand(userZone.linearLayout {
        textView {
            text = "${trump.suit}\n${trump.num}"
            gravity = Gravity.LEFT
            backgroundColor = Color.parseColor(CARDF)
        }.lparams(width = userZone.width) {
            width = dip(CARDW)
            height = dip(CARDH)
            gravity = Gravity.LEFT
            horizontalMargin = dip(5)
            verticalMargin = dip(5)
        }
    }, trump, false)
}


//スコアに対しての画面書き込みを行う
@SuppressLint("SetTextI18n")
private fun calcCardScore(user: MutableList<Hand>, write: TextView, playerFlg: Boolean): Int {
    val cc = calcScore(user)
    if (write.text.indexOf("Player") != -1) {
        write.text = "Player:$cc"
        if (cc > BLACKJACK) {
            write.text = "Player:$cc <Bust>"
        } else if (cc == BLACKJACK) {
            write.text = "Player:$cc <BJ>"
        }
    } else {
        write.text = "Dealer:$cc"
        if (cc > BLACKJACK) {
            write.text = "Dealer:$cc <Bust>"
        } else if (cc == BLACKJACK) {
            write.text = "Dealer:$cc <BJ>"
        }
    }
    if (playerFlg) {
        dpVs[PLAYER] = cc
    } else {
        dpVs[DEALER] = cc
    }
    return cc
}

//スコア計算
fun calcScore(hands: List<Hand>): Int {

    val reduce: List<Int> = hands.map { it.point() }.reduceRight { list, acc -> list.flatMap { num -> acc.map { it + num } } }

    if (BLACKJACK < reduce.min()!!) {
        return reduce.min()!!
    }

    return reduce.filter { it <= BLACKJACK }.max()!!

}

/**
 *初回の手札を引く
 * @userZone 手札を表示するView
 * @user 手札を格納する変数
 */
@SuppressLint("SetTextI18n", "RtlHardcoded")
fun makeHand(userZone: LinearLayout, user: MutableList<Hand>, playerFlg: Boolean, deck: Deck) {
    user.clear()
    var score = 0
    for (i in 1..HAND_NUM) {
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

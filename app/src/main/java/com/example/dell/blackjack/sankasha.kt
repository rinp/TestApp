package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*

private const val ACEHIGH: Int = 11 //ACEを高い値で数える
private const val ACELOW: Int = 1 //ACEを低い値で数える
private const val JQK: Int = 10 //絵札の値

class Player {
    private var hand = mutableListOf<Hand>() //手札(プレイヤー)
    fun addCard(userZone: LinearLayout) {
        addCard(hand, userZone)
    }

    fun makeHand(handZone: LinearLayout) {
        makeHand(handZone, hand, true)
    }

    fun printScore(playerCS: TextView): Int {
        return calcCardScore(hand, playerCS, true)
    }

    fun calcScore(): Int {
        return calcpt(hand, true)
    }
}

class Dealer {
    private var hand = mutableListOf<Hand>() //手札
    fun addCard(userZone: LinearLayout) {
        addCard(hand, userZone)
    }

    fun makeHand(handZone: LinearLayout) {
        makeHand(handZone, hand, false)
    }

    fun printScore(playerCS: TextView): Int {
        return calcCardScore(hand, playerCS, false)
    }

    fun calcScore(): Int {
        return calcpt(hand, false)
    }

    @SuppressLint("SetTextI18n")
    fun openHand(userZone: LinearLayout) {
        for (d in hand) {
            if (!d.hidFlg) {
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
            d.open(d.hidFlg)
            //裏のカードとして使用していた空のテキストビューを削除するindex2: 配列:0~3 count:3なので裏は配列:1(count-2)
            d.card.removeView(d.card.getChildAt(d.card.childCount - 2))
        }
    }
}

//手札にカードを追加する
@SuppressLint("SetTextI18n", "RtlHardcoded")
private fun addCard(user: MutableList<Hand>, userZone: LinearLayout) {
    val card = deck.dealCard()
    user += Hand(userZone.linearLayout {
        textView {
            text = "${card.suit}\n${card.num}"
            gravity = Gravity.LEFT
            backgroundColor = Color.parseColor(CARDF)
        }.lparams(width = userZone.width) {
            width = dip(CARDW)
            height = dip(CARDH)
            gravity = Gravity.LEFT
            horizontalMargin = dip(5)
            verticalMargin = dip(5)
        }
    }, card, false)
}


//スコアに対しての画面書き込みを行う
@SuppressLint("SetTextI18n")
private fun calcCardScore(user: MutableList<Hand>, write: TextView, playerFlg: Boolean): Int {
    val cc = calcpt(user, false)
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
private fun calcpt(user: MutableList<Hand>, firstFlg: Boolean): Int {
    var cc = 0
    var aceCount01 = 0
    var aceCount11 = 0

    calcLi.clear()
    calcLi.addAll(user)
    calcLi.sortBy { it.trump.num }

    //なんかいろんな判定とか計算する
    for (card in calcLi) {
        //裏返しのカード初回以外計算しない
        if (card.hidFlg && !firstFlg) continue

        //絵札は10で統一
        if (card.trump.num > JQK) {
            cc += JQK
            continue
        }

        //ACELOW ACEHIGH判定
        if (card.trump.num == 1) {
            if (cc <= (BLACKJACK - ACEHIGH)) {
                cc += ACEHIGH
                aceCount11++
                continue
            } else {
                cc++
                aceCount01++
                continue
            }
            //+ACELOWするとBustかつ1度以上ACEHIGHを利用している
            // (ACE(11),ACE(1)) -> (ACE(1),ACE(1))
            if (aceCount11 > 0 && cc > (BLACKJACK - ACEHIGH)) {
                cc -= (ACEHIGH - ACELOW)//ace(11)->ace(1)
                cc++ //今回のace(1)
                aceCount01 += 2
                aceCount11--
                continue
            }
        }
        //ACEHIGHありかつ今回のカードでBustになる
        if (cc + card.trump.num > BLACKJACK && aceCount11 > 0) {
            cc -= (ACEHIGH - ACELOW)
            cc += card.trump.num
            aceCount11--
            aceCount01++
            continue
        }
        //その他(ACELOW加算)
        cc += card.trump.num
    }
    return cc
}

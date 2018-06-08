package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*

private const val ACE_HIGH: Int = 11 //ACEを高い値で数える
private const val ACE_LOW: Int = 1 //ACEを低い値で数える
private const val JQK: Int = 10 //絵札の値
private const val HAND_NUM: Int = 2 //初回の手札の数

class Player {
    private var hand = mutableListOf<Hand>() //手札(プレイヤー)
    fun addCard(userZone: LinearLayout, deck: Deck) {
        addCard(hand, userZone, deck)
    }

    fun makeHand(handZone: LinearLayout, deck: Deck) {
        makeHand(handZone, hand, true, deck)
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
    fun addCard(userZone: LinearLayout, deck: Deck) {
        addCard(hand, userZone, deck)
    }

    fun makeHand(handZone: LinearLayout, deck: Deck) {
        makeHand(handZone, hand, false, deck)
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
private fun addCard(user: MutableList<Hand>, userZone: LinearLayout, deck: Deck) {
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

        //ACE_LOW ACEHIGH判定
        if (card.trump.num == 1) {
            if (cc <= (BLACKJACK - ACE_HIGH)) {
                cc += ACE_HIGH
                aceCount11++
                continue
            } else {
                cc++
                aceCount01++
                continue
            }
            //+ACELOWするとBustかつ1度以上ACEHIGHを利用している
            // (ACE(11),ACE(1)) -> (ACE(1),ACE(1))
            if (aceCount11 > 0 && cc > (BLACKJACK - ACE_HIGH)) {
                cc -= (ACE_HIGH - ACE_LOW)//ace(11)->ace(1)
                cc++ //今回のace(1)
                aceCount01 += 2
                aceCount11--
                continue
            }
        }
        //ACEHIGHありかつ今回のカードでBustになる
        if (cc + card.trump.num > BLACKJACK && aceCount11 > 0) {
            cc -= (ACE_HIGH - ACE_LOW)
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
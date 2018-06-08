@file:Suppress("UNREACHABLE_CODE", "NAME_SHADOWING")

package com.example.dell.blackjack

import android.annotation.SuppressLint

import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dell.blackjack.R.id.*
import org.jetbrains.anko.*


var deck = Deck()

/*処理*/
//キャプションのセット
@SuppressLint("SetTextI18n")
fun setCaption(text: TextView) {
    text.text =
            """
    |【RANK】
    | Ace :1or11
    | Jack,Queen,King:10
    | else:ownNumber
    |
    |【Rate】
    | Win(BJ):×2.5
    | Win:×2
    | PUSH:×1
    | LOSE:×0
""".trimMargin()
}

//トランプセットの残り枚数を更新する
@SuppressLint("SetTextI18n")
fun countCards(view: TextView) {
    if (view.id == endsCards) {
        view.text = deck.remainingCardCount()
    }
}

/**
 *初回の手札を引く
 * @userZone 手札を表示するView
 * @user 手札を格納する変数
 */
@SuppressLint("SetTextI18n", "RtlHardcoded")
fun makeHand(userZone: LinearLayout, user: MutableList<Hand>, playerFlg: Boolean) {
    user.clear()
    var score = 0
    for (i in 1..HANDNUM) {
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

/**
 * カードを引く
 * @userZone 手札を表示するView
 */
@SuppressLint("SetTextI18n")
fun drawCardDealer(userZone: LinearLayout) {
    val user  = dealer
    //ディーラーパターン(条件を満たすまでカードを引き続ける)
    openCard(userZone)
    val playerscore = calcpt(hand, false)
    var dealerScore = calcpt(dealer, false)
    while (dealerScore < DEALSTOPSCR) {
        if (playerscore < dealerScore) {
            return
        }
        addCard(user, userZone)
        dealerScore = calcpt(dealer, false)
    }
}

//手札にカードを追加する
@SuppressLint("SetTextI18n", "RtlHardcoded")
fun addCard(user: MutableList<Hand>, userZone: LinearLayout) {
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

//裏返しのカードを返す
@SuppressLint("SetTextI18n", "RtlHardcoded")
fun openCard(userZone: LinearLayout) {
    if (userZone.childCount != HANDNUM) {
        return
    }
    for (d in dealer) {
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
                gravity = Gravity.LEFT
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

//スコアに対しての画面書き込みを行う
@SuppressLint("SetTextI18n")
fun calcCardScore(user: MutableList<Hand>, write: TextView, playerFlg: Boolean): Int {
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
fun calcpt(user: MutableList<Hand>, firstFlg: Boolean): Int {
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

/**
 * 勝負判定
 * BJWIN 0
 * WIN 1
 * LOSE 2
 * PUSH 3
 */
fun cmpScore(playerScr: Int, dealerScr: Int, rst: Wager): Int {
    val BustFlgP = playerScr > BLACKJACK
    val BustFlgD = dealerScr > BLACKJACK
    val BJFlgP = playerScr == BLACKJACK
    when {
        BustFlgP -> {
            //LOSE
            rst.resultChip(LOSEOS)
            return 2
        }
        BustFlgD -> {
            if (BJFlgP) {
                //BJ WIN
                rst.resultChip(BJOS)
                return 0
            }
            //WIN
            rst.resultChip(WINOS)
            return 1
        }
        playerScr > dealerScr -> {
            if (BJFlgP) {
                //BJ WIN
                rst.resultChip(BJOS)
                return 0
            }
            //WIN
            rst.resultChip(WINOS)
            return 1
        }
        playerScr < dealerScr -> {
            //LOSE
            rst.resultChip(LOSEOS)
            return 2
        }
        else ->
            //PUSH
            return 3
    }
}

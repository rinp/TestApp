package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dell.blackjack.domain.*
import org.jetbrains.anko.*

// 最終的にはMainActivityに戻ることになる。
// BlackJackの一定量のデータ変化に連動する形で各々の値が変わる形にする。
// RxAndroidの利用？

data class GameLayout(
        val hit: Button,
        val playerCS: TextView,
        val dealerCS: TextView,
        val userZone: LinearLayout,
        val dealerZone: LinearLayout,
        val stand: Button,
        val result: TextView,
        val ownChip: TextView,
        val nextGame: Button,
        val backTop1: Button,
        val applicationContext: Context,
        val endsCards: TextView,
        val socView: LinearLayout,
        val caption00: TextView,
        val bet: TextView
) {
    /**
     * 次のゲームを始める
     */
    @SuppressLint("SetTextI18n")
    fun nextSet(playerChip: Chip) {
        ownChip.text = "chip: $playerChip"
        nextGame.visibility = View.VISIBLE
        backTop1.visibility = View.VISIBLE
    }

    //トランプセットの残り枚数を更新する
    @SuppressLint("SetTextI18n")
    fun countCards(view: TextView, deck: Deck) {
        if (view.id == R.id.endsCards) {
            view.text = deck.remainingCardCount()
        }
    }

    /*処理*/
//キャプションのセット
    @SuppressLint("SetTextI18n")
    fun setCaption() {
        caption00.text =
                """
    |【RANK】
    | Ace :1or11
    | Jack,Queen,King:10
    | else:ownNumber
    |
    |【Rate】
    | Win(BJ):×${Judge.BJ_WIN.dividendPercent}
    | Win:×${Judge.WIN.dividendPercent}
    | PUSH:×${Judge.PUSH.dividendPercent}
    | LOSE:×${Judge.LOSE.dividendPercent}
""".trimMargin()
    }

    @SuppressLint("SetTextI18n")
    fun showUserHand(trump: Hand) {
        showHand(trump, userZone)
    }

    fun showUserHands(userHands: MutableList<Hand>) {
        userHands.forEach { showUserHand(it) }
    }

    fun showDealerHands(userHands: MutableList<Hand>) {
        userHands.forEach { showDealerHand(it) }
    }

    @SuppressLint("SetTextI18n")
    fun showDealerHand(trump: Hand) {
        showHand(trump, dealerZone)
    }

    private fun showHand(trump: Hand, zone: LinearLayout) {
        val backGroundColor = if (trump.isHide) {
            Color.parseColor(CARDB)
        } else {
            Color.parseColor(CARDF)
        }
        val showText = if (trump.isHide) {
            ""
        } else {
            "${trump.suit}\n${trump.num}"
        }

        zone.linearLayout {
            textView {
                text = showText
                gravity = Gravity.LEFT
                backgroundColor = backGroundColor
            }.lparams(width = userZone.width) {
                width = dip(CARDW)
                height = dip(CARDH)
                gravity = Gravity.LEFT
                horizontalMargin = dip(5)
                verticalMargin = dip(5)
            }
        }

    }

    fun resetShowDealerHands(dealerHands: MutableList<Hand>) {
        dealerZone.removeAllViewsInLayout()
        showDealerHands(dealerHands)
    }

    //スコアに対しての画面書き込みを行う
    @SuppressLint("SetTextI18n")
    fun calcUserCardScore(user: User, write: TextView): Score {
        val score: Score = user.score
        val cc: Int = score.num

        write.text = "User:$cc"
        if (score === Score.BlackJack) {
            write.text = "User:$cc <Bust>"
        } else if (score === Score.BlackJack) {
            write.text = "User:$cc <BJ>"
        }
        return score
    }

    //スコアに対しての画面書き込みを行う
    @SuppressLint("SetTextI18n")
    fun calcDealerCardScore(dealer: Dealer, write: TextView): Score {
        val score: Score = dealer.score
        val cc: Int = score.num

        write.text = "Dealer:$cc"
        if (score is Score.Bust) {
            write.text = "Dealer:$cc <Bust>"
        } else if (score === Score.BlackJack) {
            write.text = "Dealer:$cc <BJ>"
        }
        return score
    }

}
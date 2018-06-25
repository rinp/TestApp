package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import com.example.dell.blackjack.domain.*
import com.example.dell.blackjack.presentation.MainView

////カードルール
private const val DEALER_STOP_SCR: Int = 17 //ディーラーがこれ以上カードを引かなくなる数

class BlackJackGame(
        private val view: MainView,
        private val gl: GameLayout,
        playerChip: Chip,
        private val betChip: Chip,
        private val applicationContext: Context
) {

    val dealer = Dealer()
    private val you = User(playerChip)
    private val deck = Deck()

    fun hit() {
        val card = deck.dealCard()
        val handCard = you.addCard(card)

        gl.showUserHand(handCard)

        val score: Score = you.score
        gl.view.setPlayerScore(score)

        view.setDeckCount(deck.remainingCardCount())
        val pCS = you.score

        //ブラックジャックの時はhitを止める(standを押させる)
        if (pCS === Score.BlackJack) {
            view.disabledHit()
            view.disabledHit()
            return
        }

        //22以上<Bust>で敗北
        if (pCS is Score.Bust) {
            dealerTurn()
        }
    }

    fun stand() {
        view.disabledStand()
        view.disabledHit()
        //結果
        dealerTurn()
        view.setResult(issue().output)

        gl.nextSet(you.chip)
    }

    fun nextGame() {
        Log.d("game", "nextGame in")
        zoneReset(1)
        view.hideNextGame()
        view.hideBackTop()
        Log.d("game", "nextGame end")
    }

    private fun dealerTurn() {
        Log.d("game", "ディーラーターン開始")
        Log.d("game", "ディーラースコア${dealer.score}")

        view.disabledHit()

        val dealerHands = dealer.openHand()
        gl.resetShowDealerHands(dealerHands)

        var dealerScore = dealer.score.num
        while (dealerScore < DEALER_STOP_SCR) {
            val hand = dealer.addCard(deck.dealCard())
            gl.showDealerHand(hand)
            val score: Score = dealer.score
            gl.view.setDealerScore(score)
            dealerScore = dealer.score.num
        }
        turnEnd()
    }

    private fun turnEnd() {
        val issue = issue()

        view.setResult(issue.output)
        moveChip(issue)

        gl.nextSet(you.chip)
    }

    // MainActivityを開いた際は0
    // Next Game では1
    fun gameInit(status: Int = 0) {
        zoneReset(status)
    }

    @SuppressLint("SetTextI18n")
    private fun zoneReset(status: Int) {
        Log.d("game", "zoneReset in")

        //TODO ここは手札の削除がされたことを基準に表示をなくすべき
        //場のカード情報の削除
        gl.userZone.removeAllViews()
        gl.dealerZone.removeAllViews()

        view.setResult("")

        // 文字列まで変更されているのか？
        view.renameHitBtn("hit")

        //残りチップの判定
        if (you.chip < betChip) {
            gl.socView.visibility = View.VISIBLE
        }

        if (status == 0) {
            view.setCaption()
            deck.init()
        }

        Log.d("game", "zoneReset 手札再配布")

        //手札生成(プレイヤー、ディーラー)
        val userHands = you.makeHand(deck)
        gl.showUserHands(userHands)
        val dealerHands = dealer.makeHand(deck)
        gl.showDealerHands(dealerHands)

        //合計値の表示
        val score: Score = you.score
        gl.view.setPlayerScore(score)
        val score1: Score = dealer.score
        gl.view.setDealerScore(score1)

        //山札の残り
        view.setDeckCount(deck.remainingCardCount())
        //ボタンの活性化
        view.enableHit()
        view.enableStand()
        //自身のチップデータの読みこみ
        val chip = loadChip(applicationContext)
        ////仮置きtest(最低限のベットを行う)
        //player.setBet(BET1)
        //情報の画面表示
        view.renameOwnChip("chip: $chip")
        view.renameBetChip("bet: $betChip")

        //初回カードの判定
        val playerFstScore: Score = you.score
        val dealerFstScore: Score = dealer.score

        if (playerFstScore === Score.BlackJack) {
            //プレイヤー初回BJなら即勝負を掛けれるようにしとく(なくても良いやつ？)
            view.disabledHit()
            view.renameHitBtn("BJ")
        }
        if (dealerFstScore === Score.BlackJack) {
            //ディーラーBJだと強制勝負
            val dealerHands2 = dealer.openHand()
            gl.resetShowDealerHands(dealerHands2)

            view.setDealerScore(dealer.score)
            val score1: Score = you.score
            gl.view.setPlayerScore(score1)
            //            you.printScore(gl.playerCS)
            //プレイヤーの操作は不可
            view.disabledHit()
            view.renameHitBtn("BJ")
        }
        Log.d("game", "zoneReset end")
    }

    private fun moveChip(judge: Judge) {
        you.chip += betChip * judge.dividendPercent
        setChip(applicationContext, you.chip)
    }

    private fun issue(): Judge {
        val playerScr = you.score
        val dealerScr = dealer.score

        val bustFlgP = playerScr is Score.Bust
        val bustFlgD = dealerScr is Score.Bust
        val bjFlgP = playerScr === Score.BlackJack
        return when {
            bustFlgP -> Judge.LOSE
            bustFlgD -> {
                if (bjFlgP) {
                    Judge.BJ_WIN
                }
                Judge.WIN
            }
            playerScr > dealerScr -> {
                if (bjFlgP) {
                    Judge.BJ_WIN
                }
                Judge.WIN
            }
            playerScr < dealerScr -> Judge.LOSE
            else -> Judge.PUSH
        }
    }
}
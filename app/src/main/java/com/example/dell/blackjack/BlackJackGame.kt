package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View

////カードルール
private const val DEALER_STOP_SCR: Int = 17 //ディーラーがこれ以上カードを引かなくなる数

class BlackJackGame(private val gl: GameLayout,
                    playerChip: Int,
                    private val betChip: Int,
                    private val applicationContext: Context
) {

    val dealer = Dealer()
    private val you = Player(playerChip)
    private val deck = Deck()

    fun hit() {
        val card = deck.dealCard()
        val handCard = you.addCard(card)

        gl.showUserHand(handCard)

        gl.calcCardScore(you.hand, gl.playerCS)

        gl.countCards(gl.endsCards, deck)
        val pCS = you.score

        //ブラックジャックの時はhitを止める(standを押させる)
        if (pCS === Score.BlackJack) {
            gl.hit.isEnabled = false
            return
        }

        //22以上<Bust>で敗北
        if (pCS is Score.Bust) {
            dealerTurn()
        }
    }

    fun stand() {
        gl.stand.isEnabled = false
        gl.hit.isEnabled = false
        //結果
        dealerTurn()
        gl.result.text = issue().output

        gl.nextSet(you.chip)
    }

    fun nextGame() {
        zoneReset(1)
        gl.nextGame.visibility = View.GONE
        gl.backTop1.visibility = View.GONE
    }

    private fun dealerTurn() {
        Log.d("game", "ディーラーターン開始")
        Log.d("game", "ディーラースコア${dealer.score}")

        gl.hit.isEnabled = false
        gl.stand.isEnabled = false

        val dealerHands = dealer.openHand()
        gl.resetShowDealerHands(dealerHands)

        var dealerScore = dealer.score.num
        while (dealerScore < DEALER_STOP_SCR) {
            val hand = dealer.addCard(deck.dealCard())
            gl.showDealerHand(hand)
            gl.calcCardScore(dealer.hand, gl.dealerCS)
            dealerScore = dealer.score.num
        }
        turnEnd()
    }

    private fun turnEnd() {
        val issue = issue()

        gl.result.text = issue.output
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

        //TODO ここは手札の削除がされたことを基準に表示をなくすべき
        //場のカード情報の削除
        gl.userZone.removeAllViews()
        gl.dealerZone.removeAllViews()

        gl.result.text = ""

        // 文字列まで変更されているのか？
        gl.hit.text = "hit"
        gl.stand.text = "stand"

        //残りチップの判定
        if (you.chip < betChip) {
            gl.socView.visibility = View.VISIBLE
        }
        if (status == 0) {
            gl.setCaption()
            deck.init()
        }

        //手札生成(プレイヤー、ディーラー)
        val userHands = you.makeHand(deck)
        gl.showUserHands(userHands)
        val dealerHands = dealer.makeHand(deck)
        gl.showDealerHands(dealerHands)

        //合計値の表示
        gl.calcCardScore(you.hand, gl.playerCS)
        gl.calcCardScore(dealer.hand, gl.dealerCS)

        //山札の残り
        gl.countCards(gl.endsCards, deck)
        //ボタンの活性化
        gl.hit.isEnabled = true
        gl.stand.isEnabled = true
        //自身のチップデータの読みこみ
        val chip = loadChip(gl.applicationContext)
        ////仮置きtest(最低限のベットを行う)
        //player.setBet(BET1)
        //情報の画面表示
        gl.ownChip.text = "chip: $chip"
        gl.bet.text = "bet: $betChip"
        //初回カードの判定
        val playerFstScore: Score = you.score
        val dealerFstScore: Score = dealer.score

        if (playerFstScore === Score.BlackJack) {
            //プレイヤー初回BJなら即勝負を掛けれるようにしとく(なくても良いやつ？)
            gl.hit.isEnabled = false
            gl.hit.text = "BJ"
        }
        if (dealerFstScore === Score.BlackJack) {
            //ディーラーBJだと強制勝負
            val dealerHands2 = dealer.openHand()
            gl.resetShowDealerHands(dealerHands2)

            gl.dealerCS.text = "Dealer:$BLACK_JACK_NUM <BJ>"
            gl.calcCardScore(you.hand, gl.playerCS)
//            you.printScore(gl.playerCS)
            //プレイヤーの操作は不可
            gl.hit.isEnabled = false
            gl.hit.text = "BJ"
        }
    }

    private fun moveChip(judge: Judge) {
        you.chip += (betChip * judge.dividendPercent).toInt()
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
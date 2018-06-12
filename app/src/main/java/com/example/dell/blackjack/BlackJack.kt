package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.view.View

////カードルール
private const val DEALER_STOP_SCR: Int = 17 //ディーラーがこれ以上カードを引かなくなる数

private const val LOSEOS: Double = 0.0 //敗北時配当
private const val WINOS: Double = 3.0 //勝利時配当
private const val BJOS: Double = 3.5 //BJ勝利時配当

class BlackJack(private val gl: GameLayout) {

    val dealer = Dealer()
    val you = Player()
    val deck = Deck()

    fun hit() {
        val card = deck.dealCard()
        you.addCard(gl.handZone, card)
        val pCS = you.printScore(gl.playerCS)
        // TODO プレイヤーがカードを引く場面でディーラーのスコアの再計算は必要なのか？
        // val dCS = dealer.printScore(gl.dealerCS)

        gl.countCards(gl.endsCards, deck)

        //ブラックジャックの時はhitを止める(standを押させる)
        if (pCS == BLACKJACK) {
            gl.hit.isEnabled = false
            return
        }

        //22以上<Bust>で敗北
        if (pCS > BLACKJACK) {
            val dCS = dealer.printScore(gl.dealerCS)
            dealer.openHand(gl.dealerZone)
            gl.hit.isEnabled = false
            gl.stand.isEnabled = false
            gl.result.text = cmpScore(pCS, dCS, player).output
            gl.nextSet()
        }
    }

    fun stand() {
        gl.stand.isEnabled = false
        gl.hit.isEnabled = false
        //結果
        val pCS = you.printScore(gl.playerCS)
        val dCS = dealer.printScore(gl.dealerCS)
        dealerTurn()
        gl.result.text = cmpScore(pCS, dCS, player).output

        gl.nextSet()
    }

    fun nextGame() {
        zoneReset(1, dealer, you, deck)
        gl.nextGame.visibility = View.GONE
        gl.backTop1.visibility = View.GONE
    }

    private fun dealerTurn() {
        dealer.openHand(gl.dealerZone)
        var dealerScore = dealer.calcScore()
        while (dealerScore < DEALER_STOP_SCR) {
            dealer.addCard(gl.dealerZone, deck.dealCard())
            dealerScore = dealer.calcScore()
        }
    }

    @SuppressLint("SetTextI18n")
    fun zoneReset(status: Int, dealer: Dealer, you: Player, deck: Deck) {
        //場のカード情報の削除
        gl.handZone.removeAllViews()
        gl.dealerZone.removeAllViews()
        gl.result.text = ""
        gl.hit.text = "hit"
        gl.stand.text = "stand"
        //残りチップの判定
        if (!player.callChip()) {
            gl.socView.visibility = View.VISIBLE
        }
        if (status == 0) {
            gl.setCaption()
            deck.init()
        }
        //手札生成(プレイヤー、ディーラー)
        you.makeHand(gl.handZone, deck)
        dealer.makeHand(gl.dealerZone, deck)
        //合計値の算出
        you.printScore(gl.playerCS)
        dealer.printScore(gl.dealerCS)
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
        gl.bet.text = "bet: ${player.betChip}"
        //初回カードの判定
        val playerFstScore = you.calcScore()
        val dealerFstScore = dealer.calcScore()

        if (playerFstScore == BLACKJACK) {
            //プレイヤー初回BJなら即勝負を掛けれるようにしとく(なくても良いやつ？)
            gl.hit.isEnabled = false
            gl.hit.text = "BJ"
        }
        if (dealerFstScore == BLACKJACK) {
            //ディーラーBJだと強制勝負
            dealer.openHand(gl.dealerZone)
            gl.dealerCS.text = "Dealer:$BLACKJACK <BJ>"
            you.printScore(gl.playerCS)
            //プレイヤーの操作は不可
            gl.hit.isEnabled = false
            gl.hit.text = "BJ"
        }
    }

    fun processChips(judge: Judge, rst: Wager) {
        when (judge) {
            Judge.BJ_WIN -> rst.resultChip(BJOS)
            Judge.WIN -> rst.resultChip(WINOS)
            Judge.LOSE -> rst.resultChip(LOSEOS)
            Judge.PUSH -> return
        }
    }

    /**
     * 勝負判定
     * BJWIN 0
     * WIN 1
     * LOSE 2
     * PUSH 3
     */
    fun cmpScore(playerScr: Int, dealerScr: Int, rst: Wager): Judge {
        val bustFlgP = playerScr > BLACKJACK
        val bustFlgD = dealerScr > BLACKJACK
        val bjFlgP = playerScr == BLACKJACK
        val result = when {
            bustFlgP -> Judge.LOSE
            bustFlgD -> {
                // TODO あってる？
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

        processChips(result, rst)

        return result

    }
}
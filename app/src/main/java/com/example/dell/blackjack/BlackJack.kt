package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class BlackJack(private val gl: GameLayout) {
    /** 画面書き込み用 */
    private val duelResult = listOf("BJ WIN", "WIN", "LOSE", "PUSH")

    val dealer = Dealer()
    val you = Player()
    val deck = Deck()

    fun hit() {
        you.addCard(gl.handZone, deck)

        gl.countCards(gl.endsCards, deck)
        val pCS = you.printScore(gl.playerCS)
        val dCS = dealer.printScore(gl.dealerCS)

        //ブラックジャックの時はhitを止める(standを押させる)
        if (pCS == BLACKJACK) {
            gl.hit.isEnabled = false
            return
        }

        //22以上<Bust>で敗北
        if (pCS > BLACKJACK) {
            dealer.openHand(gl.dealerZone)
            gl.hit.isEnabled = false
            gl.stand.isEnabled = false
            gl.result.text = duelResult[cmpScore(pCS, dCS, player)]
            gl.nextSet()
        }
    }

    fun stand() {
        gl.stand.isEnabled = false
        gl.hit.isEnabled = false
        dealer.openHand(gl.dealerZone)
        val playerScore = you.calcScore()
        var dealerScore = dealer.calcScore()
        while (dealerScore < DEALSTOPSCR) {
            if (playerScore < dealerScore) {
                break
            }
            dealer.addCard(gl.dealerZone, deck)
            dealerScore = dealer.calcScore()
        }
        //結果
        val pCS = you.printScore(gl.playerCS)
        val dCS = dealer.printScore(gl.dealerCS)
        gl.result.text = duelResult[cmpScore(pCS, dCS, player)]

        gl.nextSet()
    }

    fun nextGame() {
        zoneReset(1, dealer, you, deck)
        gl.nextGame.visibility = View.GONE
        gl.backTop1.visibility = View.GONE
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
            setCaption(gl.caption00)
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

}
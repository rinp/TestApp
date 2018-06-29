package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.util.Log
import com.example.dell.blackjack.domain.*
import com.example.dell.blackjack.presentation.MainView
import com.example.dell.blackjack.userCase.MainUseCase


class MainPresenter(
        private val view: MainView,
        private val useCase: MainUseCase
) {

    fun hit() {
        useCase.playerDrawCard()

        view.showUserHand(useCase.playerHand)
        view.setPlayerScore(useCase.playerScore)
        view.setDeckCount(useCase.remainingCardCount())

        //ブラックジャックの時はhitを止める(standを押させる)
        if (useCase.playerScore === Score.BlackJack) {
            view.disabledHit()
        }
    }

    fun stand() {
        //結果

        useCase.dealerPlayTurn()

        view.setResult(useCase.issue)

        view.disabledStand()
        view.disabledHit()
        view.setUserChip(useCase.loadPlayerChip)
        view.showBackTop()
        view.showNextGame()
    }

    fun nextGame() {
        Log.d("game", "nextGame in")
        zoneReset(false)
        view.hideNextGame()
        view.hideBackTop()
        Log.d("game", "nextGame end")
    }

    // MainActivityを開いた際は0
    // Next Game では1
    fun onCreate(status: Boolean = true) {
        zoneReset(status)
    }

    @SuppressLint("SetTextI18n")
    private fun zoneReset(first: Boolean) {
        Log.d("game", "zoneReset in")

        //残りチップの判定
        if (useCase.lackChip) {
            view.showSocView()
        }

        if (first) {
            view.setCaption()
        }

        //TODO ここは手札の削除がされたことを基準に表示をなくすべき
        //場のカード情報の削除
        view.removeAllCardZone()

        //FIXME 勝敗表示の部分に何も書かないはできるのか
        view.removeResult()

        // 文字列まで変更されているのか？
        view.renameHitBtn("hit")

        Log.d("game", "zoneReset 手札再配布")

        //手札生成(プレイヤー、ディーラー)
        useCase.initHand()


        //合計値の表示
        val playerScore: Score = useCase.playerScore
        val dealerScore: Score = useCase.dealerFirstScore

        view.showUserHand(useCase.playerHand)

        view.setPlayerScore(playerScore)
        view.setDeckCount(useCase.remainingCardCount())
        view.enableHit()
        view.enableStand()

        view.setUserChip(useCase.loadPlayerChip)
        view.renameBetChip(useCase.betChip)

        if (playerScore === Score.BlackJack) {
            //プレイヤー初回BJなら即勝負を掛けれるようにしとく(なくても良いやつ？)
            view.disabledHit()
            view.renameHitBtn("BJ")
        }
        if (dealerScore === Score.BlackJack) {
            //ディーラーBJだと強制勝負
            useCase.openDealerHand()

            view.showDealerHand(useCase.dealerHand)
            view.setDealerScore(useCase.dealerScore)

            view.setPlayerScore(playerScore)

            view.disabledHit()
            view.renameHitBtn("BJ")
        } else {
            view.setDealerScore(dealerScore)
            view.showDealerHand(useCase.dealerHand)

        }

        Log.d("game", "zoneReset end")
    }
}
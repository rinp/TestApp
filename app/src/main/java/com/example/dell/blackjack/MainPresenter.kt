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

        val playerScore = useCase.playerScore

        view.showUserHand(useCase.playerHand)
        view.setPlayerScore(playerScore)
        view.setDeckCount(useCase.remainingCardCount())

        //ブラックジャックの時はhitを止める(standを押させる)
        if (playerScore === Score.BlackJack || playerScore is Score.Bust) {
            view.disabledHit()
        }
    }

    fun stand() {
        //結果

        useCase.dealerPlayTurn()


        view.setDealerScore(useCase.dealerScore)
        useCase.dealerHand.let {
            Log.d("presenter","letによるディーラーハンドの処理")
            view.showAllDealerHand(it)
            view.showDealerHand(it)
        }

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
        view.removeAllCardZone()

        view.removeResult()

        // 文字列まで変更されているのか？
        view.renameHitBtn("hit")

        Log.d("game", "zoneReset 手札再配布")

        //手札生成(プレイヤー、ディーラー)
        useCase.initHand()


        val dealerFirstScore: Score = useCase.dealerFirstScore
        val playerScore: Score = useCase.playerScore

        view.setDeckCount(useCase.remainingCardCount())
        view.setUserChip(useCase.loadPlayerChip)
        view.renameBetChip(useCase.betChip)

        view.showUserHand(useCase.playerHand)
        view.setPlayerScore(playerScore)

        if (dealerFirstScore === Score.BlackJack || playerScore === Score.BlackJack) {

            //ディーラーBJだと強制勝負
            stand()
            return
        }

        view.enableHit()
        view.enableStand()

        view.setDealerScore(useCase.dealerScore)
        view.showDealerHand(useCase.dealerHand)

        Log.d("game", "zoneReset end")
    }
}
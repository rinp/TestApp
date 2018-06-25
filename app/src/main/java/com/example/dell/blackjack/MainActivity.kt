package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.dell.blackjack.domain.Deck
import com.example.dell.blackjack.domain.Judge
import com.example.dell.blackjack.domain.toChip
import com.example.dell.blackjack.presentation.MainView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {

    private var interstitialAd: InterstitialAd? = null //インテンション広告用
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layout = GameLayout(this, playerCS, dealerCS, handZone, dealerZone, result, nextGame, backTop1,
                this.applicationContext, endsCards, socView,
                caption00
        )

        val betChip = intent.getIntExtra("BET_CHIP", -1).toChip()
        if (betChip.isEmpty()) {
            throw RuntimeException("ベットしたチップ数が取得できない")
        }
        val blackJack = BlackJackGame(this, layout, loadChip(this.applicationContext), betChip, applicationContext)

        blackJack.gameInit()

        //インテンション広告の生成
        interstitialAd = newInterstitialAd()
        loadInterstitial()

        //カードを引く
        hit.setOnClickListener {
            blackJack.hit()
        }

        //今の持ち札で対戦
        stand.setOnClickListener {
            blackJack.stand()
        }


        //次のゲームを始める
        nextGame.setOnClickListener {
            blackJack.nextGame()
        }

        //TOPに戻る
        backTop1.setOnClickListener {
            nextGame.visibility = View.GONE
            backTop1.visibility = View.GONE
            showInterstitial()
        }

        //チップ不足
        socView.setOnClickListener {}//下位画面ボタン操作制御
        backTop2.setOnClickListener {
            val intent = Intent(this, StartMenu::class.java)
            finish()
            startActivity(intent)
        }

        //ヘルプ表示/ゲーム画面再表示
        help.setOnClickListener {
            capZone.visibility = View.VISIBLE
        }
        backGame1.setOnClickListener {
            capZone.visibility = View.GONE
        }

        //広告
        ad02.loadAd(AdRequest.Builder().build())
    }


    //トランプセットの残り枚数を更新する
    @SuppressLint("SetTextI18n")
    override fun countCards(deck: Deck) {
        endsCards.text = deck.remainingCardCount()
    }

    /**
     *
     *インターナル広告
     *
     * */
    private fun showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (interstitialAd?.isLoaded == true) {
            interstitialAd?.show()
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
            //
            goToNextLevel()
        }
    }

    private fun newInterstitialAd(): InterstitialAd {
        return InterstitialAd(this).apply {
            adUnitId = getString(R.string.interstitial_ad_unit_id)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    backTop1.isEnabled = true
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    backTop1.isEnabled = true
                }

                override fun onAdClosed() {
                    // Proceed to the next level.
                    goToNextLevel()
                }
            }
        }
    }

    private fun loadInterstitial() {
        // Disable the next level button and load the ad.
        backTop1.isEnabled = false
        val adRequest = AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template")
                .build()
        interstitialAd?.loadAd(adRequest)
    }

    private fun goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.
//        level.text = "Level " + (++currentLevel)
        interstitialAd = newInterstitialAd()
        loadInterstitial()
        // XXX 広告とプレイヤーのチップ・ベット数は変動させるべきでないだろう。
        // player.resetBet()
        val intent = Intent(this, StartMenu::class.java)
        startActivity(intent)
        finish()
    }

    ////////////////////////////////////////////////////////////////////
    override fun disabledHit() {
        hit.isEnabled = false
    }

    override fun enableHit() {
        hit.isEnabled = true
    }

    override fun renameHitBtn(text: String) {
        hit.text = text
    }

    override fun renameBetChip(text: String) {
        bet.text = text
    }

    override fun renameOwnChip(text: String) {
        ownChip.text = text
    }

    @SuppressLint("SetTextI18n")
    override fun setCaption() {
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
    override fun disabledStand() {
        stand.isEnabled = false
    }
    override fun enableStand() {
        stand.isEnabled = true
    }



}

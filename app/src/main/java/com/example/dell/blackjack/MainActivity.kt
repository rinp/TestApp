package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.dell.blackjack.domain.*
import com.example.dell.blackjack.presentation.MainView
import com.example.dell.blackjack.userCase.MainUseCase
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*


class MainActivity : UserChipPref(), UserChip, MainView, InterstitialAdModel {

    private var interstitialAd: InterstitialAd? = null //インテンション広告用
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val betChip = intent.getSerializableExtra("BET_CHIP") as Chip
        if (betChip.isEmpty()) {
            throw RuntimeException("ベットしたチップ数が取得できない")
        }
        val presenter = MainPresenter(this,
                MainUseCase(
                        this,
                        betChip,
                        Deck(),
                        Dealer(),
                        User()
                )
        )

        presenter.onCreate()

        //インテンション広告の生成
        interstitialAd = newInterstitialAd()
        loadInterstitial()

        //カードを引く
        hit.setOnClickListener {
            presenter.hit()
        }

        //今の持ち札で対戦
        stand.setOnClickListener {
            presenter.stand()
        }


        //次のゲームを始める
        nextGame.setOnClickListener {
            presenter.nextGame()
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

//
//    //トランプセットの残り枚数を更新する
//    @SuppressLint("SetTextI18n")
//    override fun countCards(deck: Deck) {
//        setDeckCount(deck.remainingCardCount())
//    }

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

    @SuppressLint("SetTextI18n")
    override fun renameBetChip(chip: Chip) {
        bet.text = "bet: $chip"
    }

    @SuppressLint("SetTextI18n")
    override fun setUserChip(chip: Chip) {
        ownChip.text = "chip: $chip"
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

    override fun setDealerScore(score: Score) {
        setScore(dealerCS, score, "Dealer")
    }

    override fun setPlayerScore(score: Score) {
        setScore(playerCS, score, "User")
    }

    private fun setScore(tv: TextView, score: Score, name: String) {
        val cc: Int = score.num

        tv.text = when {
            score === Score.BlackJack -> "$name:$cc <BJ>"
            score is Score.Bust -> "$name:$cc <Bust>"
            else -> "$name:$cc"
        }

    }

    override fun setResult(judge: Judge) {
        result.text = judge.output
    }

    override fun removeResult() {
        result.text = ""
    }


    @SuppressLint("SetTextI18n")
    override fun setDeckCount(count: Int) {
        endsCards.text = "count:$count"
    }

    override fun hideNextGame() {
        nextGame.visibility = View.GONE
    }

    override fun showNextGame() {
        nextGame.visibility = View.VISIBLE
    }

    override fun hideBackTop() {
        backTop1.visibility = View.GONE
    }

    override fun showBackTop() {
        backTop1.visibility = View.VISIBLE
    }

    override fun addDealerCard(hand: Hand) {
        Log.d("main", "ディーラーカード追加$hand")
        dealerZone.addCard(hand)
    }

    override fun addPlayerCard(hand: Hand) {
        handZone.addCard(hand)
    }

    override fun addDealerCards(hands: List<Hand>) {
        Log.d("main", "カード群追加$hands")
        hands.forEach { addDealerCard(it) }
    }

    override fun addPlayerCards(hands: List<Hand>) {
        hands.forEach { addPlayerCard(it) }
    }


    override fun showUserHand(hands: List<Hand>) {
        if (handZone.childCount < hands.size) {
            addPlayerCards(hands.subList(handZone.childCount, hands.size))
        }
    }

    override fun showDealerHand(hands: List<Hand>) {
        Log.d("main", "ディーラーハンド表示開始")
        Log.d("main", "arg=$hands")
        Log.d("main", "child=${dealerZone.childCount} hand=${hands.size}")
        if (dealerZone.childCount < hands.size) {
            addDealerCards(hands.subList(dealerZone.childCount, hands.size))
        }
        Log.d("main", "ディーラーハンド表示終了")
    }

    @SuppressLint("RtlHardcoded")
    private fun LinearLayout.addCard(hand: Hand) {
        val backGroundColor = if (hand.isHide) {
            Color.parseColor(CARDB)
        } else {
            Color.parseColor(CARDF)
        }
        val showText = if (hand.isHide) {
            ""
        } else {
            "${hand.suit}\n${hand.num}"
        }

        this.linearLayout {
            textView {
                text = showText
                gravity = Gravity.LEFT
                backgroundColor = backGroundColor
            }.lparams(width = this.width) {
                width = dip(CARDW)
                height = dip(CARDH)
                gravity = Gravity.LEFT
                horizontalMargin = dip(5)
                verticalMargin = dip(5)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun showAllDealerHand(dealerHands: List<Hand>) {
        Log.d("activity", "ディーラーカードオープン処理")
        dealerZone.forEachChildWithIndex { index, view ->
            Log.d("activity", "${index}のカードは${view.javaClass}")
            // TODo TextViewが検知できない
            if (view is TextView) {
                val hand = dealerHands[index]
                if (view.text.isEmpty()) {
                    view.backgroundColor = Color.parseColor(CARDF)
                    view.text = "${hand.suit}\n${hand.num}"
                }
            }
        }
    }


    override fun removeAllCardZone() {
        handZone.removeAllViews()
        dealerZone.removeAllViews()
    }

    override fun showSocView() {
        socView.visibility = View.VISIBLE
    }

}


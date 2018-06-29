package com.example.dell.blackjack

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.dell.blackjack.domain.Chip
import com.example.dell.blackjack.domain.toChip
import com.example.dell.blackjack.presentation.StartView
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_start.*
import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.util.*

class StartMenu : AppCompatActivity(), AbstractPreferencesModel {
    override val appContext: Context
        get() = this.applicationContext

    companion object {
        private val BET1: Chip = 500.toChip() //ベット1
        private val BET2: Chip = 1000.toChip() //ベット2
        private val BET3: Chip = 5000.toChip() //ベット3
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //初期処理
        startPrcs()

        //広告のビュー todo:広告クリック時処理を探す
        ad01.setOnClickListener {
            if (addChipView.visibility == View.VISIBLE) {
                handler.removeCallbacks(runnable)
                count = 0
                chargeText.text = "Charge Complete !!"
                backToMenu.visibility = View.VISIBLE
                timerRunningView.visibility = View.GONE
            }
        }

        //広告
        ad01.loadAd(AdRequest.Builder().build())

        //チップ500未満時処理
        addChipView.setOnClickListener {}
        backToMenu.setOnClickListener {
            //チップ初期化
            setChip(FIRSTCHIP)
            backToMenu.visibility = View.GONE
            timerRunningView.visibility = View.VISIBLE
            addChipView.visibility = View.GONE
            startPrcs()
        }

        //各BET
        start500Bet.setOnClickListener {
            startActivity(intentFor<MainActivity>("BET_CHIP" to BET1))
            finish()
        }
        start1000Bet.setOnClickListener {
            startActivity(intentFor<MainActivity>("BET_CHIP" to BET2))
            finish()
        }
        start5000Bet.setOnClickListener {
            startActivity(intentFor<MainActivity>("BET_CHIP" to BET3))
            finish()
        }


////////test用////////
        ////////////////
        ////chipを1,000,000にする
        debagChipEq1M.setOnClickListener {
            //自身のチップデータの読みこみ
            setChip(DEBAGMANYCHIP)
            startPrcs()
        }
        ////chipの格納されたプリファレンスを空にする
        debagChipClear.setOnClickListener {
            val chip = loadChip()
            if (chip.notEmpty()) {
                removePref()
            }
            startPrcs()
        }
        ////チップを0にする
        debagChipEq0.setOnClickListener {
            val chip = loadChip()
            if (chip.notEmpty()) {
                setChip(0)
            }
            startPrcs()
        }
////////////////////


    }

    /**
     * タイマーの作成
     */
    val handler = Handler()
    val dataFormat = SimpleDateFormat("s", Locale.US)
    val waiting = 15
    var count = 0
    val period = 1000
    private val runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            count++
            chargeTime.text = (waiting - (dataFormat.format(count * period).toInt())).toString()
            if (count > waiting) {
                timerRunningView.visibility = View.GONE
                chargeText.text = "Charge Complete !!"
                backToMenu.visibility = View.VISIBLE
                count = 0
                return
            }
            handler.postDelayed(this, period.toLong())
        }
    }

    /**
     * 画面読込処理
     */
    @SuppressLint("SetTextI18n")
    private fun startPrcs() {
        //自身のチップデータの読みこみ
        var chip = loadChip()

        //プリファレンスがないときは初期値を入れる
        if (chip.isEmpty()) {
            setChip(FIRSTCHIP)
            chip = loadChip()
        }

        //チップ所持数で掛けられるBETの処理
        start500Bet.isEnabled = chip >= BET1
        start1000Bet.isEnabled = chip >= BET2
        start5000Bet.isEnabled = chip >= BET3

        //各BETが掛けられないときの処理
        if (!start500Bet.isEnabled) {
            start500Bet.text = "chipOver\n$BET1"
            //チップ追加処理
            addChipView.visibility = View.VISIBLE
            //タイマー開始
            handler.post(runnable)
        } else {
            start500Bet.text = "$BET1"
        }
        if (!start1000Bet.isEnabled) {
            start1000Bet.text = "chipOver\n$BET2"
        } else {
            start1000Bet.text = "$BET2"
        }
        if (!start5000Bet.isEnabled) {
            start5000Bet.text = "chipOver\n$BET3"
        } else {
            start5000Bet.text = "$BET3"
        }

        //TODO 用途確認
        nowChipStartMenu.text = chip.toString()
    }
}

private fun Intent.putExtra(name: String, value: Chip) {
    this.putExtra(name, value.num)
}

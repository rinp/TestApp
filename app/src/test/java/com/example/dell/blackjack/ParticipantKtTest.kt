package com.example.dell.blackjack

import android.widget.LinearLayout
import com.example.dell.blackjack.domain.Hand
import com.example.dell.blackjack.domain.Trump
import org.junit.Assert
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as cis

class ParticipantKtTest {
    @Test
    fun calcptTest1() {
        val one = Hand(LinearLayout(null), Trump("", 1), false)
        val result = calcScore(listOf(one))

        Assert.assertThat(result, cis(11))
    }

    @Test
    fun calcptTest2() {
        val one = Hand(LinearLayout(null), Trump("", 1), false)
        val result = calcScore(listOf(one, one))

        Assert.assertThat(result, cis(12))
    }

    @Test
    fun calcptTest3() {
        val one = Hand(LinearLayout(null), Trump("", 6), false)
        val six = Hand(LinearLayout(null), Trump("", 1), false)
        val result = calcScore(listOf(one, six))

        Assert.assertThat(result, cis(17))
    }

    @Test
    fun calcptTest4() {
        val one = Hand(LinearLayout(null), Trump("", 6), false)
        val six = Hand(LinearLayout(null), Trump("", 1), false)
        val result = calcScore(listOf(six, one, six, six))

        Assert.assertThat(result, cis(19))
    }


}
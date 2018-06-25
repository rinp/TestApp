package com.example.dell.blackjack.userCase

import io.reactivex.Scheduler

interface PostExecutionThread {
    val scheduler: Scheduler
}
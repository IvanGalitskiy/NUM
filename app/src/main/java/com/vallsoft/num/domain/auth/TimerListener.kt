package com.vallsoft.num.domain.auth

interface TimerListener {
    fun onTimerStart(initTimeout:Long)
    fun onTimerChanged(changedTimeout:Long)
    fun onTimerStop()
}
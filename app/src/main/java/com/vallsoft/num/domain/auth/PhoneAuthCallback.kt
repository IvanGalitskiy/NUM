package com.vallsoft.num.domain.auth

interface PhoneAuthCallback {
    fun onLoginSuccess(phone:String)
    fun onLoginFailed()
    fun onNeedSendSms()
}
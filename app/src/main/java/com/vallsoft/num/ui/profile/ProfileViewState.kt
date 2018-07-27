package com.vallsoft.num.ui.profile

sealed class ProfileViewState {

    data class Profile(var editing: Boolean) : ProfileViewState()
    data class SmsCode(var isCodeInput:Boolean) : ProfileViewState()
}
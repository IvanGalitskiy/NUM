package com.vallsoft.num.presentation.profile

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.squareup.picasso.Picasso
import com.vallsoft.num.data.API.FirebaseDatabaseHelper
import com.vallsoft.num.data.AvatarStorage
import com.vallsoft.num.data.UserCache
import com.vallsoft.num.data.database.UserPreference
import com.vallsoft.num.domain.auth.FirebaseAuthManager
import com.vallsoft.num.domain.auth.PhoneAuthCallback
import com.vallsoft.num.domain.auth.TimerListener
import com.vallsoft.num.domain.utils.User
import com.vallsoft.num.presentation.SimpleBasePresenterImpl
import io.reactivex.android.schedulers.AndroidSchedulers


class ProfilePresenterImpl constructor(private var mPrefernce: UserPreference, private val userCache: UserCache)
    : SimpleBasePresenterImpl<ProfilePresenter.View>(), ProfilePresenter.Presenter, TimerListener , PhoneAuthCallback{


    private var firebaseAuthManager: FirebaseAuthManager =
            FirebaseAuthManager.getInstance()

    val database = FirebaseDatabaseHelper { u, _ ->
        userCache.user = u
        mView?.hideProgress()
        mView.displayUser(u)
    }
    val avatarStorage = AvatarStorage()

    init {
        firebaseAuthManager.timerListener = this
        firebaseAuthManager.authCallback = this
    }

    override fun sendMessageAgain(activity: Activity?) {
        firebaseAuthManager.sendSmsAgain(activity!!)
    }

    override fun updatePhone(phone: String?, activity: Activity) {
        firebaseAuthManager.attachPhoneNumber(phone!!, activity)
    }

    override fun updatePhoneWithSmsCode(smsCode: String?) {
        firebaseAuthManager.attachPhoneWithSmsCode(smsCode!!)
                .subscribe({
                    mPrefernce.phoneNumber = firebaseAuthManager.lastUsedPhone
                    mView?.onPhoneAttachSuccess(firebaseAuthManager.lastUsedPhone)
                    getProfile(firebaseAuthManager.lastUsedPhone!!, false)
                }, {
                    mView?.onWrongCodeSend()
                })

    }


    override fun getPhoneNumber() {
        mView?.displayPhoneNumber(mPrefernce.phoneNumber)
    }


    override fun getProfile(phone: String, getFromCache:Boolean) {
        //database.getUserByPhone(phone)
        mView?.showProgress()
        if (userCache.user != null && getFromCache) {
            userCache.user?.phone = phone
            mView?.hideProgress()
            mView?.displayUser(userCache.user)
        } else {
            database.getUserByPhone(phone)
        }
    }

    override fun updateUser(user: User?) {
        database.updateUserByPhone(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    userCache.user = user
                    mView?.onUpdateSuccess()
                }, {
                    mView?.onUpdateFail()
                })
    }
    override fun removeProfile() {
        userCache.user = User()
        mPrefernce.phoneNumber = ""
        database.removeProfile(mPrefernce.phoneNumber)
        mView.displayUser(User())
    }
    override fun onLoginSuccess(phone: String) {
        mView.onPhoneAttachSuccess(phone)
        mPrefernce.phoneNumber = phone
        getProfile(phone, false)
    }

    override fun onLoginFailed() {
        mView?.onPhoneAttachFailed()
    }

    override fun onNeedSendSms() {
        mView?.onNeedDisplayCode()
    }

    override fun updateAvatar(bitmap: Bitmap?) {
        var imagePath:String? =null
        avatarStorage
                .uploadAvatar(bitmap, mPrefernce.phoneNumber)
                .doOnSuccess{imagePath = it}
                .flatMapCompletable { database.updateAvatar(it,mPrefernce.phoneNumber) }
                .subscribe({
                    userCache.user?.avatar = imagePath
                },{})
    }

    override fun updateAvatar(bitmap: Uri?) {
        var imagePath:String? =null
        avatarStorage
                .uploadAvatar(bitmap, mPrefernce.phoneNumber)
                .doOnSuccess{imagePath = it}
                .flatMapCompletable { database.updateAvatar(it,mPrefernce.phoneNumber) }
                .subscribe({
                    userCache.user?.avatar = imagePath
                    Picasso.get().load(imagePath)
                },{})
    }


    override fun onTimerChanged(changedTimeout: Long) {
        mView?.onTimerUpdate(changedTimeout)
    }

    override fun onTimerStop() {
        mView?.onTimerStop()
    }

    override fun onTimerStart(initTimeout: Long) {
    }


}
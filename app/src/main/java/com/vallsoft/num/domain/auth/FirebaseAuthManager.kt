package com.vallsoft.num.domain.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.vallsoft.num.data.AvatarStorage
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class FirebaseAuthManager private constructor() : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

    companion object {
        val SMS_CODE_TIMEOUT = 60L
        private var instance: FirebaseAuthManager? = null
        fun getInstance(): FirebaseAuthManager {
            if (instance == null) {
                instance = FirebaseAuthManager()
            }
            return instance!!
        }
    }

    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    var timerListener: TimerListener? = null
    var authCallback:PhoneAuthCallback? =null

    var timeout: Long = SMS_CODE_TIMEOUT
    var timerSubject = BehaviorSubject.create<Long>()
    var timerObservable = Observable.interval(1, TimeUnit.SECONDS).take(timeout).observeOn(AndroidSchedulers.mainThread())
    var lastUsedPhone: String? = null


    fun attachPhoneNumber(phone: String, activity: Activity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                SMS_CODE_TIMEOUT,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                this)
        lastUsedPhone = phone
    }

    fun sendSmsAgain(activity: Activity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                lastUsedPhone!!,        // Phone number to verify
                SMS_CODE_TIMEOUT,       // Timeout duration
                TimeUnit.SECONDS,       // Unit of timeout
                activity,               // Activity (for callback binding)
                this)
    }

    fun attachPhoneWithSmsCode(code: String): Completable {
        return Completable.create { emitter ->
            run {
                val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnFailureListener { emitter.onError(it) }
                        .addOnSuccessListener {
                            emitter.onComplete()
                        }
            }
        }
    }


    override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
        authCallback?.onLoginSuccess(lastUsedPhone!!)
    }

    override fun onVerificationFailed(p0: FirebaseException?) {
        authCallback?.onLoginFailed()
    }

    override fun onCodeSent(verificationId: String?, resendToken: PhoneAuthProvider.ForceResendingToken?) {
        super.onCodeSent(verificationId, resendToken)
        mVerificationId = verificationId
        mResendToken = resendToken

        authCallback?.onNeedSendSms()

        timeout = SMS_CODE_TIMEOUT

        timerObservable
                .doOnSubscribe { timerListener?.onTimerStart(timeout) }
                .doFinally {timerListener?.onTimerStop()}
                .subscribe {
                    timeout-=1
                    timerListener?.onTimerChanged(timeout)
                }
    }


}
package com.vallsoft.num.domain.billing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vallsoft.num.presentation.BillingPresenter

class BillingListener :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val billingPresenter = BillingPresenter.getInstance(context!!)
        billingPresenter.checkSubscription()
    }

}
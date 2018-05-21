package com.vallsoft.num.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.vallsoft.num.R
import kotlinx.android.synthetic.main.dialog_ads_subscription.view.*

class AdsDialog(context: Context) : AlertDialog(context) {


    lateinit var mView: View
    var callback: Callback? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_ads_subscription, null)
        setContentView(mView)
        mView.vYes.setOnClickListener { callback?.onYes() }
        mView.vNo.setOnClickListener { callback?.onNo() }
        setOnDismissListener { callback?.onDismiss() }
        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun showProgress(b: Boolean) {
        mView.vYes.visibility = if (b) View.INVISIBLE else View.VISIBLE
        mView.vNo.visibility = if (b) View.INVISIBLE else View.VISIBLE
        mView.vMessage.visibility = if (b) View.INVISIBLE else View.VISIBLE
        mView.vProgress.visibility = if (b) View.VISIBLE else View.INVISIBLE
    }

    interface Callback {
        fun onYes()
        fun onNo()
        fun onDismiss()
    }
}

package com.vallsoft.num.domain.ads

import android.app.Activity
import android.util.Log
import android.view.View
import com.adcolony.sdk.*
import com.vallsoft.num.domain.ads.Constants.AdColony.Companion.ZONE_ID

class AdsManager(activity: Activity) {
    var adv: AdColonyInterstitial? = null
    private var listener: AdColonyInterstitialListener? = null
    private var ad_options: AdColonyAdOptions? = null
    private val TAG = "AdsManager"
    init {
        /** Construct optional app options object to be sent with configure */
        val app_options = AdColonyAppOptions()
                .setUserID("unique_user_id")

        /**
         * Configure AdColony in your launching Activity's onCreate() method so that cached ads can
         * be available as soon as possible.
         */
        AdColony.configure(activity, app_options, Constants.AdColony.APP_ID, ZONE_ID)

        /** Optional user metadata sent with the ad options in each request */
        val metadata = AdColonyUserMetadata()
                .setUserAge(26)
                .setUserEducation(AdColonyUserMetadata.USER_EDUCATION_BACHELORS_DEGREE)
                .setUserGender(AdColonyUserMetadata.USER_MALE)

        ad_options = AdColonyAdOptions()
                .enableConfirmationDialog(true)
                .enableResultsDialog(true)
                .setUserMetadata(metadata)

        /** Create and set a reward listener */
        AdColony.setRewardListener {
            /** Query reward object for info here  */
            /** Query reward object for info here  */
            Log.d(TAG, "onReward")
        }

        listener = object : AdColonyInterstitialListener() {
            /** Ad passed back in request filled callback, ad can now be shown  */
            override fun onRequestFilled(ad: AdColonyInterstitial) {
                adv = ad
//                show_button.setEnabled(true)
//                progress.setVisibility(View.INVISIBLE)
                Log.d(TAG, "onRequestFilled")
            }

            /** Ad request was not filled  */
            override fun onRequestNotFilled(zone: AdColonyZone?) {
               // progress.setVisibility(View.INVISIBLE)
                Log.d(TAG, "onRequestNotFilled")
            }

            /** Ad opened, reset UI to reflect state change  */
            override fun onOpened(ad: AdColonyInterstitial?) {
//                show_button.setEnabled(false)
//                progress.setVisibility(View.VISIBLE)
                Log.d(TAG, "onOpened")
            }

            /** Request a new ad if ad is expiring  */
            override fun onExpiring(ad: AdColonyInterstitial?) {
//                show_button.setEnabled(false)
//                progress.setVisibility(View.VISIBLE)
                AdColony.requestInterstitial(ZONE_ID, this, ad_options)
                Log.d(TAG, "onExpiring")
            }
        }
    }
    fun onResume(){
        if (adv == null || adv!!.isExpired)
        {
            /**
             * Optionally update location info in the ad options for each request:
             * LocationManager location_manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
             * Location location = location_manager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
             * ad_options.setUserMetadata( ad_options.getUserMetadata().setUserLocation( location ) );
             */
           // progress.setVisibility( View.VISIBLE );
            AdColony.requestInterstitial( ZONE_ID, listener!!, ad_options );
        }
    }
    fun show(){
        adv!!.show()
    }
}
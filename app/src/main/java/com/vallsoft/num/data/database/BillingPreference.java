package com.vallsoft.num.data.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import java.util.HashMap;

public class BillingPreference {
    public static final String FILENAME = "billing";

    private SecurePreferences preferences;
    public static final String BILLING_GRANTED = "isGranted";

    public static final String SUBSCRIPTION_DATE="subscription";


    public BillingPreference(Context context) {
        preferences  = new SecurePreferences(context, "userpassword",
                "my_user_prefs.xml");
    }
    public void setBillingGranted(boolean isGranted){
        preferences.edit()
                .putBoolean(BILLING_GRANTED,isGranted)
                .apply();
    }
    public void setSubscriptionDate(long date)  {
        SecurePreferences.Editor editor = preferences.edit();
        editor.putLong(SUBSCRIPTION_DATE,date);
        editor.apply();
    }
    public long getSubscriptionDate(){
        return preferences.getLong(SUBSCRIPTION_DATE,-1);
    }
    public boolean getBillingGranted(){
        return preferences.getBoolean(BILLING_GRANTED,false);
    }

}

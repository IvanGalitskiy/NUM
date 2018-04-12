package com.vallsoft.num.data.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class BillingPreference {
    public static final String FILENAME = "billing";


    public static final String BILLING_GRANTED = "isGranted";


    private SharedPreferences preferences;

    public BillingPreference(Context context) {
        preferences = context.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);
    }
    public void setBillingGranted(boolean isGranted){
        preferences.edit()
                .putBoolean(BILLING_GRANTED,isGranted)
                .apply();
    }
    public boolean getBillingGranted(){
        return preferences.getBoolean(BILLING_GRANTED,false);
    }

}

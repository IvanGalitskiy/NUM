package com.vallsoft.num.data.database;

import android.content.Context;

public class UserPreference extends Preference {
    private final String PHONE_NUMBER = "phone";
    public UserPreference(Context context) {
        super(context);
    }

    public void setPhoneNumber(String phone){
        mPreference.edit()
                .putString(PHONE_NUMBER, phone)
                .apply();
    }

    public String getPhoneNumber(){
        return mPreference.getString(PHONE_NUMBER, //"380985866052" +
                "");
    }

    @Override
    protected String getFilename() {
        return "user";
    }
}

package com.vallsoft.num.data.database;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class Preference {
    protected SharedPreferences mPreference;

    public Preference(Context context) {
        mPreference = context.getSharedPreferences(getFilename(),Context.MODE_PRIVATE);
    }

    protected abstract String getFilename();
}

package com.vallsoft.num.data.database;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SettingsPreference {
    public static final String FILENAME = "settings";
    public static final String OPERATOR = "operator";
    public static final String REGION = "region";
    public static final String NAME = "name";
    public static final String AVATAR = "avatar";
    public static final String CATEGORY = "category";
    public static final String COUNTRY = "country";
    public static final String NAMEGROUP = "namegroup";
    public static final String LANGUAGE = "language";

    public static final String POSITIONOFMESSAGE = "positionofmessage";

    public static final String IS_PRIVACY_ACCEPTED = "privacy_accepted";

    private SharedPreferences preferences;

    public SettingsPreference(Context context) {
        preferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    public void saveSettings(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public HashMap<String, Boolean> getAllSettings() {
        HashMap<String, Boolean> map = new HashMap<>();
        map.put(OPERATOR, preferences.getBoolean(OPERATOR, true));
        map.put(REGION, preferences.getBoolean(REGION, true));
        map.put(NAME, preferences.getBoolean(NAME, true));
        map.put(AVATAR, preferences.getBoolean(AVATAR, true));
        map.put(CATEGORY, preferences.getBoolean(CATEGORY, true));
        map.put(COUNTRY, preferences.getBoolean(COUNTRY, true));
        map.put(NAMEGROUP, preferences.getBoolean(NAMEGROUP, true));
        return map;
    }

    public void saveMessagePos(int topMargin) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(POSITIONOFMESSAGE, topMargin);
        editor.apply();
    }

    public int getPositionOfMessage() {
        return preferences.getInt(POSITIONOFMESSAGE, 0);
    }

    /*сохранение ответа пользователя на политику конфидециальности */
    public void savePrivacyStatus(boolean accepted) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_PRIVACY_ACCEPTED, accepted);
        editor.apply();
    }

    /* принял ли  политику пользователь*/
    public boolean isPrivacyAccepted() {
        return preferences.getBoolean(IS_PRIVACY_ACCEPTED, false);
    }

    public void changeLanguage(String localeString) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE, localeString);
        editor.apply();
    }

    public String getCurrentLanguage() {
        return preferences.getString(LANGUAGE, "EN");
    }
}

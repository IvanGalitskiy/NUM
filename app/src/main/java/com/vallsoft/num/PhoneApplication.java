package com.vallsoft.num;

import android.app.Application;
import android.provider.Settings;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import io.fabric.sdk.android.Fabric;


public class PhoneApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Инициализируем базу данных
        MultiDex.install(this);
        Fabric.with(this, new Crashlytics());
    }
}

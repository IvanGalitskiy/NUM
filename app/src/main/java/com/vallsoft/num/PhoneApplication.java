package com.vallsoft.num;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.vallsoft.num.data.database.SettingsPreference;
import com.vallsoft.num.domain.billing.AlarmManagerSubscription;
import com.vallsoft.num.domain.calls.CallReceiver;
import com.vallsoft.num.presentation.LanguagePresenter;

import java.util.Set;
import java.util.UUID;

import io.fabric.sdk.android.Fabric;


public class PhoneApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Инициализируем базу данных
        LanguagePresenter.getInstance(new SettingsPreference(this)).loadLanguage(this);

        MultiDex.install(this);

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }


        final String defValue ="empty";
        SettingsPreference preference = new SettingsPreference(this);
        // если ранее не был установлен идентификатор устройства
        if (!preference.getDeviceId(defValue).equals(defValue)){
            // генерируем уникальный идентификатор телефона
            preference.saveSettings(SettingsPreference.DEVICE_ID, UUID.randomUUID().toString());
        }
    }
    public void enableBroadcastReceiver() {
        ComponentName receiver = new ComponentName(this, CallReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void disableBroadcastReceiver() {
        ComponentName receiver = new ComponentName(this, CallReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}

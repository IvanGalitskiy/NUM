package com.vallsoft.num.presentation;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.vallsoft.num.data.database.SettingsPreference;
import com.vallsoft.num.presentation.view.ILanguageView;

import java.util.Locale;

public class LanguagePresenter {
    private SettingsPreference mRepository;
    private ILanguageView view;

    public LanguagePresenter(SettingsPreference mRepository) {
        this.mRepository = mRepository;
    }

    public void changeLanguage(Context context, String localeString) {
        mRepository.changeLanguage(localeString);
        changeLang(context, localeString);
    }

    public void loadLanguage(Context context) {
        String lang = mRepository.getCurrentLanguage();
        changeLang(context, lang);

    }

    private void changeLang(Context context, String lang) {
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang.toLowerCase())); // API 17+ only.
        context.createConfigurationContext(conf);
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);

        view.onLanguageChanged();
    }
}

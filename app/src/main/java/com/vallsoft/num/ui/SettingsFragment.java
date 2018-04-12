package com.vallsoft.num.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.vallsoft.num.R;
import com.vallsoft.num.data.database.SettingsPreference;
import com.vallsoft.num.presentation.BillingPresenter;
import com.vallsoft.num.presentation.view.IBillingView;

import java.util.HashMap;


public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, IBillingView {
    private SwitchCompat vRegion, vOperator, vName,vNamegroup,vAvatar,vCategory,vCountry;
    private SettingsPreference preference;
    private BillingPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        vOperator = v.findViewById(R.id.operator_switch);
        vRegion = v.findViewById(R.id.region_switch);
        vName = v.findViewById(R.id.name_switch);
        vNamegroup = v.findViewById(R.id.namegroup_switch);
        vAvatar = v.findViewById(R.id.avatar_switch);
        vCategory = v.findViewById(R.id.category_switch);
        vCountry = v.findViewById(R.id.country_switch);

        vOperator.setOnCheckedChangeListener(this);
        vRegion.setOnCheckedChangeListener(this);
        vName.setOnCheckedChangeListener(this);
        vNamegroup.setOnCheckedChangeListener(this);
        vAvatar.setOnCheckedChangeListener(this);
        vCategory.setOnCheckedChangeListener(this);
        vCountry.setOnCheckedChangeListener(this);

        preference = new SettingsPreference(getActivity());

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = BillingPresenter.getInstance(getActivity());
        presenter.attachView(this);
    }

    @Override
    public void onStop() {
        presenter.detachView(this);
        super.onStop();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String key = null;
        switch (buttonView.getId()) {
            case R.id.operator_switch:
                key = SettingsPreference.OPERATOR;
                break;
            case R.id.region_switch:
                key = SettingsPreference.REGION;
                break;
            case R.id.name_switch:
                key = SettingsPreference.NAME;
                break;
            case R.id.namegroup_switch:
                key = SettingsPreference.NAMEGROUP;
                break;
            case R.id.country_switch:
                key = SettingsPreference.COUNTRY;
                break;
            case R.id.category_switch:
                key = SettingsPreference.CATEGORY;
                break;
            case R.id.avatar_switch:
                key = SettingsPreference.AVATAR;
                break;
            default:
                break;
        }
        preference.saveSettings(key, isChecked);
    }

    @Override
    public void subscriptionGranted() {
        HashMap<String, Boolean> settings = preference.getAllSettings();
        vOperator.setChecked(settings.get(SettingsPreference.OPERATOR));
        vRegion.setChecked(settings.get(SettingsPreference.REGION));
        vName.setChecked(settings.get(SettingsPreference.NAME));
        vNamegroup.setChecked(settings.get(SettingsPreference.NAMEGROUP));
        vCountry.setChecked(settings.get(SettingsPreference.COUNTRY));
        vCategory.setChecked(settings.get(SettingsPreference.CATEGORY));
        vAvatar.setChecked(settings.get(SettingsPreference.AVATAR));

        setEnabledFunctionality(true);
    }

    @Override
    public void subscriptionDenied() {
        setEnabledFunctionality(false);
    }

    private void setEnabledFunctionality(boolean isEnabled) {
        HashMap<String, Boolean> settings = preference.getAllSettings();
        vOperator.setChecked(settings.get(SettingsPreference.OPERATOR));
        vRegion.setChecked(settings.get(SettingsPreference.REGION));
        vCategory.setEnabled(isEnabled);
        vNamegroup.setEnabled(isEnabled);
        vCountry.setEnabled(isEnabled);
        vName.setEnabled(isEnabled);
        vAvatar.setEnabled(isEnabled);

        if (!isEnabled){
            vCategory.setChecked(false);
            vNamegroup.setChecked(false);
            vCountry.setChecked(false);
            vName.setChecked(false);
            vAvatar.setChecked(false);
        }
    }
}

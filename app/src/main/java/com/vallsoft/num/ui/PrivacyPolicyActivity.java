package com.vallsoft.num.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.vallsoft.num.PhoneApplication;
import com.vallsoft.num.R;
import com.vallsoft.num.data.database.SettingsPreference;

import java.util.List;


public class PrivacyPolicyActivity extends AppCompatActivity implements View.OnClickListener, MultiplePermissionsListener {
    private CardView vAcceptPrivacy;
    private SettingsPreference preference;
    private RewardedVideoAd mRewardedVideoAd;
    private final int OVERLAY_REQUEST = 1;

    private boolean permissionGranted = false, overlayGranted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = new SettingsPreference(this);
        if (preference.isPrivacyAccepted()) {
            goToMain();
        } else {
            setContentView(R.layout.activity_privacy_policy);
            ImageView vTel = findViewById(R.id.num_text);
            ImageView vNum = findViewById(R.id.tel);
            Picasso.get()
                    .load(R.drawable.num)
                    .into(vTel);
            Picasso.get()
                    .load(R.drawable.tel)
                    .into(vNum);
            vAcceptPrivacy = findViewById(R.id.accept_privacy);
            vAcceptPrivacy.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept_privacy:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_REQUEST);
                    } else {
                        overlayGranted = true;
                    }
                } else {
                    overlayGranted = true;
                    if (permissionGranted) {
                        goToMain();
                    }
                }
                Dexter.withActivity(this)
                        .withPermissions(
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.PROCESS_OUTGOING_CALLS,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_CONTACTS)
                        .withListener(this)
                        .check();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OVERLAY_REQUEST:
                if (!Settings.canDrawOverlays(this)) {
                    ((PhoneApplication) getApplication()).disableBroadcastReceiver();
                    finish();
                }
                if (permissionGranted) {
                    preference.savePrivacyStatus(true);
                    goToMain();
                }
                break;
        }
    }

    private void goToMain() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainActivity);
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        permissionGranted = !report.isAnyPermissionPermanentlyDenied();
        if (overlayGranted && permissionGranted) {
            goToMain();
        }
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

    }
}

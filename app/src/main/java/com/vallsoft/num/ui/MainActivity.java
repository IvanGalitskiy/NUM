package com.vallsoft.num.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vallsoft.num.R;
import com.vallsoft.num.domain.calls.CallReceiver;
import com.vallsoft.num.presentation.BillingPresenter;
import com.vallsoft.num.presentation.view.IBillingView;

import java.util.List;


public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, IBillingView, MultiplePermissionsListener
       {

    private static final int PERMISION_REQUEST_CODE = 2;
    private BottomNavigationView vNav;
    private final int OVERLAY_REQUEST = 1;

    private BillingPresenter billingPresenter;
  //  private RewardedVideoAd mRewardedVideoAd;
    private boolean isStateLoss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vallsoft.num.R.layout.activity_main);
        vNav = findViewById(R.id.navigation);
        vNav.setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null && !isStateLoss) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new SettingsFragment())
                    .commit();
        }

        // Дальше в этом методе заполнение программы данных для тестов

        //    сохраняем пользователя с известным нам номером

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_REQUEST);
            }
        }

        billingPresenter = BillingPresenter.getInstance(this);
        billingPresenter.attachView(this);
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS)
                .withListener(this)
                .check();
        MobileAds.initialize(this, "ca-app-pub-6822678516999860~3405197048");
//
//        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
//        mRewardedVideoAd.setRewardedVideoAdListener(this);
//        mRewardedVideoAd.loadAd("ca-app-pub-6822678516999860/4520020827",
//                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
//        Log.d("qwe", mRewardedVideoAd.isLoaded() + "");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        isStateLoss = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  mRewardedVideoAd.resume(this);
        isStateLoss = false;
    }

    @Override
    protected void onStop() {
        billingPresenter.detachView(this);
        super.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (!isStateLoss) {
            switch (item.getItemId()) {
                case R.id.settings:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new SettingsFragment())
                            .commit();
                    break;
                case R.id.pos:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new FragmentPositionPicker())
                            .commit();
                    break;
//                case R.id.language:
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.container, new LanguageFragment())
//                            .commit();
//                    break;
            }
        }
        return true;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OVERLAY_REQUEST:
                if (!Settings.canDrawOverlays(this)) {
                    disableBroadcastReceiver();
                    finish();
                }
                break;
        }
    }

    @Override
    public void subscriptionGranted() {

    }

    @Override
    public void subscriptionDenied() {

    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        if (report.isAnyPermissionPermanentlyDenied()) {
            finish();
        }
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
        Log.d("qwe", "should be shown");
    }

//    @Override
//    public void onRewardedVideoAdLoaded() {
//        Log.d("qwe", "loaded");
//        mRewardedVideoAd.show();
//
//    }
//
//    @Override
//    public void onRewardedVideoAdOpened() {
//        Log.d("qwe", "opened");
//    }
//
//    @Override
//    public void onRewardedVideoStarted() {
//        Log.d("qwe", "started");
//    }
//
//    @Override
//    public void onRewardedVideoAdClosed() {
//        Log.d("qwe", "closed");
//    }
//
//    @Override
//    public void onRewarded(RewardItem rewardItem) {
//        Log.d("qwe", "rewarded");
//    }
//
//    @Override
//    public void onRewardedVideoAdLeftApplication() {
//        Log.d("qwe", "left application");
//    }
//
//    @Override
//    public void onRewardedVideoAdFailedToLoad(int i) {
//        Log.d("qwe", "failed to load" + i);
//    }
//
//    @Override
//    public void onRewardedVideoCompleted() {
//        Log.d("qwe", "completed");
//    }
}

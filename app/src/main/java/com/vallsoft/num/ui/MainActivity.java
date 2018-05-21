package com.vallsoft.num.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyUserMetadata;
import com.adcolony.sdk.AdColonyZone;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.jirbo.adcolony.AdColonyAdapter;
import com.jirbo.adcolony.AdColonyBundleBuilder;
import com.vallsoft.num.PhoneApplication;
import com.vallsoft.num.R;
import com.vallsoft.num.data.database.SettingsPreference;
import com.vallsoft.num.domain.ads.AdsManager;
import com.vallsoft.num.domain.billing.AlarmManagerSubscription;
import com.vallsoft.num.presentation.BillingPresenter;
import com.vallsoft.num.presentation.LanguagePresenter;
import com.vallsoft.num.presentation.view.IBillingView;
import com.vallsoft.num.presentation.view.ILanguageView;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, IBillingView,
        RewardedVideoAdListener, ILanguageView {

    private static final int PERMISION_REQUEST_CODE = 2;
    private BottomNavigationView vNav;

    private BillingPresenter billingPresenter;
    private RewardedVideoAd mRewardedVideoAd;
    private boolean isStateLoss;
    private PhoneApplication app;
    private LanguagePresenter languagePresenter;
    private ProgressBar vProgress;
    private FrameLayout vContainer;
    private IProgressListener progressListener;
    private AdsManager adsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.vallsoft.num.R.layout.activity_main);
        app = (PhoneApplication) getApplication();
        vNav = findViewById(R.id.navigation);
        vProgress = findViewById(R.id.progress);
        vContainer = findViewById(R.id.container);
        vNav.setOnNavigationItemSelectedListener(this);
        languagePresenter = LanguagePresenter.getInstance(new SettingsPreference(this));
        languagePresenter.attach(this);
        languagePresenter.loadLanguage(this);
        if (savedInstanceState == null && !isStateLoss && !isFinishing()) {
            SettingsFragment settingsFragment = new SettingsFragment();
            progressListener = settingsFragment;
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, settingsFragment)
                    .commit();
        }

        // Дальше в этом методе заполнение программы данных для тестов

        //    сохраняем пользователя с известным нам номером


        billingPresenter = BillingPresenter.getInstance(this.getApplicationContext());
        billingPresenter.attachView(this);


        MobileAds.initialize(this, "ca-app-pub-6822678516999860~3405197048");

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        adsManager = new AdsManager(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        isStateLoss = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRewardedVideoAd.resume(this);
        isStateLoss = false;
        adsManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        billingPresenter.detachView(this);
        mRewardedVideoAd.destroy(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (!isStateLoss && !isFinishing()) {
            switch (item.getItemId()) {
                case R.id.settings:
                    SettingsFragment settingsFragment = new SettingsFragment();
                    progressListener = settingsFragment;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, settingsFragment)
                            .commit();
                    break;
                case R.id.profile:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new ProfileFragment())
                            .commit();
                    break;
                case R.id.pos:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new FragmentPositionPicker())
                            .commit();
                    break;
                case R.id.language:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new LanguageFragment())
                            .commit();
                    break;
            }
        }
        return true;
    }

    public void showAds() {
         //adsManager.show();

        mRewardedVideoAd.loadAd("ca-app-pub-6822678516999860/4520020827",
                new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdColonyAdapter.class, AdColonyBundleBuilder.build())
                        //.addTestDevice("25B90DCB96FAFFD2D192D15F9997F8FA")
                        .build());
        Disposable d = null;
        final Disposable finalD = d;
        d = Observable.interval(500, TimeUnit.MILLISECONDS)
                .take(2 * 30)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (mRewardedVideoAd.isLoaded()) {
                            mRewardedVideoAd.show();
                            finalD.dispose();
                            progressListener.hideProgress();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                    }
                });


    }

    @Override
    public void subscriptionGranted() {
        AlarmManagerSubscription.Companion.getInstance(this).startAlarm();
    }

    @Override
    public void subscriptionDenied() {
        AlarmManagerSubscription.Companion.getInstance(this).startAlarm();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d("qwe", "loaded");
//        mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d("qwe", "opened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d("qwe", "started");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d("qwe", "closed");
        progressListener.hideProgress();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d("qwe", "rewarded");
        billingPresenter.addSubscriptionForAds();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d("qwe", "left application");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d("qwe", "failed to load" + i);
        progressListener.hideProgress();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.d("qwe", "completed");
    }

    @Override
    public void onLanguageChanged() {
        vNav.getMenu().getItem(0).setTitle(R.string.settings);
        vNav.getMenu().getItem(1).setTitle(R.string.position);
        vNav.getMenu().getItem(2).setTitle(R.string.language);
    }
}

package com.vallsoft.num.presentation;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.vallsoft.num.data.database.BillingPreference;
import com.vallsoft.num.data.database.SettingsPreference;
import com.vallsoft.num.domain.billing.Constants;
import com.vallsoft.num.presentation.view.IBillingView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.disposables.Disposable;

import static com.vallsoft.num.util.CalendarUtilsKt.addSevenDays;

public class BillingPresenter implements PurchasesUpdatedListener, BillingClientStateListener {
    private BillingClient client;
    public static final String TAG = "BillingPresenter";
    private boolean isConnected = false;
    private List<Purchase> subscriptions;
    private Context context;
    private Set<IBillingView> views;
    public static BillingPresenter instance;
    public static SettingsPreference settingsPreference;

    public static final int COMMAND_GRANTED = 0;
    public static final int COMMAND_DECLINE = 1;
    private int lastCommand;
    private BillingPreference billingPreference;

    private Disposable checkingSubscribeState;

    public static BillingPresenter getInstance(Context context) {
        if (instance == null) {
            instance = new BillingPresenter(context);
        } else {
            instance.context = context;
        }
        return instance;
    }

    private BillingPresenter(Context context) {
        this.context = context;
        subscriptions = new ArrayList<>();
        views = new HashSet<>();
        client = BillingClient.newBuilder(context)
                .setListener(this)
                .build();
        client.startConnection(this);
        billingPreference = new BillingPreference(context);
        settingsPreference = new SettingsPreference(context);
    }


    private BillingPresenter(final Context context, IBillingView view) {
        this.context = context;
        subscriptions = new ArrayList<>();
        client = BillingClient.newBuilder(context)
                .setListener(this)
                .build();
        client.startConnection(this);
    }

    public void attachView(IBillingView view) {
        views.add(view);
        sendCommand(lastCommand, view);
    }

    public void detachView(IBillingView view) {
        views.remove(view);
    }

    @Override
    public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode,
                                   List<Purchase> purchases) {
        Log.d(TAG, "onPurchasesUpdated");
        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            if (!purchases.isEmpty()) {
                sendCommand(COMMAND_GRANTED);
            }
        } else if (subscriptions != null && !subscriptions.isEmpty()) {

        } else {
            sendCommand(COMMAND_DECLINE);
        }
    }

    @Override
    public void onBillingSetupFinished(int responseCode) {
        Purchase.PurchasesResult purchasesResult = client.queryPurchases(BillingClient.SkuType.SUBS);
        Log.d(TAG, "active - " + purchasesResult.getPurchasesList() + "");
        if (!isAdsDateGranted() &&
                (purchasesResult.getPurchasesList() == null ||
                        purchasesResult.getPurchasesList().isEmpty())
                && !views.isEmpty()) {
            if (!isAdsDateGranted()) {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSku(Constants.MONTH_SUBSCRIPTION)
                        .setType(BillingClient.SkuType.SUBS)
                        .build();
                if (context instanceof Activity)
                    client.launchBillingFlow((Activity) context, flowParams);
                else{
                    lastCommand = COMMAND_DECLINE;
                }
            } else {
                sendCommand(COMMAND_GRANTED);
            }
        } else {
            subscriptions = purchasesResult.getPurchasesList();
            sendCommand(COMMAND_GRANTED);
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        Log.d(TAG, "disconnected");
    }

    public void sendCommand(int command) {
        Iterator<IBillingView> iterator = views.iterator();
        while (iterator.hasNext()) {
            switch (command) {
                case COMMAND_GRANTED:
                    iterator.next().subscriptionGranted();
                    break;
                case COMMAND_DECLINE:
                    iterator.next().subscriptionDenied();
                    break;
            }
        }
        // При исполнении метода setBillingGranted возникает ошибка NullPointerException
        try {

            billingPreference.setBillingGranted(command == COMMAND_GRANTED);
        }
        catch (NullPointerException e){
            Log.d("Null pointer exp", e.getMessage());
        }
        lastCommand = command;
    }

    public void sendCommand(int command, IBillingView view) {
        switch (command) {
            case COMMAND_GRANTED:
                view.subscriptionGranted();
                break;
            case COMMAND_DECLINE:
                view.subscriptionDenied();
                break;
        }

    }

    public boolean checkSubscription() {
        boolean granted;
        if (!isAdsDateGranted()) {
            Purchase.PurchasesResult purchasesResult = client.queryPurchases(BillingClient.SkuType.SUBS);
            granted = purchasesResult.getPurchasesList() != null && !purchasesResult.getPurchasesList().isEmpty();
        } else {
            granted = true;
        }
        sendCommand(granted ? COMMAND_GRANTED : COMMAND_DECLINE);

        return granted;
    }

    public void addSubscriptionForAds() {
        billingPreference.setSubscriptionDate(addSevenDays(new Date()));
        sendCommand(COMMAND_GRANTED);
    }

    private boolean isAdsDateGranted() {
        if (billingPreference != null) {
            return billingPreference.getSubscriptionDate() > -1 &&
                    new Date(billingPreference.getSubscriptionDate()).after(new Date());
        }
        return false;
    }


}

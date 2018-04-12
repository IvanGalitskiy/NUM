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
import com.vallsoft.num.domain.billing.Constants;
import com.vallsoft.num.presentation.view.IBillingView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BillingPresenter implements PurchasesUpdatedListener, BillingClientStateListener {
    private BillingClient client;
    public static final String TAG = "BillingPresenter";
    private boolean isConnected = false;
    private List<Purchase> subscriptions;
    private Context context;
    private IBillingView view;
    private Set<IBillingView> views;
    public static BillingPresenter instance;

    private final int COMMAND_GRANTED =0;
    private final int COMMAND_DECLINE =1;
    private int lastCommand;
    private BillingPreference billingPreference;

    public static BillingPresenter getInstance(Context context) {
        if (instance == null) {
            instance = new BillingPresenter(context);
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
    }


    private BillingPresenter(final Context context, IBillingView view) {
        this.view = view;
        this.context = context;
        subscriptions = new ArrayList<>();
        client = BillingClient.newBuilder(context)
                .setListener(this)
                .build();
        client.startConnection(this);
    }

    public void attachView(IBillingView view) {
        views.add(view);
        switch (lastCommand) {
            case COMMAND_GRANTED:
                view.subscriptionGranted();
                break;
            case COMMAND_DECLINE:
                view.subscriptionDenied();
                break;
        }
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
        if (purchasesResult.getPurchasesList()==null ||
                purchasesResult.getPurchasesList().isEmpty()) {
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku(Constants.MONTH_SUBSCRIPTION)
                    .setType(BillingClient.SkuType.SUBS)
                    .build();
            client.launchBillingFlow((Activity) context, flowParams);
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
            billingPreference.setBillingGranted(command==COMMAND_GRANTED);
            lastCommand = command;
        }
    }
}

package com.vallsoft.num.domain.calls;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.vallsoft.num.data.database.BillingPreference;
import com.vallsoft.num.data.database.SettingsPreference;
import com.vallsoft.num.domain.utils.User;
import com.vallsoft.num.presentation.PhonePresenter;
import com.vallsoft.num.presentation.view.IUserDisplayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.vallsoft.num.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;


public class CallReceiver extends AbstractPhonecallReceiver implements IUserDisplayer {
    private Context context;
    private PhonePresenter presenter;
    private static List<AlertDialog> alertDialogs = new ArrayList<>();
    private SettingsPreference preference;
    private BillingPreference billingPreference;

    public CallReceiver() {

    }

    /**
     * Здесь обрабатываем события от звонков
     */
    // Звонит - запрашиваем пользователя
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        if (context == null) {
            presenter = new PhonePresenter(ctx, this);
            preference = new SettingsPreference(ctx);
            billingPreference = new BillingPreference(ctx);
        }
        context = ctx;
        if (number != null && !number.isEmpty())
            presenter.getUser(number.trim().replaceAll(" ", "").replaceAll("-", "")
                    .replaceAll("\\+", ""));
    }

    // Ответили - прячем окошко
    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        hideAlert();
    }

    // Закончили разговор - прячем окошко
    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        hideAlert();
    }

    // Пропустили - прячем окошко
    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        hideAlert();
    }

    // Исходящие вызовы не обрабатываются
    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }


    // Показываем окошко, когда получили пользователя
    @Override
    public void displayUser(User u, String source) {
        if (context != null) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = null;
            HashMap<String, Boolean> map = preference.getAllSettings();
            if (map.get(SettingsPreference.AVATAR) && u.getAvatar() != null && !u.getAvatar().isEmpty() &&
                    billingPreference.getBillingGranted()) {
                dialogView = inflater.inflate(R.layout.dialog_userinfo_withavatar, null);
                ImageView vAvatar = dialogView.findViewById(R.id.avatar);
                Picasso.get().load(u.getAvatar()).into(vAvatar);
            } else {
                dialogView = inflater.inflate(R.layout.dialog_userinfo, null);
            }
            AlertDialog alertDialog = dialogBuilder.create();

            alertDialog.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            alertDialog.setView(dialogView);
            TextView vOperator = dialogView.findViewById(R.id.operator);
            TextView vRegion = dialogView.findViewById(R.id.region);
            TextView vName = dialogView.findViewById(R.id.name);
            TextView vNamegroup = dialogView.findViewById(R.id.namegroup);
            TextView vCategory = dialogView.findViewById(R.id.category);
            TextView vCountry = dialogView.findViewById(R.id.country);
            TextView vAdditional = dialogView.findViewById(R.id.additionalInfo);
            TextView vAddress = dialogView.findViewById(R.id.address);

            if (map.get(SettingsPreference.OPERATOR) && u.getOperator() != null && !u.getOperator().isEmpty()) {
                vOperator.setText(u.getOperator());
            } else {
                vOperator.setVisibility(View.GONE);
            }
            if (map.get(SettingsPreference.NAME) && u.getName() != null && !u.getName().isEmpty()) {
                vName.setText(u.getName());
            } else {
                vName.setVisibility(View.GONE);
            }
            if (map.get(SettingsPreference.NAMEGROUP) && u.getNamegroup() != null && !u.getNamegroup().isEmpty()) {
                vNamegroup.setText(u.getNamegroup());
            } else {
                vNamegroup.setVisibility(View.GONE);
            }
            if (map.get(SettingsPreference.CATEGORY) && u.getCategory() != null && !u.getCategory().isEmpty()) {
                vCategory.setText(u.getCategory());
            } else {
                vCategory.setVisibility(View.GONE);
            }
            if (map.get(SettingsPreference.COUNTRY) && u.getCountry() != null && !u.getCountry().isEmpty()) {
                vCountry.setText(u.getCountry());
            } else {
                vCountry.setVisibility(View.GONE);
            }
            if (map.get(SettingsPreference.REGION) && u.getRegion() != null && !u.getRegion().isEmpty()) {
                vRegion.setText(u.getRegion());
            } else {
                vRegion.setVisibility(View.GONE);
            }
            if (map.get(SettingsPreference.ADDRESS) && u.getAddress() != null && !u.getAddress().isEmpty()) {
                vAddress.setText(u.getAddress());
            } else {
                vAddress.setVisibility(View.GONE);
            }
            if (vAdditional != null) {
                if (billingPreference.getBillingGranted()) {
                    vAdditional.setVisibility(View.GONE);
                } else {
                    vAdditional.setVisibility(View.VISIBLE);
                }
            }
            if (Build.VERSION.SDK_INT > 25) {
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            }


            WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();

            wmlp.y = preference.getPositionOfMessage();   //y position
            wmlp.x = WRAP_CONTENT;
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            wakeLock.acquire(60 * 1000L /*1 minute*/);
            KeyguardManager keyguardManager = (KeyguardManager) context.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
            keyguardLock.disableKeyguard();

            dialogView.requestLayout();
            dialogView.invalidate();

            if (!u.isEmpty()) {
                alertDialog.show();
                alertDialogs.add(alertDialog);
            }

        }
    }

    // Скрываем окошко
    private void hideAlert() {
        if (alertDialogs != null && alertDialogs.size() > 0) {
            for (AlertDialog dialog : alertDialogs) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
            alertDialogs.clear();
        }

    }


}

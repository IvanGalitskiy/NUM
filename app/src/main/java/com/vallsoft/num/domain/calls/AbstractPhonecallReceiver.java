package com.vallsoft.num.domain.calls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.Date;

/**
 * Клас для получения статуса телефона в контексте дзвонков
 * Т.е. вызов приходит, мы звоним, ничего не происходит и т.п.
 * Реализация обработчиков находится в класе CallReceiver
 */
public abstract class AbstractPhonecallReceiver extends BroadcastReceiver {
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;


    public AbstractPhonecallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // Мы слушаем два интента (события)
        // Новый исходяций звонок сообщает нам только номер телефона
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        }
        // иначе определяем тип звонка
        // здесь нас интересует только 3 пункт - RINGING, т.е. звонит
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }

    //Обработичик событий, реализованы в CallReceiver
    protected abstract void onIncomingCallReceived(Context ctx, String number, Date start);
    protected abstract void onIncomingCallAnswered(Context ctx, String number, Date start);
    protected abstract void onIncomingCallEnded(Context ctx, String number, Date start, Date end);

    protected abstract void onOutgoingCallStarted(Context ctx, String number, Date start);
    protected abstract void onOutgoingCallEnded(Context ctx, String number, Date start, Date end);

    protected abstract void onMissedCall(Context ctx, String number, Date start);

    //Определяем событие

    //Входящий вызов-  из состояния IDLE в  RINGING когда звонит, дальше OFFHOOK когда отвечено на звонок, к IDLE когда сброшен
    //Исходящий вызов -  из состояния IDLE в OFFHOOK когда идет набор, в IDLE когда сброшен
    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //Изменений не было - не обрабатываем
            return;
        }
        switch (state) {
            // Получаем входящий звонок
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                // Обрабатываем
                onIncomingCallReceived(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Переход из ringing->offhook являются датчиками входящих вызовов
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                else
                {
                    isIncoming = true;
                    callStartTime = new Date();
                    onIncomingCallAnswered(context, savedNumber, callStartTime);
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Конец звонка
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Пропущен
                    onMissedCall(context, savedNumber, callStartTime);
                }
                else if(isIncoming){
                    // Завершен
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                else{
                    // Завершен
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }
}

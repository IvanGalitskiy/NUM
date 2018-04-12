package com.vallsoft.num.presentation;

import android.content.Context;
import android.net.ConnectivityManager;

import com.vallsoft.num.data.API.FirebaseDatabaseHelper;
import com.vallsoft.num.data.ContactHelper;
import com.vallsoft.num.domain.IUserRetriever;
import com.vallsoft.num.domain.utils.User;
import com.vallsoft.num.presentation.view.IUserDisplayer;


// Клас  посредник для поиска пользователя
public class PhonePresenter implements IUserRetriever {
    private ContactHelper helper;
    private FirebaseDatabaseHelper remoteDatabase;
    private IUserDisplayer displayer;
    private Context context;

    public PhonePresenter(Context context, IUserDisplayer displayer) {
        helper = new ContactHelper(context);
        remoteDatabase = new FirebaseDatabaseHelper(this);
        this.displayer = displayer;
        this.context = context;
    }

    public void getUser(String phone) {
        Long p = Long.parseLong(phone.replaceAll("[^\\d.]", ""));
        // если пользователя не существует в контактах
        User u = helper.getUserByPhone(p);

        if (u == null && isNetworkAvailable()) {
            //Если есть доступ к сети
            remoteDatabase.getUserByPhone(p);
        }

        // иначе не отображаем сообщение
    }

    @Override
    public void onUserRecieved(User u, String source) {
        // получили пользователя, если он существует - записываем в контакты и отображаем
        if (u != null) {
            helper.saveUser(u);
            displayer.displayUser(u, source);
        }
    }

    // проверка есть ли интернет
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}

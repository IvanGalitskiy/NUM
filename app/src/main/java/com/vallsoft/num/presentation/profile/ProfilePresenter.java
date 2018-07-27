package com.vallsoft.num.presentation.profile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.vallsoft.num.domain.utils.User;
import com.vallsoft.num.presentation.IBaseView;
import com.vallsoft.num.presentation.SimpleBasePresenter;

public interface ProfilePresenter {
    interface View extends IBaseView{
        void displayUser(User user);
        void displayPhoneNumber(String phone);
        void onNeedDisplayCode();
        void onPhoneAttachSuccess(String phone);
        void onPhoneAttachFailed();
        void onWrongCodeSend();
        void onUpdateFail();
        void onUpdateSuccess();
        void onTimerUpdate(Long secondsLeft);
        void onTimerStop();
        void showProgress();
        void hideProgress();
    }
    interface Presenter extends SimpleBasePresenter<View>{
        void getProfile(String phone, boolean getFromCache);
        void getPhoneNumber();
        void updateUser(User user);
        void updatePhone(String phone, Activity activity);
        void updatePhoneWithSmsCode(String smsCode);
        void updateAvatar(Bitmap bitmap);
        void updateAvatar(Uri bitmap);
        void sendMessageAgain(Activity activity);
        void removeProfile();
    }
}

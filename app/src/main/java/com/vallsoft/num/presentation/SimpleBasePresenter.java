package com.vallsoft.num.presentation;

public interface SimpleBasePresenter<IBaseView> {
    void attachView(IBaseView mView);
    void detachView();
}

package com.vallsoft.num.presentation;



public interface IBasePresenter<IBaseView> {
    void attachView(IBaseView mView);
    void detachView(IBaseView mView);
}

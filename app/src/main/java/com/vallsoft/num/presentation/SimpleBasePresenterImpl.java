package com.vallsoft.num.presentation;

public abstract class SimpleBasePresenterImpl<V extends IBaseView> implements SimpleBasePresenter<V> {
    protected V mView;

    @Override
    public void attachView(V mView) {
        this.mView = mView;
    }

    @Override
    public void detachView() {
        this.mView = null;
    }
}

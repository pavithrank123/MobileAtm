package com.proj.mobileAtm.base.model.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import javax.inject.Inject;

public class BaseViewModel extends BaseObservable {

    @Inject
    public BaseViewModel() {
    }

    private boolean apiLoading;
    private boolean transactionError;

    @Bindable
    public boolean isTransactionError() {
        return transactionError;
    }

    public void setTransactionError(boolean transactionError) {
        this.transactionError = transactionError;
        notifyPropertyChanged(BR.transactionError);
    }

    @Bindable
    public boolean isApiLoading() {
        return apiLoading;
    }

    public void setApiLoading(boolean apiLoading) {
        this.apiLoading = apiLoading;
        notifyPropertyChanged(BR.apiLoading);
    }
}

package com.proj.mobileAtm.transaction.model.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proj.mobileAtm.BR;
import com.proj.mobileAtm.common.CommonCallBack;
import com.proj.mobileAtm.dashboard.service.SharedPreferenceService;
import com.proj.mobileAtm.transaction.model.entity.ResponseData;
import com.proj.mobileAtm.transaction.model.entity.TransactionEntity;
import com.proj.mobileAtm.transaction.service.TransactionLocalCacheManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TransactionListViewModel extends BaseObservable {

    private boolean apiLoading;
    private boolean noDataFound;
    private boolean transactionCompleted;
    private TransactionLocalCacheManager transactionLocalCacheManager;
    private MutableLiveData<ResponseData<String>> transactionListLiveData = new MutableLiveData<>();

    @Inject
    public TransactionListViewModel(TransactionLocalCacheManager transactionLocalCacheManager) {
        this.transactionLocalCacheManager = transactionLocalCacheManager;
    }

    @Bindable
    public boolean isApiLoading() {
        return apiLoading;
    }

    @Bindable
    public boolean isNoDataFound() {
        return noDataFound;
    }

    public void setNoDataFound(boolean noDataFound) {
        this.noDataFound = noDataFound;
        notifyPropertyChanged(BR.noDataFound);
    }

    public void setApiLoading(boolean apiLoading) {
        this.apiLoading = apiLoading;
        notifyPropertyChanged(BR.apiLoading);
    }

    public void fetchTransactionList() {
        setApiLoading(true);
        transactionLocalCacheManager.getTransactionList((responseData, success) -> {
            transactionListLiveData.setValue(responseData);
        });
    }

    public LiveData<ResponseData<String>> getTransactionListLiveData() {
        return transactionListLiveData;
    }

    public boolean isTransactionCompleted() {
        return transactionCompleted;
    }

    public void setTransactionCompleted(boolean transactionCompleted) {
        this.transactionCompleted = transactionCompleted;
    }

}

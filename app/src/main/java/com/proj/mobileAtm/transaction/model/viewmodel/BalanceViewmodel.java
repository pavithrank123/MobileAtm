package com.proj.mobileAtm.transaction.model.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proj.mobileAtm.transaction.model.entity.ResponseData;
import com.proj.mobileAtm.transaction.service.TransactionLocalCacheManager;

import javax.inject.Inject;

public class BalanceViewmodel extends BaseObservable {

    private TransactionLocalCacheManager transactionLocalCacheManager;
    private MutableLiveData<ResponseData<String>> balanceDetailsLiveData = new MutableLiveData<>();

    @Inject
    public BalanceViewmodel(TransactionLocalCacheManager localCacheManager) {
        this.transactionLocalCacheManager = localCacheManager;
    }

    private boolean processCompleted;
    private String balance;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void fetchBalance() {
        transactionLocalCacheManager.fetchBalance((responseData, success) -> {
            balanceDetailsLiveData.setValue(responseData);
        });
    }

    public LiveData<ResponseData<String>> getBalanceDetailsLiveData() {
        return balanceDetailsLiveData;
    }

    public boolean isProcessCompleted() {
        return processCompleted;
    }

    public void setProcessCompleted(boolean processCompleted) {
        this.processCompleted = processCompleted;
    }
}

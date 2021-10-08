package com.proj.mobileAtm.transaction.model.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.proj.mobileAtm.BR;
import com.proj.mobileAtm.transaction.model.entity.KeyValueEntity;

import java.util.List;

import javax.inject.Inject;

public class TransactionStatusViewModel extends BaseObservable {

    private boolean transactionStatus;
    private List<KeyValueEntity> currencyList;
    private String failureMessage;

    @Inject
    public TransactionStatusViewModel() {
    }

    @Bindable
    public boolean isTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(boolean transactionStatus) {
        this.transactionStatus = transactionStatus;
        notifyPropertyChanged(BR.transactionStatus);
    }

    @Bindable
    public List<KeyValueEntity> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<KeyValueEntity> currencyList) {
        this.currencyList = currencyList;
        notifyPropertyChanged(BR.currencyList);
    }

    @Bindable
    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
        notifyPropertyChanged(BR.failureMessage);
    }
}

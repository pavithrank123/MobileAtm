package com.proj.mobileAtm.transaction.model.viewmodel;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proj.mobileAtm.common.CommonCallBack;
import com.proj.mobileAtm.common.CommonUtils;
import com.proj.mobileAtm.transaction.model.entity.CurrencyDispatched;
import com.proj.mobileAtm.transaction.model.entity.ResponseData;
import com.proj.mobileAtm.transaction.service.TransactionLocalCacheManager;

import javax.inject.Inject;

public class TransactionViewModel extends BaseObservable {

    private MutableLiveData<Boolean> submitButtonClickLiveData = new MutableLiveData<>();
    public ObservableField<String> amountDisplayText = new ObservableField<>();
    private String enteredKey;
    private boolean currencyError;

    private boolean transactionProcessed;

    private TransactionLocalCacheManager transactionLocalCacheManager;
    private MutableLiveData<ResponseData<CurrencyDispatched>> responseDataMutableLiveData = new MutableLiveData<>();

    @Inject
    public TransactionViewModel(TransactionLocalCacheManager localCacheManager) {
        this.transactionLocalCacheManager = localCacheManager;
        amountDisplayText.set("00.00");
    }

    public void onCancel(View v){
        submitButtonClickLiveData.setValue(false);
    }

    public void onSubmitClick(View v){
        submitButtonClickLiveData.setValue(true);
    }


    @Bindable
    public String getEnteredKey() {
        return enteredKey;
    }

    public void setEnteredKey(String enteredKey) {
        this.enteredKey = enteredKey;
        notifyPropertyChanged(BR.enteredKey);
    }

    public void addKey(String key) {
        if (validateInputKey(key) && checkIfPinIsNotCompletelyEntered()) {
            String currentKey = getEnteredKey();
            if(currentKey == null){
                currentKey = "";
            }
            currentKey += key;
            setEnteredKey(currentKey);
            setCurrencyError(false);
            amountDisplayText.set(currentKey +".00");
        }
    }


    private boolean checkIfPinIsNotCompletelyEntered() {
        if (getEnteredKey() == null || getEnteredKey().length() < 10 ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validateInputKey(String key) {
        if (key == null || key.length() != 1) {
            return false;
        } else{
            return true;
        }
    }

    public void removeLastEnteredKey() {
        if(CommonUtils.checkIsNotNullAndEmpty(getEnteredKey()) && getEnteredKey().length() > 0 ){
            StringBuilder newString = new StringBuilder(getEnteredKey()).deleteCharAt(getEnteredKey().length()-1);
            setEnteredKey(newString.toString());
            if(newString.length() ==0){
                amountDisplayText.set("00.00");
            } else {
                amountDisplayText.set(newString.toString()+".00");
            }
        } else {
        }
    }


    @Bindable
    public boolean isCurrencyError() {
        return currencyError;
    }

    public void setCurrencyError(boolean currencyError) {
        this.currencyError = currencyError;
        notifyPropertyChanged(BR.currencyError);
    }

    public LiveData<Boolean> getSubmitButtonClickLiveData() {
        return submitButtonClickLiveData;
    }

    public void validateAndProcessTransaction() {
        if (validateInputAmount()) {
            setCurrencyError(false);
            String enteredKey = getEnteredKey();
            int amount = Integer.parseInt(enteredKey);
            transactionLocalCacheManager.processTransaction(amount, (responseData, success) -> {
                setTransactionProcessed(true);
                responseDataMutableLiveData.setValue(responseData);
            });
        } else {
            setCurrencyError(true);
        }
    }

    private boolean validateInputAmount() {
        if(CommonUtils.checkIsNotNullAndEmpty(getEnteredKey())){
            String enteredKey = getEnteredKey();
            int amount = Integer.parseInt(enteredKey);
            if(amount % 100 == 0){
                return true;
            }
        }
        return false;
    }

    public LiveData<ResponseData<CurrencyDispatched>> getResponseDataMutableLiveData() {
        return responseDataMutableLiveData;
    }

    @Bindable
    public boolean isTransactionProcessed() {
        return transactionProcessed;
    }

    public void setTransactionProcessed(boolean transactionProcessed) {
        this.transactionProcessed = transactionProcessed;
        notifyPropertyChanged(BR.transactionProcessed);
    }
}

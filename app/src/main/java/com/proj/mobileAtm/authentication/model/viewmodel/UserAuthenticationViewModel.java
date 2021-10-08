package com.proj.mobileAtm.authentication.model.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proj.mobileAtm.authentication.service.AuthLocalCacheManager;
import com.proj.mobileAtm.common.CommonFunctions;
import com.proj.mobileAtm.common.CommonUtils;

import javax.inject.Inject;

public class UserAuthenticationViewModel extends BaseObservable {

    private String enteredKey;
    private MutableLiveData<Boolean> submitButtonClick = new MutableLiveData<>();
    private MutableLiveData<Boolean> authenticationLiveData = new MutableLiveData<>();
    private AuthLocalCacheManager authLocalCacheManager;

    @Inject
    public UserAuthenticationViewModel(AuthLocalCacheManager authLocalCacheManager) {
        this.authLocalCacheManager = authLocalCacheManager;
    }

    public void addKey(String key) {
        if (validateInputKey(key) && checkIfPinIsNotCompletelyEntered()) {
            String currentKey = getEnteredKey();
            if(currentKey == null){
               currentKey = "";
            }
            currentKey += key;
            setEnteredKey(currentKey);
        }
    }

    private boolean checkIfPinIsNotCompletelyEntered() {
        if (getEnteredKey() == null || getEnteredKey().length() < 4 ) {
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
        }
    }

    public void onSubmitClick(View view){
        submitButtonClick.setValue(true);
    }
    public void onCancelTransaction(View view){
        submitButtonClick.setValue(false);
    }


    public void setAuthentication(Context context) {
        if(getEnteredKey()!=null && getEnteredKey().length() == 4){
            authLocalCacheManager.setSessionAuthentication(getEnteredKey(), (a, success) -> {
                if(success){
                    authenticationLiveData.setValue(true);
                }
            });
        } else {
            CommonFunctions.toastString("Enter Passcode",context);
        }
    }


    @Bindable
    public String getEnteredKey() {
        return enteredKey;
    }

    public void setEnteredKey(String enteredKey) {
        this.enteredKey = enteredKey;
        notifyPropertyChanged(BR.enteredKey);
    }

    public LiveData<Boolean> getSubmitButtonClick() {
        return submitButtonClick;
    }

    @Bindable
    public LiveData<Boolean> getAuthenticationLiveData() {
        return authenticationLiveData;
    }

    public void setDefaultValues() {
        authLocalCacheManager.setDefaultValues();
    }
}

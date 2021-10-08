package com.proj.mobileAtm.authentication.model.viewmodel;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class KeyBoardViewModel extends BaseObservable {

    @Inject
    public KeyBoardViewModel() {
    }

    private MutableLiveData<String> enteredKeyLiveData = new MutableLiveData<>();

    public void handleClick(View v, String key){
        enteredKeyLiveData.setValue(key);
    }

    //Must emit only livedata
    public LiveData<String> getEnteredKeyLiveData() {
        return enteredKeyLiveData;
    }

}

package com.proj.mobileAtm.dashboard.model.viewmodel;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class LauncherViewModel extends BaseObservable {

    private MutableLiveData<Boolean> onNextButtonClickLiveData = new MutableLiveData<>();

    @Inject
    public LauncherViewModel() {
    }

    public void onClickEnter(View view){
        onNextButtonClickLiveData.setValue(true);
    }

    public LiveData<Boolean> getOnNextButtonClickLiveData() {
        return onNextButtonClickLiveData;
    }
}

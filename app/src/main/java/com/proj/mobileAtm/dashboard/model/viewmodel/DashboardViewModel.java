package com.proj.mobileAtm.dashboard.model.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import com.proj.mobileAtm.common.Constants;

import javax.inject.Inject;

public class DashboardViewModel extends BaseObservable {

    private MutableLiveData<String> navigateLiveData = new MutableLiveData<>();

    @Inject
    public DashboardViewModel() {
    }

    public void handleQuickWithdraw(){
        navigateLiveData.setValue(Constants.NAV_QUICK_WITHDRAW);
    }

    public void handleRecentTransactions(){
        navigateLiveData.setValue(Constants.NAV_RECENT_TRANSACTIONS);
    }
    public void handleBalanceOnclick(){
        navigateLiveData.setValue(Constants.NAV_BALANCE);
    }

    public void handleCashWithdraw(){
        navigateLiveData.setValue(Constants.NAV_CASH_WITHDRAW);
    }

    public MutableLiveData<String> getNavigateLiveData() {
        return navigateLiveData;
    }
}

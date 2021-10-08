package com.proj.mobileAtm.dashboard.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.Observer;

import com.proj.mobileAtm.R;
import com.proj.mobileAtm.authentication.model.viewmodel.KeyBoardViewModel;
import com.proj.mobileAtm.base.view.BaseActivity;
import com.proj.mobileAtm.common.CommonFunctions;
import com.proj.mobileAtm.common.Constants;
import com.proj.mobileAtm.dashboard.model.viewmodel.DashboardViewModel;
import com.proj.mobileAtm.dashboard.service.SharedPreferenceService;
import com.proj.mobileAtm.databinding.ActDashboardBinding;
import com.proj.mobileAtm.transaction.view.BalanceActivity;
import com.proj.mobileAtm.transaction.view.TransactionActivity;
import com.proj.mobileAtm.transaction.view.TransactionsListActivity;

import javax.inject.Inject;

public class DashboardActivity extends BaseActivity<ActDashboardBinding>{

    private ActDashboardBinding actDashboardBinding;

    @Inject
    public DashboardViewModel dashboardViewModel;

    @Inject
    public SharedPreferenceService sharedPreferenceService;

    @Override
    public void onBackPressed() {
        invalidateSession();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actDashboardBinding = bindView(this, R.layout.act_dashboard);
        initMenu();
        observeLiveData();
    }

    private void observeLiveData() {
        dashboardViewModel.getNavigateLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String tag) {
                if(tag!=null){
                    if(validateSession()){
                        handleNavigation(tag);
                    } else {
                        CommonFunctions.toastString("Session Invalid",DashboardActivity.this);
                        invalidateSession();
                    }
                }
            }
        });
    }

    private void invalidateSession() {
        Intent intent = new Intent(this, LauncherPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validateSession() {
        if(sharedPreferenceService.isSessionValid()) {
            return true;
        }
        return false;
    }

    private void handleNavigation(String navTag) {
        switch(navTag){
            case Constants.NAV_CASH_WITHDRAW:
                handleCashWithdrawClick();
                break;
            case Constants.NAV_QUICK_WITHDRAW:
                handleQuickWithdrawClick();
                break;
            case Constants.NAV_RECENT_TRANSACTIONS:
                openRecentTransactionsActivity();
                break;
            case Constants.NAV_BALANCE:
                openBalanceDetailsActivity();
                break;
        }
    }

    private void openBalanceDetailsActivity() {
        Intent intent = new Intent(this, BalanceActivity.class);
        startActivity(intent);
    }

    private void openRecentTransactionsActivity() {
        Intent intent = new Intent(this, TransactionsListActivity.class);
        startActivity(intent);
    }

    private void handleQuickWithdrawClick() {
        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);
    }

    private void handleCashWithdrawClick() {
        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);
    }

    private void initMenu() {
        actDashboardBinding.setDashboardViewmodel(dashboardViewModel);
    }

}

package com.proj.mobileAtm.transaction.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.proj.mobileAtm.R;
import com.proj.mobileAtm.base.view.BaseActivity;
import com.proj.mobileAtm.common.CommonFunctions;
import com.proj.mobileAtm.common.Constants;
import com.proj.mobileAtm.dashboard.view.LauncherPageActivity;
import com.proj.mobileAtm.databinding.ActBalanceBinding;
import com.proj.mobileAtm.transaction.model.entity.ResponseData;
import com.proj.mobileAtm.transaction.model.viewmodel.BalanceViewmodel;

import javax.inject.Inject;

public class BalanceActivity extends BaseActivity<ActBalanceBinding> {

    private ActBalanceBinding actBalanceBinding;
    @Inject  public BalanceViewmodel balanceViewmodel;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actBalanceBinding = bindView(this, R.layout.act_balance);
        initViewmodel();
        observeLiveData();
        fetchBalance();
    }

    @Override
    public void onBackPressed() {
        if(balanceViewmodel.isProcessCompleted()){
            CommonFunctions.toastString("Please wait...",this);
        } else {
            clearSessionAndGoBack();
        }
    }

    private void clearSessionAndGoBack() {
        //Clearing all data
        Intent intent = new Intent(this, LauncherPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void setMessageTimeout() {
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                takeBackToActivity();
            }
        };
        countDownTimer.start();
    }


    private void takeBackToActivity() {
        if(BalanceActivity.this!=null && !BalanceActivity.this.isFinishing()){
            clearSessionAndGoBack();
        }
    }

    private void observeLiveData() {
        balanceViewmodel.getBalanceDetailsLiveData().observe(this, responseData -> {
            if(responseData!=null){
                handleBalanceDetailsResponse(responseData);
                setMessageTimeout();
            }
        });
    }

    private void handleBalanceDetailsResponse(ResponseData<String> responseData) {
        baseViewModel.setTransactionError(!responseData.isSuccess());
        balanceViewmodel.setProcessCompleted(true);
        if(responseData.isSuccess()){
            balanceViewmodel.setBalance(Constants.INR_CURRENCY_SYMBOL+ responseData.getData());
        } else {
            transactionStatusViewModel.setTransactionStatus(false);
            transactionStatusViewModel.setFailureMessage(responseData.getMessage());
        }
    }

    private void fetchBalance() {
        balanceViewmodel.fetchBalance();
    }

    private void initViewmodel() {
        actBalanceBinding.setBalanceViewmodel(balanceViewmodel);
    }
}

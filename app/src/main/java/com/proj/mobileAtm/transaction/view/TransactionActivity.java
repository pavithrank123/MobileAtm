package com.proj.mobileAtm.transaction.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.lifecycle.Observer;

import com.proj.mobileAtm.R;
import com.proj.mobileAtm.authentication.constants.KeyboardInputConstants;
import com.proj.mobileAtm.authentication.model.viewmodel.KeyBoardViewModel;
import com.proj.mobileAtm.base.view.BaseActivity;
import com.proj.mobileAtm.common.CommonFunctions;
import com.proj.mobileAtm.dashboard.service.SharedPreferenceService;
import com.proj.mobileAtm.dashboard.view.LauncherPageActivity;
import com.proj.mobileAtm.databinding.ActTransactionBinding;
import com.proj.mobileAtm.transaction.model.entity.CurrencyDispatched;
import com.proj.mobileAtm.transaction.model.entity.ResponseData;
import com.proj.mobileAtm.transaction.model.viewmodel.TransactionStatusViewModel;
import com.proj.mobileAtm.transaction.model.viewmodel.TransactionViewModel;

import javax.inject.Inject;

public class TransactionActivity extends BaseActivity<ActTransactionBinding> {

    ActTransactionBinding actTransactionBinding;

    @Inject public TransactionViewModel transactionViewModel;
    @Inject public KeyBoardViewModel keyBoardViewModel;
    @Inject public TransactionStatusViewModel transactionStatusViewModel;
    @Inject public SharedPreferenceService sharedPreferenceService;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actTransactionBinding = bindView(this, R.layout.act_transaction);
        initViewModel();
        observeLiveData();
    }

    @Override
    public void onBackPressed() {
        if(transactionViewModel.isTransactionProcessed()){
            CommonFunctions.toastString("Please wait...",this);
        } else {
            clearSessionAndGoBack();

        }
    }


    private void observeLiveData() {
        keyBoardViewModel.getEnteredKeyLiveData().observe(this, str -> {
            if(str!=null){
                handleOnKeyboardPress(str);
            }
        });
        transactionViewModel.getSubmitButtonClickLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
               if(aBoolean!=null){
                   handleSubmitButtonClick(aBoolean);
               }
            }
        });

        transactionViewModel.getResponseDataMutableLiveData().observe(this, new Observer<ResponseData<CurrencyDispatched>>() {
            @Override
            public void onChanged(ResponseData<CurrencyDispatched> responseData) {
                if(responseData!=null){
                    setMessageTimeout();
                    transactionViewModel.setTransactionProcessed(true);
                    handleOnTransactionResponse(responseData);
                }
            }
        });
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
        if(TransactionActivity.this!=null && !TransactionActivity.this.isFinishing()){
            clearSessionAndGoBack();
        }
    }

    private void handleOnTransactionResponse(ResponseData<CurrencyDispatched> responseData) {
        boolean isSuccess = responseData.isSuccess();
        transactionStatusViewModel.setTransactionStatus(responseData.isSuccess());
        if(isSuccess){
            transactionStatusViewModel.setCurrencyList(responseData.getData().getCurrencyList());
        } else {
            transactionStatusViewModel.setFailureMessage(responseData.getMessage());
        }
    }

    private void handleSubmitButtonClick(boolean success) {
        if(success){
            if(validateSession()) {
                transactionViewModel.validateAndProcessTransaction();
            } else {
                CommonFunctions.toastString("Session Timeout",this);
                clearSessionAndGoBack();
            }
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

    private boolean validateSession() {
        if(sharedPreferenceService.isSessionValid()) {
            return true;
        }
        return false;
    }


    private void handleOnKeyboardPress(String keyInput) {
        switch(keyInput){
            case KeyboardInputConstants.KEY_ZERO:
            case KeyboardInputConstants.KEY_ONE:
            case KeyboardInputConstants.KEY_TWO:
            case KeyboardInputConstants.KEY_THREE:
            case KeyboardInputConstants.KEY_FOUR:
            case KeyboardInputConstants.KEY_FIVE:
            case KeyboardInputConstants.KEY_SIX:
            case KeyboardInputConstants.KEY_SEVEN:
            case KeyboardInputConstants.KEY_EIGHT:
            case KeyboardInputConstants.KEY_NINE:
                transactionViewModel.addKey(keyInput);
                break;
            case KeyboardInputConstants.DELETE:
                transactionViewModel.removeLastEnteredKey();
                break;
        }

    }


    private void initViewModel() {

        actTransactionBinding.keyboard.setKeyboardViewModel(keyBoardViewModel);
        actTransactionBinding.setTransactionViewmodel(transactionViewModel);
        actTransactionBinding.layoutTransactionCompleted.setTransactionStatusViewModel(transactionStatusViewModel);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}

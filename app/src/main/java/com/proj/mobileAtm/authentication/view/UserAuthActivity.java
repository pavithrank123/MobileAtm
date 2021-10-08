package com.proj.mobileAtm.authentication.view;

import android.content.Intent;
import android.os.Bundle;

import com.proj.mobileAtm.R;
import com.proj.mobileAtm.authentication.constants.KeyboardInputConstants;
import com.proj.mobileAtm.authentication.model.viewmodel.KeyBoardViewModel;
import com.proj.mobileAtm.authentication.model.viewmodel.UserAuthenticationViewModel;
import com.proj.mobileAtm.base.view.BaseActivity;
import com.proj.mobileAtm.common.CommonFunctions;
import com.proj.mobileAtm.dashboard.service.SharedPreferenceService;
import com.proj.mobileAtm.dashboard.view.DashboardActivity;
import com.proj.mobileAtm.databinding.ActUserAuthActivityBinding;

import javax.inject.Inject;

public class UserAuthActivity extends BaseActivity<ActUserAuthActivityBinding> {

    private ActUserAuthActivityBinding actUserAuthActivityBinding;

    @Inject public UserAuthenticationViewModel userAuthenticationViewModel;

    @Inject public KeyBoardViewModel keyBoardViewModel;

    @Inject public SharedPreferenceService sharedPreferenceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actUserAuthActivityBinding = bindView(this, R.layout.act_user_auth_activity);
        initViewModel();
        initDefaultValues();
        initObservables();
    }

    @Override
    public void onBackPressed() {
        CommonFunctions.toastString("Transaction Cancelled",this);
        super.onBackPressed();
    }

    private void initDefaultValues() {
        userAuthenticationViewModel.setDefaultValues();
    }

    private void initObservables() {

        keyBoardViewModel.getEnteredKeyLiveData().observe(this, str -> {
            if(str!=null){
                handleOnKeyboardPress(str);
            }
        });

        userAuthenticationViewModel.getSubmitButtonClick().observe(this, aBoolean -> {
            if(aBoolean!=null){
                if(aBoolean)
                    handleOnSubmitButtonClick();
                else {
                    CommonFunctions.toastString("Transaction Cancelled", this);
                    finish();
                }
            }
        });

        userAuthenticationViewModel.getAuthenticationLiveData().observe(this, aBoolean -> {
            if(aBoolean!=null && aBoolean){
                openMainDashBoard();
            }
        });

    }

    private void openMainDashBoard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private void handleOnSubmitButtonClick() {
        if(sharedPreferenceService.isSessionValid()) {
            userAuthenticationViewModel.setAuthentication(this);
        } else {
            invalidateSessionAndLogout();
        }
    }

    private void invalidateSessionAndLogout() {
        CommonFunctions.toastString("Invalid Session",this);
        finish();
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
                userAuthenticationViewModel.addKey(keyInput);
                break;
            case KeyboardInputConstants.DELETE:
                userAuthenticationViewModel.removeLastEnteredKey();
                break;
        }

    }

    private void initViewModel() {
        actUserAuthActivityBinding.keyboard.setKeyboardViewModel(keyBoardViewModel);
        actUserAuthActivityBinding.setUserAuthViewModel(userAuthenticationViewModel);
    }
}

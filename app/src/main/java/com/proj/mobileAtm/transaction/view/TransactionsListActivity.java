package com.proj.mobileAtm.transaction.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proj.mobileAtm.R;
import com.proj.mobileAtm.base.view.BaseActivity;
import com.proj.mobileAtm.common.CommonFunctions;
import com.proj.mobileAtm.dashboard.view.LauncherPageActivity;
import com.proj.mobileAtm.databinding.ActTransactionListBinding;
import com.proj.mobileAtm.transaction.adapters.TransactionListAdapter;
import com.proj.mobileAtm.transaction.model.entity.ResponseData;
import com.proj.mobileAtm.transaction.model.entity.TransactionEntity;
import com.proj.mobileAtm.transaction.model.viewmodel.TransactionListViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TransactionsListActivity extends BaseActivity<ActTransactionListBinding> {

    private ActTransactionListBinding actTransactionListBinding;
    @Inject public TransactionListAdapter transactionListAdapter;
    @Inject public TransactionListViewModel transactionListViewModel;
    private CountDownTimer countDownTimer;


    @Override
    public void onBackPressed() {
        if(transactionListViewModel.isTransactionCompleted()){
            CommonFunctions.toastString("Please wait...",this);
        } else {
            clearSessionAndGoBack();

        }
    }

    private void clearSessionAndGoBack() {
        Intent intent = new Intent(this, LauncherPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actTransactionListBinding = bindView(this, R.layout.act_transaction_list);
        initViewmodel();
        observeLiveData();
        transactionListViewModel.fetchTransactionList();
    }

    private void observeLiveData() {
        transactionListViewModel.getTransactionListLiveData().observe(this, transactionEntities -> {
            if(transactionEntities!=null){
                transactionListViewModel.setApiLoading(false);
                transactionListViewModel.setTransactionCompleted(true);
                handleOnResponse(transactionEntities);
            }
        });


    }

    private void handleOnResponse(ResponseData<String> listResponseData) {
        if(listResponseData.isSuccess()){

            String transactionList = listResponseData.getData();
            List<TransactionEntity> transactionEntityList = new ArrayList<>();
            if(transactionList!=null && !transactionList.isEmpty()){
                List<TransactionEntity> currentTransactionList = new Gson().fromJson(transactionList, new TypeToken<List<TransactionEntity>>(){}.getType());

                if(currentTransactionList!=null && !currentTransactionList.isEmpty()){
                    transactionEntityList.addAll(currentTransactionList);
                }
            }
            baseViewModel.setTransactionError(false);
            transactionListViewModel.setNoDataFound(transactionEntityList.isEmpty());
            initAdapterList(transactionEntityList);

        } else {
            baseViewModel.setTransactionError(true);
            String message = listResponseData.getMessage();
            transactionStatusViewModel.setTransactionStatus(false);
            transactionStatusViewModel.setFailureMessage(message);
        }
        startTimerToGoBack();
    }

    private void startTimerToGoBack() {
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
        if(TransactionsListActivity.this!=null && !TransactionsListActivity.this.isFinishing()){
            clearSessionAndGoBack();
        }
    }

    private void initAdapterList(List<TransactionEntity> transactionEntities) {
        actTransactionListBinding.setTransactionAdapter(transactionListAdapter);
        transactionListAdapter.setTransactionEntityList(transactionEntities);
    }

    private void initViewmodel() {
        actTransactionListBinding.setViewModel(transactionListViewModel);
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

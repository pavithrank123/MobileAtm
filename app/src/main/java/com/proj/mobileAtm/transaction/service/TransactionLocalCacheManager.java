package com.proj.mobileAtm.transaction.service;

import com.proj.mobileAtm.base.service.LocalCacheService;
import com.proj.mobileAtm.common.CommonCallBack;
import com.proj.mobileAtm.transaction.model.entity.CurrencyDispatched;
import com.proj.mobileAtm.transaction.model.entity.ResponseData;

import javax.inject.Inject;

public class TransactionLocalCacheManager {

    LocalCacheService localCacheService;

    @Inject
    public TransactionLocalCacheManager(LocalCacheService localCacheService) {
        this.localCacheService = localCacheService;
    }

    public void processTransaction(int amount, CommonCallBack<ResponseData<CurrencyDispatched>> callBack) {
        localCacheService.processTransaction(amount, (a, success) -> callBack.data(a,success));
    }

    public void getTransactionList(CommonCallBack<ResponseData<String>> callBack) {
        localCacheService.getTransactionList((a, success) -> callBack.data(a,success));
    }

    public void fetchBalance(CommonCallBack<ResponseData<String>> callBack) {
        localCacheService.fetchBalance((a, success) -> callBack.data(a,success));
    }
}

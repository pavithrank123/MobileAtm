package com.proj.mobileAtm.base.service;

import com.proj.mobileAtm.common.CommonCallBack;
import com.proj.mobileAtm.transaction.model.entity.CurrencyDispatched;
import com.proj.mobileAtm.transaction.model.entity.ResponseData;

public interface LocalCacheService {

    boolean checkUserCredentials(String enteredPass);

    void setUserCredentials(String enteredPass);

    void processTransaction(int enteredAmount, CommonCallBack<ResponseData<CurrencyDispatched>> callBack);

    void getTransactionList(CommonCallBack<ResponseData<String>> callBack);

    void setDefaultValues();

    void fetchBalance(CommonCallBack<ResponseData<String>> callBack);
}

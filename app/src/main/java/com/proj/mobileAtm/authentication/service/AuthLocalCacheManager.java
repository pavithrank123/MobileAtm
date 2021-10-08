package com.proj.mobileAtm.authentication.service;

import com.proj.mobileAtm.base.service.LocalCacheService;
import com.proj.mobileAtm.common.CommonCallBack;

import javax.inject.Inject;

public class AuthLocalCacheManager {

    LocalCacheService localCacheService;

    @Inject
    public AuthLocalCacheManager(LocalCacheService localCacheService) {
        this.localCacheService = localCacheService;
    }

    public void setSessionAuthentication(String enteredKey, CommonCallBack<String> callBack) {
        localCacheService.setUserCredentials(enteredKey);
        callBack.data(null,true);
    }

    public void setDefaultValues() {
        localCacheService.setDefaultValues();
    }
}

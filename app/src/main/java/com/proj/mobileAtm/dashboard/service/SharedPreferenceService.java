package com.proj.mobileAtm.dashboard.service;

import android.content.SharedPreferences;

import com.proj.mobileAtm.common.Constants;
import com.proj.mobileAtm.common.SharedPreferenceUtils;

import javax.inject.Inject;

public class SharedPreferenceService {

    private SharedPreferences sharedPreferences;

    @Inject
    public SharedPreferenceService(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean checkIfGeofencingIsEnabled(){
        return SharedPreferenceUtils.getBoolean(sharedPreferences, Constants.PREF_GEOFENCING_ENABLED);
    }

    public boolean checkIfUserIsInProperLocation(){
        return SharedPreferenceUtils.getBoolean(sharedPreferences, Constants.PREF_USER_LOCATION);
    }

    public void startSession() {
        SharedPreferenceUtils.putBoolean(sharedPreferences, Constants.PREF_SESSION, true);
    }

    public boolean isSessionValid() {
        return SharedPreferenceUtils.getBoolean(sharedPreferences, Constants.PREF_SESSION);
    }

    public void setMockGpsUsed(boolean flag) {
        SharedPreferenceUtils.putBoolean(sharedPreferences, Constants.PREF_MOCK_GPS, flag);
    }

    public void setWarningCount(int count) {
        SharedPreferenceUtils.putInt(sharedPreferences, Constants.PREF_WARNING_COUNT, count);
    }

    public int getWarningCount() {
        return SharedPreferenceUtils.getInt(sharedPreferences, Constants.PREF_WARNING_COUNT);
    }

    public boolean isMockGpsUsed() {
        return SharedPreferenceUtils.getBoolean(sharedPreferences, Constants.PREF_MOCK_GPS);
    }

    public void setTransactionList(String transactionList) {
        SharedPreferenceUtils.putString(sharedPreferences, Constants.TRANSACTION_LIST, transactionList);
    }

    public String getTransactionList() {
        return SharedPreferenceUtils.getString(sharedPreferences, Constants.TRANSACTION_LIST);
    }

    public void setGeoFencingInitialized(boolean geoFencingInitialized) {
        SharedPreferenceUtils.putBoolean(sharedPreferences, Constants.PREF_GEOFENCING, geoFencingInitialized);
    }

    public boolean isGeoFencingInitialized() {
        return SharedPreferenceUtils.getBoolean(sharedPreferences, Constants.PREF_GEOFENCING);
    }


}

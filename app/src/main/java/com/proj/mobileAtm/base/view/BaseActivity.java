package com.proj.mobileAtm.base.view;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.proj.mobileAtm.R;
import com.proj.mobileAtm.base.model.viewmodel.BaseViewModel;
import com.proj.mobileAtm.common.CommonFunctions;
import com.proj.mobileAtm.common.Constants;
import com.proj.mobileAtm.common.LocationFactory;
import com.proj.mobileAtm.dashboard.service.SharedPreferenceService;
import com.proj.mobileAtm.databinding.ActBaseBinding;
import com.proj.mobileAtm.transaction.model.viewmodel.TransactionStatusViewModel;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public abstract class BaseActivity<B extends ViewDataBinding> extends DaggerAppCompatActivity {

    @Inject public BaseViewModel baseViewModel;
    @Inject public TransactionStatusViewModel transactionStatusViewModel;
    @Inject public SharedPreferenceService sharedPreferenceService;
    @Inject public LocationFactory locationFactory;
    private ActBaseBinding actBaseBinding;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actBaseBinding = DataBindingUtil.setContentView(this, R.layout.act_base);
        initBaseViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerGeoFenceBroadCastReceiver();
    }

    private void registerGeoFenceBroadCastReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(geofencingReceiver, new IntentFilter(Constants.ACTION_GEOFENCING));
    }


    private void initBaseViewModel(){
        actBaseBinding.setViewModel(baseViewModel);
        actBaseBinding.layoutCommonError.setTransactionStatusViewModel(transactionStatusViewModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocation();
    }

    private void updateLocation() {
        baseViewModel.setApiLoading(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> handleOnLocationReceive(location));

        }
    }

    private void handleOnLocationReceive(Location location) {
        baseViewModel.setApiLoading(false);
        if (location != null) {
            if(CommonFunctions.validateMockLocation(location)){
                increaseWarningCount();
                sharedPreferenceService.setMockGpsUsed(true);
            }else {
                sharedPreferenceService.setMockGpsUsed(false);
            }
        } else {

        }
        checkUserDetails();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearAlertDialog();
    }

    private void increaseWarningCount() {
        int count = sharedPreferenceService.getWarningCount();
        sharedPreferenceService.setWarningCount(count + 1);
    }


    private void checkUserDetails() {
//        if(sharedPreferenceService.getWarningCount() >= Constants.WARNING_COUNT_LIMIT){
//            showNonDismissableDialog(this, "BLOCKED", "You are blocked from using remote atm");
//        } else {
//            if(validateMockGpsUsed()){
                if(validateUserLocation()){
                    clearAlertDialog();
                }
//            }

//        }
    }

    public B bindView(Activity activity, int layoutId) {

        FrameLayout frameLayout = actBaseBinding.flMainContainer;
        B viewDataBinding = DataBindingUtil.inflate(activity.getLayoutInflater(), layoutId, frameLayout, true);
        return viewDataBinding;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterGeofenceBroadCastReceiver();
    }

    private void unregisterGeofenceBroadCastReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geofencingReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean validateMockGpsUsed() {
        clearAlertDialog();
        boolean mockGpsUsed = sharedPreferenceService.isMockGpsUsed();
        if(mockGpsUsed){
            showNonDismissableDialog(this, "Location", "Fake location apps are not allowed");
            return false;
        }
        return true;
    }

    private boolean validateUserLocation() {
        clearAlertDialog();
        if(sharedPreferenceService.checkIfGeofencingIsEnabled() && !sharedPreferenceService.checkIfUserIsInProperLocation()){
            showNonDismissableDialog(this, "Location", "You are not allowed in to use ATM in this location");
            return false;
        }
        return true;
    }


    private final BroadcastReceiver geofencingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean flag = intent.getBooleanExtra("flag", false);
            checkUserDetails();
        }
    };


    private void clearAlertDialog() {
        if(alertDialog!=null){
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public void showNonDismissableDialog(final Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if (title != null && title.length() > 0)
            alertDialogBuilder.setTitle(title);
        if (message != null && message.length() > 0)
            alertDialogBuilder.setMessage(message);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
package com.proj.mobileAtm.dashboard.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.proj.mobileAtm.R;
import com.proj.mobileAtm.authentication.view.UserAuthActivity;
import com.proj.mobileAtm.base.service.SessionTimerService;
import com.proj.mobileAtm.base.view.BaseActivity;
import com.proj.mobileAtm.common.CommonFunctions;
import com.proj.mobileAtm.common.Constants;
import com.proj.mobileAtm.common.SharedPreferenceUtils;
import com.proj.mobileAtm.dashboard.model.viewmodel.LauncherViewModel;
import com.proj.mobileAtm.dashboard.service.SharedPreferenceService;
import com.proj.mobileAtm.databinding.ActLauncherBinding;
import com.proj.mobileAtm.geofence.GeoFenceUtils;

import javax.inject.Inject;

public class LauncherPageActivity extends BaseActivity<ActLauncherBinding> {

    private ActLauncherBinding actLauncherBinding;
    @Inject public LauncherViewModel launcherViewModel;
    @Inject public SharedPreferenceService sharedPreferenceService;
    private AlertDialog alertDialog;

    private final int ACTION_CODE_RESULT = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actLauncherBinding = bindView(this, R.layout.act_launcher);
        checkIfAllPermissionsEnabled();
        initViewModel();
        observeLiveData();
    }

    private void observeLiveData() {
        launcherViewModel.getOnNextButtonClickLiveData().observe(this, aBoolean -> {
            if(aBoolean!=null&& aBoolean){
                handleOnNextButtonClick();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkPermissions()){
            if(!sharedPreferenceService.isGeoFencingInitialized()){
                GeoFenceUtils.initGeoFencing(this);
                sharedPreferenceService.setGeoFencingInitialized(true);
            }
        }
    }

    private void handleOnNextButtonClick() {
        if(!checkIfAllPermissionsEnabled()) {
            CommonFunctions.toastString("Enable the proper location permissions", this);

        } else if(!checkGeofencingIsEnabled()){
            CommonFunctions.toastString("Your location is out of range",this);
        }
        else{
            sharedPreferenceService.startSession();
            startSessionService();
            Intent intent = new Intent(this, UserAuthActivity.class);
            startActivityForResult(intent, ACTION_CODE_RESULT);
        }
    }

    private void startSessionService(){
        Intent sessionService = new Intent(this, SessionTimerService.class);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            startForegroundService(sessionService);
        } else {
            startService(sessionService);
        }
    }

    private boolean checkGeofencingIsEnabled() {
        if(sharedPreferenceService.checkIfGeofencingIsEnabled() && sharedPreferenceService.checkIfUserIsInProperLocation()){
            return true;
        }
        return false;
    }

    private void initViewModel() {
        actLauncherBinding.setLauncherViewmodel(launcherViewModel);
    }

    private boolean checkIfAllPermissionsEnabled() {
        if(!checkPermissions()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return checkBackGroundPermission(100);
            } else {
                return updatedCheckPermisson(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showAlertDialogForRequestingPermission() {
        if(alertDialog!=null){
            alertDialog.dismiss();
            alertDialog = null;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Location permission");
        alertDialogBuilder.setMessage("Please click allow all the time to verify your location");
        alertDialogBuilder.setPositiveButton("YES", null);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button okBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okBtn.setTextColor(ContextCompat.getColor(LauncherPageActivity.this, R.color.white));
            okBtn.setOnClickListener(view -> {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                alertDialog.dismiss();
            });

        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private boolean updatedCheckPermisson(Activity context, String[] permissions, final int permission_request_code) {
        for (String checkPermisson : permissions) {
            if (ContextCompat.checkSelfPermission(context,
                    checkPermisson)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, permissions,
                        permission_request_code);
                return false;
            }
        }
        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean checkBackGroundPermission(final int permission_request_code) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ) {
                showAlertDialogForRequestingPermission();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        1);
            }

            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            boolean granted = true;
            for( int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    granted = false;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alertDialog!=null){
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

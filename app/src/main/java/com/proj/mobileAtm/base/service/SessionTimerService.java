package com.proj.mobileAtm.base.service;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.proj.mobileAtm.R;
import com.proj.mobileAtm.common.CommonFunctions;
import com.proj.mobileAtm.common.Constants;
import com.proj.mobileAtm.common.SharedPreferenceUtils;

import javax.inject.Inject;

import dagger.android.DaggerService;

public class SessionTimerService extends DaggerService {

    @Inject
    public SharedPreferences sharedPreferences;

    CountDownTimer countDownTimer = null;
    private boolean mRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = CommonFunctions.createNotificationChannel(getApplicationContext());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),channelId);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setPriority(PRIORITY_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(101,notification);
        }
        mRunning = false;

    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mRunning){
            countDownTimer.cancel();
            countDownTimer.start();
        }
        if(!mRunning){
            mRunning = true;
            countDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d("SERVICE RUNNING", String.valueOf(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    Log.d("SERVICE STOPPING","STOPPED");
                    SharedPreferenceUtils.putBoolean(sharedPreferences, Constants.PREF_SESSION, false);
                    stopSelf();
                }
            };

            countDownTimer.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

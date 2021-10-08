package com.proj.mobileAtm.common;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class CommonFunctions {

    public static void toastString(String toastText, Context context) {
        showToastMessage(toastText, 2000, context);
    }

    public static void showToastMessage(String text, int duration, Context context) {
        if (context != null) {
            final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 100);
            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, duration);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static final String createNotificationChannel(Context context) {
        String channelId = "my_service";
        String channelName = "Notifications";

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {

            NotificationChannel chan = new NotificationChannel(channelId, (CharSequence) channelName, 0);
            chan.setLightColor(-16776961);
            chan.setLockscreenVisibility(0);
            Object var10000 = context.getSystemService("notification");
            if (var10000 == null) {
//            throw new TypeCastException("null cannot be cast to non-null type android.app.NotificationManager");
                throw new ClassCastException();
            } else {
                NotificationManager service = (NotificationManager) var10000;
                service.createNotificationChannel(chan);
            }
        }

        return channelId;
    }

    public static boolean validateMockLocation(Location location) {
        return location.isFromMockProvider();
    }
}

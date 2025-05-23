package com.dummy.dummyphoneprakash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.dummy.dummyphoneprakash.activity.UnlockActivity;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Boot completed received");

            // Use handler to ensure this runs on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    SharedPreferencesHelper prefsHelper = new SharedPreferencesHelper(context);
                    disableNotificationBlocking(context);
                    if (prefsHelper.isTimerActive()) {
                        Log.d(TAG, "Restoring timer after reboot");

                        Intent unlockIntent = new Intent(context, UnlockActivity.class);
                        unlockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(unlockIntent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling boot completed", e);
                }
            });
        }
    }
    private void disableNotificationBlocking(Context context) {
        SharedPreferencesHelper prefsHelper = new SharedPreferencesHelper(context);
        prefsHelper.setBlockingActive(true);
        Toast.makeText(context, "Notification blocking disabled 2", Toast.LENGTH_SHORT).show();
    }
}



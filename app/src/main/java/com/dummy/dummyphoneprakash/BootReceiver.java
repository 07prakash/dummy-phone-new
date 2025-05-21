package com.dummy.dummyphoneprakash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dummy.dummyphoneprakash.activity.UnlockActivity;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferencesHelper prefsHelper = new SharedPreferencesHelper(context);

            if (prefsHelper.isTimerActive()) {
                // Restart the unlock activity
                Intent unlockIntent = new Intent(context, UnlockActivity.class);
                unlockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(unlockIntent);
            }
        }
    }
}
package com.dummy.dummyphoneprakash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Service to handle midnight reset of daily usage counters
 */
public class MidnightResetService extends Service {
    private static final String TAG = "MidnightResetService";
    private static final String ACTION_MIDNIGHT_RESET = "com.dummy.dummyphoneprakash.MIDNIGHT_RESET";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_MIDNIGHT_RESET.equals(intent.getAction())) {
            performMidnightReset();
        } else {
            scheduleMidnightReset();
        }
        
        return START_STICKY; // Restart service if killed
    }

    /**
     * Schedule the next midnight reset
     */
    private void scheduleMidnightReset() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        // Create intent for midnight reset
        Intent resetIntent = new Intent(this, MidnightResetService.class);
        resetIntent.setAction(ACTION_MIDNIGHT_RESET);
        
        PendingIntent pendingIntent = PendingIntent.getService(
            this, 
            0, 
            resetIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Calculate next midnight
        Calendar midnight = Calendar.getInstance();
        midnight.add(Calendar.DAY_OF_MONTH, 1);
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        // Schedule exact alarm for midnight
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                midnight.getTimeInMillis(),
                pendingIntent
            );
            
            Log.d(TAG, "Midnight reset scheduled for: " + midnight.getTime());
        }
    }

    /**
     * Perform the actual midnight reset
     */
    private void performMidnightReset() {
        Log.d(TAG, "Performing midnight reset of daily usage counters");
        
        try {
            SharedPreferencesHelper prefsHelper = new SharedPreferencesHelper(this);
            prefsHelper.resetDailyCounters();
            
            // Show toast notification
            Toast.makeText(this, "🌙 Daily usage counters reset - New day started!", Toast.LENGTH_LONG).show();
            
            Log.d(TAG, "Daily counters reset successfully");
            
            // Schedule next midnight reset
            scheduleMidnightReset();
            
        } catch (Exception e) {
            Log.e(TAG, "Error during midnight reset", e);
        }
        
        // Stop the service
        stopSelf();
    }

    /**
     * Static method to start the midnight reset service
     */
    public static void startMidnightResetService(Context context) {
        Intent serviceIntent = new Intent(context, MidnightResetService.class);
        context.startService(serviceIntent);
    }
}

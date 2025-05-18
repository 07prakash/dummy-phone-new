package com.dummy.dummyphoneprakash;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationBlockerService extends NotificationListenerService {
    private static final String TAG = "NotificationBlocker";
    private SharedPreferencesHelper prefsHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        prefsHelper = new SharedPreferencesHelper(this);
        Log.d(TAG, "Notification blocker service created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if (shouldBlockNotification(packageName)) {
            try {
                cancelNotification(sbn.getKey());
                Log.d(TAG, "Blocked notification from: " + packageName);
            } catch (Exception e) {
                Log.e(TAG, "Error blocking notification", e);
            }
        }
    }

    private boolean shouldBlockNotification(String packageName) {
        // Check if blocking is active
        if (!prefsHelper.isBlockingActive()) {
            return false;
        }

        // Don't block if app is allowed or essential
        return !prefsHelper.isAppAllowed(packageName);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Not needed for this implementation
    }
}
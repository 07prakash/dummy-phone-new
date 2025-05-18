package com.dummy.dummyphoneprakash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.widget.Toast;

public class NotificationPermissionHelper {
    private static final String TAG = "NotifPermissionHelper";

    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                showPermissionExplanation(activity);
            } catch (Exception e) {
                Toast.makeText(activity,
                        "Please enable notification access in settings",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private static void showPermissionExplanation(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Notification Access Required")
                .setMessage("To block notifications from unchecked apps, please enable notification access for this app.")
                .setPositiveButton("Enable", (dialog, which) -> {
                    openNotificationSettings(activity);
                })
                .setNegativeButton("Later", null)
                .show();
    }

    private static void openNotificationSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        activity.startActivity(intent);
    }

    public static boolean isNotificationServiceEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            String enabledListeners = Settings.Secure.getString(
                    context.getContentResolver(),
                    "enabled_notification_listeners"
            );
            ComponentName myComponent = new ComponentName(context, NotificationBlockerService.class);
            return enabledListeners != null && enabledListeners.contains(myComponent.flattenToString());
        }
        return false;
    }

    public static void ensureServiceBound(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                NotificationListenerService.requestRebind(
                        new ComponentName(context, NotificationBlockerService.class)
                );
            } catch (Exception e) {
                Log.e(TAG, "Error binding notification service", e);
            }
        }
    }
}
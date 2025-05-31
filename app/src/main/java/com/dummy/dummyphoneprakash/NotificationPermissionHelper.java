package com.dummy.dummyphoneprakash;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
        // Create dialog with custom layout
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.notification_dialog); // Use your XML layout name here

        // Set dialog properties
        dialog.setCancelable(false);

        // Configure dialog window
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        // Initialize buttons
        Button exitButton = dialog.findViewById(R.id.exitButton);
        Button enableButton = dialog.findViewById(R.id.enableButton);

        // Set button click listeners
        exitButton.setOnClickListener(v -> {
            try {
                Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
                activity.startActivity(homeSettingsIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "Settings not available", Toast.LENGTH_SHORT).show();
            }
            activity.finish();
            dialog.dismiss();
        });

        enableButton.setOnClickListener(v -> {
            openNotificationSettings(activity);
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
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
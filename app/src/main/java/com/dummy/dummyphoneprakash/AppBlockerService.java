package com.dummy.dummyphoneprakash;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.Set;

public class AppBlockerService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName() != null ?
                    event.getPackageName().toString() : null;

            if (packageName != null && isAppBlocked(packageName)) {
                performGlobalAction(GLOBAL_ACTION_HOME);
                Toast.makeText(getApplicationContext(), "Current: " + packageName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isAppBlocked(String packageName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // First check if app is in the unblock_always list
        Set<String> unblockAlwaysApps = prefs.getStringSet("unblock_always", null);
        if (unblockAlwaysApps != null && unblockAlwaysApps.contains(packageName)) {
            return false; // Never block apps in this list
        }

        // Check if blocking is still active
        boolean isLocked = prefs.getBoolean("is_locked", false);
        if (!isLocked) {
            return false; // Blocking has ended
        }

        Set<String> allowedApps = prefs.getStringSet("allowed_apps", null);
        Set<String> essentialApps = prefs.getStringSet("essential_apps", null);

        // Block if app is not in allowed or essential lists
        return !(allowedApps != null && allowedApps.contains(packageName)) &&
                !(essentialApps != null && essentialApps.contains(packageName));
    }

    @Override
    public void onInterrupt() {}

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        setServiceInfo(info);
    }
}
package com.dummy.dummyphoneprakash;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Set;

public class AppBlockerService extends AccessibilityService {
    private static final String TAG = "AppBlockerService";
    private SharedPreferencesHelper prefsHelper;
    private Handler handler;
    private long lastScrollTime = 0;
    private static final long SCROLL_COOLDOWN = 1000; // 1 second cooldown between scroll blocks

    @Override
    public void onCreate() {
        super.onCreate();
        prefsHelper = new SharedPreferencesHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String packageName = event.getPackageName() != null ? event.getPackageName().toString() : null;
        
        if (packageName == null) return;

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                handleWindowStateChanged(packageName);
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                handleViewScrolled(packageName, event);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                handleViewClicked(packageName, event);
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                handleViewFocused(packageName, event);
                break;
        }
    }

    private void handleWindowStateChanged(String packageName) {
        if (isAppBlocked(packageName)) {
            performGlobalAction(GLOBAL_ACTION_HOME);
            Toast.makeText(getApplicationContext(), "App blocked: " + packageName, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleViewScrolled(String packageName, AccessibilityEvent event) {
        if (!prefsHelper.isScrollBlockingEnabled() || !prefsHelper.isAppScrollBlocked(packageName)) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScrollTime < SCROLL_COOLDOWN) {
            return; // Still in cooldown period
        }

        // Block scroll gesture
        blockScrollGesture();
        lastScrollTime = currentTime;
        
        Log.d(TAG, "Scroll blocked in: " + packageName);
        Toast.makeText(getApplicationContext(), "Scroll blocked", Toast.LENGTH_SHORT).show();
    }

    private void handleViewClicked(String packageName, AccessibilityEvent event) {
        if (!prefsHelper.isShortVideoBlockingEnabled() || !prefsHelper.isAppScrollBlocked(packageName)) {
            return;
        }

        // Check if the click is on a video element
        AccessibilityNodeInfo source = event.getSource();
        if (source != null) {
            if (isVideoElement(source)) {
                blockVideoInteraction();
                Log.d(TAG, "Video interaction blocked in: " + packageName);
                Toast.makeText(getApplicationContext(), "Video interaction blocked", Toast.LENGTH_SHORT).show();
            }
            source.recycle();
        }
    }

    private void handleViewFocused(String packageName, AccessibilityEvent event) {
        if (!prefsHelper.isShortVideoBlockingEnabled() || !prefsHelper.isAppScrollBlocked(packageName)) {
            return;
        }

        // Check if focus is on a video element
        AccessibilityNodeInfo source = event.getSource();
        if (source != null) {
            if (isVideoElement(source)) {
                // Delay the block to allow user to see the focus
                handler.postDelayed(() -> {
                    blockVideoInteraction();
                    Log.d(TAG, "Video focus blocked in: " + packageName);
                    Toast.makeText(getApplicationContext(), "Video focus blocked", Toast.LENGTH_SHORT).show();
                }, 500);
            }
            source.recycle();
        }
    }

    private boolean isVideoElement(AccessibilityNodeInfo node) {
        if (node == null) return false;

        // Check for video-related class names and content descriptions
        String className = node.getClassName() != null ? node.getClassName().toString() : "";
        String contentDesc = node.getContentDescription() != null ? node.getContentDescription().toString() : "";
        String text = node.getText() != null ? node.getText().toString() : "";

        // Common video element identifiers
        String[] videoKeywords = {
            "video", "player", "media", "play", "pause", "reel", "story", "short",
            "VideoView", "MediaPlayer", "ExoPlayer", "PlayerView", "VideoPlayer",
            "reels", "stories", "shorts", "feed", "timeline", "stream"
        };

        String combinedText = (className + " " + contentDesc + " " + text).toLowerCase();
        
        for (String keyword : videoKeywords) {
            if (combinedText.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        // Check child nodes recursively
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (isVideoElement(child)) {
                    child.recycle();
                    return true;
                }
                child.recycle();
            }
        }

        return false;
    }

    private void blockScrollGesture() {
        // Perform a small gesture to interrupt scrolling
        try {
            // Get screen dimensions
            Rect screenBounds = new Rect();
            getWindow().getRootInActiveWindow().getBoundsInScreen(screenBounds);
            
            int centerX = screenBounds.centerX();
            int centerY = screenBounds.centerY();
            
            // Create a small tap gesture to interrupt scroll
            Path tapPath = new Path();
            tapPath.moveTo(centerX, centerY);
            
            GestureDescription.Builder builder = new GestureDescription.Builder();
            builder.addStroke(new GestureDescription.StrokeDescription(tapPath, 0, 100));
            
            dispatchGesture(builder.build(), null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error blocking scroll gesture", e);
        }
    }

    private AccessibilityService getWindow() {
        return null;
    }

    private void blockVideoInteraction() {
        // Perform back action to exit video
        try {
            performGlobalAction(GLOBAL_ACTION_BACK);
        } catch (Exception e) {
            Log.e(TAG, "Error blocking video interaction", e);
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
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | 
                         AccessibilityEvent.TYPE_VIEW_SCROLLED |
                         AccessibilityEvent.TYPE_VIEW_CLICKED |
                         AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}
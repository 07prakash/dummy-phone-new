package com.dummy.dummyphoneprakash;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.SharedPreferences;
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
    private long lastVideoBlockTime = 0;
    private boolean isAnyBlockingInProgress = false; // GLOBAL flag to prevent ANY double back actions
    private static final long SCROLL_COOLDOWN = 1000; // 1 second cooldown between scroll blocks
    private static final long VIDEO_COOLDOWN = 1000; // 1 second cooldown between video blocks
    private static final long GLOBAL_BLOCK_DURATION = 800; // Global block duration to prevent overlaps

    @Override
    public void onCreate() {
        super.onCreate();
        prefsHelper = new SharedPreferencesHelper(this);
        handler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "AppBlockerService created");
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
        Log.d(TAG, "Scroll detected in: " + packageName);
        
        // 🛑 GLOBAL CHECK: Prevent any action if another blocking is in progress
        if (isAnyBlockingInProgress) {
            Log.d(TAG, "Another blocking action in progress, ignoring scroll");
            return;
        }
        
        // 🔍 Check if main blocking is active first
        if (!prefsHelper.isBlockingActive()) {
            Log.d(TAG, "Main blocking not active - scroll allowed");
            return;
        }
        
        // 🔍 Check if scroll blocking is enabled
        if (!prefsHelper.isScrollBlockingEnabled()) {
            Log.d(TAG, "Scroll blocking disabled - scroll allowed");
            return;
        }
        
        // 🔍 Check if app is in scroll blocked list
        if (!prefsHelper.isAppScrollBlocked(packageName)) {
            Log.d(TAG, "App not in scroll blocked list: " + packageName);
            return;
        }

        // 🔍 Check cooldown period
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScrollTime < SCROLL_COOLDOWN) {
            Log.d(TAG, "Cooldown active, ignoring scroll");
            return;
        }

        // ✅ All conditions met - block the scroll
        Log.d(TAG, "All conditions met - blocking scroll for: " + packageName);
        executeBlockAction("SCROLL", packageName);
        lastScrollTime = currentTime;
    }

    private void handleViewClicked(String packageName, AccessibilityEvent event) {
        // 🛑 GLOBAL CHECK: Prevent any action if another blocking is in progress
        if (isAnyBlockingInProgress) {
            Log.d(TAG, "Another blocking action in progress, ignoring click");
            return;
        }
        
        // 🔍 Check if main blocking is active first
        if (!prefsHelper.isBlockingActive()) {
            return;
        }
        
        // 🔍 Check if video blocking is enabled
        if (!prefsHelper.isShortVideoBlockingEnabled()) {
            return;
        }
        
        // 🔍 Check if app is in blocked list
        if (!prefsHelper.isAppScrollBlocked(packageName)) {
            return;
        }

        // 🔍 Check cooldown period
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastVideoBlockTime < VIDEO_COOLDOWN) {
            Log.d(TAG, "Video block cooldown active, ignoring");
            return;
        }

        // Check if the click is on a video element
        AccessibilityNodeInfo source = event.getSource();
        if (source != null) {
            if (isVideoElement(source)) {
                executeBlockAction("VIDEO_CLICK", packageName);
                lastVideoBlockTime = currentTime;
            }
            source.recycle();
        }
    }

    private void handleViewFocused(String packageName, AccessibilityEvent event) {
        // 🛑 GLOBAL CHECK: Prevent any action if another blocking is in progress
        if (isAnyBlockingInProgress) {
            Log.d(TAG, "Another blocking action in progress, ignoring focus");
            return;
        }
        
        // 🔍 Check if main blocking is active first
        if (!prefsHelper.isBlockingActive()) {
            return;
        }
        
        // 🔍 Check if video blocking is enabled
        if (!prefsHelper.isShortVideoBlockingEnabled()) {
            return;
        }
        
        // 🔍 Check if app is in blocked list
        if (!prefsHelper.isAppScrollBlocked(packageName)) {
            return;
        }

        // Check if focus is on a video element
        AccessibilityNodeInfo source = event.getSource();
        if (source != null) {
            if (isVideoElement(source)) {
                // Delay the block to allow user to see the focus
                handler.postDelayed(() -> {
                    if (!isAnyBlockingInProgress) { // Check flag before blocking
                        executeBlockAction("VIDEO_FOCUS", packageName);
                    }
                }, 500);
            }
            source.recycle();
        }
    }

    /**
     * SINGLE METHOD to execute back action - prevents any double calls
     */
    private void executeBlockAction(String actionType, String packageName) {
        Log.d(TAG, "Executing " + actionType + " block - performing SINGLE back action for: " + packageName);
        
        // 🛑 Set GLOBAL flag to prevent ANY other back actions
        isAnyBlockingInProgress = true;
        
        try {
            // Perform EXACTLY ONE back action
            boolean success = performGlobalAction(GLOBAL_ACTION_BACK);
            Log.d(TAG, "Single back action result: " + success + " (Type: " + actionType + ")");
            
            // Show appropriate toast
            String message = "";
            switch (actionType) {
                case "SCROLL":
                    message = "🛑 Scroll blocked!";
                    break;
                case "VIDEO_CLICK":
                    message = "🎥 Video click blocked!";
                    break;
                case "VIDEO_FOCUS":
                    message = "🎥 Video focus blocked!";
                    break;
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error performing back action for " + actionType, e);
        } finally {
            // ALWAYS reset the global flag after a delay
            handler.postDelayed(() -> {
                isAnyBlockingInProgress = false;
                Log.d(TAG, "Global blocking flag reset after " + actionType);
            }, GLOBAL_BLOCK_DURATION);
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
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "AccessibilityService connected");
        
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | 
                         
                         AccessibilityEvent.TYPE_VIEW_CLICKED |
                         AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        
        // Log initial state for debugging
        Log.d(TAG, "Service configured successfully");
        Log.d(TAG, "Initial state - Blocking active: " + prefsHelper.isBlockingActive() + 
                   ", Scroll blocking enabled: " + prefsHelper.isScrollBlockingEnabled());
                   
        // Show service ready toast
        handler.post(() -> {
            Toast.makeText(getApplicationContext(), "🚀 Single Back Action Ready", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AppBlockerService destroyed");
    }
}
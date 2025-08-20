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
    private long lastVideoBlockTime = 0;
    private boolean isAnyBlockingInProgress = false;
    private static final long VIDEO_COOLDOWN = 1000;
    private static final long GLOBAL_BLOCK_DURATION = 800;

    // Usage tracking variables
    private String currentApp = "";

    @Override
    public void onCreate() {
        super.onCreate();
        prefsHelper = new SharedPreferencesHelper(this);
        handler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "AppBlockerService created with PER-APP granular blocking");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String packageName = event.getPackageName() != null ? event.getPackageName().toString() : null;
        
        if (packageName == null) return;

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                handleWindowStateChanged(packageName);
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
        // Handle app switching for usage tracking
        handleAppSwitch(packageName);
        
        // NEW LOGIC: Only block regular apps, NEVER block short video apps entirely
        if (prefsHelper.shouldBlockEntireApp(packageName)) {
            performGlobalAction(GLOBAL_ACTION_HOME);
            Toast.makeText(getApplicationContext(), "App blocked: " + packageName, Toast.LENGTH_SHORT).show();
        }
        // Note: Short video apps are NEVER blocked at the app level
    }

    /**
     * Handle app switching and usage tracking
     */
    private void handleAppSwitch(String newPackageName) {
        if (newPackageName.equals(currentApp)) {
            return; // Same app, no switch
        }
        
        // Stop tracking previous app
        if (!currentApp.isEmpty() && prefsHelper.isShortVideoApp(currentApp)) {
            prefsHelper.stopAppUsageTracking(currentApp);
            Log.d(TAG, "Stopped tracking: " + currentApp);
        }
        
        // Start tracking new app (only if it's a short video app and during locked session)
        if (prefsHelper.isShortVideoApp(newPackageName) && prefsHelper.isBlockingActive()) {
            prefsHelper.startAppUsageTracking(newPackageName);
            Log.d(TAG, "Started tracking: " + newPackageName);
            
            // Show per-app usage info
            showPerAppUsageInfo(newPackageName);
        }
        
        currentApp = newPackageName;
    }

    /**
     * Show per-app usage information when opening a short video app
     */
    private void showPerAppUsageInfo(String packageName) {
        if (!prefsHelper.isBlockingActive()) {
            return;
        }
        
        long appUsage = prefsHelper.getDailyUsage(packageName);
        long appRemaining = prefsHelper.getRemainingTimeForApp(packageName);
        
        String appName = getAppDisplayName(packageName);
        String usageStr = prefsHelper.getFormattedUsageTime(appUsage);
        String remainingStr = prefsHelper.getFormattedUsageTime(appRemaining);
        
        handler.postDelayed(() -> {
            if (prefsHelper.isAppLimitReached(packageName)) {
                Toast.makeText(getApplicationContext(), 
                    "🚫 " + appName + " short videos blocked!\n📱 App accessible for other features\n⏰ Used: " + usageStr + " / 1m", 
                    Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), 
                    "📊 " + appName + " - Used: " + usageStr + " | Remaining: " + remainingStr, 
                    Toast.LENGTH_LONG).show();
            }
        }, 1000);
    }

    private String getAppDisplayName(String packageName) {
        switch (packageName) {
            case "com.google.android.youtube": return "YouTube";
            case "com.instagram.android": return "Instagram";
            case "com.zhiliaoapp.musically":
            case "com.ss.android.ugc.tiktok": return "TikTok";
            case "com.facebook.katana": return "Facebook";
            case "com.snapchat.android": return "Snapchat";
            case "com.twitter.android": return "Twitter";
            case "com.reddit.frontpage": return "Reddit";
            case "com.pinterest": return "Pinterest";
            default: return packageName.substring(packageName.lastIndexOf('.') + 1);
        }
    }

    private void handleViewClicked(String packageName, AccessibilityEvent event) {
        if (isAnyBlockingInProgress) {
            Log.d(TAG, "Another blocking action in progress, ignoring click");
            return;
        }
        
        // NEW PER-APP LOGIC: Handle short video apps vs regular apps differently
        if (prefsHelper.isShortVideoApp(packageName)) {
            // For short video apps: Only block if THIS SPECIFIC APP reached its limit
            if (!prefsHelper.shouldBlockShortVideoFeatures(packageName)) {
                Log.d(TAG, "Short video app " + packageName + " - limit not reached for THIS app, allowing");
                return;
            }
            
            // Check if click is on a short video element
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                if (isShortVideoElement(source)) {
                    String appName = getAppDisplayName(packageName);
                    executeBlockAction("SHORT_VIDEO_FEATURE", packageName, appName + " short videos");
                    Log.d(TAG, "Short video feature blocked in: " + packageName);
                }
                source.recycle();
            }
        } else {
            // For regular apps: Use normal scroll blocking logic
            if (!prefsHelper.shouldBlockScrollForApp(packageName)) {
                return;
            }
            
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                if (isVideoElement(source)) {
                    executeBlockAction("REGULAR_VIDEO", packageName, "Video");
                }
                source.recycle();
            }
        }
    }

    private void handleViewFocused(String packageName, AccessibilityEvent event) {
        if (isAnyBlockingInProgress) {
            Log.d(TAG, "Another blocking action in progress, ignoring focus");
            return;
        }
        
        // NEW PER-APP LOGIC: Handle short video apps vs regular apps differently
        if (prefsHelper.isShortVideoApp(packageName)) {
            // For short video apps: Only block if THIS SPECIFIC APP reached its limit
            if (!prefsHelper.shouldBlockShortVideoFeatures(packageName)) {
                return;
            }
            
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                if (isShortVideoElement(source)) {
                    handler.postDelayed(() -> {
                        if (!isAnyBlockingInProgress) {
                            String appName = getAppDisplayName(packageName);
                            executeBlockAction("SHORT_VIDEO_FOCUS", packageName, appName + " short video focus");
                        }
                    }, 500);
                }
                source.recycle();
            }
        } else {
            // For regular apps: Use normal video blocking logic
            if (!prefsHelper.shouldBlockScrollForApp(packageName)) {
                return;
            }
            
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                if (isVideoElement(source)) {
                    handler.postDelayed(() -> {
                        if (!isAnyBlockingInProgress) {
                            executeBlockAction("REGULAR_VIDEO_FOCUS", packageName, "Video focus");
                        }
                    }, 500);
                }
                source.recycle();
            }
        }
    }

    /**
     * Enhanced detection for short video elements (Reels, Shorts, Stories)
     */
    private boolean isShortVideoElement(AccessibilityNodeInfo node) {
        if (node == null) return false;

        String className = node.getClassName() != null ? node.getClassName().toString() : "";
        String contentDesc = node.getContentDescription() != null ? node.getContentDescription().toString() : "";
        String text = node.getText() != null ? node.getText().toString() : "";
        String viewId = node.getViewIdResourceName() != null ? node.getViewIdResourceName() : "";

        // Enhanced keywords specifically for short video features
        String[] shortVideoKeywords = {
            // Generic short video terms
            "reel", "reels", "short", "shorts", "story", "stories", "feed",
            
            // YouTube Shorts specific
            "shorts", "short_video", "shorts_player", "shorts_shelf", "shorts_tab",
            
            // Instagram Reels specific
            "reel", "reels", "reels_viewer", "reel_video", "story_video",
            "clips_viewer", "video_player_layout", "reels_tab",
            
            // TikTok specific (most content is short video)
            "aweme", "video_view", "video_player", "feed_video", "for_you",
            
            // Facebook Reels specific
            "video_attachment", "video_player_container", "reel_video",
            
            // Snapchat Stories specific
            "story", "snap", "story_video", "camera_preview",
            
            // General video player terms for short content
            "VideoView", "MediaPlayer", "ExoPlayer", "PlayerView", "VideoPlayer"
        };

        String combinedText = (className + " " + contentDesc + " " + text + " " + viewId).toLowerCase();
        
        // Check for short video specific keywords
        for (String keyword : shortVideoKeywords) {
            if (combinedText.contains(keyword.toLowerCase())) {
                Log.d(TAG, "Short video element detected: " + keyword + " in " + combinedText);
                return true;
            }
        }

        // Check child nodes recursively for short video elements
        return isShortVideoElementInChildren(node, 0, 2);
    }

    private boolean isShortVideoElementInChildren(AccessibilityNodeInfo node, int currentDepth, int maxDepth) {
        if (node == null || currentDepth >= maxDepth) return false;
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                try {
                    String childClassName = child.getClassName() != null ? child.getClassName().toString() : "";
                    String childDesc = child.getContentDescription() != null ? child.getContentDescription().toString() : "";
                    
                    String childText = (childClassName + " " + childDesc).toLowerCase();
                    if (childText.contains("reel") || childText.contains("short") || 
                        childText.contains("story") || childText.contains("video")) {
                        return true;
                    }
                    
                    if (isShortVideoElementInChildren(child, currentDepth + 1, maxDepth)) {
                        return true;
                    }
                } finally {
                    child.recycle();
                }
            }
        }
        
        return false;
    }

    /**
     * Regular video element detection (for non-short-video apps)
     */
    private boolean isVideoElement(AccessibilityNodeInfo node) {
        if (node == null) return false;

        String className = node.getClassName() != null ? node.getClassName().toString() : "";
        String contentDesc = node.getContentDescription() != null ? node.getContentDescription().toString() : "";
        String text = node.getText() != null ? node.getText().toString() : "";

        String[] videoKeywords = {
            "video", "player", "media", "play", "pause",
            "VideoView", "MediaPlayer", "ExoPlayer", "PlayerView", "VideoPlayer"
        };

        String combinedText = (className + " " + contentDesc + " " + text).toLowerCase();
        
        for (String keyword : videoKeywords) {
            if (combinedText.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * SINGLE METHOD to execute back action - prevents any double calls
     */
    private void executeBlockAction(String actionType, String packageName, String featureDescription) {
        Log.d(TAG, "Executing " + actionType + " block - performing SINGLE back action for: " + packageName + " (" + featureDescription + ")");
        
        isAnyBlockingInProgress = true;
        
        try {
            boolean success = performGlobalAction(GLOBAL_ACTION_BACK);
            Log.d(TAG, "Single back action result: " + success + " (Type: " + actionType + ")");
            
            // Show appropriate toast based on action type
            String message = "";
            switch (actionType) {
                case "SHORT_VIDEO_FEATURE":
                    message = "🚫 " + featureDescription + " blocked (1min limit reached)";
                    break;
                case "SHORT_VIDEO_FOCUS":
                    message = "🚫 " + featureDescription + " blocked";
                    break;
                case "REGULAR_VIDEO":
                    message = "🛑 " + featureDescription + " blocked";
                    break;
                case "REGULAR_VIDEO_FOCUS":
                    message = "🛑 " + featureDescription + " blocked";
                    break;
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error performing back action for " + actionType, e);
        } finally {
            handler.postDelayed(() -> {
                isAnyBlockingInProgress = false;
                Log.d(TAG, "Global blocking flag reset after " + actionType);
            }, GLOBAL_BLOCK_DURATION);
        }
    }

    private boolean isAppBlocked(String packageName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Set<String> unblockAlwaysApps = prefs.getStringSet("unblock_always", null);
        if (unblockAlwaysApps != null && unblockAlwaysApps.contains(packageName)) {
            return false;
        }

        boolean isLocked = prefs.getBoolean("is_locked", false);
        if (!isLocked) {
            return false;
        }

        Set<String> allowedApps = prefs.getStringSet("allowed_apps", null);
        Set<String> essentialApps = prefs.getStringSet("essential_apps", null);

        return !(allowedApps != null && allowedApps.contains(packageName)) &&
                !(essentialApps != null && essentialApps.contains(packageName));
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
        if (!currentApp.isEmpty() && prefsHelper.isShortVideoApp(currentApp)) {
            prefsHelper.stopAppUsageTracking(currentApp);
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "AccessibilityService connected with PER-APP granular blocking");
        
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | 
                         AccessibilityEvent.TYPE_VIEW_CLICKED |
                         AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        
        Log.d(TAG, "Service configured for PER-APP feature-level blocking");
        
        // Show per-app status
        handler.post(() -> {
            StringBuilder status = new StringBuilder("📊 Per-App Limits (1min each):\n");
            
            boolean anyBlocked = false;
            for (String app : new String[]{"com.google.android.youtube", "com.instagram.android", "com.zhiliaoapp.musically"}) {
                if (prefsHelper.isAppLimitReached(app)) {
                    String appName = getAppDisplayName(app);
                    status.append("🚫 ").append(appName).append(" ");
                    anyBlocked = true;
                }
            }
            
            if (anyBlocked) {
                status.append("blocked");
            } else {
                status.append("All available");
            }
            
            Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AppBlockerService destroyed");
        
        if (!currentApp.isEmpty() && prefsHelper.isShortVideoApp(currentApp)) {
            prefsHelper.stopAppUsageTracking(currentApp);
            Log.d(TAG, "Stopped tracking on destroy: " + currentApp);
        }
    }
}
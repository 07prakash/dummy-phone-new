package com.dummy.dummyphoneprakash;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SharedPreferencesHelper {
    private static final String KEY_ALLOWED_APPS = "allowed_apps";
    private static final String KEY_ESSENTIAL_APPS = "essential_apps";
    private static final String KEY_IS_LOCKED = "is_locked";

    private static final String KEY_UNBLOCK_ALWAYS = "unblock_always";

    // Scroll blocking keys
    private static final String KEY_SCROLL_BLOCKING_ENABLED = "scroll_blocking_enabled";
    private static final String KEY_SCROLL_BLOCKED_APPS = "scroll_blocked_apps";
    private static final String KEY_SHORT_VIDEO_BLOCKING_ENABLED = "short_video_blocking_enabled";

    private static final String KEY_TARGET_END_TIME = "target_end_time";

    // Usage tracking keys (PER-APP)
    private static final String KEY_DAILY_USAGE_PREFIX = "daily_usage_";
    private static final String KEY_LAST_RESET_DATE = "last_reset_date";
    private static final String KEY_APP_LIMIT_REACHED_PREFIX = "app_limit_reached_"; // PER-APP limit
    private static final String KEY_APP_SESSION_START = "app_session_start_";
    private static final long DAILY_LIMIT_MINUTES = 1; // 1 minute per app
    private static final long DAILY_LIMIT_MILLIS = DAILY_LIMIT_MINUTES * 60 * 1000L;

    // Short video apps that should be tracked individually
    private static final Set<String> SHORT_VIDEO_APPS = new HashSet<String>() {{
        add("com.google.android.youtube");    // YouTube (only Shorts blocked)
        add("com.instagram.android");         // Instagram (only Reels blocked)
        add("com.zhiliaoapp.musically");      // TikTok (short videos blocked)
        add("com.ss.android.ugc.tiktok");     // TikTok (alternative package)
        add("com.ss.android.ugc.aweme");      // Douyin
        add("com.ss.android.ugc.aweme.lite"); // Douyin Lite
        add("com.kwai.video");                // Kuaishou
        add("com.kwai.video.lite");           // Kuaishou Lite
        add("com.facebook.katana");           // Facebook (only Reels blocked)
        add("com.snapchat.android");          // Snapchat (only Stories blocked)
        add("com.reddit.frontpage");          // Reddit (only video posts blocked)
        add("com.twitter.android");           // Twitter/X (only video posts blocked)
        add("com.pinterest");                 // Pinterest (only video pins blocked)
        add("com.lemon.lv");                  // Lemon8
        add("com.bytedance.lemon8");          // Lemon8 (alternative)
        add("com.ss.android.ugc.trill");      // Triller
        add("com.musical.ly");                // Musical.ly (old TikTok)
    }};

    // Default essential apps that cannot be blocked
    private static final Set<String> DEFAULT_ESSENTIAL_APPS = new HashSet<String>() {{
        add("com.android.dialer");
        add("com.android.contacts");
        add("com.android.mms");
        add("com.google.android.apps.maps");
        add("com.waze");
        add("com.huawei.maps.app");
        add("com.google.android.apps.messaging");
        add("com.samsung.android.messaging");
        add("com.miui.mms");
        add("com.coloros.mms");
        add("com.vivo.messaging");
        add("com.google.android.dialer");
        add("com.samsung.android.dialer");
        add("com.coloros.dialer");
        add("com.motorola.dialer");
        add("com.google.android.contacts");
        add("com.samsung.android.contacts");
        add("com.miui.contacts");
        add("com.coloros.contacts");
        add("com.vivo.contact");
        add("com.google.android.deskclock");
        add("com.sec.android.app.clockpackage");
        add("com.miui.clock");
        add("com.oneplus.deskclock");
        add("com.coloros.alarmclock");
        add("com.vivo.alarmclock");
        add("com.huawei.deskclock");
        add("com.motorola.deskclock");
        add("com.android.calculator2");
        add("com.sec.android.app.popupcalculator");
        add("com.miui.calculator");
        add("com.oneplus.calculator");
        add("com.coloros.calculator");
        add("com.vivo.calculator");
        add("com.huawei.calculator");
        add("com.motorola.calculator");
        add("com.dummy.dummyphoneprakash");
    }};

    // Default apps that should never be blocked (even beyond essentials)
    private static final Set<String> DEFAULT_UNBLOCK_ALWAYS = new HashSet<String>() {{
        add("com.dummy.dummyphoneprakash");
        add("com.sec.android.gallery3d");
        add("com.google.android.apps.photos");
        add("com.android.gallery3d");
        add("com.samsung.android.gallery.app");
        add("com.miui.gallery");
        add("com.android.gallery");
        add("com.coloros.gallery3d");
        add("com.oneplus.gallery");
        add("com.vivo.gallery");
        add("com.bbk.gallery");
        add("com.sonyericsson.album");
        add("com.lge.gallery");
        add("com.motorola.gallery");
        add("com.alensw.PicFolder");
        add("com.htc.album");
        add("com.cyngn.gallerynext");
        add("com.simplemobiletools.gallery.pro");
        add("com.android.inputmethod.latin");
        add("com.google.android.inputmethod.latin");
        add("com.samsung.android.honeyboard");
        add("com.sec.android.inputmethod");
        add("com.sohu.inputmethod.sogou.xiaomi");
        add("com.baidu.input_mi");
        add("com.vivo.ime");
        add("com.oppo.quickboard");
        add("com.coloros.engineer");
        add("com.oplus.quickboard");
        add("com.huawei.inputmethod");
        add("com.baidu.input_huawei");
        add("com.motorola.xt9.inputmethod");
        add("com.touchtype.swiftkey");
        add("com.emoji.keyboard.touchpal");
        add("com.syntellia.fleksy.keyboard");
        add("com.microsoft.swiftkey");
        add("com.sec.android.app.samsungapps");
        add("com.tencent.qqpinyin");
        add("com.sogou.inputmethod");
        add("com.zhihu.android");
        add("com.simplemobiletools.thankyou");
        add("com.rkkb.ime");
        add("com.sonyericsson.textinput.uxp");
        add("com.lge.ime");
        add("com.asus.ime");
        add("com.nuance.swype.dtc");
        add("com.android.systemui");
        add("com.google.android.systemui");
        add("com.miui.systemui");
        add("com.oplus.systemui");
        add("com.vivo.systemui");
        add("com.huawei.systemui");
        add("com.hihonor.systemui");
        add("com.realme.systemui");
        add("com.oneplus.systemui");
        add("com.coloros.systemui");
        add("com.motorola.systemui");
        add("com.sonymobile.systemui");
        add("com.lenovo.systemui");
        add("com.asus.systemui");
        add("com.samsung.android.systemui");
        add("com.transsion.systemui");
        add("com.nothing.systemui");
        add("com.zui.systemui");
        add("com.funtouch.systemui");
        add("com.rog.systemui");
        add("com.sec.android.app.launcher");
        add("com.sec.android.app.easylauncher");
        add("com.miui.home");
        add("com.mi.android.globallauncher");
        add("com.miui.systemAdSolution");
        add("net.oneplus.launcher");
        add("com.oppo.launcher");
        add("com.realme.launcher");
        add("com.coloros.launcher");
        add("com.bbk.launcher2");
        add("com.huawei.android.launcher");
        add("com.motorola.launcher");
        add("com.sonymobile.home");
        add("com.asus.launcher");
        add("com.lenovo.launcher");
        add("com.nothing.launcher");
        add("com.android.launcher3");
        add("com.google.android.apps.nexuslauncher");
        add("com.transsion.XOSLauncher");
        add("com.itel.launcher");
        add("com.tecno.hios.launcher");
        add("com.google.android.apps.go.launcher");
        add("com.android.quickstep");
        add("com.funtouch.launcher");
        add("com.zui.launcher");
        add("com.infinity.launcher");
        add("com.rog.launcher");
        add("com.htc.launcher");
        add("com.lge.launcher2");
        add("com.samsung.android.app.cocktailbarservice");
        add("com.samsung.android.biometrics.app.setting");
        add("com.samsung.android.biometrics.service");
        add("com.samsung.android.fingerprint.service");
        add("com.samsung.android.face.service");
        add("com.samsung.android.knox.containercore");
        add("com.android.facelock");
        add("com.android.fingerprint");
        add("com.android.biometrics.fingerprint.service");
        add("com.google.android.systemui.faceunlock");
        add("com.oplus.biometrics.fingerprint.service");
        add("com.oplus.faceunlock");
        add("com.coloros.faceunlock");
        add("com.coloros.fingerprint");
        add("com.vivo.fingerprint.service");
        add("com.vivo.face");
        add("com.vivo.abe");
        add("com.huawei.facerecognition");
        add("com.huawei.fingerprint.service");
        add("com.huawei.trustagent");
        add("com.goodix.fingerprint.service");
        add("com.fingerprints.fingerprintsensordemo");
        add("com.synaptics.fingerprint.service");
        add("com.xiaomi.face");
        add("com.xiaomi.fingerprint.service");
        add("com.miui.securitycenter");
        add("com.samsung.android.spay");
        add("com.google.android.gms");
        add("com.samsung.android.knox.attestation");
        add("com.huawei.systemmanager");
        add("com.miui.securitycore");
        add("com.coloros.safecenter");
        add("com.vivo.securepay");
        add("com.oneplus.security");
        add("com.zui.fingerprint");
        add("com.transsion.fingerprint.service");
        add("com.transsion.faceunlock");
        add("com.android.server.biometrics");
        add("com.qualcomm.qti.biometrics.fingerprint.service");
        add("com.mediatek.biometrics.fingerprint.service");
        add("com.android.keyguard");
        add("com.google.android.apps.wellbeing");
        add("com.google.android.as");
        add("com.android.providers.settings");
    }};

    // Regular apps that should have scroll blocking enabled (NOT short video apps)
    private static final Set<String> DEFAULT_SCROLL_BLOCKED_APPS = new HashSet<String>() {{
        // Regular social media apps (not short video focused)
        add("com.facebook.orca");             // Facebook Messenger
        add("com.whatsapp");                  // WhatsApp
        add("com.tencent.mm");                // WeChat
        add("com.tencent.qq");                // QQ
        add("com.tencent.mobileqq");          // QQ (alternative)
        add("com.sina.weibo");                // Weibo
        add("com.linkedin.android");          // LinkedIn
        add("com.ss.android.article.news");   // Toutiao
        add("com.netease.cloudmusic");        // NetEase Cloud Music
        add("com.tencent.music");             // QQ Music
        add("com.kugou.android");             // Kugou Music
        add("com.kuwo.kwmusiccar");           // Kuwo Music
        add("com.ximalaya.ting.android");     // Ximalaya
        add("com.qingting.fm");               // Qingting FM
        add("com.spotify.music");             // Spotify
        add("com.apple.android.music");       // Apple Music
        add("com.amazon.music.android");      // Amazon Music
        add("com.soundcloud.android");        // SoundCloud
        add("com.deezer.android");            // Deezer
        add("com.audible.application");       // Audible
        add("com.google.android.apps.podcasts"); // Google Podcasts
        add("com.podcastaddict");             // Podcast Addict
        add("com.anchor.fm");                 // Anchor
        add("com.stitcher.app");              // Stitcher
        add("com.overcast.fm");               // Overcast
        add("com.castro.fm");                 // Castro
        add("com.pocketcasts.android");       // Pocket Casts
        add("com.breaker.audio");             // Breaker
        add("com.radiopublic.android");       // RadioPublic
    }};

    private final SharedPreferences prefs;

    public SharedPreferencesHelper(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        initializeEssentialApps();
        initializeUnblockAlwaysApps();
        initializeScrollBlockedApps();
        initializeScrollBlockingSettings();
        checkAndResetDailyCounters();
    }

    private void initializeUnblockAlwaysApps() {
        if (!prefs.contains(KEY_UNBLOCK_ALWAYS)) {
            prefs.edit()
                    .putStringSet(KEY_UNBLOCK_ALWAYS, DEFAULT_UNBLOCK_ALWAYS)
                    .apply();
        }
    }

    private void initializeEssentialApps() {
        if (!prefs.contains(KEY_ESSENTIAL_APPS)) {
            prefs.edit()
                    .putStringSet(KEY_ESSENTIAL_APPS, DEFAULT_ESSENTIAL_APPS)
                    .apply();
        }
    }

    public void setBlockingActive(boolean isActive) {
        prefs.edit().putBoolean(KEY_IS_LOCKED, isActive).apply();
    }

    public boolean isBlockingActive() {
        return prefs.getBoolean(KEY_IS_LOCKED, false);
    }

    public void saveAllowedApps(Set<String> allowedApps, Set<String> essentialApps) {
        Set<String> filteredEssential = new HashSet<>(essentialApps);
        filteredEssential.retainAll(DEFAULT_ESSENTIAL_APPS);

        prefs.edit()
                .putStringSet(KEY_ALLOWED_APPS, new HashSet<>(allowedApps))
                .putStringSet(KEY_ESSENTIAL_APPS, filteredEssential)
                .apply();
    }

    public Set<String> getUnblockAlwaysApps() {
        return prefs.getStringSet(KEY_UNBLOCK_ALWAYS, new HashSet<>(DEFAULT_UNBLOCK_ALWAYS));
    }

    public void addToUnblockAlways(String packageName) {
        Set<String> unblockAlways = new HashSet<>(getUnblockAlwaysApps());
        unblockAlways.add(packageName);
        prefs.edit()
                .putStringSet(KEY_UNBLOCK_ALWAYS, unblockAlways)
                .apply();
    }

    public void removeFromUnblockAlways(String packageName) {
        Set<String> unblockAlways = new HashSet<>(getUnblockAlwaysApps());
        unblockAlways.remove(packageName);
        prefs.edit()
                .putStringSet(KEY_UNBLOCK_ALWAYS, unblockAlways)
                .apply();
    }

    public boolean isInUnblockAlways(String packageName) {
        return getUnblockAlwaysApps().contains(packageName);
    }

    public Set<String> getAllowedApps() {
        return prefs.getStringSet(KEY_ALLOWED_APPS, new HashSet<>());
    }

    public Set<String> getEssentialApps() {
        return prefs.getStringSet(KEY_ESSENTIAL_APPS, new HashSet<>());
    }

    public boolean isAppAllowed(String packageName) {
        if (isInUnblockAlways(packageName)) {
            return true;
        }

        Set<String> allowed = getAllowedApps();
        Set<String> essential = getEssentialApps();
        return allowed.contains(packageName) || essential.contains(packageName);
    }

    public void setTargetEndTime(long targetEndTime) {
        prefs.edit()
                .putLong(KEY_TARGET_END_TIME, targetEndTime)
                .putBoolean(KEY_IS_LOCKED, true)
                .apply();
    }

    public long getTargetEndTime() {
        return prefs.getLong(KEY_TARGET_END_TIME, 0);
    }

    public void cancelTimer() {
        prefs.edit()
                .remove(KEY_TARGET_END_TIME)
                .putBoolean(KEY_IS_LOCKED, false)
                .apply();
    }

    public boolean isTimerActive() {
        return prefs.getBoolean(KEY_IS_LOCKED, false) &&
                System.currentTimeMillis() < prefs.getLong(KEY_TARGET_END_TIME, 0);
    }

    // ==================== BLOCKING METHODS (PER-APP LOGIC) ====================
    
    public void setScrollBlockingEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SCROLL_BLOCKING_ENABLED, enabled).apply();
    }

    public boolean isScrollBlockingEnabled() {
        return prefs.getBoolean(KEY_SCROLL_BLOCKING_ENABLED, false);
    }

    public void setShortVideoBlockingEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SHORT_VIDEO_BLOCKING_ENABLED, enabled).apply();
    }

    public boolean isShortVideoBlockingEnabled() {
        return prefs.getBoolean(KEY_SHORT_VIDEO_BLOCKING_ENABLED, false);
    }

    public Set<String> getScrollBlockedApps() {
        return prefs.getStringSet(KEY_SCROLL_BLOCKED_APPS, new HashSet<>(DEFAULT_SCROLL_BLOCKED_APPS));
    }

    public void setScrollBlockedApps(Set<String> apps) {
        prefs.edit().putStringSet(KEY_SCROLL_BLOCKED_APPS, new HashSet<>(apps)).apply();
    }

    public void addScrollBlockedApp(String packageName) {
        Set<String> blockedApps = new HashSet<>(getScrollBlockedApps());
        blockedApps.add(packageName);
        setScrollBlockedApps(blockedApps);
    }

    public void removeScrollBlockedApp(String packageName) {
        Set<String> blockedApps = new HashSet<>(getScrollBlockedApps());
        blockedApps.remove(packageName);
        setScrollBlockedApps(blockedApps);
    }

    public boolean isAppScrollBlocked(String packageName) {
        return getScrollBlockedApps().contains(packageName);
    }

    // ==================== NEW PER-APP GRANULAR BLOCKING LOGIC ====================

    /**
     * Check if short video features should be blocked for a SPECIFIC app
     * NEW LOGIC: Each app has its own 1-minute limit
     */
    public boolean shouldBlockShortVideoFeatures(String packageName) {
        return isBlockingActive() && 
               isShortVideoApp(packageName) && 
               isAppLimitReached(packageName); // PER-APP limit check
    }

    /**
     * Check if regular scroll blocking should be active (non-short-video apps)
     */
    public boolean shouldBlockScrollForApp(String packageName) {
        return isBlockingActive() && 
               isScrollBlockingEnabled() && 
               isAppScrollBlocked(packageName) &&
               !isShortVideoApp(packageName); // Exclude short video apps from regular scroll blocking
    }

    /**
     * NEVER block entire short video apps - only their short video features
     */
    public boolean shouldBlockEntireApp(String packageName) {
        // Short video apps should NEVER be fully blocked
        if (isShortVideoApp(packageName)) {
            return false;
        }
        
        // Regular app blocking logic for non-short-video apps
        return isBlockingActive() && !isAppAllowed(packageName);
    }

    // ==================== PER-APP USAGE TRACKING METHODS ====================

    public boolean isShortVideoApp(String packageName) {
        return SHORT_VIDEO_APPS.contains(packageName);
    }

    private String getCurrentDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void checkAndResetDailyCounters() {
        String currentDate = getCurrentDateString();
        String lastResetDate = prefs.getString(KEY_LAST_RESET_DATE, "");
        
        if (!currentDate.equals(lastResetDate)) {
            resetDailyCounters();
            prefs.edit().putString(KEY_LAST_RESET_DATE, currentDate).apply();
        }
    }

    public void resetDailyCounters() {
        SharedPreferences.Editor editor = prefs.edit();
        
        for (String app : SHORT_VIDEO_APPS) {
            editor.remove(KEY_DAILY_USAGE_PREFIX + app);
            editor.remove(KEY_APP_LIMIT_REACHED_PREFIX + app);
            editor.remove(KEY_APP_SESSION_START + app);
        }
        
        // Reset short video blocking when counters reset
        editor.putBoolean(KEY_SHORT_VIDEO_BLOCKING_ENABLED, false);
        editor.apply();
    }

    public void startAppUsageTracking(String packageName) {
        if (!isShortVideoApp(packageName) || !isBlockingActive()) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        prefs.edit()
                .putLong(KEY_APP_SESSION_START + packageName, currentTime)
                .apply();
    }

    public void stopAppUsageTracking(String packageName) {
        if (!isShortVideoApp(packageName) || !isBlockingActive()) {
            return;
        }
        
        long sessionStart = prefs.getLong(KEY_APP_SESSION_START + packageName, 0);
        if (sessionStart == 0) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        long sessionDuration = currentTime - sessionStart;
        
        addToDailyUsage(packageName, sessionDuration);
        prefs.edit().remove(KEY_APP_SESSION_START + packageName).apply();
    }

    private void addToDailyUsage(String packageName, long durationMillis) {
        String today = getCurrentDateString();
        String key = KEY_DAILY_USAGE_PREFIX + packageName + "_" + today;
        
        long currentUsage = prefs.getLong(key, 0);
        long newUsage = currentUsage + durationMillis;
        
        prefs.edit().putLong(key, newUsage).apply();
        
        // Check if THIS SPECIFIC APP's limit exceeded for the first time
        if (newUsage >= DAILY_LIMIT_MILLIS && !isAppLimitReached(packageName)) {
            // Set limit reached for THIS SPECIFIC APP only
            setAppLimitReached(packageName, true);
        }
    }

    public long getDailyUsage(String packageName) {
        String today = getCurrentDateString();
        String key = KEY_DAILY_USAGE_PREFIX + packageName + "_" + today;
        return prefs.getLong(key, 0);
    }

    public long getTotalDailyUsage() {
        long totalUsage = 0;
        for (String app : SHORT_VIDEO_APPS) {
            totalUsage += getDailyUsage(app);
        }
        return totalUsage;
    }

    public long getRemainingDailyTime() {
        long totalUsage = getTotalDailyUsage();
        return Math.max(0, DAILY_LIMIT_MILLIS - totalUsage);
    }

    /**
     * Check if ANY app has reached its limit (for general UI display)
     */
    public boolean isDailyLimitReached() {
        return getTotalDailyUsage() >= DAILY_LIMIT_MILLIS;
    }

    /**
     * Check if a SPECIFIC app has reached its 1-minute limit
     */
    public boolean isAppLimitReached(String packageName) {
        String today = getCurrentDateString();
        String key = KEY_APP_LIMIT_REACHED_PREFIX + packageName + "_" + today;
        return prefs.getBoolean(key, false) || getDailyUsage(packageName) >= DAILY_LIMIT_MILLIS;
    }

    /**
     * Set limit reached status for a SPECIFIC app
     */
    private void setAppLimitReached(String packageName, boolean reached) {
        String today = getCurrentDateString();
        String key = KEY_APP_LIMIT_REACHED_PREFIX + packageName + "_" + today;
        prefs.edit().putBoolean(key, reached).apply();
    }

    /**
     * Get remaining time for a SPECIFIC app
     */
    public long getRemainingTimeForApp(String packageName) {
        long appUsage = getDailyUsage(packageName);
        return Math.max(0, DAILY_LIMIT_MILLIS - appUsage);
    }

    public String getFormattedUsageTime(long millis) {
        if (millis <= 0) return "0s";
        
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        
        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }

    public String getUsageReport() {
        StringBuilder report = new StringBuilder();
        report.append("📊 Per-App Short Video Limits:\n");
        report.append("Each app has a 1-minute daily limit\n\n");
        
        boolean anyLimitReached = false;
        
        report.append("App Status:\n");
        for (String app : SHORT_VIDEO_APPS) {
            long usage = getDailyUsage(app);
            if (usage > 0 || isPopularApp(app)) {
                String appName = getAppDisplayName(app);
                String usageStr = getFormattedUsageTime(usage);
                long remaining = getRemainingTimeForApp(app);
                String remainingStr = getFormattedUsageTime(remaining);
                
                if (isAppLimitReached(app)) {
                    report.append("🚫 ").append(appName).append(": ").append(usageStr)
                           .append(" (BLOCKED)\n");
                    anyLimitReached = true;
                } else {
                    report.append("✅ ").append(appName).append(": ").append(usageStr)
                           .append(" (").append(remainingStr).append(" left)\n");
                }
            }
        }
        
        if (!anyLimitReached && getTotalDailyUsage() == 0) {
            report.append("No usage recorded today\n");
        }
        
        report.append("\nNote: Only short video features are blocked\n");
        report.append("Apps remain accessible for other functions");
        
        return report.toString();
    }

    private boolean isPopularApp(String packageName) {
        return packageName.equals("com.google.android.youtube") ||
               packageName.equals("com.instagram.android") ||
               packageName.equals("com.zhiliaoapp.musically") ||
               packageName.equals("com.ss.android.ugc.tiktok");
    }

    public String getAppDisplayName(String packageName) {
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

    private void initializeScrollBlockedApps() {
        if (!prefs.contains(KEY_SCROLL_BLOCKED_APPS)) {
            prefs.edit()
                    .putStringSet(KEY_SCROLL_BLOCKED_APPS, DEFAULT_SCROLL_BLOCKED_APPS)
                    .apply();
        }
    }

    private void initializeScrollBlockingSettings() {
        if (!prefs.contains(KEY_SCROLL_BLOCKING_ENABLED)) {
            prefs.edit()
                    .putBoolean(KEY_SCROLL_BLOCKING_ENABLED, false) // Default FALSE
                    .apply();
        }
        if (!prefs.contains(KEY_SHORT_VIDEO_BLOCKING_ENABLED)) {
            prefs.edit()
                    .putBoolean(KEY_SHORT_VIDEO_BLOCKING_ENABLED, false) // Default FALSE
                    .apply();
        }
    }
}
package com.dummy.dummyphoneprakash;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {
    private static final String KEY_ALLOWED_APPS = "allowed_apps";
    private static final String KEY_ESSENTIAL_APPS = "essential_apps";
    private static final String KEY_IS_LOCKED = "is_locked";

    private static final String KEY_UNBLOCK_ALWAYS = "unblock_always"; // New key

    // Scroll blocking keys
    private static final String KEY_SCROLL_BLOCKING_ENABLED = "scroll_blocking_enabled";
    private static final String KEY_SCROLL_BLOCKED_APPS = "scroll_blocked_apps";
    private static final String KEY_SHORT_VIDEO_BLOCKING_ENABLED = "short_video_blocking_enabled";

    private static final String KEY_TARGET_END_TIME = "target_end_time";


    // Default essential apps that cannot be blocked
    private static final Set<String> DEFAULT_ESSENTIAL_APPS = new HashSet<String>() {{
        add("com.android.dialer");         // Phone dialer
        add("com.android.contacts");       // Contacts
        add("com.android.mms");            // Messaging

        // Maps
        add("com.google.android.apps.maps");
        add("com.waze");
        add("com.huawei.maps.app");

// Messages
        add("com.google.android.apps.messaging");
        add("com.samsung.android.messaging");
        add("com.miui.mms");
        add("com.coloros.mms");
        add("com.vivo.messaging");

// Dialer
        add("com.google.android.dialer");
        add("com.samsung.android.dialer");
        add("com.coloros.dialer");
        add("com.motorola.dialer");

// Contacts
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

    // New: Default apps that should never be blocked (even beyond essentials)
    private static final Set<String> DEFAULT_UNBLOCK_ALWAYS = new HashSet<String>() {{

        add("com.dummy.dummyphoneprakash");

       //package names for gallery
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

//        keyboard packages
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

        // SystemUI packages for major Android OEMs
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

// Launcher packages
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

        // Foldable devices
        add("com.samsung.android.app.cocktailbarservice");  // Samsung Edge/Fold panels



// Biometric authentication packages
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

        add("com.samsung.android.knox.attestation");  // Samsung Knox
        add("com.huawei.systemmanager");  // Huawei security
        add("com.miui.securitycore");  // Xiaomi security
        add("com.coloros.safecenter");  // Oppo/Realme security
        add("com.vivo.securepay");  // Vivo payment security
        add("com.oneplus.security");  // OnePlus security

        // Additional OEM biometrics
        add("com.zui.fingerprint");
        add("com.transsion.fingerprint.service");
        add("com.transsion.faceunlock");

// Underlying biometric services
        add("com.android.server.biometrics");
        add("com.qualcomm.qti.biometrics.fingerprint.service");
        add("com.mediatek.biometrics.fingerprint.service");

        add("com.android.keyguard");  // Handles lock screen
        add("com.google.android.apps.wellbeing");  // Digital wellbeing (affects UI)
        add("com.google.android.as");  // Android System Intelligence
        add("com.android.providers.settings");  // System settings provider


    }};

    // Default apps that should have scroll blocking enabled
    private static final Set<String> DEFAULT_SCROLL_BLOCKED_APPS = new HashSet<String>() {{
        // Social media apps with short videos
        add("com.facebook.katana");           // Facebook
        add("com.facebook.orca");             // Facebook Messenger
        add("com.instagram.android");         // Instagram
        add("com.zhiliaoapp.musically");      // TikTok
        add("com.ss.android.ugc.tiktok");     // TikTok (alternative package)
        add("com.google.android.youtube");    // YouTube
        add("com.google.android.apps.youtube.kids"); // YouTube Kids
        add("com.snapchat.android");          // Snapchat
        add("com.twitter.android");           // Twitter/X
        add("com.reddit.frontpage");          // Reddit
        add("com.reddit.launch");             // Reddit (alternative)
        add("com.linkedin.android");          // LinkedIn
        add("com.pinterest");                 // Pinterest
        add("com.whatsapp");                  // WhatsApp
        add("com.tencent.mm");                // WeChat
        add("com.tencent.qq");                // QQ
        add("com.tencent.mobileqq");          // QQ (alternative)
        add("com.sina.weibo");                // Weibo
        add("com.tencent.weishi");            // Weishi
        add("com.ss.android.article.news");   // Toutiao
        add("com.ss.android.ugc.aweme");      // Douyin
        add("com.ss.android.ugc.aweme.lite"); // Douyin Lite
        add("com.kwai.video");                // Kuaishou
        add("com.kwai.video.lite");           // Kuaishou Lite
        add("com.ss.android.ugc.live");       // Live streaming apps
        add("com.netease.cloudmusic");        // NetEase Cloud Music
        add("com.tencent.music");             // QQ Music
        add("com.kugou.android");             // Kugou Music
        add("com.kuwo.kwmusiccar");           // Kuwo Music
        add("com.ximalaya.ting.android");     // Ximalaya
        add("com.qingting.fm");               // Qingting FM
        add("com.xiaoying.tvmenuv8");         // Xiaoying
        add("com.quvideo.xiaoying");          // Xiaoying (alternative)
        add("com.lemon.lv");                  // Lemon8
        add("com.bytedance.lemon8");          // Lemon8 (alternative)
        add("com.bytedance.ies");             // ByteDance apps
        add("com.bytedance.ies.lite");        // ByteDance apps lite
        add("com.ss.android.ugc.trill");      // Triller
        add("com.musical.ly");                // Musical.ly (old TikTok)
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
        add("com.spotify.music.android");     // Spotify (alternative)
        add("com.spotify.music.android.lite"); // Spotify Lite
        add("com.spotify.music.android.beta"); // Spotify Beta
        add("com.spotify.music.android.debug"); // Spotify Debug
        add("com.spotify.music.android.test"); // Spotify Test
        add("com.spotify.music.android.dev"); // Spotify Dev
        add("com.spotify.music.android.staging"); // Spotify Staging
        add("com.spotify.music.android.production"); // Spotify Production
        add("com.spotify.music.android.release"); // Spotify Release
        add("com.spotify.music.android.debug"); // Spotify Debug
        add("com.spotify.music.android.test"); // Spotify Test
        add("com.spotify.music.android.dev"); // Spotify Dev
        add("com.spotify.music.android.staging"); // Spotify Staging
        add("com.spotify.music.android.production"); // Spotify Production
        add("com.spotify.music.android.release"); // Spotify Release
    }};

    private final SharedPreferences prefs;



    public SharedPreferencesHelper(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        initializeEssentialApps();
        initializeUnblockAlwaysApps(); // Initialize the new set
        initializeScrollBlockedApps(); // Initialize scroll blocked apps
        initializeScrollBlockingSettings(); // Initialize scroll blocking settings
    }

    // New method to initialize unblock_always set
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

    // New methods for unblock_always functionality
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
        // First check unblock_always
        if (isInUnblockAlways(packageName)) {
            return true;
        }

        // Then check normal allowed/essential apps
        Set<String> allowed = getAllowedApps();
        Set<String> essential = getEssentialApps();
        return allowed.contains(packageName) || essential.contains(packageName);
    }
    // Add these methods to SharedPreferencesHelper
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

    // Scroll blocking methods
    public void setScrollBlockingEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SCROLL_BLOCKING_ENABLED, enabled).apply();
    }

    public boolean isScrollBlockingEnabled() {
        return prefs.getBoolean(KEY_SCROLL_BLOCKING_ENABLED, true);
    }

    public void setShortVideoBlockingEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SHORT_VIDEO_BLOCKING_ENABLED, enabled).apply();
    }

    public boolean isShortVideoBlockingEnabled() {
        return prefs.getBoolean(KEY_SHORT_VIDEO_BLOCKING_ENABLED, true);
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
                    .putBoolean(KEY_SCROLL_BLOCKING_ENABLED, true)
                    .apply();
        }
        if (!prefs.contains(KEY_SHORT_VIDEO_BLOCKING_ENABLED)) {
            prefs.edit()
                    .putBoolean(KEY_SHORT_VIDEO_BLOCKING_ENABLED, true)
                    .apply();
        }
    }
}
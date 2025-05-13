package com.example.dummyphoneprakash;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {

    private static final String KEY_ALLOWED_APPS = "allowed_apps";
    private static final String KEY_ESSENTIAL_APPS = "essential_apps";

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


        add("com.example.dummyphoneprakash");



        add("com.android.systemui");             // AOSP / base Android
        add("com.google.android.systemui");      // Pixel phones
        add("com.miui.systemui");                // Xiaomi / POCO / Redmi
        add("com.oplus.systemui");               // Oppo / Realme / OnePlus (ColorOS base)
        add("com.vivo.systemui");                // Vivo / iQOO
        add("com.huawei.systemui");              // Huawei (EMUI)
        add("com.hihonor.systemui");             // Honor (Magic UI)
        add("com.realme.systemui");              // Realme (Realme UI)
        add("com.oneplus.systemui");             // OnePlus (OxygenOS older versions)
        add("com.coloros.systemui");             // Shared ColorOS base
        add("com.motorola.systemui");            // Motorola
        add("com.sonymobile.systemui");          // Sony
        add("com.lenovo.systemui");              // Lenovo
        add("com.asus.systemui");                // Asus
        add("com.samsung.android.systemui");     // Samsung
        add("com.transsion.systemui");           // Infinix, itel, Tecno (XOS / HiOS)
        add("com.nothing.systemui");             // Nothing Phone



        add("com.sec.android.app.launcher");            // Samsung
        add("com.sec.android.app.easylauncher");        // Samsung Easy Mode
        add("com.miui.home");                           // Xiaomi
        add("com.mi.android.globallauncher");           // Xiaomi/POCO
        add("com.miui.systemAdSolution");               // Xiaomi (gesture/nav helper)
        add("net.oneplus.launcher");                    // OnePlus
        add("com.oppo.launcher");                       // Oppo
        add("com.realme.launcher");                     // Realme
        add("com.coloros.launcher");                    // Newer Oppo/Realme/OnePlus
        add("com.bbk.launcher2");                       // Vivo
        add("com.huawei.android.launcher");             // Huawei
        add("com.motorola.launcher");                   // Motorola
        add("com.sonymobile.home");                     // Sony
        add("com.asus.launcher");                       // Asus
        add("com.lenovo.launcher");                     // Lenovo
        add("com.nothing.launcher");                    // Nothing Phone
        add("com.android.launcher3");                   // AOSP
        add("com.google.android.apps.nexuslauncher");   // Pixel
        add("com.transsion.XOSLauncher");              // Infinix
        add("com.itel.launcher");                      // itel
        add("com.tecno.hios.launcher");                // Tecno
        add("com.google.android.apps.go.launcher");     // Android Go
        add("com.android.quickstep");                   // Used in recents/gestures system-wide


    }};

    private final SharedPreferences prefs;

    public SharedPreferencesHelper(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Initialize essential apps if not set
        if (!prefs.contains(KEY_ESSENTIAL_APPS)) {
            prefs.edit()
                    .putStringSet(KEY_ESSENTIAL_APPS, DEFAULT_ESSENTIAL_APPS)
                    .apply();
        }
    }

    public void saveAllowedApps(Set<String> allowedApps, Set<String> essentialApps) {
        // Combine with default essential apps that should never be removed
        Set<String> finalEssentialApps = new HashSet<>(essentialApps);
        finalEssentialApps.addAll(DEFAULT_ESSENTIAL_APPS);

        prefs.edit()
                .putStringSet(KEY_ALLOWED_APPS, new HashSet<>(allowedApps))
                .putStringSet(KEY_ESSENTIAL_APPS, finalEssentialApps)
                .apply();
    }

    public Set<String> getAllowedApps() {
        return prefs.getStringSet(KEY_ALLOWED_APPS, new HashSet<>());
    }

    public Set<String> getEssentialApps() {
        // Always include default essential apps
        Set<String> savedEssential = prefs.getStringSet(KEY_ESSENTIAL_APPS, new HashSet<>());
        Set<String> allEssential = new HashSet<>(savedEssential);
        allEssential.addAll(DEFAULT_ESSENTIAL_APPS);
        return allEssential;
    }

    public boolean isAppAllowed(String packageName) {
        Set<String> allowed = getAllowedApps();
        Set<String> essential = getEssentialApps();
        return allowed.contains(packageName) || essential.contains(packageName);
    }
}
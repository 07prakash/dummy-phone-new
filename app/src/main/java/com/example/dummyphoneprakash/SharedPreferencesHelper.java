package com.example.dummyphoneprakash;




import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "AppLauncherPrefs";
    private static final String KEY_ALLOWED_APPS = "allowed_apps";
    private static final String KEY_DISABLED_ESSENTIALS = "disabled_essentials";

    private static final Set<String> ESSENTIAL_APPS = new HashSet<String>() {{
        add("com.android.dialer");
        add("com.android.contacts");
        add("com.android.mms");
        add("com.google.android.dialer");
        add("com.google.android.apps.messaging");
        add("com.samsung.android.dialer");
        add("com.samsung.android.messaging");
    }};

    private final SharedPreferences prefs;

    public SharedPreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public Set<String> getEssentialApps() {
        return new HashSet<>(ESSENTIAL_APPS);
    }

    public Set<String> getAllowedRegularApps() {
        return new HashSet<>(prefs.getStringSet(KEY_ALLOWED_APPS, new HashSet<>()));
    }

    public Set<String> getDisabledEssentialApps() {
        return new HashSet<>(prefs.getStringSet(KEY_DISABLED_ESSENTIALS, new HashSet<>()));
    }

    public void saveSelections(Set<String> allowedRegularApps, Set<String> disabledEssentialApps) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(KEY_ALLOWED_APPS, new HashSet<>(allowedRegularApps));
        editor.putStringSet(KEY_DISABLED_ESSENTIALS, new HashSet<>(disabledEssentialApps));
        editor.apply();
    }

    public boolean isAppAllowed(String packageName) {
        if (ESSENTIAL_APPS.contains(packageName)) {
            return !getDisabledEssentialApps().contains(packageName);
        }
        Set<String> allowed = getAllowedRegularApps();
        return allowed.isEmpty() || allowed.contains(packageName);
    }

    public boolean isEssentialApp(String packageName) {
        return ESSENTIAL_APPS.contains(packageName);
    }
}
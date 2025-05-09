package com.example.dummyphoneprakash;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {

    private static final String KEY_ALLOWED_APPS = "allowed_apps";
    private static final String KEY_ESSENTIAL_APPS = "essential_apps";
    private static final Set<String> DEFAULT_ESSENTIAL_APPS = new HashSet<String>() {{
        add("com.android.dialer");
        add("com.android.contacts");
        add("com.android.mms");
        add("com.example.dummyphoneprakash");
    }};

    private final SharedPreferences prefs;

    public SharedPreferencesHelper(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveAllowedApps(Set<String> allowedApps, Set<String> essentialApps) {
        prefs.edit()
                .putStringSet(KEY_ALLOWED_APPS, new HashSet<>(allowedApps))
                .putStringSet(KEY_ESSENTIAL_APPS, new HashSet<>(essentialApps))
                .apply();
    }

    public Set<String> getAllowedApps() {
        return prefs.getStringSet(KEY_ALLOWED_APPS, new HashSet<>());
    }

    public Set<String> getEssentialApps() {
        return prefs.getStringSet(KEY_ESSENTIAL_APPS, DEFAULT_ESSENTIAL_APPS);
    }

    public boolean isAppAllowed(String packageName) {
        Set<String> allowed = getAllowedApps();
        Set<String> essential = getEssentialApps();
        return allowed.contains(packageName) || essential.contains(packageName);
    }
}
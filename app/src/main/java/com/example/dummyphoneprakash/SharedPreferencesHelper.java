package com.example.dummyphoneprakash;




import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "AppLauncherPrefs";
    private static final String KEY_ALLOWED_APPS = "allowed_apps";

    // Essential apps that should always be enabled
    private static final Set<String> ESSENTIAL_APPS = new HashSet<String>() {{
        add("com.android.dialer");         // Default dialer
        add("com.android.contacts");       // Contacts
        add("com.android.mms");            // Messaging
        add("com.google.android.dialer");  // Google Dialer
        add("com.google.android.apps.messaging"); // Google Messages
        add("com.samsung.android.dialer"); // Samsung Dialer
        add("com.samsung.android.messaging"); // Samsung Messages
    }};

    private final SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public Set<String> getAllowedApps() {
        return new HashSet<>(sharedPreferences.getStringSet(KEY_ALLOWED_APPS, new HashSet<>()));
    }

    public void setAllowedApps(Set<String> packageNames) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_ALLOWED_APPS, new HashSet<>(packageNames));
        editor.apply();
    }

    public boolean isAppAllowed(String packageName) {
        Set<String> allowedApps = getAllowedApps();
        return isEssentialApp(packageName) || allowedApps.isEmpty() || allowedApps.contains(packageName);
    }

    public boolean isEssentialApp(String packageName) {
        return ESSENTIAL_APPS.contains(packageName);
    }

    public Set<String> getEssentialApps() {
        return new HashSet<>(ESSENTIAL_APPS);
    }

    public void allowApp(String packageName) {
        Set<String> allowedApps = getAllowedApps();
        allowedApps.add(packageName);
        setAllowedApps(allowedApps);
    }

    public void disallowApp(String packageName) {
        if (!isEssentialApp(packageName)) {
            Set<String> allowedApps = getAllowedApps();
            allowedApps.remove(packageName);
            setAllowedApps(allowedApps);
        }
    }

    public void clearAllRestrictions() {
        setAllowedApps(new HashSet<>());
    }

    public boolean isLockingEnabled() {
        return !getAllowedApps().isEmpty();
    }
}
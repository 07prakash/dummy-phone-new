package com.dummy.dummyphoneprakash.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentContainerView;
import androidx.preference.PreferenceManager;

import com.dummy.dummyphoneprakash.FrgmentDialog.CustomTimePickerFragment;
import com.dummy.dummyphoneprakash.FrgmentDialog.WelcomeDialogFragment;
import com.dummy.dummyphoneprakash.R;
import com.dummy.dummyphoneprakash.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LockSettingActivity extends BaseActivity implements CustomTimePickerFragment.TimePickerListener {

    // UI Components
    private TextView timerDisplay, choostext;
    private Button timePickerBtn, lockBtn, unlockBtn, exitBtn;
    private FragmentContainerView fragmentContainerView;

    // State variables
    private int selectedMinutes = 1;
    private SharedPreferences prefs;
    private long lockStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setting);

        // Remove from recent apps
        removeFromRecentApps();

        // Initialize SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lockStartTime = prefs.getLong("lock_start_time", 0);

        // Initialize views
        timerDisplay = findViewById(R.id.timerDisplay);
        choostext = findViewById(R.id.choostext);
        timePickerBtn = findViewById(R.id.timePickerBtn);
        lockBtn = findViewById(R.id.lockBtn);
        unlockBtn = findViewById(R.id.unlockBtn);
        exitBtn = findViewById(R.id.exitBtn);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);

        if (WelcomeDialogFragment.shouldShow(this)) {
            new WelcomeDialogFragment().show(getSupportFragmentManager(), "welcome_dialog");
        }

        timePickerBtn.setOnClickListener(v -> {
            CustomTimePickerFragment timePickerFragment = new CustomTimePickerFragment();
            timePickerFragment.show(getSupportFragmentManager(), "timePicker");
        });

        lockBtn.setOnClickListener(v -> {
            // Create dialog with custom layout
            final Dialog dialog = new Dialog(LockSettingActivity.this);
            dialog.setContentView(R.layout.accessibility_permission_dialog); // Use your XML layout

            // Set dialog properties
            dialog.setCancelable(true);

            // Configure dialog window
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }

            // Initialize buttons from your layout
            Button goToSettingsButton = dialog.findViewById(R.id.goToSettingsButton);
            Button cancelButton = dialog.findViewById(R.id.cancelButton);

            // Set button click listeners
            goToSettingsButton.setOnClickListener(view -> {
                checkAccessibilityPermission();
                // Save lock state and duration
                long currentTime = System.currentTimeMillis();
                long durationMillis = selectedMinutes * 60 * 1000L;
                long targetEndTime = currentTime + durationMillis;

                prefs.edit()
                        .putLong("lock_start_time", currentTime)
                        .putLong("target_end_time", targetEndTime)
                        .putLong("lock_duration", durationMillis)
                        .putBoolean("is_locked", true)
                        .apply();
                dialog.dismiss();
            });

            cancelButton.setOnClickListener(view -> {
                dialog.dismiss();
            });

            // Show the dialog
            dialog.show();
        });
        // In LockSettingActivity's unlockBtn click listener:
        unlockBtn.setOnClickListener(v -> {
            // Calculate remaining time in milliseconds
            long elapsedMillis = System.currentTimeMillis() - lockStartTime;
            long totalLockMillis = selectedMinutes * 60 * 1000L;
            long remainingMillis = Math.max(totalLockMillis - elapsedMillis, 0);

            Intent unlockIntent = new Intent(this, UnlockActivity.class);
            unlockIntent.putExtra("REMAINING_TIME", remainingMillis);
            startActivity(unlockIntent);
            finish();
        });
        exitBtn.setOnClickListener(v -> {
            // Open home settings to change launcher
            Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
            startActivity(homeSettingsIntent);
            clearLockState();
        });

        // Update UI based on current state
        updateUI();
    }

    @Override
    public void onHomeSettingsSelected() {

    }


//    @Override
//    public void onCancelSelected() {
//        // User cancelled
//        Log.d("HomeSettings", "User cancelled launcher change");
//        // You might want to finish the activity or show a warning
//        finish();
//    }



    private void checkAccessibilityPermission() {
        String serviceName = getPackageName() + "/.AppBlockerService";
        String enabledServices = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );

        if (enabledServices == null || !enabledServices.contains(serviceName)) {
            Toast.makeText(this,
                    "Please enable dummy phone in Accessibility Settings",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
    }

    private void removeFromRecentApps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.AppTask> tasks = am.getAppTasks();
                if (tasks != null && !tasks.isEmpty()) {
                    tasks.get(0).setExcludeFromRecents(true);
                }
            }
        }
    }

    private boolean isMyLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        List<IntentFilter> filters = new ArrayList<>();
        filters.add(filter);

        final String myPackageName = getPackageName();
        List<ComponentName> activities = new ArrayList<>();

        PackageManager pm = getPackageManager();
        pm.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void resetForm() {
        selectedMinutes = 1;
        updateTimerDisplay();
    }

    private void updateUI() {
        boolean isLocked = prefs.getBoolean("is_locked", false);

        if (isLocked && lockStartTime > 0) {
            // Locked state - hide settings and card, show unlock button
            choostext.setVisibility(View.GONE);
            timePickerBtn.setVisibility(View.GONE);
            timerDisplay.setVisibility(View.GONE);
            lockBtn.setVisibility(View.GONE);
            unlockBtn.setVisibility(View.VISIBLE);
            fragmentContainerView.setVisibility(View.GONE);
            exitBtn.setVisibility(View.GONE);
        } else {
            // Unlocked state - show settings and card, hide unlock button
            timePickerBtn.setVisibility(View.VISIBLE);
            timerDisplay.setVisibility(View.VISIBLE);
            choostext.setVisibility(View.VISIBLE);
            lockBtn.setVisibility(View.VISIBLE);
            unlockBtn.setVisibility(View.GONE);
            fragmentContainerView.setVisibility(View.VISIBLE);
            exitBtn.setVisibility(View.VISIBLE);
            updateTimerDisplay();
        }
    }
    private void clearLockState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .remove("lock_start_time")
                .remove("lock_duration")
                .putBoolean("is_locked", false)
                .apply();
    }
    private void updateTimerDisplay() {
        // Convert minutes to milliseconds for calculation
        long totalMillis = selectedMinutes * 60 * 1000L;

        // Calculate time components
        long months = TimeUnit.MILLISECONDS.toDays(totalMillis) / 30;
        long days = TimeUnit.MILLISECONDS.toDays(totalMillis) % 30;
        long hours = TimeUnit.MILLISECONDS.toHours(totalMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis) % 60;

        // Build the time string
        StringBuilder timeText = new StringBuilder();
        if (months > 0) timeText.append(months).append("mo ");
        if (days > 0) timeText.append(days).append("d ");
        if (hours > 0) timeText.append(hours).append("h ");
        if (minutes > 0 || timeText.length() == 0) timeText.append(minutes).append("m");

        timerDisplay.setText(timeText.toString().trim());
    }

    @Override
    public void onTimeSet(long durationInMillis) {
        // Convert milliseconds to minutes (minimum 1 minute)
        selectedMinutes = (int) Math.max(durationInMillis / (60 * 1000), 1);
        updateTimerDisplay();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!isMyLauncherDefault()) {
            // Only open launcher selection if not default launcher
            Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
            startActivity(homeSettingsIntent);
        }

    }

}
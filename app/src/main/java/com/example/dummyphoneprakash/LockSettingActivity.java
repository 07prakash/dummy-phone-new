package com.example.dummyphoneprakash;

import android.app.ActivityManager;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentContainerView;
import androidx.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LockSettingActivity extends AppCompatActivity {

    // UI Components
    private TextView timerDisplay,choostext;
    private Button  timePickerBtn, lockBtn, unlockBtn, exitBtn;
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

        if (!isMyLauncherDefault()) {
            // Only open launcher selection if not default launcher
            Intent homeSettingsIntent = new Intent(android.provider.Settings.ACTION_HOME_SETTINGS);
            startActivity(homeSettingsIntent);
        }

        timePickerBtn.setOnClickListener(v -> showTimePickerDialog());

        lockBtn.setOnClickListener(v -> {

            checkAccessibilityPermission();
            // Save lock state and duration
            long currentTime = System.currentTimeMillis();
            prefs.edit()
                    .putLong("lock_start_time", currentTime)
                    .putInt("lock_duration", selectedMinutes)
                    .putBoolean("is_locked", true)
                    .apply();


            // Reset form and finish
            resetForm();
            finish();
        });

        unlockBtn.setOnClickListener(v -> {
            // Calculate remaining time
            long elapsedMillis = System.currentTimeMillis() - lockStartTime;
            int totalLockMillis = prefs.getInt("lock_duration", 1) * 60 * 1000;
            int remainingMillis = (int) Math.max(totalLockMillis - elapsedMillis, 0);

            Intent unlockIntent = new Intent(this, UnlockActivity.class);
            unlockIntent.putExtra("REMAINING_TIME", remainingMillis);
            startActivity(unlockIntent);
            finish();
        });

        exitBtn.setOnClickListener(v -> {
            // Open home settings to change launcher
            Intent homeSettingsIntent = new Intent(android.provider.Settings.ACTION_HOME_SETTINGS);
            startActivity(homeSettingsIntent);
        });

        // Update UI based on current state
        updateUI();
    }

    private void checkAccessibilityPermission() {
        String serviceName = getPackageName() + "/.AppBlockerService";
        String enabledServices = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );

        if (enabledServices == null || !enabledServices.contains(serviceName)) {
            Toast.makeText(this,
                    "Please enable App Blocker in Accessibility Settings",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));

        }
    }


    //    Remove from recent apps method
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

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedMinutes = hourOfDay * 60 + minute;
                    if (selectedMinutes == 0) selectedMinutes = 1;
                    updateTimerDisplay();
                },
                0, 1, true
        );
        timePickerDialog.setTitle("Select Lock Duration");
        timePickerDialog.show();
    }

    private void updateTimerDisplay() {
        int hours = selectedMinutes / 60;
        int minutes = selectedMinutes % 60;
        String timeText = hours > 0 ?
                String.format(Locale.getDefault(), "%dh %02dm", hours, minutes) :
                String.format(Locale.getDefault(), "%d minutes", minutes);
        timerDisplay.setText(timeText);
    }
}
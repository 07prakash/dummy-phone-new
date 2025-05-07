package com.example.dummyphoneprakash;



import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import java.util.Locale;

public class LockSettingActivity extends AppCompatActivity {

    // UI Components
    private TextView timerDisplay, lockDurationText;
    private Button chooseAppsBtn, timePickerBtn, lockBtn, unlockBtn, exitBtn;
    private CardView exitCardView;

    // State variables
    private int selectedMinutes = 1;
    private SharedPreferences prefs;
    private long lockStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setting);

        // Initialize SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lockStartTime = prefs.getLong("lock_start_time", 0);

        // Initialize views
        timerDisplay = findViewById(R.id.timerDisplay);
        chooseAppsBtn = findViewById(R.id.chooseAppsBtn);
        timePickerBtn = findViewById(R.id.timePickerBtn);
        lockBtn = findViewById(R.id.lockBtn);
        unlockBtn = findViewById(R.id.unlockBtn);
        lockDurationText = findViewById(R.id.lockDurationText);
        exitCardView = findViewById(R.id.exitCardView);
        exitBtn = findViewById(R.id.exitBtn);

        // Set up button click listeners
        chooseAppsBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LockActivity.class));
        });

        timePickerBtn.setOnClickListener(v -> showTimePickerDialog());

        lockBtn.setOnClickListener(v -> {
            // Save lock state and duration
            long currentTime = System.currentTimeMillis();
            prefs.edit()
                    .putLong("lock_start_time", currentTime)
                    .putInt("lock_duration", selectedMinutes)
                    .putBoolean("is_locked", true)
                    .apply();

            // Open launcher selection
            Intent homeSettingsIntent = new Intent(android.provider.Settings.ACTION_HOME_SETTINGS);
            startActivity(homeSettingsIntent);

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

    private void resetForm() {
        selectedMinutes = 1;
        updateTimerDisplay();
    }

    private void updateUI() {
        boolean isLocked = prefs.getBoolean("is_locked", false);

        if (isLocked && lockStartTime > 0) {
            // Locked state - hide settings and card, show unlock button
            chooseAppsBtn.setVisibility(View.GONE);
            timePickerBtn.setVisibility(View.GONE);
            lockDurationText.setVisibility(View.GONE);
            timerDisplay.setVisibility(View.GONE);
            lockBtn.setVisibility(View.GONE);
            exitCardView.setVisibility(View.GONE);
            unlockBtn.setVisibility(View.VISIBLE);
        } else {
            // Unlocked state - show settings and card, hide unlock button
            chooseAppsBtn.setVisibility(View.VISIBLE);
            timePickerBtn.setVisibility(View.VISIBLE);
            lockDurationText.setVisibility(View.VISIBLE);
            timerDisplay.setVisibility(View.VISIBLE);
            lockBtn.setVisibility(View.VISIBLE);
            exitCardView.setVisibility(View.VISIBLE);
            unlockBtn.setVisibility(View.GONE);
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
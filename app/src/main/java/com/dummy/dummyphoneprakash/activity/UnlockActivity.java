package com.dummy.dummyphoneprakash.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dummy.dummyphoneprakash.R;
import com.dummy.dummyphoneprakash.SharedPreferencesHelper;

public class UnlockActivity extends BaseActivity {

    private static final String TAG = "UnlockActivity";

    // UI Components
    private TextView countdownText;
    private Button earlyAccessBtn;
    private Button exitBtn;

    // Timer
    private CountDownTimer countDownTimer;
    private long targetEndTime;

    // Preferences
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        // Initialize components
        prefsHelper = new SharedPreferencesHelper(this);
        countdownText = findViewById(R.id.countdownText);
        earlyAccessBtn = findViewById(R.id.earlyAccessBtn);
        exitBtn = findViewById(R.id.exitBtn);

        // Handle exit flow (when coming from settings)
        if (getIntent().getBooleanExtra("EXIT_FLOW", false)) {
            handleExitFlow();
            return;
        }

        // Setup UI
        setupButtons();

        // Start the persistent timer
        startPersistentTimer();
    }

    private void handleExitFlow() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
                homeSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeSettingsIntent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error opening settings", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, 100);
    }

    private void setupButtons() {
        earlyAccessBtn.setOnClickListener(v -> showEarlyUnlockConfirmation());

        exitBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, UnlockActivity.class);
            intent.putExtra("EXIT_FLOW", true);
            startActivity(intent);
            finish();
        });
    }

    private void startPersistentTimer() {
        // Get target end time from preferences
        targetEndTime = prefsHelper.getTargetEndTime();

        if (targetEndTime <= System.currentTimeMillis()) {
            // Timer already expired
            handleTimerFinish();
            return;
        }

        // Calculate initial remaining time
        long initialRemaining = targetEndTime - System.currentTimeMillis();

        // Start countdown
        startCountdownTimer(initialRemaining);
        updateCountdownText(initialRemaining);
    }

    private void startCountdownTimer(long millisInFuture) {
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Always calculate remaining time based on target end time
                long remaining = targetEndTime - System.currentTimeMillis();
                updateCountdownText(remaining);
            }

            @Override
            public void onFinish() {
                handleTimerFinish();
            }
        }.start();
    }

    private void updateCountdownText(long millisUntilFinished) {
        if (millisUntilFinished <= 0) {
            countdownText.setText("00:00:00");
            return;
        }

        long totalSeconds = millisUntilFinished / 1000;

        // Calculate time components
        int months = (int) (totalSeconds / (30L * 24 * 60 * 60));
        int days = (int) ((totalSeconds % (30L * 24 * 60 * 60)) / (24 * 60 * 60));
        int hours = (int) ((totalSeconds % (24 * 60 * 60)) / (60 * 60));
        int minutes = (int) ((totalSeconds % (60 * 60)) / 60);
        int seconds = (int) (totalSeconds % 60);

        // Build time string
        StringBuilder timeString = new StringBuilder();
        if (months > 0) timeString.append(months).append("mo ");
        if (days > 0) timeString.append(days).append("d ");
        if (hours > 0) timeString.append(hours).append("h ");
        if (minutes > 0) timeString.append(minutes).append("m ");
        timeString.append(seconds).append("s");

        countdownText.setText(timeString.toString().trim());
    }

    private void showEarlyUnlockConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Early Unlock")
                .setMessage("Are you sure you want to unlock the device before the timer expires?")
                .setPositiveButton("Unlock Now", (dialog, which) -> unlockDevice())
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void handleTimerFinish() {
        showTimeEndDialog();
        unlockDevice();
    }

    public void showTimeEndDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Timer Completed")
                .setMessage("The lock duration has ended. Your device will now be unlocked.")
                .setPositiveButton("OK", (dialog, which) -> {})
                .setCancelable(false)
                .show();
    }

    @Override
    public void onHomeSettingsSelected() {

    }

    private void unlockDevice() {
        // Cancel timer if running
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Clear lock state
        prefsHelper.cancelTimer();

        // Return to lock settings
        Intent intent = new Intent(this, LockSettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
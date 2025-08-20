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
import com.dummy.dummyphoneprakash.fragments.UsageStatsFragment;

public class UnlockActivity extends BaseActivity {

    private static final String TAG = "UnlockActivity";

    // UI Components
    private TextView countdownText;
    private Button earlyAccessBtn;
    private Button exitOut;
    private Button usageStatsBtn;

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
        exitOut = findViewById(R.id.exitOut);
        usageStatsBtn = findViewById(R.id.usageStatsBtn);

        // Setup UI
        setupButtons();

        // Start the persistent timer
        startPersistentTimer();
        
        // Show current usage info
        showCurrentUsageInfo();
    }

    private void setupButtons() {
        earlyAccessBtn.setOnClickListener(v -> showEarlyUnlockConfirmation());

        exitOut.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainPagerActivity.class);
            startActivity(intent);
            finish();
        });
        
        // Usage stats button
        usageStatsBtn.setOnClickListener(v -> showUsageStatsFragment());
    }

    /**
     * Show usage statistics fragment
     */
    private void showUsageStatsFragment() {
        // Create a simple dialog to show usage stats since UnlockActivity doesn't have fragment container
        String report = prefsHelper.getUsageReport();
        
        new AlertDialog.Builder(this)
                .setTitle("📊 Short Video Usage Statistics")
                .setMessage(report)
                .setPositiveButton("OK", null)
                .setNeutralButton("Reset (Debug)", (dialog, which) -> {
                    prefsHelper.resetDailyCounters();
                    Toast.makeText(this, "🔄 Daily counters reset", Toast.LENGTH_SHORT).show();
                    showCurrentUsageInfo(); // Refresh display
                })
                .show();
    }
    
    /**
     * Show current usage information
     */
    private void showCurrentUsageInfo() {
        long totalUsage = prefsHelper.getTotalDailyUsage();
        
        if (totalUsage > 0) {
            String totalStr = prefsHelper.getFormattedUsageTime(totalUsage);
            long remaining = prefsHelper.getRemainingDailyTime();
            String remainingStr = prefsHelper.getFormattedUsageTime(remaining);
            
            String message;
            if (prefsHelper.isDailyLimitReached()) {
                message = "🚫 Daily limit reached!\nUsed: " + totalStr + " / 30m";
            } else {
                message = "📊 Short video usage today:\nUsed: " + totalStr + " | Remaining: " + remainingStr;
            }
            
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
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
        // Show current usage before unlock confirmation
        String usageInfo = "";
        long totalUsage = prefsHelper.getTotalDailyUsage();
        if (totalUsage > 0) {
            String totalStr = prefsHelper.getFormattedUsageTime(totalUsage);
            usageInfo = "\n\n📊 Today's short video usage: " + totalStr + " / 30m";
            if (prefsHelper.isDailyLimitReached()) {
                usageInfo += "\n🚫 Daily limit reached - blocking will continue until midnight";
            }
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Early Unlock")
                .setMessage("Are you sure you want to unlock the device before the timer expires?" + usageInfo)
                .setPositiveButton("Unlock Now", (dialog, which) -> unlockDevice())
                .setNegativeButton("Cancel", null)
                .setNeutralButton("View Stats", (dialog, which) -> showUsageStatsFragment())
                .show();
    }

    private void handleTimerFinish() {
        showTimeEndDialog();
        unlockDevice();
    }

    public void showTimeEndDialog() {
        // Show usage summary when timer ends
        String usageInfo = "";
        long totalUsage = prefsHelper.getTotalDailyUsage();
        if (totalUsage > 0) {
            String totalStr = prefsHelper.getFormattedUsageTime(totalUsage);
            usageInfo = "\n\n📊 Session summary:\nShort video usage: " + totalStr + " / 30m";
            if (prefsHelper.isDailyLimitReached()) {
                usageInfo += "\n🚫 Daily limit was reached during this session";
            }
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Timer Completed")
                .setMessage("The lock duration has ended. Your device will now be unlocked." + usageInfo)
                .setPositiveButton("OK", null)
                .setNeutralButton("View Full Stats", (dialog, which) -> showUsageStatsFragment())
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

        // Show final usage summary
        long totalUsage = prefsHelper.getTotalDailyUsage();
        if (totalUsage > 0) {
            String totalStr = prefsHelper.getFormattedUsageTime(totalUsage);
            String summaryMessage = "🔓 Session ended\n📊 Short video usage: " + totalStr + " / 30m";
            if (prefsHelper.isDailyLimitReached()) {
                summaryMessage += "\n🚫 Daily limit reached - blocking continues until midnight";
            }
            Toast.makeText(this, summaryMessage, Toast.LENGTH_LONG).show();
        }

        // DISABLE scroll blocking but keep usage tracking for daily limit
        prefsHelper.setScrollBlockingEnabled(false);
        // Note: Don't disable short video blocking if limit reached - it should persist until midnight

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
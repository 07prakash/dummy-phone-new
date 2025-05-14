package com.example.dummyphoneprakash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

public class UnlockActivity extends BaseActivity {
    private TextView countdownText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        // Check if we're exiting the app
        if (getIntent().getBooleanExtra("EXIT_FLOW", false)) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
                    homeSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeSettingsIntent);
                    finish(); // Close UnlockActivity
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 100); // 1 second delay
        }


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        countdownText = findViewById(R.id.countdownText);
        Button earlyAccessBtn = findViewById(R.id.earlyAccessBtn);

        // Calculate remaining time from when it was locked
        long lockStartTime = prefs.getLong("lock_start_time", 0);
        long duration = prefs.getLong("lock_duration", 60000);
        timeLeftInMillis = Math.max(lockStartTime + duration - System.currentTimeMillis(), 0);

        updateCountdownText(timeLeftInMillis);
        startTimer();

        earlyAccessBtn.setOnClickListener(v -> unlockDevice());
    }

    @Override
    public void onHomeSettingsSelected() {

    }


    private void startTimer() {
        if (timeLeftInMillis <= 0) {
            unlockDevice();
            return;
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                showTimeEndDialog();
                unlockDevice();
            }
        }.start();
    }

    private void updateCountdownText(long millisUntilFinished) {
        long totalSeconds = millisUntilFinished / 1000;

        int months = (int) (totalSeconds / (30L * 24 * 60 * 60));
        int days = (int) ((totalSeconds % (30L * 24 * 60 * 60)) / (24 * 60 * 60));
        int hours = (int) ((totalSeconds % (24 * 60 * 60)) / (60 * 60));
        int minutes = (int) ((totalSeconds % (60 * 60)) / 60);
        int seconds = (int) (totalSeconds % 60);

        StringBuilder timeString = new StringBuilder();
        if (months > 0) timeString.append(months).append("mo ");
        if (days > 0) timeString.append(days).append("d ");
        if (hours > 0) timeString.append(hours).append("h ");
        if (minutes > 0) timeString.append(minutes).append("m ");
        timeString.append(seconds).append("s");

        countdownText.setText(timeString.toString().trim());
    }

    private void unlockDevice() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Clear lock state
        prefs.edit()
                .putBoolean("is_locked", false)
                .putLong("lock_start_time", 0)
                .putLong("lock_duration", 0)
                .apply();

        startActivity(new Intent(this,LockSettingActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
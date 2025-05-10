package com.example.dummyphoneprakash;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class UnlockActivity extends AppCompatActivity {
    private TextView countdownText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        countdownText = findViewById(R.id.countdownText);
        Button earlyAccessBtn = findViewById(R.id.earlyAccessBtn);

        // Get remaining time from intent
        timeLeftInMillis = getIntent().getIntExtra("REMAINING_TIME", 60000);

        startTimer();

        earlyAccessBtn.setOnClickListener(v -> {
            unlockDevice();
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {

              unlockDevice();
//                endBlocking();

            }
        }.start();
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        countdownText.setText(timeLeft);
    }

    private void unlockDevice() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Clear lock state
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean("is_locked", false)
                .putLong("lock_start_time", 0)
                .apply();

        startActivity(new Intent(this, MainPagerActivity.class));
        finish();
    }
//
//    private void endBlocking() {
//        // Clear all blocking preferences
//        prefs.edit()
//                .remove("is_locked")
//                .remove("lock_start_time")
//                .remove("lock_duration")
//                .apply();
//
//        // Return to home screen
//        Intent intent = new Intent(this, MainPagerActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
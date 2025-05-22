package com.dummy.dummyphoneprakash.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.view.Window;

import androidx.fragment.app.Fragment;

import com.dummy.dummyphoneprakash.FrgmentDialog.TimeEndDialog;
import com.dummy.dummyphoneprakash.R;

public abstract class BaseActivity extends AppCompatActivity implements TimeEndDialog.TimeEndListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkTimeEnded();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkTimeEnded();
    }

    protected void checkTimeEnded() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long lockStartTime = prefs.getLong("lock_start_time", 0);
        long duration = prefs.getLong("lock_duration", 0);

        if (lockStartTime > 0 && duration > 0 &&
                System.currentTimeMillis() >= (lockStartTime + duration)) {
            showTimeEndDialog();
        }
    }

    public void showTimeEndDialog() {
        // Check if dialog is already showing
        Fragment existingDialog = getSupportFragmentManager()
                .findFragmentByTag("TimeEndDialog");
        if (existingDialog == null) {
            TimeEndDialog dialog = new TimeEndDialog();
            dialog.show(getSupportFragmentManager(), "TimeEndDialog");
        }
    }


    @Override
    public void onContinueSelected() {
        // Handle continue action
        clearLockState();
        startActivity(new Intent(this, UnlockActivity.class));
    }




    @Override
    public void onExitSelected() {
        // Launch UnlockActivity with a flag to indicate exit
        clearLockState();
        Intent unlockIntent = new Intent(this, UnlockActivity.class);
        unlockIntent.putExtra("EXIT_FLOW", true);
        startActivity(unlockIntent);

        // Close current activity

    }

    private void clearLockState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .remove("lock_start_time")
                .remove("lock_duration")
                .putBoolean("is_locked", false)
                .apply();
    }


    public abstract void onHomeSettingsSelected();
}
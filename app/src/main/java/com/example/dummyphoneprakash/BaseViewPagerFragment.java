package com.example.dummyphoneprakash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public abstract class BaseViewPagerFragment extends Fragment implements TimeEndDialog.TimeEndListener {

    protected BaseActivity baseActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkTimeEnded();
    }

    protected void checkTimeEnded() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        long lockStartTime = prefs.getLong("lock_start_time", 0);
        long duration = prefs.getLong("lock_duration", 0);

        if (lockStartTime > 0 && duration > 0 &&
                System.currentTimeMillis() >= (lockStartTime + duration)) {
            showTimeEndDialog();
        }
    }

    protected void showTimeEndDialog() {
        if (baseActivity != null) {
            baseActivity.showTimeEndDialog();
        } else {
            TimeEndDialog dialog = new TimeEndDialog();
            dialog.show(getParentFragmentManager(), "TimeEndDialog");
        }
    }

    @Override
    public void onContinueSelected() {
        if (baseActivity != null) {
            baseActivity.onContinueSelected();
        } else {
            startActivity(new Intent(requireContext(), UnlockActivity.class));
        }
    }

    @Override
    public void onExitSelected() {
        if (baseActivity != null) {
            baseActivity.onExitSelected();
        } else {
            startActivity(new Intent(requireContext(), UnlockActivity.class));
            // Open home settings to change launcher
            Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
            startActivity(homeSettingsIntent);
        }
    }
}

package com.dummy.dummyphoneprakash.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.dummy.dummyphoneprakash.activity.BaseActivity;
import com.dummy.dummyphoneprakash.FrgmentDialog.TimeEndDialog;
import com.dummy.dummyphoneprakash.activity.LockSettingActivity;
import com.dummy.dummyphoneprakash.activity.UnlockActivity;

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
            clearLockState();
            startActivity(new Intent(requireContext(), LockSettingActivity.class));
        } else {
            clearLockState();
            startActivity(new Intent(requireContext(),LockSettingActivity.class));
            requireActivity().finish();
        }
    }

    @Override
    public void onExitSelected() {
        if (baseActivity != null) {
            baseActivity.onExitSelected();
           clearLockState();
            Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
            startActivity(homeSettingsIntent);
        } else {
            clearLockState();
            Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
            startActivity(homeSettingsIntent);

        }

    }
    private void clearLockState() {
        // Get the Activity context safely
        Context context = getActivity();
        if (context != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit()
                    .remove("lock_start_time")
                    .remove("lock_duration")
                    .putBoolean("is_locked", false)
                    .apply();
        }
    }
}

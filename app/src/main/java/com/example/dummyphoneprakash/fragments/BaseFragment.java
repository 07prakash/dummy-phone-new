package com.example.dummyphoneprakash.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.dummyphoneprakash.activity.BaseActivity;
import com.example.dummyphoneprakash.FrgmentDialog.TimeEndDialog;
import com.example.dummyphoneprakash.activity.UnlockActivity;

public class BaseFragment extends Fragment implements TimeEndDialog.TimeEndListener {

    protected BaseActivity baseActivity;
    protected SharedPreferences prefs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkTimeEnded();
    }

    protected void checkTimeEnded() {
        long lockStartTime = prefs.getLong("lock_start_time", 0);
        long duration = prefs.getLong("lock_duration", 0);

        if (lockStartTime > 0 && duration > 0 &&
                System.currentTimeMillis() >= (lockStartTime + duration)) {
            showTimeEndDialog();
        }
    }

    public void showTimeEndDialog() {
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
            startActivity(new Intent(requireContext(), UnlockActivity.class));
        }
    }

    @Override
    public void onExitSelected() {
        if (baseActivity != null) {
            baseActivity.onExitSelected();
            Intent unlockIntent = new Intent(requireContext(), UnlockActivity.class);
            unlockIntent.putExtra("EXIT_FLOW", true);
            startActivity(unlockIntent);
        }
    }
}
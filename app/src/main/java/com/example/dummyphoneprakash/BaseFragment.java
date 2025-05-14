package com.example.dummyphoneprakash;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

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
        }
    }

    @Override
    public void onExitSelected() {
        if (baseActivity != null) {
            baseActivity.onExitSelected();
        }
    }
}
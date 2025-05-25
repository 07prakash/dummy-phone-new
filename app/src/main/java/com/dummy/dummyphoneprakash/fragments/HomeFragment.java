package com.dummy.dummyphoneprakash.fragments;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dummy.dummyphoneprakash.NotificationPermissionHelper;
import com.dummy.dummyphoneprakash.SharedPreferencesHelper;
import com.dummy.dummyphoneprakash.activity.LockSettingActivity;
import com.dummy.dummyphoneprakash.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends BaseFragment {

    private TextView timeTextView, dateTextView;
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        timeTextView = view.findViewById(R.id.timeTextView);
        dateTextView = view.findViewById(R.id.dateTextView);
        Button lockSettingsButton = view.findViewById(R.id.lockSettingsButton);

        updateTime();
        startTimeUpdates();

        lockSettingsButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LockSettingActivity.class));
        });

        return view;
    }

    private void updateTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        timeTextView.setText(timeFormat.format(new Date()));
        dateTextView.setText(dateFormat.format(new Date()));
    }

    private void startTimeUpdates() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(timeRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timeHandler.removeCallbacks(timeRunnable);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

}
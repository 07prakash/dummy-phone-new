package com.dummy.dummyphoneprakash.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dummy.dummyphoneprakash.R;
import com.dummy.dummyphoneprakash.SharedPreferencesHelper;
import com.dummy.dummyphoneprakash.adapter.ScrollBlockingAdapter;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScrollBlockingFragment extends Fragment {
    private RecyclerView appsRecyclerView;
    private ScrollBlockingAdapter adapter;
    private List<String> apps = new ArrayList<>();
    private SharedPreferencesHelper prefsHelper;
    private SwitchMaterial scrollBlockingSwitch;
    private SwitchMaterial shortVideoBlockingSwitch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scroll_blocking, container, false);
        prefsHelper = new SharedPreferencesHelper(requireContext());
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        // Setup switches
        scrollBlockingSwitch = view.findViewById(R.id.scrollBlockingSwitch);
        shortVideoBlockingSwitch = view.findViewById(R.id.shortVideoBlockingSwitch);
        
        // Setup RecyclerView
        appsRecyclerView = view.findViewById(R.id.scrollBlockingAppsRecyclerView);
        appsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        
        // Load apps and setup adapter
        loadScrollBlockingApps();
        adapter = new ScrollBlockingAdapter(apps, this::onAppToggle);
        appsRecyclerView.setAdapter(adapter);
        
        // Setup switch listeners
        setupSwitchListeners();
        
        // Update switch states
        updateSwitchStates();
    }

    private void setupSwitchListeners() {
        scrollBlockingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsHelper.setScrollBlockingEnabled(isChecked);
            Toast.makeText(requireContext(), 
                isChecked ? "Scroll blocking enabled" : "Scroll blocking disabled", 
                Toast.LENGTH_SHORT).show();
        });

        shortVideoBlockingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsHelper.setShortVideoBlockingEnabled(isChecked);
            Toast.makeText(requireContext(), 
                isChecked ? "Short video blocking enabled" : "Short video blocking disabled", 
                Toast.LENGTH_SHORT).show();
        });
    }

    private void updateSwitchStates() {
        scrollBlockingSwitch.setChecked(prefsHelper.isScrollBlockingEnabled());
        shortVideoBlockingSwitch.setChecked(prefsHelper.isShortVideoBlockingEnabled());
    }

    private void loadScrollBlockingApps() {
        Set<String> scrollBlockedApps = prefsHelper.getScrollBlockedApps();
        apps.clear();
        apps.addAll(scrollBlockedApps);
    }

    private void onAppToggle(String packageName, boolean isBlocked) {
        if (isBlocked) {
            prefsHelper.addScrollBlockedApp(packageName);
        } else {
            prefsHelper.removeScrollBlockedApp(packageName);
        }
        
        Toast.makeText(requireContext(), 
            packageName + (isBlocked ? " added to" : " removed from") + " scroll blocking", 
            Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSwitchStates();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}

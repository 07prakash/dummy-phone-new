package com.dummy.dummyphoneprakash.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dummy.dummyphoneprakash.R;
import com.dummy.dummyphoneprakash.SharedPreferencesHelper;

import java.util.Arrays;
import java.util.List;

public class UsageStatsFragment extends Fragment {
    private static final String TAG = "UsageStatsFragment";
    
    private SharedPreferencesHelper prefsHelper;
    private LinearLayout statsContainer;
    private TextView totalUsageText, remainingTimeText, statusText;
    private ProgressBar totalUsageProgress;
    private Button resetButton, backButton;
    private Handler handler;
    
    // Short video apps to display
    private final List<String> TRACKED_APPS = Arrays.asList(
        "com.google.android.youtube",
        "com.instagram.android", 
        "com.zhiliaoapp.musically",
        "com.ss.android.ugc.tiktok",
        "com.facebook.katana",
        "com.snapchat.android",
        "com.twitter.android",
        "com.reddit.frontpage",
        "com.pinterest"
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsHelper = new SharedPreferencesHelper(requireContext());
        handler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_usage_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupButtons();
        updateUsageStats();
        
        // Auto-refresh every 5 seconds
        startAutoRefresh();
    }

    private void initializeViews(View view) {
        statsContainer = view.findViewById(R.id.statsContainer);
        totalUsageText = view.findViewById(R.id.totalUsageText);
        remainingTimeText = view.findViewById(R.id.remainingTimeText);
        statusText = view.findViewById(R.id.statusText);
        totalUsageProgress = view.findViewById(R.id.totalUsageProgress);
        resetButton = view.findViewById(R.id.resetButton);
        backButton = view.findViewById(R.id.backButton);
    }

    private void setupButtons() {
        resetButton.setOnClickListener(v -> {
            prefsHelper.resetDailyCounters();
            updateUsageStats();
            Toast.makeText(getContext(), "🔄 Daily counters reset", Toast.LENGTH_SHORT).show();
        });

        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().finish();
            }
        });
    }

    private void updateUsageStats() {
        if (getContext() == null) return;
        
        // Update total usage
        long totalUsage = prefsHelper.getTotalDailyUsage();
        long remainingTime = prefsHelper.getRemainingDailyTime();
        boolean limitReached = prefsHelper.isDailyLimitReached();
        
        // Update total usage display
        String totalStr = prefsHelper.getFormattedUsageTime(totalUsage);
        totalUsageText.setText("Total Usage: " + totalStr + " / 30m");
        
        // Update remaining time
        if (limitReached) {
            remainingTimeText.setText("⚠️ Daily limit reached!");
            remainingTimeText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            String remainingStr = prefsHelper.getFormattedUsageTime(remainingTime);
            remainingTimeText.setText("Remaining: " + remainingStr);
            remainingTimeText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
        
        // Update progress bar
        int progressPercentage = (int) ((totalUsage * 100) / (30 * 60 * 1000L));
        totalUsageProgress.setProgress(Math.min(progressPercentage, 100));
        
        // Update status
        if (prefsHelper.isBlockingActive()) {
            if (limitReached) {
                statusText.setText("🚫 Short video blocking ACTIVE (limit reached)");
                statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                statusText.setText("📊 Tracking usage (blocking inactive)");
                statusText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            }
        } else {
            statusText.setText("⏸️ Tracking paused (not in locked session)");
            statusText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        
        // Update individual app stats
        updateIndividualAppStats();
    }

    private void updateIndividualAppStats() {
        statsContainer.removeAllViews();
        
        LayoutInflater inflater = getLayoutInflater();
        
        for (String packageName : TRACKED_APPS) {
            long usage = prefsHelper.getDailyUsage(packageName);
            
            // Only show apps with usage > 0 or currently popular apps
            if (usage > 0 || isPopularApp(packageName)) {
                View appStatView = inflater.inflate(R.layout.item_app_usage_stat, statsContainer, false);
                
                TextView appNameText = appStatView.findViewById(R.id.appNameText);
                TextView usageTimeText = appStatView.findViewById(R.id.usageTimeText);
                ProgressBar appProgressBar = appStatView.findViewById(R.id.appProgressBar);
                TextView percentageText = appStatView.findViewById(R.id.percentageText);
                
                // Set app name
                String appName = getAppDisplayName(packageName);
                appNameText.setText(appName);
                
                // Set usage time
                String usageStr = prefsHelper.getFormattedUsageTime(usage);
                usageTimeText.setText(usageStr);
                
                // Set progress (percentage of 30 minutes)
                int percentage = (int) ((usage * 100) / (30 * 60 * 1000L));
                appProgressBar.setProgress(Math.min(percentage, 100));
                percentageText.setText(percentage + "%");
                
                // Color coding based on usage
                if (percentage >= 100) {
                    usageTimeText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else if (percentage >= 75) {
                    usageTimeText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                } else if (percentage >= 50) {
                    usageTimeText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                } else {
                    usageTimeText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
                
                statsContainer.addView(appStatView);
            }
        }
        
        // Show message if no usage
        if (statsContainer.getChildCount() == 0) {
            TextView noUsageText = new TextView(getContext());
            noUsageText.setText("📱 No short video usage recorded today");
            noUsageText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            noUsageText.setPadding(20, 40, 20, 40);
            statsContainer.addView(noUsageText);
        }
    }

    private boolean isPopularApp(String packageName) {
        // Show these apps even with 0 usage for user awareness
        return packageName.equals("com.google.android.youtube") ||
               packageName.equals("com.instagram.android") ||
               packageName.equals("com.zhiliaoapp.musically") ||
               packageName.equals("com.ss.android.ugc.tiktok");
    }

    private String getAppDisplayName(String packageName) {
        switch (packageName) {
            case "com.google.android.youtube": return "YouTube";
            case "com.instagram.android": return "Instagram";
            case "com.zhiliaoapp.musically":
            case "com.ss.android.ugc.tiktok": return "TikTok";
            case "com.facebook.katana": return "Facebook";
            case "com.snapchat.android": return "Snapchat";
            case "com.twitter.android": return "Twitter";
            case "com.reddit.frontpage": return "Reddit";
            case "com.pinterest": return "Pinterest";
            default: return packageName.substring(packageName.lastIndexOf('.') + 1);
        }
    }

    private void startAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded() && getView() != null) {
                    updateUsageStats();
                    handler.postDelayed(this, 5000); // Refresh every 5 seconds
                }
            }
        }, 5000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}

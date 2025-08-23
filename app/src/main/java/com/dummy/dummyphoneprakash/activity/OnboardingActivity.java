package com.dummy.dummyphoneprakash.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.dummy.dummyphoneprakash.FrgmentDialog.WelcomeDialogFragment;
import com.dummy.dummyphoneprakash.R;
import com.dummy.dummyphoneprakash.adapter.OnboardingPagerAdapter;

public class OnboardingActivity extends BaseActivity {
    
    private ViewPager2 viewPager;
    private Button skipButton, nextButton;
    private TextView[] dots;
    private OnboardingPagerAdapter onboardingPagerAdapter;
    private int[] layouts;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if onboarding has been completed before
        if (isOnboardingCompleted()) {
            launchLockSettingActivity();
            finish();
            return;
        }
        
        setContentView(R.layout.activity_onboarding);
        
        // Show welcome dialog first
        showWelcomeDialog();
        
        // Initialize views
        viewPager = findViewById(R.id.onboarding_viewpager);
        skipButton = findViewById(R.id.btn_skip);
        nextButton = findViewById(R.id.btn_next);
        
        // Change skip button to previous button
        skipButton.setText(R.string.previous);
        
        // Layouts for all onboarding screens
        layouts = new int[]{
                R.layout.onboarding_screen1,
                R.layout.onboarding_screen2,
                R.layout.onboarding_screen3,
                R.layout.onboarding_screen4
        };
        
        // Setup ViewPager
        onboardingPagerAdapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(onboardingPagerAdapter);
        viewPager.registerOnPageChangeCallback(pageChangeCallback);
        
        // Setup dot indicators
        setupDotIndicators();
        
        // Button click listeners
        skipButton.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current > 0) {
                // Move to previous screen
                viewPager.setCurrentItem(current - 1);
            }
        });
        
        nextButton.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < layouts.length - 1) {
                // Move to next screen
                viewPager.setCurrentItem(current + 1);
            } else {
                // Last screen reached, launch lock setting activity
                launchLockSettingActivity();
                markOnboardingAsCompleted();
            }
        });
    }
    
    private void showWelcomeDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        WelcomeDialogFragment welcomeDialog = new WelcomeDialogFragment();
        welcomeDialog.show(fragmentManager, "WelcomeDialogFragment");
    }
    
    private ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            updateDotIndicators(position);
            
            // Show/hide previous button based on position
            skipButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            
            // Change button text on last screen
            if (position == layouts.length - 1) {
                nextButton.setText(R.string.get_started);
            } else {
                nextButton.setText(R.string.next);
            }
        }
    };
    
    private void setupDotIndicators() {
        LinearLayout dotsLayout = findViewById(R.id.dotsLayout);
        dots = new TextView[layouts.length];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            dotsLayout.addView(dots[i]);

            // Add margin between dots
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dots[i].getLayoutParams();
            params.setMargins(8, 0, 8, 0);
            dots[i].setLayoutParams(params);
        }

        // Set the first dot as active
        if (dots.length > 0) {
            dots[0].setTextColor(getResources().getColor(android.R.color.white));
        }
    }
    
    private void updateDotIndicators(int currentPage) {
        for (int i = 0; i < dots.length; i++) {
            if (i == currentPage) {
                dots[i].setTextColor(getResources().getColor(android.R.color.white));
            } else {
                dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }
    
    private boolean isOnboardingCompleted() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("onboarding_completed", false);
    }
    
    private void markOnboardingAsCompleted() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("onboarding_completed", true).apply();
    }
    
    private void launchLockSettingActivity() {
        Intent intent = new Intent(OnboardingActivity.this, LockSettingActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
    }
    
    @Override
    public void onHomeSettingsSelected() {
        // Implementation for home settings selection
        Intent homeSettingsIntent = new Intent(android.provider.Settings.ACTION_HOME_SETTINGS);
        startActivity(homeSettingsIntent);
    }
}
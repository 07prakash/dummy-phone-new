package com.dummy.dummyphoneprakash.activity;





import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.viewpager2.widget.ViewPager2;

import com.dummy.dummyphoneprakash.FrgmentDialog.WelcomeDialogFragment;
import com.dummy.dummyphoneprakash.R;
import com.dummy.dummyphoneprakash.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainPagerActivity extends BaseActivity {
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);



        if (isTaskRoot()) {
            // This is being launched as the home activity
            Intent intent = new Intent(this, LockSettingActivity.class);
            startActivity(intent);

        } else {
            // Handle cases where it's not launched as home (e.g., from recent apps)
            setContentView(R.layout.activity_main_pager);
        }

//        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//        startActivity(intent);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Check if we're the default launcher
        if (isMyLauncherDefault()) {
            viewPager.setCurrentItem(0); // Show HomeFragment
        }
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();

        if (currentItem == 1) {
            // If on page 1, go back to page 0
            viewPager.setCurrentItem(0, true); // true for smooth scroll animation
        } else if (currentItem == 0) {
     // back press is disabled on the first page

        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        viewPager.setCurrentItem(0); // Always show HomeFragment when returning
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMyLauncherDefault()) {
            viewPager.setCurrentItem(0); // Ensure HomeFragment is visible
        }

        if (!isMyLauncherDefault()) {
            // Only open launcher selection if not default launcher
            Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
            startActivity(homeSettingsIntent);
        }
    }

    @Override
    public void onHomeSettingsSelected() {

    }

    private boolean isMyLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        List<IntentFilter> filters = new ArrayList<>();
        filters.add(filter);

        final String myPackageName = getPackageName();
        List<ComponentName> activities = new ArrayList<>();

        PackageManager pm = getPackageManager();
        pm.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
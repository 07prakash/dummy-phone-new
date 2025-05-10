package com.example.dummyphoneprakash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockActivity extends AppCompatActivity {

    private LockAppAdapter adapter;
    private SharedPreferencesHelper prefsHelper;
    private RecyclerView appsRecyclerView;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        prefsHelper = new SharedPreferencesHelper(this);
        appsRecyclerView = findViewById(R.id.appsRecyclerView);
        saveButton = findViewById(R.id.lockButton);

        setupRecyclerView();
        setupSaveButton();
        checkAccessibilityPermission();
    }

    private void setupRecyclerView() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

        adapter = new LockAppAdapter(
                apps,
                pm,
                prefsHelper.getAllowedApps(),
                prefsHelper.getEssentialApps()
        );

        appsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appsRecyclerView.setAdapter(adapter);
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            Set<String> selectedApps = adapter.getSelectedRegularApps();
            Set<String> essentialApps = prefsHelper.getEssentialApps();

            prefsHelper.saveAllowedApps(selectedApps, essentialApps);

            Toast.makeText(this, "App blocking settings saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void checkAccessibilityPermission() {
        if (!isAccessibilityEnabled()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this,
                    "Please enable App Blocker in Accessibility Settings",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isAccessibilityEnabled() {
        String serviceName = getPackageName() + "/.AppBlockerService";
        String enabledServices = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        return enabledServices != null && enabledServices.contains(serviceName);
    }
}
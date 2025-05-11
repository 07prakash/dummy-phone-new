package com.example.dummyphoneprakash;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Set;

public class LockActivity extends AppCompatActivity {

    private LockAppAdapter adapter;
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        prefsHelper = new SharedPreferencesHelper(this);
        RecyclerView appsRecyclerView = findViewById(R.id.appsRecyclerView);
        Button saveButton = findViewById(R.id.lockButton);

        setupRecyclerView(appsRecyclerView);
        setupSaveButton(saveButton);
        checkAccessibilityPermission();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        adapter = new LockAppAdapter(
                pm.queryIntentActivities(intent, 0),
                pm,
                prefsHelper.getAllowedApps(),
                prefsHelper.getEssentialApps()
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSaveButton(Button saveButton) {
        saveButton.setOnClickListener(v -> {
            Set<String> selectedApps = adapter.getSelectedRegularApps();
            Set<String> essentialApps = prefsHelper.getEssentialApps();

            prefsHelper.saveAllowedApps(selectedApps, essentialApps);

            Toast.makeText(this, "App blocking settings saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void checkAccessibilityPermission() {
        String serviceName = getPackageName() + "/.AppBlockerService";
        String enabledServices = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );

        if (enabledServices == null || !enabledServices.contains(serviceName)) {
            Toast.makeText(this,
                    "Please enable App Blocker in Accessibility Settings",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
    }
}
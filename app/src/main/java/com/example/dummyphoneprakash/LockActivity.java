package com.example.dummyphoneprakash;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockActivity extends AppCompatActivity implements LockAppAdapter.OnAppCheckedListener {

    private RecyclerView appsRecyclerView;
    private Button lockButton;
    private Set<String> selectedApps;
    private Set<String> essentialApps;
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        prefsHelper = new SharedPreferencesHelper(this);
        essentialApps = prefsHelper.getEssentialApps();
        selectedApps = new HashSet<>(prefsHelper.getAllowedApps());

        // Always include essential apps
        selectedApps.addAll(essentialApps);

        appsRecyclerView = findViewById(R.id.appsRecyclerView);
        lockButton = findViewById(R.id.lockButton);

        setupRecyclerView();
        setupLockButton();
    }

    private void setupRecyclerView() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

        appsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appsRecyclerView.setAdapter(new LockAppAdapter(
                apps, pm, selectedApps, essentialApps, this));
    }

    private void setupLockButton() {
        lockButton.setOnClickListener(v -> {
            prefsHelper.setAllowedApps(selectedApps);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    @Override
    public void onAppChecked(String packageName, boolean isChecked) {
        if (!essentialApps.contains(packageName)) {
            if (isChecked) {
                selectedApps.add(packageName);
            } else {
                selectedApps.remove(packageName);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
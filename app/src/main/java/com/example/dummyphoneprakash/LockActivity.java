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

public class LockActivity extends AppCompatActivity {

    private RecyclerView appsRecyclerView;
    private Button saveButton;
    private LockAppAdapter adapter;
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        prefsHelper = new SharedPreferencesHelper(this);

        appsRecyclerView = findViewById(R.id.appsRecyclerView);
        saveButton = findViewById(R.id.lockButton);

        setupRecyclerView();
        setupSaveButton();
    }

    private void setupRecyclerView() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

        adapter = new LockAppAdapter(
                apps,
                pm,
                prefsHelper.getAllowedRegularApps(),
                prefsHelper.getDisabledEssentialApps(),
                prefsHelper.getEssentialApps(),
                (packageName, isSelected) -> {
                    // Selection changes are handled by the adapter
                }
        );

        appsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appsRecyclerView.setAdapter(adapter);
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            // Get current selections from adapter
            prefsHelper.saveSelections(
                    adapter.getSelectedRegularApps(),
                    adapter.getDisabledEssentialApps()
            );
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
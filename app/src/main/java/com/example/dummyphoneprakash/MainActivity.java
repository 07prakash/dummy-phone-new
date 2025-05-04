package com.example.dummyphoneprakash;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AppAdapter.AppClickListener {

    private RecyclerView appsRecyclerView;
    private AppAdapter appAdapter;
    private List<AppInfo> apps = new ArrayList<>();
    private GestureDetector gestureDetector;
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsHelper = new SharedPreferencesHelper(this);
        gestureDetector = new GestureDetector(this, new GestureListener());

        appsRecyclerView = findViewById(R.id.appsRecyclerView);
        appsRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        loadFilteredApps();

        appAdapter = new AppAdapter(apps, this);
        appsRecyclerView.setAdapter(appAdapter);
    }

    private void loadFilteredApps() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        apps.clear();

        for (ResolveInfo ri : resolveInfos) {
            String packageName = ri.activityInfo.packageName;

            if (prefsHelper.isAppAllowed(packageName)) {
                AppInfo app = new AppInfo();
                app.label = ri.loadLabel(pm).toString();
                app.packageName = packageName;
                app.icon = ri.activityInfo.loadIcon(pm);
                apps.add(app);
            }
        }
    }

    @Override
    public void onAppClick(AppInfo app) {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(app.packageName);
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Swipe right - go to HomeActivity
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh app list when returning from LockActivity
        loadFilteredApps();
        appAdapter.notifyDataSetChanged();
    }
}
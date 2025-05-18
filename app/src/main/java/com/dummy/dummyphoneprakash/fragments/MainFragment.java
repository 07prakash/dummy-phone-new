package com.dummy.dummyphoneprakash.fragments;









import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dummy.dummyphoneprakash.R;
import com.dummy.dummyphoneprakash.adapter.AppAdapter;
import com.dummy.dummyphoneprakash.AppInfo;
import com.dummy.dummyphoneprakash.SharedPreferencesHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainFragment extends BaseFragment implements AppAdapter.AppClickListener {
    private RecyclerView appsRecyclerView;
    private AppAdapter appAdapter;
    private List<AppInfo> apps = new ArrayList<>();
    private SharedPreferencesHelper prefsHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        prefsHelper = new SharedPreferencesHelper(requireContext());
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        appsRecyclerView = view.findViewById(R.id.appsRecyclerView);
        appsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        loadFilteredApps();
        appAdapter = new AppAdapter(apps, this);
        appsRecyclerView.setAdapter(appAdapter);
    }

    private void loadFilteredApps() {
        PackageManager pm = requireActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        apps.clear();

        Set<String> allowedApps = prefsHelper.getAllowedApps();
        Set<String> essentialApps = prefsHelper.getEssentialApps();

        for (ResolveInfo ri : resolveInfos) {
            String packageName = ri.activityInfo.packageName;
            if (allowedApps.contains(packageName) || essentialApps.contains(packageName)) {
                AppInfo app = new AppInfo();
                app.label = ri.loadLabel(pm).toString();
                app.packageName = packageName;
                app.icon = ri.activityInfo.loadIcon(pm);
                apps.add(app);
                Log.d("MainFragment", "Added app: " + app.label);
            }
        }
        Log.d("MainFragment", "Total apps shown: " + apps.size());
    }

    @Override
    public void onAppClick(AppInfo app) {
        try {
            PackageManager pm = requireActivity().getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            if (intent != null) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("MainFragment", "Error launching app", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFilteredApps();
        if (appAdapter != null) {
            appAdapter.notifyDataSetChanged();
        }
    }
}
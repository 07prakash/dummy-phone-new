
package com.example.dummyphoneprakash.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dummyphoneprakash.adapter.LockAppAdapter;
import com.example.dummyphoneprakash.R;
import com.example.dummyphoneprakash.SharedPreferencesHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LockFragment extends Fragment {

    private LockAppAdapter adapter;
    private SharedPreferencesHelper prefsHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsHelper = new SharedPreferencesHelper(requireContext());
        RecyclerView appsRecyclerView = view.findViewById(R.id.appsRecyclerView);

        setupRecyclerView(appsRecyclerView);
//        checkAccessibilityPermission();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        PackageManager pm = requireContext().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        Set<String> excludedPackages = new HashSet<>(Arrays.asList(
                "com.example.dummyphoneprakash",      // Dummy Phone app
                "com.android.settings",               // Default on most devices
                "com.samsung.android.settings",       // Samsung
                "com.miui.securitycenter",            // Xiaomi
                "com.coloros.safecenter",             // Oppo / Realme
                "com.huawei.systemmanager",           // Huawei
                "com.hihonor.systemmanager",          // Honor
                "com.transsion.XOSLauncher.settings"  // Infinix / Tecno / itel
        ));

        adapter = new LockAppAdapter(
                pm.queryIntentActivities(intent, 0),
                pm,
                prefsHelper.getAllowedApps(),
                prefsHelper.getEssentialApps(),
                excludedPackages
        );

        adapter.setOnAppCheckedListener((packageName, isChecked) -> {
            Set<String> selectedApps = adapter.getSelectedRegularApps();
            Set<String> essentialApps = adapter.getSelectedEssentialApps();
            prefsHelper.saveAllowedApps(selectedApps, essentialApps);
            if (isChecked) {
            Toast.makeText(requireContext(), "Added", Toast.LENGTH_SHORT).show();}
            else {
                Toast.makeText(requireContext(), "Removed", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        recyclerView.setAdapter(adapter);
    }

//    private void checkAccessibilityPermission() {
//        String serviceName = requireContext().getPackageName() + "/.AppBlockerService";
//        String enabledServices = Settings.Secure.getString(
//                requireContext().getContentResolver(),
//                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
//        );
//
//        if (enabledServices == null || !enabledServices.contains(serviceName)) {
//            Toast.makeText(requireContext(),
//                    "Please enable App Blocker in Accessibility Settings",
//                    Toast.LENGTH_LONG).show();
//            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//        }
//    }
}
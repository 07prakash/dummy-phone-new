
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
import com.example.dummyphoneprakash.R;
import com.example.dummyphoneprakash.adapter.LockAppAdapter;
import com.example.dummyphoneprakash.SharedPreferencesHelper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LockFragment extends BaseFragment {
    private LockAppAdapter adapter;
    private SharedPreferencesHelper prefsHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefsHelper = new SharedPreferencesHelper(requireContext());
        setupRecyclerView(view.findViewById(R.id.appsRecyclerView));
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        PackageManager pm = requireContext().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        Set<String> excludedPackages = new HashSet<>(Arrays.asList(
                "com.example.dummyphoneprakash",
                "com.android.settings",
                "com.samsung.android.settings",
                "com.miui.securitycenter",
                "com.coloros.safecenter",
                "com.huawei.systemmanager",
                "com.hihonor.systemmanager",
                "com.transsion.XOSLauncher.settings"
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
            Set<String> selectedEssential = adapter.getSelectedEssentialApps();
            prefsHelper.saveAllowedApps(selectedApps, selectedEssential);

            String message = isChecked ? "Added to allowed apps" : "Removed from allowed apps";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
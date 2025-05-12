// LockFragment.java
package com.example.dummyphoneprakash;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        PackageManager pm = requireContext().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        adapter = new LockAppAdapter(
                pm.queryIntentActivities(intent, 0),
                pm,
                prefsHelper.getAllowedApps(),
                prefsHelper.getEssentialApps()
        );

        adapter.setOnAppCheckedListener((packageName, isChecked) -> {
            Set<String> selectedApps = adapter.getSelectedRegularApps();
            Set<String> essentialApps = prefsHelper.getEssentialApps();
            prefsHelper.saveAllowedApps(selectedApps, essentialApps);
            Toast.makeText(requireContext(), "Settings saved", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        recyclerView.setAdapter(adapter);
    }


}
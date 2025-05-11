package com.example.dummyphoneprakash;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockAppAdapter extends RecyclerView.Adapter<LockAppAdapter.AppViewHolder> {

    interface OnAppCheckedListener {
        void onAppChecked(String packageName, boolean isChecked);
    }

    private final List<ResolveInfo> apps;
    private final PackageManager pm;
    private final Set<String> selectedPackages;
    private final Set<String> essentialApps;

    public LockAppAdapter(List<ResolveInfo> apps,
                          PackageManager pm,
                          Set<String> initiallySelected,
                          Set<String> essentialApps) {
        this.apps = apps;
        this.pm = pm;
        this.selectedPackages = new HashSet<>(initiallySelected);
        this.essentialApps = new HashSet<>(essentialApps);
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lock_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        ResolveInfo app = apps.get(position);
        String packageName = app.activityInfo.packageName;
        boolean isEssential = essentialApps.contains(packageName);
        boolean isChecked = isEssential || selectedPackages.contains(packageName);

        holder.bind(app, pm, isChecked, isEssential, (pkgName, checked) -> {
            if (!isEssential) { // Only allow changes for non-essential apps
                if (checked) {
                    selectedPackages.add(pkgName);
                } else {
                    selectedPackages.remove(pkgName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public Set<String> getSelectedRegularApps() {
        return new HashSet<>(selectedPackages);
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        private final ImageView appIcon;
        private final TextView appName;
        private final CheckBox checkBox;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            checkBox = itemView.findViewById(R.id.appCheckBox);
        }

        void bind(ResolveInfo app,
                  PackageManager pm,
                  boolean isChecked,
                  boolean isEssential,
                  OnAppCheckedListener listener) {

            String packageName = app.activityInfo.packageName;
            appIcon.setImageDrawable(app.loadIcon(pm));
            appName.setText(app.loadLabel(pm));

            // Visual indication for essential apps
            float alpha = isEssential ? 0.7f : 1.0f;
            appIcon.setAlpha(alpha);
            appName.setAlpha(alpha);

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isChecked);
            checkBox.setEnabled(!isEssential); // Disable checkbox for essential apps

            checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
                if (!isEssential) { // Only notify for non-essential apps
                    listener.onAppChecked(packageName, checked);
                } else {
                    // Force checked state for essential apps
                    checkBox.setChecked(true);
                }
            });
        }
    }
}
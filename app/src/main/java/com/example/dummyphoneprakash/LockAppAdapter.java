// LockAppAdapter.java
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockAppAdapter extends RecyclerView.Adapter<LockAppAdapter.AppViewHolder> {

    public interface OnAppCheckedListener {
        void onAppChecked(String packageName, boolean isChecked);
    }

    private List<ResolveInfo> originalApps;
    private List<ResolveInfo> filteredApps;
    private final PackageManager pm;
    private final Set<String> selectedPackages;
    private final Set<String> essentialApps;
    private OnAppCheckedListener checkedListener;

    public LockAppAdapter(List<ResolveInfo> apps,
                          PackageManager pm,
                          Set<String> initiallySelected,
                          Set<String> essentialApps) {
        this.originalApps = apps;
        this.pm = pm;
        this.selectedPackages = new HashSet<>(initiallySelected);
        this.essentialApps = new HashSet<>(essentialApps);
        this.filteredApps = new ArrayList<>(apps);
        sortAppsByCheckedState();
    }

    public void setOnAppCheckedListener(OnAppCheckedListener listener) {
        this.checkedListener = listener;
    }

    private void sortAppsByCheckedState() {
        Collections.sort(filteredApps, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                String pkgA = a.activityInfo.packageName;
                String pkgB = b.activityInfo.packageName;

                boolean isCheckedA = isChecked(pkgA);
                boolean isCheckedB = isChecked(pkgB);

                if (isCheckedA && !isCheckedB) {
                    return -1; // A comes first
                } else if (!isCheckedA && isCheckedB) {
                    return 1; // B comes first
                } else {
                    // If both are checked or both are unchecked, sort alphabetically
                    String labelA = a.loadLabel(pm).toString();
                    String labelB = b.loadLabel(pm).toString();
                    return labelA.compareToIgnoreCase(labelB);
                }
            }
        });
    }

    private boolean isChecked(String packageName) {
        return essentialApps.contains(packageName) || selectedPackages.contains(packageName);
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
        ResolveInfo app = filteredApps.get(position);
        String packageName = app.activityInfo.packageName;
        boolean isEssential = essentialApps.contains(packageName);
        boolean isChecked = isEssential || selectedPackages.contains(packageName);

        holder.bind(app, pm, isChecked, isEssential, (pkgName, checked) -> {
            if (!isEssential) {
                if (checked) {
                    selectedPackages.add(pkgName);
                } else {
                    selectedPackages.remove(pkgName);
                }
                sortAppsByCheckedState();
                notifyDataSetChanged();
                if (checkedListener != null) {
                    checkedListener.onAppChecked(pkgName, checked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredApps.size();
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

            float alpha = isEssential ? 0.7f : 1.0f;
            appIcon.setAlpha(alpha);
            appName.setAlpha(alpha);

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isChecked);
            checkBox.setEnabled(!isEssential);

            checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
                if (!isEssential) {
                    listener.onAppChecked(packageName, checked);
                } else {
                    checkBox.setChecked(true);
                }
            });
        }
    }
}
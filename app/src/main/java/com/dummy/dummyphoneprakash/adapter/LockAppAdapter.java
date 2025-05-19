package com.dummy.dummyphoneprakash.adapter;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dummy.dummyphoneprakash.R;

import java.util.ArrayList;
import java.util.Collections;
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
    private final Set<String> selectedRegularApps;
    private final Set<String> selectedEssentialApps;
    private final Set<String> essentialApps;
    private OnAppCheckedListener checkedListener;
    private final Set<String> excludedPackages;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public LockAppAdapter(List<ResolveInfo> apps,
                          PackageManager pm,
                          Set<String> initiallySelected,
                          Set<String> essentialApps,
                          Set<String> excludedPackages) {
        this.pm = pm;
        this.selectedRegularApps = new HashSet<>(initiallySelected);
        this.essentialApps = new HashSet<>(essentialApps);
        this.selectedEssentialApps = new HashSet<>(essentialApps);
        this.excludedPackages = excludedPackages;
        this.originalApps = filterOutExcludedApps(apps);
        this.filteredApps = new ArrayList<>(originalApps);
        sortAppsByCheckedState();
    }

    private List<ResolveInfo> filterOutExcludedApps(List<ResolveInfo> apps) {
        List<ResolveInfo> filtered = new ArrayList<>();
        for (ResolveInfo app : apps) {
            String packageName = app.activityInfo.packageName;
            if (!excludedPackages.contains(packageName)) {
                filtered.add(app);
            }
        }
        return filtered;
    }

    private void sortAppsByCheckedState() {
        Collections.sort(filteredApps, (a, b) -> {
            String pkgA = a.activityInfo.packageName;
            String pkgB = b.activityInfo.packageName;

            boolean isCheckedA = isChecked(pkgA);
            boolean isCheckedB = isChecked(pkgB);

            if (isCheckedA && !isCheckedB) {
                return -1;
            } else if (!isCheckedA && isCheckedB) {
                return 1;
            } else {
                String labelA = a.loadLabel(pm).toString();
                String labelB = b.loadLabel(pm).toString();
                return labelA.compareToIgnoreCase(labelB);
            }
        });
    }

    private boolean isChecked(String packageName) {
        return selectedEssentialApps.contains(packageName) ||
                (!essentialApps.contains(packageName) && selectedRegularApps.contains(packageName));
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
        boolean isChecked = isChecked(packageName);

        holder.bind(app, pm, isChecked, isEssential, (pkgName, checked) -> {
            if (isEssential) {
                if (checked) {
                    selectedEssentialApps.add(pkgName);
                } else {
                    selectedEssentialApps.remove(pkgName);
                }
            } else {
                if (checked) {
                    selectedRegularApps.add(pkgName);
                } else {
                    selectedRegularApps.remove(pkgName);
                }
            }

            // Add slight delay before sorting and updating
            handler.postDelayed(() -> {
                sortAppsByCheckedState();
                notifyDataSetChanged();
                if (checkedListener != null) {
                    checkedListener.onAppChecked(pkgName, checked);
                }
            }, 200); // 150ms delay for smoother UX
        });
    }

    @Override
    public int getItemCount() {
        return filteredApps.size();
    }

    public Set<String> getSelectedRegularApps() {
        return new HashSet<>(selectedRegularApps);
    }

    public Set<String> getSelectedEssentialApps() {
        return new HashSet<>(selectedEssentialApps);
    }

    public void setOnAppCheckedListener(OnAppCheckedListener listener) {
        this.checkedListener = listener;
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

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isChecked);
            checkBox.setEnabled(true);

            checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
                listener.onAppChecked(packageName, checked);
            });

            // Make entire item clickable
            itemView.setOnClickListener(v -> checkBox.setChecked(!checkBox.isChecked()));

            itemView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            });
        }


    }
}
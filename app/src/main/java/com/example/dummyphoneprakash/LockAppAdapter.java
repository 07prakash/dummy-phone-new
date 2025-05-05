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

    public interface AppSelectionListener {
        void onAppSelectionChanged(String packageName, boolean isSelected);
    }

    private final List<ResolveInfo> apps;
    private final PackageManager packageManager;
    private final Set<String> essentialApps;
    private final AppSelectionListener selectionListener;

    // Track all selected states
    private final Set<String> selectedPackages = new HashSet<>();
    private final Set<String> disabledEssentialPackages = new HashSet<>();

    public LockAppAdapter(List<ResolveInfo> apps,
                          PackageManager pm,
                          Set<String> initiallySelectedRegularApps,
                          Set<String> initiallyDisabledEssentialApps,
                          Set<String> essentialApps,
                          AppSelectionListener listener) {
        this.apps = apps;
        this.packageManager = pm;
        this.essentialApps = essentialApps;
        this.selectionListener = listener;

        // Initialize selections
        this.selectedPackages.addAll(initiallySelectedRegularApps);
        this.disabledEssentialPackages.addAll(initiallyDisabledEssentialApps);
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
        ResolveInfo appInfo = apps.get(position);
        String packageName = appInfo.activityInfo.packageName;
        boolean isEssential = essentialApps.contains(packageName);

        // Determine if this app should be checked
        boolean isChecked = isEssential ?
                !disabledEssentialPackages.contains(packageName) :
                selectedPackages.contains(packageName);

        holder.bind(appInfo, packageManager, isChecked, isEssential, (pkgName, checked) -> {
            if (isEssential) {
                if (checked) {
                    disabledEssentialPackages.remove(pkgName);
                } else {
                    disabledEssentialPackages.add(pkgName);
                }
            } else {
                if (checked) {
                    selectedPackages.add(pkgName);
                } else {
                    selectedPackages.remove(pkgName);
                }
            }
            selectionListener.onAppSelectionChanged(pkgName, checked);
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public Set<String> getSelectedRegularApps() {
        return new HashSet<>(selectedPackages);
    }

    public Set<String> getDisabledEssentialApps() {
        return new HashSet<>(disabledEssentialPackages);
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

        void bind(ResolveInfo appInfo,
                  PackageManager pm,
                  boolean isChecked,
                  boolean isEssential,
                  AppSelectionListener listener) {

            String packageName = appInfo.activityInfo.packageName;

            // Set app info
            appIcon.setImageDrawable(appInfo.loadIcon(pm));
            appName.setText(appInfo.loadLabel(pm));

            // Clear previous listener to avoid duplicate triggers
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isChecked);

            // Visual styling for essential apps
            float alpha = isEssential ? 0.9f : 1.0f;
            appIcon.setAlpha(alpha);
            appName.setAlpha(alpha);

            // Set new listener
            checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
                listener.onAppSelectionChanged(packageName, checked);
            });
        }
    }
 }
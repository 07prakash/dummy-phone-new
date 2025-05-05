package com.example.dummyphoneprakash;






import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
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
    private final List<ResolveInfo> apps;
    private final PackageManager pm;
    private final Set<String> essentialApps;
    private final Set<String> selectedPackages = new HashSet<>();
    private final Set<String> disabledEssentialPackages = new HashSet<>();

    public LockAppAdapter(List<ResolveInfo> apps,
                          PackageManager pm,
                          Set<String> initiallySelectedRegularApps,
                          Set<String> initiallyDisabledEssentialApps,
                          Set<String> essentialApps) {
        this.apps = apps;
        this.pm = pm;
        this.essentialApps = essentialApps;
        this.selectedPackages.addAll(initiallySelectedRegularApps);
        this.disabledEssentialPackages.addAll(initiallyDisabledEssentialApps);

        Log.d("LockAdapter", "Initialized with " + selectedPackages.size() + " regular and "
                + disabledEssentialPackages.size() + " disabled essential apps");
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
        boolean isChecked = isEssential ?
                !disabledEssentialPackages.contains(packageName) :
                selectedPackages.contains(packageName);

        holder.bind(app, pm, isChecked, isEssential, (pkgName, checked) -> {
            if (isEssential) {
                if (checked) {
                    disabledEssentialPackages.remove(pkgName);
                } else {
                    disabledEssentialPackages.add(pkgName);
                }
                Log.d("LockAdapter", "Essential app " + pkgName + " " + (checked ? "enabled" : "disabled"));
            } else {
                if (checked) {
                    selectedPackages.add(pkgName);
                } else {
                    selectedPackages.remove(pkgName);
                }
                Log.d("LockAdapter", "Regular app " + pkgName + " " + (checked ? "selected" : "deselected"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    @Override
    public long getItemId(int position) {
        return apps.get(position).activityInfo.packageName.hashCode();
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

        void bind(ResolveInfo app, PackageManager pm,
                  boolean isChecked, boolean isEssential,
                  OnCheckedChangeListener listener) {
            String packageName = app.activityInfo.packageName;

            appIcon.setImageDrawable(app.loadIcon(pm));
            appName.setText(app.loadLabel(pm));

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isChecked);

            float alpha = isEssential ? 0.9f : 1.0f;
            appIcon.setAlpha(alpha);
            appName.setAlpha(alpha);

            checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
                listener.onCheckedChanged(packageName, checked);
            });
        }
    }

    interface OnCheckedChangeListener {
        void onCheckedChanged(String packageName, boolean isChecked);
    }
}
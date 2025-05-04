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
import java.util.List;
import java.util.Set;

public class LockAppAdapter extends RecyclerView.Adapter<LockAppAdapter.AppViewHolder> {

    private final List<ResolveInfo> apps;
    private final PackageManager pm;
    private final Set<String> selectedApps;
    private final Set<String> essentialApps;
    private final OnAppCheckedListener listener;

    public interface OnAppCheckedListener {
        void onAppChecked(String packageName, boolean isChecked);
    }

    public LockAppAdapter(List<ResolveInfo> apps, PackageManager pm,
                          Set<String> selectedApps, Set<String> essentialApps,
                          OnAppCheckedListener listener) {
        this.apps = apps;
        this.pm = pm;
        this.selectedApps = selectedApps;
        this.essentialApps = essentialApps;
        this.listener = listener;
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
        boolean isChecked = selectedApps.contains(packageName);

        holder.bind(app, pm, isChecked, isEssential, listener);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        private final ImageView appIcon;
        private final TextView appName;
        private final CheckBox checkBox;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            checkBox = itemView.findViewById(R.id.appCheckBox);
        }

        public void bind(ResolveInfo app, PackageManager pm,
                         boolean isChecked, boolean isEssential,
                         OnAppCheckedListener listener) {
            appIcon.setImageDrawable(app.loadIcon(pm));
            appName.setText(app.loadLabel(pm));
            checkBox.setChecked(isChecked);

            // Disable interaction for essential apps
            checkBox.setEnabled(!isEssential);
            itemView.setAlpha(isEssential ? 0.7f : 1.0f);

            if (!isEssential) {
                checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
                    listener.onAppChecked(app.activityInfo.packageName, checked);
                });
            } else {
                checkBox.setOnCheckedChangeListener(null);
            }
        }
    }
}
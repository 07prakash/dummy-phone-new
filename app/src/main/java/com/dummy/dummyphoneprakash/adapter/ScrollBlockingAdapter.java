package com.dummy.dummyphoneprakash.adapter;

import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dummy.dummyphoneprakash.R;
import com.dummy.dummyphoneprakash.SharedPreferencesHelper;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

public class ScrollBlockingAdapter extends RecyclerView.Adapter<ScrollBlockingAdapter.AppViewHolder> {

    private List<String> apps;
    private OnAppToggleListener listener;
    private SharedPreferencesHelper prefsHelper;

    public interface OnAppToggleListener {
        void onAppToggle(String packageName, boolean isBlocked);
    }

    public ScrollBlockingAdapter(List<String> apps, OnAppToggleListener listener) {
        this.apps = apps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scroll_blocking_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        String packageName = apps.get(position);
        holder.bind(packageName);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        MaterialCheckBox checkBox;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            checkBox = itemView.findViewById(R.id.checkBox);
            
            if (prefsHelper == null) {
                prefsHelper = new SharedPreferencesHelper(itemView.getContext());
            }
        }

        public void bind(String packageName) {
            try {
                PackageManager pm = itemView.getContext().getPackageManager();
                
                // Set app icon
                appIcon.setImageDrawable(pm.getApplicationIcon(packageName));
                
                // Set app name
                String appLabel = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString();
                appName.setText(appLabel);
                
                // Set checkbox state
                boolean isBlocked = prefsHelper.isAppScrollBlocked(packageName);
                checkBox.setChecked(isBlocked);
                
                // Set checkbox listener
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (listener != null) {
                        listener.onAppToggle(packageName, isChecked);
                    }
                });
                
                // Make entire item clickable
                itemView.setOnClickListener(v -> checkBox.setChecked(!checkBox.isChecked()));
                
            } catch (PackageManager.NameNotFoundException e) {
                // App not found, show package name
                appName.setText(packageName);
                appIcon.setImageResource(R.drawable.ic_info);
            }
        }
    }
}

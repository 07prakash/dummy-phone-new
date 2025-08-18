package com.dummy.dummyphoneprakash.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dummy.dummyphoneprakash.R;
import com.dummy.dummyphoneprakash.WebViewActivity;
import com.dummy.dummyphoneprakash.adapter.AppAdapter;
import com.dummy.dummyphoneprakash.AppInfo;
import com.dummy.dummyphoneprakash.SharedPreferencesHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainFragment extends BaseFragment implements AppAdapter.AppClickListener {
    private RecyclerView appsRecyclerView;
    private AppAdapter appAdapter;
    private List<AppInfo> apps = new ArrayList<>();
    private SharedPreferencesHelper prefsHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        prefsHelper = new SharedPreferencesHelper(requireContext());
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        appsRecyclerView = view.findViewById(R.id.appsRecyclerView);
        appsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        loadFilteredApps();
        appAdapter = new AppAdapter(apps, this);
        appsRecyclerView.setAdapter(appAdapter);
    }

    private void loadFilteredApps() {
        PackageManager pm = requireActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        apps.clear();

        Set<String> allowedApps = prefsHelper.getAllowedApps();
        Set<String> essentialApps = prefsHelper.getEssentialApps();

        for (ResolveInfo ri : resolveInfos) {
            String packageName = ri.activityInfo.packageName;
            if (allowedApps.contains(packageName) || essentialApps.contains(packageName)) {
                AppInfo app = new AppInfo();
                app.label = ri.loadLabel(pm).toString();
                app.packageName = packageName;
                app.icon = ri.activityInfo.loadIcon(pm);
                apps.add(app);
                Log.d("MainFragment", "Added app: " + app.label);
            }
        }
        Log.d("MainFragment", "Total apps shown: " + apps.size());
    }

    @Override
    public void onAppClick(AppInfo app) {
        try {
            // Check if the app is a browser and redirect to WebView
            if (isBrowserApp(app.packageName)) {
                Intent webViewIntent = new Intent(requireActivity(), WebViewActivity.class);
                startActivity(webViewIntent);
            } else {
                // Normal app launch for other apps
                PackageManager pm = requireActivity().getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(app.packageName);
                if (intent != null) {
                    startActivity(intent);
                }
            }

        } catch (Exception e) {
            Log.e("MainFragment", "Error launching app", e);
        }
    }

    private boolean isBrowserApp(String packageName) {
        // List of common browser package names
        String[] browserPackages = {
                "com.google.android.googlequicksearchbox", // Google Search
                "com.android.chrome",           // Google Chrome
                "com.google.android.apps.chrome",
                "org.mozilla.firefox",          // Mozilla Firefox
                "org.mozilla.firefox_beta",
                "com.microsoft.emmx",           // Microsoft Edge
                "com.microsoft.emmx.beta",
                "com.opera.browser",            // Opera Browser
                "com.opera.browser.beta",
                "com.opera.mini.native",        // Opera Mini
                "com.uc.browser.en",            // UC Browser
                "com.uc.browser.hd",
                "com.baidu.browser.apps",       // Baidu Browser
                "com.baidu.browser",
                "com.qihoo.browser",            // 360 Browser
                "com.qihoo.browser.standard",
                "com.tencent.mtt",              // QQ Browser
                "com.tencent.mtt.x86",
                "com.sogou.mobile.explorer",    // Sogou Browser
                "com.sec.android.app.browser",  // Samsung Internet
                "com.samsung.android.app.sbrowser",
                "com.samsung.android.browser",
                "com.android.browser",          // Android Browser (AOSP)
                "com.google.android.browser",
                "com.mi.global.browser",        // Xiaomi Browser
                "com.miui.browser",
                "com.huawei.browser",           // Huawei Browser
                "com.huawei.hwvplayer.youku",
                "com.coloros.browser",          // OPPO Browser
                "com.oppo.browser",
                "com.vivo.browser",             // Vivo Browser
                "com.vivo.browser.provider",
                "com.oneplus.browser",          // OnePlus Browser
                "com.oneplus.browser.provider",
                "com.lenovo.browser",           // Lenovo Browser
                "com.asus.browser",             // ASUS Browser
                "com.sonyericsson.browser",     // Sony Browser
                "com.lge.browser",              // LG Browser
                "com.htc.browser",              // HTC Browser
                "com.motorola.browser",         // Motorola Browser
                "com.alibaba.browser",          // Alibaba Browser
                "com.alibaba.browser.provider",
                "com.duckduckgo.mobile.android", // DuckDuckGo Browser
                "com.brave.browser",            // Brave Browser
                "com.brave.browser_beta",
                "com.kiwibrowser.browser",      // Kiwi Browser
                "com.kiwibrowser.browser.beta",
                "mark.via.gp",                  // Via Browser
                "de.baumann.browser",           // FOSS Browser
                "org.adblockplus.browser",      // Adblock Browser
                "com.cloudmosa.puffinFree",     // Puffin Browser
                "com.cloudmosa.puffin",
                "com.maxthon.cloudbrowser",     // Maxthon Browser
                "com.maxthon.cloudbrowser.pro",
                "com.yandex.browser",           // Yandex Browser
                "com.yandex.browser.beta",
                "com.naver.whale",              // Whale Browser
                "com.naver.whale.beta",
                "com.sec.android.app.sbrowser", // Samsung Internet (alternative)
                "com.samsung.android.sbrowser",
                "com.samsung.android.app.sbrowser.beta"
        };

        for (String browserPackage : browserPackages) {
            if (packageName.equals(browserPackage)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFilteredApps();
        if (appAdapter != null) {
            appAdapter.notifyDataSetChanged();
        }
    }
}
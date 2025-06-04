package com.dummy.dummyphoneprakash;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    // List of blocked domains
    private Set<String> blockedDomains = new HashSet<>(Arrays.asList(
            // Social media
            "youtube.com", "facebook.com", "instagram.com", "tiktok.com",
            "twitter.com", "snapchat.com", "reddit.com", "play.google.com",

            // Adult websites
            "pornhub.com", "xvideos.com", "xnxx.com", "redtube.com", "youporn.com",
            "xhamster.com", "spankbang.com", "beeg.com", "tube8.com", "hclips.com", "tnaflix.com"
    ));

    // List of blocked keywords (for smarter matching)
    private String[] blockedKeywords = {
//            "porn", "sex", "xxx", "xvideo", "redtube", "xnxx", "youporn", "spank", "xhamster",
//            "tiktok", "facebook", "snapchat", "instagram", "twitter", "reddit", "pron"

    };

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webView);

        // Set up back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Configure WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Set custom WebViewClient to handle URL blocking
        webView.setWebViewClient(new CustomWebViewClient());

        // Load initial homepage
        webView.loadUrl("https://www.google.com/search?&safe=active");



    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return checkAndBlockUrl(url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return checkAndBlockUrl(url);
        }

        private boolean checkAndBlockUrl(String url) {
            String lowerUrl = url.toLowerCase();

            // Check against blocked domains
            for (String domain : blockedDomains) {
                if (lowerUrl.contains(domain)) {
                    showBlockedMessage();
                    return true;
                }
            }

            // Check against blocked keywords
            for (String keyword : blockedKeywords) {
                if (lowerUrl.contains(keyword)) {
                    showBlockedMessage();
                    return true;
                }
            }

            return false; // Allow the URL
        }

        private void showBlockedMessage() {
            Toast.makeText(WebViewActivity.this,
                    "This website is restricted", Toast.LENGTH_SHORT).show();
            if (webView.canGoBack()) {
                webView.goBack();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

package com.example.dummyphoneprakash.FrgmentDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.dummyphoneprakash.R;
import com.google.android.material.checkbox.MaterialCheckBox;

public class WelcomeDialogFragment extends DialogFragment {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String PREF_FIRST_RUN = "firstRun";
    private Button okButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullWidthDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_welcome, container, false);

        // Make dialog non-cancelable
        if (getDialog() != null) {
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
        }

        MaterialCheckBox agreeCheckbox = view.findViewById(R.id.agreeCheckbox);
        Button exitButton = view.findViewById(R.id.exitButton);
        okButton = view.findViewById(R.id.okButton);

        // Set initial button state
        updateButtonState(false);

        agreeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateButtonState(isChecked);
        });

        exitButton.setOnClickListener(v -> {
            requireActivity().finish();
        });

        okButton.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(PREF_FIRST_RUN, false).apply();
            dismiss();
        });

        setupLinks(view);

        return view;
    }

    private void updateButtonState(boolean isEnabled) {
        okButton.setEnabled(isEnabled);
        if (isEnabled) {
            okButton.setBackgroundResource(R.drawable.button_primary_enabled);
            okButton.setTextColor(Color.WHITE);
        } else {
            okButton.setBackgroundResource(R.drawable.button_primary_disabled);
            okButton.setTextColor(Color.parseColor("#88FFFFFF")); // Semi-transparent white
        }
    }

    private void setupLinks(View view) {
        TextView privacyPolicy = view.findViewById(R.id.privacyPolicy);
        TextView termsOfService = view.findViewById(R.id.termsOfService);

        // Privacy Policy link
        String privacyText = "Privacy Policy";
        SpannableString privacySpannable = new SpannableString(privacyText);
        ClickableSpan privacyClickable = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                openUrl("https://dummyphone-privacy.netlify.app");
            }
        };
        privacySpannable.setSpan(privacyClickable, 0, privacyText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyPolicy.setText(privacySpannable);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        // Terms of Service link
        String termsText = "Terms of Service";
        SpannableString termsSpannable = new SpannableString(termsText);
        ClickableSpan termsClickable = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                openUrl("https://dummyphone-terms.netlify.app");
            }
        };
        termsSpannable.setSpan(termsClickable, 0, termsText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsOfService.setText(termsSpannable);
        termsOfService.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    public static boolean shouldShow(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_FIRST_RUN, true);
    }
}
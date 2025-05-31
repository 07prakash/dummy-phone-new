package com.dummy.dummyphoneprakash.FrgmentDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.dummy.dummyphoneprakash.R;

public class TimeEndDialog extends DialogFragment {

    private TimeEndListener listener;

    public interface TimeEndListener {
        void onContinueSelected();
        void onExitSelected();
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TimeEndListener) {
            listener = (TimeEndListener) context;
        } else if (getParentFragment() instanceof TimeEndListener) {
            listener = (TimeEndListener) getParentFragment();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // Allow dismissing when clicking outside
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_time_ended, container, false);

        Button exitButton = view.findViewById(R.id.exitButton);
        Button continueButton = view.findViewById(R.id.continueButton);

        exitButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExitSelected();
            }
            dismiss();
        });

        continueButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContinueSelected();
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Get screen height
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenHeight = displayMetrics.heightPixels;
                int dialogHeight = (int) (screenHeight * 0.30); // 32% of screen height

                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
                window.setDimAmount(0.7f);


            }
            // Keep this false to prevent dismissal with back button
            dialog.setCancelable(true);
        }
    }
}
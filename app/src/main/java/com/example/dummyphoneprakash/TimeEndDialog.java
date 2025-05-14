package com.example.dummyphoneprakash;

import static android.app.PendingIntent.getActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class TimeEndDialog extends DialogFragment {

    private TimeEndListener listener;

    public interface TimeEndListener {
        void onContinueSelected();
        void onExitSelected();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Try to get listener from activity
        if (context instanceof TimeEndListener) {
            listener = (TimeEndListener) context;
        }
        // Then try parent fragment
        else if (getParentFragment() instanceof TimeEndListener) {
            listener = (TimeEndListener) getParentFragment();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle("Time Ended")
                .setMessage("Your time has ended. Want to continue or exit?")
                .setPositiveButton("Continue", (d, w) -> {
                    if (listener != null) listener.onContinueSelected();
                })
                .setNegativeButton("Exit", (d, w) -> {
                    if (listener != null) listener.onExitSelected();
                })
                .setCancelable(false)
                .create();
    }
}
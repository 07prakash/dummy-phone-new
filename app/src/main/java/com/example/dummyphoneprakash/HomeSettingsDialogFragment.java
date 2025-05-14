package com.example.dummyphoneprakash;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.DialogFragment;

public class HomeSettingsDialogFragment extends DialogFragment {

    public interface HomeSettingsListener {
        void onHomeSettingsSelected();
        void onCancelSelected();
    }

    private HomeSettingsListener listener;

    public static HomeSettingsDialogFragment newInstance() {
        return new HomeSettingsDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (HomeSettingsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeSettingsListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Launcher")
                .setMessage("You need to set this app as your default launcher to continue. Open launcher settings now?")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onHomeSettingsSelected();
                        }
                        openHomeSettings();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onCancelSelected();
                        }
                    }
                });
        return builder.create();
    }

    private void openHomeSettings() {
        Intent homeSettingsIntent = new Intent(Settings.ACTION_HOME_SETTINGS);
        homeSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeSettingsIntent);
    }
}
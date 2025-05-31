package com.dummy.dummyphoneprakash.FrgmentDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.dummy.dummyphoneprakash.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.TimeUnit;

public class CustomTimePickerFragment extends DialogFragment {

    public interface TimePickerListener {
        void onTimeSet(long durationInMillis);
        void onCancel();
    }

    private TimePickerListener listener;
    private TextView selectedTimeTextView;
    private MaterialButton cancelButton, setButton;
    private long selectedDuration = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (TimePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement TimePickerListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_time_picker, null);


        // Initialize views
        NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
        NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
        NumberPicker hourPicker = dialogView.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);
        selectedTimeTextView = dialogView.findViewById(R.id.countdownTextView);
        cancelButton = dialogView.findViewById(R.id.cancelButton);
        setButton = dialogView.findViewById(R.id.setButton);

        // Set up pickers
        setupNumberPicker(monthPicker, 0, 12, "mo");
        setupNumberPicker(dayPicker, 0, 31, "d");
        setupNumberPicker(hourPicker, 0, 23, "h");
        setupNumberPicker(minutePicker, 0, 59, "m");

        // Set up value change listener
        NumberPicker.OnValueChangeListener valueChangeListener = (picker, oldVal, newVal) -> {
            selectedDuration = calculateDurationInMillis(
                    monthPicker.getValue(),
                    dayPicker.getValue(),
                    hourPicker.getValue(),
                    minutePicker.getValue()
            );
            updateSelectedTimeDisplay();
        };

        monthPicker.setOnValueChangedListener(valueChangeListener);
        dayPicker.setOnValueChangedListener(valueChangeListener);
        hourPicker.setOnValueChangedListener(valueChangeListener);
        minutePicker.setOnValueChangedListener(valueChangeListener);

        // Initialize display
        updateSelectedTimeDisplay();

        // Set button click listeners
        cancelButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });

        setButton.setOnClickListener(v -> {
            if (listener != null && selectedDuration > 0) {
                listener.onTimeSet(selectedDuration);
                dismiss();
            } else {
                selectedTimeTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                selectedTimeTextView.setText("Please select a duration");
            }
        });

        builder.setView(dialogView);
        Dialog dialog = builder.create();

        // Remove default buttons area
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void setupNumberPicker(NumberPicker picker, int min, int max, String suffix) {
        picker.setMinValue(min);
        picker.setMaxValue(max);
        picker.setFormatter(value -> value == 0 ? "0" : value + suffix);
        picker.setWrapSelectorWheel(false);
    }

    private long calculateDurationInMillis(int months, int days, int hours, int minutes) {
        return TimeUnit.DAYS.toMillis(months * 30L + days) +
                TimeUnit.HOURS.toMillis(hours) +
                TimeUnit.MINUTES.toMillis(minutes);
    }

    private void updateSelectedTimeDisplay() {
        long months = TimeUnit.MILLISECONDS.toDays(selectedDuration) / 30;
        long days = TimeUnit.MILLISECONDS.toDays(selectedDuration) % 30;
        long hours = TimeUnit.MILLISECONDS.toHours(selectedDuration) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(selectedDuration) % 60;

        StringBuilder timeText = new StringBuilder("Selected Duration: ");
        if (months > 0) timeText.append(months).append("mo ");
        if (days > 0) timeText.append(days).append("d ");
        if (hours > 0) timeText.append(hours).append("h ");
        if (minutes > 0 || (months == 0 && days == 0 && hours == 0)) {
            timeText.append(minutes).append("m");
        }

        selectedTimeTextView.setTextColor(getResources().getColor(R.color.white));
        selectedTimeTextView.setText(timeText.toString().trim());
    }
}
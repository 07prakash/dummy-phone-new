package com.example.dummyphoneprakash;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CustomTimePickerFragment extends DialogFragment {

    public interface TimePickerListener {
        void onTimeSet(long durationInMillis);
    }

    private TimePickerListener listener;
    private TextView selectedTimeTextView;
    private long selectedDuration = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (TimePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TimePickerListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_time_picker, null);

        NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
        NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
        NumberPicker hourPicker = dialogView.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);
        selectedTimeTextView = dialogView.findViewById(R.id.countdownTextView);

        // Set up pickers
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(12);
        monthPicker.setFormatter(value -> value + " mo");

        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(31);
        dayPicker.setFormatter(value -> value + " d");

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setFormatter(value -> value + " h");

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setFormatter(value -> value + " m");

        // Update selected time display when any picker changes
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

        builder.setView(dialogView)
                .setTitle("Select Duration")
                .setPositiveButton("Set", (dialog, id) -> {
                    if (listener != null) {
                        listener.onTimeSet(selectedDuration);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    getDialog().cancel();
                });

        return builder.create();
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

        StringBuilder timeText = new StringBuilder("Selected: ");
        if (months > 0) timeText.append(months).append("mo ");
        if (days > 0) timeText.append(days).append("d ");
        if (hours > 0) timeText.append(hours).append("h ");
        if (minutes > 0 || (months == 0 && days == 0 && hours == 0)) {
            timeText.append(minutes).append("m");
        }

        selectedTimeTextView.setText(timeText.toString().trim());
    }
}
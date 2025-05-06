package com.example.dummyphoneprakash;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class LockSettingActivity extends AppCompatActivity {
    private TextView timerDisplay;
    private int selectedMinutes = 1; // Default 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setting);

        timerDisplay = findViewById(R.id.timerDisplay);
        Button chooseAppsBtn = findViewById(R.id.chooseAppsBtn);
        Button timePickerBtn = findViewById(R.id.timePickerBtn);
        Button lockBtn = findViewById(R.id.lockBtn);

        updateTimerDisplay();

        chooseAppsBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LockActivity.class));
        });

        timePickerBtn.setOnClickListener(v -> showTimePickerDialog());

        lockBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, UnlockActivity.class);
            intent.putExtra("LOCK_DURATION", selectedMinutes);
            startActivity(intent);
            finish();
        });
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedMinutes = hourOfDay * 60 + minute;
                        if (selectedMinutes == 0) selectedMinutes = 1; // Minimum 1 minute
                        updateTimerDisplay();
                    }
                },
                0, // Initial hour
                1, // Initial minute
                true // 24-hour format
        );
        timePickerDialog.setTitle("Select Lock Duration");
        timePickerDialog.show();
    }

    private void updateTimerDisplay() {
        int hours = selectedMinutes / 60;
        int minutes = selectedMinutes % 60;

        String timeText;
        if (hours > 0) {
            timeText = String.format(Locale.getDefault(), "%dh %02dm", hours, minutes);
        } else {
            timeText = String.format(Locale.getDefault(), "%d minutes", minutes);
        }
        timerDisplay.setText(timeText);
    }
}
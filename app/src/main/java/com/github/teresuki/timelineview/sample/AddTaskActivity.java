package com.github.teresuki.timelineview.sample;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    Button taskTime;
    TextView taskTimeString;
    EditText taskDescription;
    Button addTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskTime = findViewById(R.id.buttonSetTime);
        taskTimeString = findViewById(R.id.textViewTaskTime);
        taskDescription = findViewById(R.id.plainTextTaskDescription);
        addTask = findViewById(R.id.buttonAddTask);

        //Set Time
        taskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String passingTaskDescription = taskDescription.getText().toString();
                String passingTaskTime = taskTimeString.getText().toString();

                Intent intentAddTask = new Intent(AddTaskActivity.this, MainActivity.class);
                intentAddTask.putExtra("taskDes", passingTaskDescription);
                intentAddTask.putExtra("taskTime", passingTaskTime);

                setResult(RESULT_OK, intentAddTask);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent indentCancelledTask = new Intent(AddTaskActivity.this, MainActivity.class);
        setResult(RESULT_CANCELED, indentCancelledTask);
        finish();
    }


    private void pickTime() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        calendar.set(0, 0, 0, hourOfDay, minute);
                        taskTimeString.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }
}




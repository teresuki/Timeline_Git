package com.github.vipulasri.timelineview.sample;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity {

    Button taskTimeEdit;
    TextView taskTimeStringEdit;
    EditText taskDescriptionEdit;
    Button editTask;
    Button deleteTask;

    Date currentTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        taskTimeEdit = findViewById(R.id.buttonSetTimeEdit);
        taskTimeStringEdit = findViewById(R.id.textViewTaskTimeEdit);
        taskDescriptionEdit = findViewById(R.id.plainTextTaskDescriptionEdit);
        editTask = findViewById(R.id.buttonEditTask);
        deleteTask = findViewById(R.id.buttonDeleteTask);

        //Get Info From Current Task (Date, Time and Position)

        Intent intentGetTask = getIntent();
        String getTime = intentGetTask.getStringExtra("curTaskTime");



        String getDescription = intentGetTask.getStringExtra("curTaskDes");
        int getPosition = intentGetTask.getIntExtra("curTaskPos", 0);

        //Display the current Time and Description on the TextView
        taskTimeStringEdit.setText(getTime);
        taskDescriptionEdit.setText(getDescription);
        //

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        Date date = null;
        try {
            if(!getTime.equals("Your Time Here"))
            {
                date = dateFormat.parse(getTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        currentTime = date;

        //Get Info From Current Task (Date and Time)

        taskTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });


        final String passingTaskDescription = taskDescriptionEdit.getText().toString();
        final String passingTaskTime = taskTimeStringEdit.getText().toString();
        final int passingTaskPosition = getPosition;

        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String passingTaskDescription = taskDescriptionEdit.getText().toString();
                String passingTaskTime = taskTimeStringEdit.getText().toString();


                Intent intentEditTask = new Intent(EditTaskActivity.this, MainActivity.class);
                intentEditTask.putExtra("taskDesEdit", passingTaskDescription);
                intentEditTask.putExtra("taskTimeEdit", passingTaskTime);
                intentEditTask.putExtra("taskPosEdit", passingTaskPosition);

                //AddTaskActivity.this.startActivity(indentAddTask);
                setResult(RESULT_OK, intentEditTask);
                finish();

            }
        });

        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                Intent intentDeleteTask = new Intent(EditTaskActivity.this, MainActivity.class);
                intentDeleteTask.putExtra("taskPosEdit", passingTaskPosition);
                setResult(RESULT_CANCELED, intentDeleteTask);
                finish();

            }
        });


    }


    private void pickTime() {
        final Calendar calendar = Calendar.getInstance();
        int hour;
        int minute;

        if(currentTime != null)
        {
            hour = currentTime.getHours();
            minute = currentTime.getMinutes();
        }
        else // If the current Task Time is null, set the clock to the current system time
        {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        calendar.set(0, 0, 0, hourOfDay, minute);
                        taskTimeStringEdit.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, hour, minute, true);

        timePickerDialog.show();

    }


}

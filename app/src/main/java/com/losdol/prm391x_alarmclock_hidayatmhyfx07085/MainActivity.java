package com.losdol.prm391x_alarmclock_hidayatmhyfx07085;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //Setting up custom toolbar
    Toolbar toolbar;
    ImageView addIcon;

    //Init for the timepicker
    int mHour, mMinute;

    //init for sqlite
//    DatabaseHelper mDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        addIcon = (ImageView) findViewById(R.id.add_button);

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                mHour = i;
                                mMinute = i1;
                                Calendar cal = Calendar.getInstance();
                                cal.set(0,0,0, mHour, mMinute);
                                long millis = cal.getTimeInMillis();

                                Log.d("timeset", "Jam" + DateFormat.format("hh:mm aa", cal));
                            }
                        }, 12, 0, true
                );
                timePickerDialog.updateTime(mHour, mMinute);
                timePickerDialog.show();
            }
        });
        setSupportActionBar(toolbar);
    }

    public void setTimer(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date date = new Date();

        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_alarm.setTime(date);
        cal_now.setTime(date);

        cal_alarm.set(Calendar.HOUR_OF_DAY, mHour);
        cal_alarm.set(Calendar.MINUTE, mMinute);
        cal_alarm.set(Calendar.SECOND, 0);

        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE, 1);
        }
    }
}

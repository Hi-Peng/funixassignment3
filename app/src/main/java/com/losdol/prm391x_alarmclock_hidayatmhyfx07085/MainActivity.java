package com.losdol.prm391x_alarmclock_hidayatmhyfx07085;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Setting up custom toolbar
    Toolbar toolbar;
    ImageView addIcon, clearIcon;

    ListView listView;

    //Init for the timepicker
    int mHour, mMinute, state;

    //init for sqlite
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.alarmList);
        toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        addIcon = (ImageView) findViewById(R.id.add_button);
        clearIcon = (ImageView) findViewById(R.id.clear_button);


        mDatabaseHelper = new DatabaseHelper(this);
        ArrayList millisdb = mDatabaseHelper.getAlarm();
        ArrayList idDb = mDatabaseHelper.getid();
        final MainActivity.listAdapter adapter = new MainActivity.listAdapter(getApplicationContext(), millisdb);
        listView.setAdapter(adapter);

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Fragment dialog thath containt timepicker
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

                                boolean isInserted = mDatabaseHelper.addData(millis, state);

                                if(isInserted == true)
                                    Toast.makeText(getApplicationContext(),"Data Inserted",Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(MainActivity.this,"Data not Inserted",Toast.LENGTH_LONG).show();

                                adapter.clear();
                                adapter.addAll(mDatabaseHelper.getAlarm());
                                adapter.notifyDataSetChanged();
                                listView.invalidateViews();
                                listView.refreshDrawableState();
                                Log.d("timeset", "Jam" + millis);
                            }
                        }, 12, 0, true

                );
                timePickerDialog.updateTime(mHour, mMinute);
                timePickerDialog.show();
            }
        });

        clearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteAll();
                adapter.clear();
                adapter.addAll(mDatabaseHelper.getAlarm());
                adapter.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
            }
        });

    }

    public void setAlarm(long millis, int reqId){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date date = new Date();


        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_alarm.setTime(date);
        cal_now.setTime(date);
        int millisintegre = (int) millis;
        cal_alarm.set(Calendar.MILLISECOND, millisintegre);//convert to int
//        cal_alarm.set(Calendar.MINUTE, min);
//        cal_alarm.set(Calendar.SECOND, 0);

        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE, 1);
        }

        Intent i = new Intent(MainActivity.this, broadcastReceiverApp.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, reqId, i, 0);
        alarmManager.set(AlarmManager.RTC, cal_alarm.getTimeInMillis(), pendingIntent); //I prefer to use RTC instead RTC_WAKEUP
    }

    public void unsetAlarm(int reqId){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(MainActivity.this, broadcastReceiverApp.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, reqId, i, 0);
        alarmManager.cancel(pendingIntent);
    }

    class listAdapter extends ArrayAdapter<Long> {
        Context context;
        ArrayList rTime;


        listAdapter(Context c, ArrayList time){
            super(c, R.layout.custom_list, R.id.alarm_time, time);
            this.context = c;
            this.rTime = time;
        }

        //Here's where I make the custom view for the list view
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customRow =  layoutInflater.inflate(R.layout.custom_list, parent, false);

            TextView iTitle = customRow.findViewById(R.id.alarm_time);
            Switch onOff = customRow.findViewById(R.id.alarm_switch);

            onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        setAlarm(Long.valueOf(String.valueOf(rTime.get(position))), position);
                        Toast.makeText(getApplicationContext(),"Alarm " + position+1 + " On",Toast.LENGTH_LONG).show(); //I think it's very crude
                    }
                    else
                        unsetAlarm(position);
                        Toast.makeText(getApplicationContext(),"Alarm " + position+1 + " Off",Toast.LENGTH_LONG).show();
                }
            });
            //millis to date conversion
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Calendar calendar = Calendar.getInstance();
            long millisRaw = Long.valueOf(String.valueOf(rTime.get(position)));
            calendar.setTimeInMillis(millisRaw);

//            Actually it will call the id and replace with Id in the db, but it was error
            iTitle.setText(format.format(calendar.getTime()));

            return customRow;
        }
    }
}



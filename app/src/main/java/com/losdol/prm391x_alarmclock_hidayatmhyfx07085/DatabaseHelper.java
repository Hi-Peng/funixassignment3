package com.losdol.prm391x_alarmclock_hidayatmhyfx07085;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Initializing the database name, table, and field
    public static final String DATABASE_NAME = "alarmlist.db";
    public static final String TABLE_NAME = "alarm_data";
    public static final String COL1 = "ID";
    public static final String COL2 = "ALARM";  //Storing the millis of timepicker
    public static final String COL3 = "STATE";  //Storing on/off state of the alarm

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ALARM INTEGER, STATE INTEGER)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(long millis, int state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, millis);
        contentValues.put(COL3, state);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        return result != -1;
    }

    //This method use for getting database content to listview on main screen
    public ArrayList getAlarm() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Long> array_list = new ArrayList<Long>();
        Cursor res = db.rawQuery( "SELECT * FROM "+ TABLE_NAME, null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            array_list.add(res.getLong((res.getColumnIndex("ALARM"))));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList getid() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Long> array_list = new ArrayList<Long>();
        Cursor res = db.rawQuery( "SELECT * FROM "+ TABLE_NAME, null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            array_list.add(res.getLong((res.getColumnIndex("ID"))));
            res.moveToNext();
        }
        return array_list;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }
}

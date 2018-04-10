package com.haibeey.android.medmanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.YuvImage;
import android.util.Log;

import com.haibeey.android.medmanager.model.DbContract.MedicalEntry;

import java.util.ArrayList;

/**
 * Created by haibeey on 3/28/2018.
 */

public class DbHandleHelper extends SQLiteOpenHelper {
    private static final  String DATABASE_NAME="medical_manager";
    private static int DATABASE_VERSION=1;

    public DbHandleHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WEATHER_TABLE ="CREATE TABLE " +MedicalEntry.TABLE_NAME+"("+
                MedicalEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MedicalEntry.COLUMN_START_YEAR       + " INTEGER, "                 +
                MedicalEntry.COLUMN_START_MONTH       + " INTEGER, "                 +
                MedicalEntry.COLUMN_START_DAY       + " INTEGER, "                 +
                MedicalEntry.COLUMN_START_HOUR       + " INTEGER, "                 +
                MedicalEntry.COLUMN_START_MINUTE      + " INTEGER, "                 +
                MedicalEntry.COLUMN_END_YEAR       + " INTEGER, "                 +
                MedicalEntry.COLUMN_END_MONTH       + " INTEGER, "                 +
                MedicalEntry.COLUMN_END_DAY       + " INTEGER, "                 +
                MedicalEntry.COLUMN_END_HOUR       + " INTEGER, "                 +
                MedicalEntry.COLUMN_END_MINUTE       + " INTEGER, "                 +
                MedicalEntry.COLUMN_INTERVAL       + " INTEGER, "                 +
                MedicalEntry.COLUMN_DESCRIPTION       + " STRING, "                 +
                MedicalEntry.COLUMN_NAME       + " STRING "                 + ")";

        //creates the table
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertEntry(int startYear,int startMonth,int startDay,int startHour,int startMinute,
                            int endYear,int endMonth,int endDay,int endHour,int endMinute,String name,
                            String description,int interval){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(MedicalEntry.COLUMN_INTERVAL,interval);

        values.put(MedicalEntry.COLUMN_START_YEAR, startYear);
        values.put(MedicalEntry.COLUMN_START_MONTH,startMonth);
        values.put(MedicalEntry.COLUMN_START_DAY,startDay);
        values.put(MedicalEntry.COLUMN_START_HOUR,startHour);
        values.put(MedicalEntry.COLUMN_START_MINUTE,startMinute);

        values.put(MedicalEntry.COLUMN_END_YEAR, endYear);
        values.put(MedicalEntry.COLUMN_END_MONTH,endMonth);
        values.put(MedicalEntry.COLUMN_END_DAY,endDay);
        values.put(MedicalEntry.COLUMN_END_HOUR,endHour);
        values.put(MedicalEntry.COLUMN_START_MINUTE,endMinute);

        values.put(MedicalEntry.COLUMN_NAME,name);
        values.put(MedicalEntry.COLUMN_DESCRIPTION,description);

        db.insert(MedicalEntry.TABLE_NAME,null,values);
        Log.e("valuesinserted",values.toString());
    }

    public ArrayList<MedRecord>  queryEntryAll(){

        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+MedicalEntry.TABLE_NAME+" ORDER BY "
                +MedicalEntry.COLUMN_END_MONTH+" DESC",null);

        ArrayList<MedRecord> resultList=new ArrayList<>();
        cursor.moveToLast();

        while (!cursor.isBeforeFirst()){
            MedRecord medRecord=new MedRecord();

            medRecord.setSTART_YEAR(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_START_YEAR)));
            medRecord.setSTART_MONTH(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_START_MONTH)));
            medRecord.setSTART_DAY(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_START_DAY)));
            medRecord.setSTART_HOUR(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_START_HOUR)));

            medRecord.setEND_YEAR(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_END_YEAR)));
            medRecord.setEND_MONTH(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_END_MONTH)));
            medRecord.setEND_DAY(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_END_DAY)));
            medRecord.setEND_HOUR(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_END_HOUR)));

            medRecord.setID(cursor.getInt(cursor.getColumnIndex(MedicalEntry._ID)));

            medRecord.setNAME(cursor.getString(cursor.getColumnIndex(MedicalEntry.COLUMN_NAME)));
            medRecord.setDESCRIPTION(cursor.getString(cursor.getColumnIndex(MedicalEntry.COLUMN_DESCRIPTION)));

            resultList.add(medRecord);
            cursor.moveToPrevious();

        }

        return resultList;

    }

    public void updateEntry(int id,int startYear,int startMonth,int startDay,int startHour,int startMinute,
                            int endYear,int endMonth,int endDay,int endHour,int endMinute,String name,String description,int interval){

            SQLiteDatabase db=getWritableDatabase();
            ContentValues values=new ContentValues();

            values.put(MedicalEntry.COLUMN_INTERVAL,interval);

            values.put(MedicalEntry.COLUMN_START_YEAR, startYear);
            values.put(MedicalEntry.COLUMN_START_MONTH,startMonth);
            values.put(MedicalEntry.COLUMN_START_DAY,startDay);
            values.put(MedicalEntry.COLUMN_START_HOUR,startHour);
            values.put(MedicalEntry.COLUMN_START_MINUTE,startMinute);

            values.put(MedicalEntry.COLUMN_END_YEAR, endYear);
            values.put(MedicalEntry.COLUMN_END_MONTH,endMonth);
            values.put(MedicalEntry.COLUMN_END_DAY,endDay);
            values.put(MedicalEntry.COLUMN_END_HOUR,endHour);
            values.put(MedicalEntry.COLUMN_START_MINUTE,endMinute);

            values.put(MedicalEntry.COLUMN_NAME,name);
            values.put(MedicalEntry.COLUMN_DESCRIPTION,description);

        db.update(MedicalEntry.TABLE_NAME,values,"id=?",new String[]{String.valueOf(id)});

    }

    public long countDb(String table_name) {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+table_name,null);
        long result=cursor.getCount();
        cursor.close();
        return result;
    }

    public void deleteFromTable(String tableName,String key,String name){
        SQLiteDatabase db=this.getReadableDatabase();
        db.delete(tableName, key + "=" + name, null);
    }

    public ArrayList<MedRecord> Search(String searchQuery){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+
                MedicalEntry.TABLE_NAME+ " WHERE "+
                MedicalEntry.COLUMN_NAME+" LIKE '%"+searchQuery+"%' OR "+
                MedicalEntry.COLUMN_DESCRIPTION+" LIKE '%"+searchQuery+"%';",null);

        ArrayList<MedRecord> resultList=new ArrayList<>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            MedRecord medRecord=new MedRecord();

            medRecord.setSTART_YEAR(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_START_YEAR)));
            medRecord.setSTART_MONTH(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_START_MONTH)));
            medRecord.setSTART_DAY(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_START_DAY)));
            medRecord.setSTART_HOUR(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_START_HOUR)));

            medRecord.setEND_YEAR(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_END_YEAR)));
            medRecord.setEND_MONTH(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_END_MONTH)));
            medRecord.setEND_DAY(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_END_DAY)));
            medRecord.setEND_HOUR(cursor.getInt(cursor.getColumnIndex(MedicalEntry.COLUMN_END_HOUR)));

            medRecord.setID(cursor.getInt(cursor.getColumnIndex(MedicalEntry._ID)));

            medRecord.setNAME(cursor.getString(cursor.getColumnIndex(MedicalEntry.COLUMN_NAME)));
            medRecord.setDESCRIPTION(cursor.getString(cursor.getColumnIndex(MedicalEntry.COLUMN_DESCRIPTION)));

            resultList.add(medRecord);
            cursor.moveToNext();
        }
        return resultList;
    }
}

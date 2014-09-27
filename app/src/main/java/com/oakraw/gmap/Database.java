package com.oakraw.gmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oakraw.gmap.model.Record;

import java.util.ArrayList;

/**
 * Created by Rawipol on 9/26/14 AD.
 */
public class Database extends SQLiteOpenHelper {

    public static final String TABLE_RECORDS = "Records";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ROUTE = "route";
    private static final String DATABASE_NAME = "records.db";
    private static final int DATABASE_VERSION = 1;
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_RECORDS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_NAME+ " text not null,"
            + COLUMN_DATE+ " text not null,"
            + COLUMN_ROUTE+ " text not null" +
            ");";
    private ArrayList<Record> records_list = new ArrayList<Record>();

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Add new record
    public int addRecord(String name, String date, String route){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,name);
        values.put(COLUMN_DATE,date);
        values.put(COLUMN_ROUTE,route);

        long insertID = db.insert(TABLE_RECORDS,null,values);
        db.close();

        return (int)insertID;
    }

    //get each record
    public Record getRecord(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECORDS, new String[] { COLUMN_ID,
                        COLUMN_NAME, COLUMN_DATE, COLUMN_ROUTE }, COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Record record = new Record(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        cursor.close();
        db.close();

        return record;
    }

    // Getting All Records
    public ArrayList<Record> getAllRecords() {
        try {
            records_list.clear();
            // Select All Query
            String selectQuery = "SELECT * FROM " + TABLE_RECORDS;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Record record = new Record(
                            Integer.parseInt(cursor.getString(0)),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
                    );
                    records_list.add(record);
                } while (cursor.moveToNext());
            }
            // return contact list
            cursor.close();
            db.close();
            return records_list;
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("all_records", "" + e);
        }
        return records_list;
    }

    public void deleteRecord(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RECORDS, COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
}

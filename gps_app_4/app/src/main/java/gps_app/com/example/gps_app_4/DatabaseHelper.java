package gps_app.com.example.gps_app_4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lenovo on 2016-11-07.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Coordinate.db";
    public static final String TABLE_COORDINATE = "coordinate_table";
    public static final String COL_ID = "ID";
    public static final String COL_LATITIUDE = "LONGITIUDE";
    public static final String COL_LONGITIUDE = "LATITIUDE";
    public static final String COL_DATE = "INSERT_DATE";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_COORDINATE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, LATITIUDE TEXT, LONGITIUDE TEXT,INSERT_DATE DATE)");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDINATE);

    }

    public boolean insertData(double latitiude, double longitiude, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LATITIUDE, String.valueOf(latitiude));
        contentValues.put(COL_LONGITIUDE, String.valueOf(longitiude));
        contentValues.put(COL_DATE, date);
        long result = db.insert(TABLE_COORDINATE, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }



    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_COORDINATE, null);
        return res;
    }

    public Cursor getData(String date1)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] argTable = new String[] { date1};
        Cursor res = db.rawQuery("select LONGITIUDE, LATITIUDE, INSERT_DATE from " + TABLE_COORDINATE + " WHERE INSERT_DATE = ?", argTable);
        return res;
    }


    public boolean updateData(String id, String latitiude, String longitiude, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_LATITIUDE, latitiude);
        contentValues.put(COL_LONGITIUDE, longitiude);
        contentValues.put(COL_DATE, date);
        db.update(TABLE_COORDINATE, contentValues, "ID = ?", new String[]{id});
        return true;
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_COORDINATE, "ID = ?", new String[]{id});
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("delete * from " + TABLE_COORDINATE, null);
    }


}

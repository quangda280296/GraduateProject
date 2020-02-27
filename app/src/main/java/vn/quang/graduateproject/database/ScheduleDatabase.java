package vn.quang.graduateproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by keban on 3/6/2018.
 */

public class ScheduleDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "schedule.db";
    private static final int SCHEMA_VERSION = 1;

    public ScheduleDatabase(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE schedule (_id INTEGER PRIMARY KEY autoincrement, name TEXT, latitude DOUBLE, longitude DOUBLE, startDate TEXT)");
        db.execSQL("CREATE TABLE schedule_latlng (_id INTEGER, latitude DOUBLE, longitude DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getAll() {
        return (getReadableDatabase().rawQuery("SELECT * FROM schedule", null));
    }

    public Cursor getAllPOI(int id) {
        return (getReadableDatabase().rawQuery("SELECT * FROM schedule_latlng WHERE _id = " + id, null));
    }

    public void delete(String id) {
        String[] whereArgs = {id};
        getWritableDatabase().delete("schedule", "_id = ?", whereArgs);
        getWritableDatabase().delete("schedule_latlng", "_id = ?", whereArgs);
    }

    public void update(String id, String name, String startDate, LatLng startLocation, List<LatLng> list) {
        ContentValues cv0 = new ContentValues();
        cv0.put("name", name);
        cv0.put("latitude", startLocation.latitude);
        cv0.put("longitude", startLocation.longitude);
        cv0.put("startDate", startDate);

        String[] whereArgs = {id};
        getWritableDatabase().update("schedule", cv0, "_id = ?", whereArgs);
        getWritableDatabase().delete("schedule_latlng", "_id = ?", whereArgs);

        ContentValues cv;
        for (LatLng l : list) {
            cv = new ContentValues();
            cv.put("_id", id);
            cv.put("latitude", l.latitude);
            cv.put("longitude", l.longitude);
            getWritableDatabase().insert("schedule_latlng", null, cv);
        }
    }

    public void insert(String name, String startDate, LatLng startLocation, List<LatLng> list) {
        ContentValues cv0 = new ContentValues();
        cv0.put("name", name);
        cv0.put("latitude", startLocation.latitude);
        cv0.put("longitude", startLocation.longitude);
        cv0.put("startDate", startDate);
        getWritableDatabase().insert("schedule", null, cv0);

        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM schedule", null);
        c.moveToLast();
        int id = c.getInt(0);

        ContentValues cv;
        for (LatLng l : list) {
            cv = new ContentValues();
            cv.put("_id", id);
            cv.put("latitude", l.latitude);
            cv.put("longitude", l.longitude);
            getWritableDatabase().insert("schedule_latlng", null, cv);
        }
    }
}

package vn.quang.graduateproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by keban on 3/6/2018.
 */

public class CameraDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "camera.db";
    private static final int SCHEMA_VERSION = 1;

    public CameraDatabase(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE camera (_id INTEGER PRIMARY KEY autoincrement, name TEXT, date TEXT, uri TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getAll() {
        return (getReadableDatabase().rawQuery("SELECT * FROM camera", null));
    }

    public void delete(String id) {
        String[] whereArgs = {id};
        getWritableDatabase().delete("camera", "_id = ?", whereArgs);
    }

    public void update(String id, String name) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);

        String[] whereArgs = {id};
        getWritableDatabase().update("camera", cv, "_id = ?", whereArgs);
    }

    public void insert(String name, String date, String uri) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("date", date);
        cv.put("uri", uri);
        getWritableDatabase().insert("camera", null, cv);
    }
}

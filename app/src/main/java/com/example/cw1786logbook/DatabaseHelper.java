package com.example.cw1786logbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UrlDb";
    private static final String TABLE_URL = "PictureURL";

    private static final String TABLE_URI = "LocalPicUri";

    public static final String URL_ID = "url_id";
    public static final String URL = "url";

    public static final String URI_ID = "uri_id";
    public static final String URI = "uri";

    private SQLiteDatabase database;

    private static final String TABLE_URL_CREATE = String.format(
      "CREATE TABLE %s (" +
      "   %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "   %s TEXT )",
            TABLE_URL, URL_ID, URL);

    private static final String TABLE_URI_CREATE = String.format(
            "CREATE TABLE %s (" +
                    "   %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "   %s TEXT )",
            TABLE_URI, URI_ID, URI);

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(TABLE_URL_CREATE);
        db.execSQL(TABLE_URI_CREATE);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_URL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_URI);

        Log.v(this.getClass().getName(), TABLE_URL + " database upgrade to version " +
                newVersion + " - old data lost");
        onCreate(db);
    }

    public long insertUrl(String url) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(URL, url);

        return database.insertOrThrow(TABLE_URL, null, rowValues);
    }

    public long insertUri(String uri) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(URI, uri);

        return database.insertOrThrow(TABLE_URI, null, rowValues);
    }

    public ArrayList<String> getUrls() {
        Cursor cursor = database.query(TABLE_URL, new String[] { URL_ID, URL },
                null, null, null, null, URL_ID + " DESC");

        ArrayList<String> results = new ArrayList<String>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String url = cursor.getString(1);

            results.add(url);

            cursor.moveToNext();
        }

        return results;
    }

    public ArrayList<String> getUris() {
        Cursor cursor = database.query(TABLE_URI, new String[] { URI_ID, URI },
                null, null, null, null, URI_ID + " DESC");

        ArrayList<String> results = new ArrayList<String>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String uri = cursor.getString(1);

            results.add(uri);

            cursor.moveToNext();
        }

        return results;
    }
}

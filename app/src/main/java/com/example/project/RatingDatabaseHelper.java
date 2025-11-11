package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RatingDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ratings.db";
    private static final int DB_VERSION = 8;

    public RatingDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ratings (lectureNumber INTEGER PRIMARY KEY, totalRating REAL, ratingCount INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ratings");
        onCreate(db);
    }

    public void saveRating(int lectureNumber, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT totalRating, ratingCount FROM ratings WHERE lectureNumber=?", new String[]{String.valueOf(lectureNumber)});

        if (cursor.moveToFirst()) {
            float total = cursor.getFloat(0) + rating;
            int count = cursor.getInt(1) + 1;
            ContentValues values = new ContentValues();
            values.put("totalRating", total);
            values.put("ratingCount", count);
            db.update("ratings", values, "lectureNumber=?", new String[]{String.valueOf(lectureNumber)});
        } else {
            ContentValues values = new ContentValues();
            values.put("lectureNumber", lectureNumber);
            values.put("totalRating", rating);
            values.put("ratingCount", 1);
            db.insert("ratings", null, values);
        }
        cursor.close();
    }

    public float getAverageRating(int lectureNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT totalRating, ratingCount FROM ratings WHERE lectureNumber=?", new String[]{String.valueOf(lectureNumber)});
        float avg = 0;
        if (cursor.moveToFirst()) {
            float total = cursor.getFloat(0);
            int count = cursor.getInt(1);
            if (count > 0) avg = total / count;
        }
        cursor.close();
        return avg;
    }

    public int getRatingCount(int lectureNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ratingCount FROM ratings WHERE lectureNumber=?", new String[]{String.valueOf(lectureNumber)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}

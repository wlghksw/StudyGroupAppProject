package com.example.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "users.db";
    private static final int DB_VERSION = 8;

    public UserDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //데이터베이스 생성: users 테이블과 UserCourses 테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT, password TEXT, birth TEXT, gender TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS UserCourses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT, " +
                "courseName TEXT)");
    }

    //데이터베이스 업그레이드: 테이블 삭제 후 재생성
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS UserCourses");
        onCreate(db);
    }

    //사용자 추가
    public boolean insertUser(String id, String name, String password, String birth, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("password", password);
        values.put("birth", birth);
        values.put("gender", gender);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    //로그인 시 사용자 로그인 체크
    public boolean checkUser(String id, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ? AND password = ?", new String[]{id, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //회원가입 시 중복 아이디 확인
    public boolean isUserIdExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE id = ?", new String[]{id});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 수강 신청
    public boolean registerCourse(String userId, String courseName) {
        if (isCourseRegistered(userId, courseName)) {
            return false;  // 이미 신청한 경우
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("courseName", courseName);
        db.insert("UserCourses", null, values);
        return true;
    }

    //수강 신청 여부 확인
    public boolean isCourseRegistered(String userId, String courseName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM UserCourses WHERE userId = ? AND courseName = ?",
                new String[]{userId, courseName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<String> getUserCourses(String userId) {
        List<String> courses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT courseName FROM UserCourses WHERE userId = ?", new String[]{userId});
        while (cursor.moveToNext()) {
            courses.add(cursor.getString(0));
        }
        cursor.close();
        return courses;
    }

    public List<String> getRegisteredCourses(String userId) {
        List<String> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT courseName FROM UserCourses WHERE userId = ?", new String[]{userId});
            while (cursor.moveToNext()) {
                courseList.add(cursor.getString(0));
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return courseList;
    }
}

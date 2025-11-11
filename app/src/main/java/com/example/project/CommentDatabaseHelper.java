package com.example.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class CommentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "comments.db";
    private static final int DB_VERSION = 8;

    public CommentDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE comments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "post_id INTEGER, " +
                "content TEXT, " +
                "date TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS comments");
        onCreate(db);
    }

    public boolean insertComment(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("post_id", comment.getPostId());
        values.put("content", comment.getContent());
        values.put("date", comment.getDate());

        long result = db.insert("comments", null, values);
        return result != -1;
    }

    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> commentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM comments WHERE post_id=? ORDER BY id DESC",
                new String[]{String.valueOf(postId)});

        while (cursor.moveToNext()) {
            commentList.add(new Comment(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3)
            ));
        }

        cursor.close();
        return commentList;
    }
}

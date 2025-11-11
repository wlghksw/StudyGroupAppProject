package com.example.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class PostDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "community.db";
    private static final int DB_VERSION = 8;

    public PostDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE posts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "content TEXT, " +
                "category TEXT, " +
                "date TEXT, " +
                "views INTEGER, " +
                "likes INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS posts");
        onCreate(db);
    }

    public boolean insertPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", post.getTitle());
        values.put("content", post.getContent());
        values.put("category", post.getCategory());
        values.put("date", post.getDate());
        values.put("views", post.getViews());
        values.put("likes", post.getLikes());
        long result = db.insert("posts", null, values);
        return result != -1;
    }

    public List<Post> getPostsByCategory(String category) {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM posts WHERE category=? ORDER BY date DESC", new String[]{category});

        while (cursor.moveToNext()) {
            Post post = new Post(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4),
                    cursor.getInt(5), cursor.getInt(6));
            postList.add(post);
        }

        cursor.close();
        return postList;
    }

    public void increaseViews(int postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE posts SET views = views + 1 WHERE id = ?", new Object[]{postId});
    }

    public Post getPostById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM posts WHERE id=?", new String[]{String.valueOf(id)});
        Post post = null;
        if (cursor.moveToFirst()) {
            post = new Post(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4),
                    cursor.getInt(5), cursor.getInt(6));
        }
        cursor.close();
        return post;
    }

    public List<Post> getTopViewedPosts(int limit) {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY views DESC LIMIT ?", new String[]{String.valueOf(limit)});

        while (cursor.moveToNext()) {
            Post post = new Post(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4),
                    cursor.getInt(5), cursor.getInt(6));
            postList.add(post);
        }

        cursor.close();
        return postList;
    }
    // 모든 게시글 조회수 기준 정렬해서 가져오기
    public List<Post> getAllPostsByViews() {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM posts ORDER BY views DESC", null);

        while (cursor.moveToNext()) {
            Post post = new Post(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4),
                    cursor.getInt(5), cursor.getInt(6));
            postList.add(post);
        }

        cursor.close();
        return postList;
    }

}
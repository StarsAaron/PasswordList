package com.aaron.passwordlist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stars on 2015/7/26.
 */
public class UserDBOpenHelper extends SQLiteOpenHelper {
    public UserDBOpenHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int dbVersion) {
        super(context, dbName, factory, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户列表
        db.execSQL("create table user(" +
                "userId integer primary key autoincrement," +
                "userName varchar(10)," +
                "userPassword varchar(20)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

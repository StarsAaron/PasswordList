package com.aaron.passwordlist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stars on 2015/7/26.
 */
public class PwdDBOpenHelper extends SQLiteOpenHelper {
    public PwdDBOpenHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int dbVersion) {
        super(context, dbName, factory, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA FOREIGN_KEYS=ON;");
        // 创建密码数据表
        db.execSQL("create table password(" +
                "pwdId integer primary key autoincrement," +
                "pwdkeyword varchar(20)," +
                "pwdAccount varchar(10)," +
                "pwdTip varchar(100)," +
                "userKey integer," +
                "foreign key(userKey) references user(userId))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

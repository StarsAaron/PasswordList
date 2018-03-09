package com.aaron.passwordlist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stars on 2015/7/26.
 */
public class PwdDBOpenHelper extends SQLiteOpenHelper {
    public PwdDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table password(_id integer primary key autoincrement,keyword varchar(20),account varchar(100),nicname varchar(100),password varchar(100),tip varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

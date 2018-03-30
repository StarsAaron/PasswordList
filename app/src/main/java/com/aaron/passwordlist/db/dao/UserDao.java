package com.aaron.passwordlist.db.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;

import com.aaron.passwordlist.MyConstant;
import com.aaron.passwordlist.bean.UserBean;
import com.aaron.passwordlist.db.UserDBOpenHelper;
import com.aaron.passwordlist.util.Md5Util;


/**
 * Created by stars on 2015/7/26.
 */
public class UserDao {
    private UserDBOpenHelper userDBOpenHelper = null;

    public UserDao(Context context) {
        userDBOpenHelper = new UserDBOpenHelper(context, MyConstant.US_DBName, null, 1);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public long insertUserMessageToDb(@NonNull UserBean userBean) {
        long rowsAffectedResult;
        try (SQLiteDatabase db = userDBOpenHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("userName", userBean.userName);
            values.put("userPassword", Md5Util.md5Arithmetic(Md5Util.md5Arithmetic(userBean.userPassword)));
            rowsAffectedResult = db.insert("user", null, values);
        }
        return rowsAffectedResult;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean checkUserMessage(@NonNull UserBean userBean) {
        try (SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
             Cursor cursor = db.query("user", null, "userName=? and userPassword=?"
                     , new String[]{userBean.userName, Md5Util.md5Arithmetic(Md5Util
                             .md5Arithmetic(userBean.userPassword))}, null, null
                     , null)
        ){
            return cursor.moveToNext();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean anyoneExist() {
        try(SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
            Cursor cursor = db.query("user", null, null, null
                    , null, null, null)){
            return cursor.moveToNext();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPassword() {
        try(SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
            Cursor cursor = db.query("user", new String[]{"userPassword"}, null
                    , null, null, null, null)
        ){
            if (cursor.moveToNext()) {
                String password = cursor.getString(0);
                return password;
            } else {
                return "";
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public int deleteAll() {
        try(SQLiteDatabase db = userDBOpenHelper.getWritableDatabase()){
            return db.delete("user", "1", null);
        }
    }
}

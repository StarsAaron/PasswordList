package com.aaron.passwordlist.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aaron.passwordlist.MyConstant;
import com.aaron.passwordlist.bean.UserBean;
import com.aaron.passwordlist.db.UserDBOpenHelper;
import com.aaron.passwordlist.util.Md5Util;


/**
 * Created by stars on 2015/7/26.
 */
public class UserDao  {
    private Context context;
    private UserDBOpenHelper userDBOpenHelper = null;

    public UserDao(Context context) {
        this.context = context;
        userDBOpenHelper = new UserDBOpenHelper(context, MyConstant.US_DBName,null,1);
    }

    public int addUser(UserBean userBean){
        SQLiteDatabase db = userDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", Md5Util.md5Arithmetic(Md5Util.md5Arithmetic(userBean.password)));
        values.put("email", userBean.email);
        long i = db.insert("user", null, values);
        db.close();
        if(i!=-1){
            return 1;
        }else{
            return 0;
        }
    }

    public boolean checkUser(Context context,String password){
        SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", null, "password=?", new String[]{password}, null, null, null);
        if(cursor.moveToNext()){
            cursor.close();
            db.close();
            return true;
        }else{
            cursor.close();
            db.close();
            return false;
        }
    }

    public boolean anyoneExit(){
        SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        if(cursor.moveToNext()){
            cursor.close();
            db.close();
            return true;
        }else{
            cursor.close();
            db.close();
            return false;
        }
    }

    public String getPassword(){
        SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", new String[]{"password"}, null, null, null, null, null);
        if(cursor.moveToNext()){
            String password = cursor.getString(0);
            cursor.close();
            db.close();
            return password;
        }else{
            cursor.close();
            db.close();
            return null;
        }
    }

    /**
     * 删除所有记录
     */
    public boolean delete(){
        SQLiteDatabase db = userDBOpenHelper.getWritableDatabase();
        if(db.delete("user", "1", null)!=-1){
            db.close();
            return true;
        }else{
            db.close();
            return false;
        }

    }

    /**
     * 获取指定邮箱
     * @return
     */
    public String getEmail(){
        SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", new String[]{"email"}, null, null, null, null, null);
        if(cursor.moveToNext()){
            String email =  cursor.getString(0);
            cursor.close();
            db.close();
            return email;
        }else{
            cursor.close();
            db.close();
            return null;
        }
    }
}

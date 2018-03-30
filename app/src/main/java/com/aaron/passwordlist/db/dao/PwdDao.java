package com.aaron.passwordlist.db.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.aaron.passwordlist.MyConstant;
import com.aaron.passwordlist.Myapplication;
import com.aaron.passwordlist.bean.PasswordBean;
import com.aaron.passwordlist.db.PwdDBOpenHelper;
import com.aaron.passwordlist.util.SymEncrypt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stars on 2015/7/26.
 */
public class PwdDao {
    private PwdDBOpenHelper pwdDBOpenHelper = null;

    public PwdDao(Context context) {
        pwdDBOpenHelper = new PwdDBOpenHelper(context, MyConstant.PW_DBName, null, 1);
    }

    private String encryptData(String encryptDataStr) {
        return SymEncrypt.encrypt(encryptDataStr, Myapplication.getPassWord(), Myapplication.getDecryptType());
    }

    private String decryptData(String decryptDataStr) {
        return SymEncrypt.decrypt(decryptDataStr, Myapplication.getPassWord(), Myapplication.getDecryptType());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public long addPasswordMessage(@NonNull PasswordBean passwordBean) {
        long rowsAffectedResult;
        try(SQLiteDatabase db = pwdDBOpenHelper.getWritableDatabase()){
            ContentValues values = new ContentValues();
            values.put("pwdkeyword", passwordBean.pwdkeyword);
            values.put("pwdAccount", passwordBean.pwdAccount);
            values.put("pwdtip", encryptData(passwordBean.pwdTip));
            values.put("userKey", passwordBean.userKey);
            rowsAffectedResult = db.insert("password", null, values);
        }
        return rowsAffectedResult;
    }

    public List<PasswordBean> getPasswordMessage(@NonNull String userId) {
        return getPasswordMessage(userId, null);
    }

    public List<PasswordBean> getPasswordMessage(@NonNull String userId, String keyWordParam) {
        List<PasswordBean> passwordBeans = new ArrayList<>();
        SQLiteDatabase db = pwdDBOpenHelper.getReadableDatabase();
        Cursor cursor;
        if (TextUtils.isEmpty(keyWordParam)) {
            cursor = db.query("password", null, "userKey=?"
                    , new String[]{userId}, null, null, null);
        } else {
            cursor = db.query("password", null, "userKey=? and pwdkeyword LIKE ? ",
                    new String[]{userId, "%" + keyWordParam + "%"}, null, null, null);
        }
        while (cursor.moveToNext()) {
            PasswordBean passwordBean = new PasswordBean();
            passwordBean.pwdId = cursor.getInt(0);
            passwordBean.pwdkeyword = cursor.getString(1);
            passwordBean.pwdAccount = cursor.getString(2);
            passwordBean.pwdTip = decryptData(cursor.getString(3));
            passwordBean.userKey = Integer.valueOf(cursor.getString(4).trim());
            passwordBeans.add(passwordBean);
        }
        cursor.close();
        db.close();
        return passwordBeans;
    }

    /**
     * 删除记录
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public int deletePasswordMessageById(@NonNull int id) {
        int rowsAffectedResult;
        try(SQLiteDatabase db = pwdDBOpenHelper.getWritableDatabase()){
            rowsAffectedResult = db.delete("password", "pwdId=?", new String[]{String.valueOf(id)});
        }
        return rowsAffectedResult;
    }

    /**
     * 删除全部记录
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public int deleteAllAccount() {
        int rowsAffectedResult;
        try(SQLiteDatabase db = pwdDBOpenHelper.getWritableDatabase()){
            rowsAffectedResult = db.delete("password", "1", null);
        }
        return rowsAffectedResult;
    }

    /**
     * 更新
     *
     * @param passwordBean
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public int changePasswordMessage(@NonNull PasswordBean passwordBean) {
        int rowsAffectedResult;
        try(SQLiteDatabase db = pwdDBOpenHelper.getReadableDatabase()){
            ContentValues values = new ContentValues();
            values.put("pwdkeyword", passwordBean.pwdkeyword);
            values.put("pwdAccount",passwordBean.pwdAccount);
            values.put("pwdtip", encryptData(passwordBean.pwdTip));
            values.put("userKey", passwordBean.userKey);

            rowsAffectedResult = db.update("password", values, "pwdId=?"
                    , new String[]{String.valueOf(passwordBean.pwdId)});
        }
        return rowsAffectedResult;
    }
}

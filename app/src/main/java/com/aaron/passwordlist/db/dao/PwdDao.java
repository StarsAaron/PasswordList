package com.aaron.passwordlist.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.widget.Toast;

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
    private Context context;
    private PwdDBOpenHelper pwdDBOpenHelper = null;

    public PwdDao(Context context) {
        this.context = context;
        pwdDBOpenHelper = new PwdDBOpenHelper(context, MyConstant.PW_DBName,null,1);
    }

    //加密
    private String  encryptData(String str){
        return SymEncrypt.encrypt(str, Myapplication.getPassWord(), Myapplication.getDecryptType());
    }
    //解密
    private String  decryptData(String str){
        return SymEncrypt.decrypt(str, Myapplication.getPassWord(), Myapplication.getDecryptType());
    }

    /**
     * 添加记录
     * @param passwordBean
     * @return
     */
    public int addAccount(PasswordBean passwordBean){
        if(TextUtils.isEmpty(Myapplication.getPassWord())){//如果密码为空，跳转到登录界面
            return 0x126; //需要跳转
        }else {
            SQLiteDatabase db = pwdDBOpenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("keyword", passwordBean.mg_keyWord);
            values.put("account", encryptData(passwordBean.mg_account));
            values.put("nicname", encryptData(passwordBean.mg_nicname));
            values.put("password", encryptData(passwordBean.mg_password));
            values.put("tip", encryptData(passwordBean.mg_tip));
            long i = db.insert("password", null, values);
            db.close();
            if (i != -1) {
                return 0x124;
            } else {
                return 0x125;
            }
        }
    }

    /**
     * 获取
     * @return
     */
    public List<PasswordBean> getAccount(String key){
        List<PasswordBean> passwordBeans = new ArrayList<>();
        SQLiteDatabase db = pwdDBOpenHelper.getReadableDatabase();
        Cursor cursor;
        if(key == null||key.equals("")){
            cursor = db.query("password", null, null, null, null, null, null);
        }else{
            String sql = "select * from password where keyword like '%"+key+"%'";
            cursor = db.rawQuery(sql,null);
//            cursor = db.query("password", null, "keyword like '%?%'", new String[]{key}, null, null, null);//不知道为什么不行
        }
        while(cursor.moveToNext()) {
            PasswordBean passwordBean = new PasswordBean();
            passwordBean.mg_id = cursor.getInt(0);
            passwordBean.mg_keyWord = cursor.getString(1);
            passwordBean.mg_account = decryptData(cursor.getString(2));
            passwordBean.mg_nicname = decryptData(cursor.getString(3));
            passwordBean.mg_password = decryptData(cursor.getString(4));
            passwordBean.mg_tip = decryptData(cursor.getString(5));
            passwordBeans.add(passwordBean);
        }
//        for(int i=0;i<20;i++){
//            PasswordBean passwordBean = new PasswordBean();
//            passwordBean.mg_id = i;
//            passwordBean.mg_keyWord = "1212";
//            passwordBean.mg_account = "1212";
//            passwordBean.mg_nicname = "1212";
//            passwordBean.mg_password = "1212";
//            passwordBean.mg_tip = "1212";
//            passwordBeans.add(passwordBean);
//        }
        cursor.close();
        db.close();
        return passwordBeans;
    }

    /**
     * 删除记录
     */
    public boolean deleteAccount(int id){
        SQLiteDatabase db = pwdDBOpenHelper.getWritableDatabase();
        if(db.delete("password", "_id=?", new String[]{String.valueOf(id)})!=-1){
            db.close();
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            db.close();
            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    /**
     * 删除全部记录
     */
    public void deleteAllAccount(){
        SQLiteDatabase db = pwdDBOpenHelper.getWritableDatabase();
        if(db.delete("password", "1", null)!=-1){
            db.close();
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
        }else{
            db.close();
            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 更新
     * @param passwordBean
     */
    public int chgAccount(PasswordBean passwordBean){
        if(TextUtils.isEmpty(Myapplication.getPassWord())){//如果密码为空，跳转到登录界面
            return 0x126; //需要跳转
        }else {
            SQLiteDatabase db = pwdDBOpenHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("keyword", passwordBean.mg_keyWord);
            values.put("account", encryptData(passwordBean.mg_account));
            values.put("nicname", encryptData(passwordBean.mg_nicname));
            values.put("password", encryptData(passwordBean.mg_password));
            values.put("tip", encryptData(passwordBean.mg_tip));
            int i = db.update("password", values, "_id=?", new String[]{String.valueOf(passwordBean.mg_id)});
            if (i != 0) {
                db.close();
                return 0x124;
            } else {
                db.close();
                return 0x125;
            }


        }
    }

}

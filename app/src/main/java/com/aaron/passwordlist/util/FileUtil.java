package com.aaron.passwordlist.util;

import android.content.Context;
import android.util.Log;

import com.aaron.passwordlist.Myapplication;
import com.aaron.passwordlist.bean.PasswordBean;
import com.aaron.passwordlist.db.dao.PwdDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by zhrjian on 2015/11/5.
 */
public class FileUtil {
    private Context MyContext = null;
    private String DataPath = "";//数据库文件的位置

    private OnResultListener mOnResultListener = null;

    /**
     * 操作回调接口
     */
    public interface OnResultListener {
        void onResult(int ResultCode);
    }

    public void setOnResultListener(OnResultListener RsLis) {
        mOnResultListener = RsLis;
    }

    // ---------------------------------------------------------------------------------------

    public FileUtil(Context paramContext) {
        MyContext = paramContext;
        DataPath = ("/data/data/" + MyContext.getPackageName() + "/databases/");
    }

    /**
     * 获取全路径名
     *
     * @param subFileName
     * @return
     */
    public String getDataPathFileName(String subFileName) {
        return DataPath + subFileName;
    }

    /**
     * 检验文件是否存在
     *
     * @param fileName 文件全路径
     * @return
     */
    public static boolean fileIsExists(String fileName) {
        if (fileName != null) {
            try {
                File localFile = new File(fileName);
                if (!localFile.exists()) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 功能: 数据库备份（数据导出）
     * 参数: databaseName ：数据库文件名
     * destFilePathName：数据库文件备份的位置（完全路径+文件名）
     * 返回: true 成功，  false 失败。
     */
    public boolean backupDatabase(String databaseName, String destFilePathName) {
        byte[] buffer = null;
        DataOutputStream Ds = null;
        FileOutputStream FileStream = null;

        String surFileName = this.getDataPathFileName(databaseName);//获取全路径名
        if (!fileIsExists(surFileName)) { //判断是否存在
            Log.v("file_not_fount", databaseName + "文件不存在");
            return false;
        }
        try { //创建输入流
            FileInputStream in = new FileInputStream(surFileName);
            int length = in.available();
            buffer = new byte[length];
            in.read(buffer);
            Log.v("EagleTag", "read ok");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try { //创建输出流
            FileStream = new FileOutputStream(destFilePathName, false);
            Ds = new DataOutputStream(FileStream);
            if (buffer != null) {
                Ds.write(buffer);
            }
            FileStream.close();
            Ds.close();
            Log.v("EagleTag", "write ok");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 功能: 数据库还原（数据导入）
     * 参数: databaseName ：数据库文件名
     * surFilePathName：数据库文件备份的位置（完全路径+文件名）
     * 返回: true 成功，  false 失败。
     */
    public boolean restoteDatabase(String databaseName, String surFilePathName) {
        byte[] buffer = null;
        DataOutputStream Ds = null;
        FileOutputStream FileStream = null;

        String destFileName = this.getDataPathFileName(databaseName);
//		if (!FileIsExists(destFileName)){
//			if(	!Funct_File.DeleteFile(destFileName)){
//				return false;
//			}
//		}
        try { //创建输入流
            FileInputStream in = new FileInputStream(surFilePathName);
            int length = in.available();
            buffer = new byte[length];
            in.read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try { //创建输出流
            FileStream = new FileOutputStream(destFileName, false);
            Ds = new DataOutputStream(FileStream);
            if (buffer != null) {
                Ds.write(buffer);
            }
            FileStream.close();
            Ds.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean backupDatabase2jsonFile(String data, String destFilePathName) {
        byte[] buffer = null;
        DataOutputStream Ds = null;
        FileOutputStream FileStream = null;

        data = SymEncrypt.encrypt(data, Myapplication.getPassWord(), "AES");
        try {
            buffer = data.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try { //创建输出流
            FileStream = new FileOutputStream(destFilePathName, false);
            Ds = new DataOutputStream(FileStream);
            if (buffer != null) {
                Ds.write(buffer);
                Ds.flush();
            }
            FileStream.close();
            Ds.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean restoteDataFromJsonFile(Context context, String surFilePathName) {
        byte[] buffer = null;
        DataOutputStream Ds = null;
        FileOutputStream FileStream = null;

        try { //创建输入流
            FileInputStream in = new FileInputStream(surFilePathName);
            int length = in.available();
            buffer = new byte[length];
            in.read(buffer);
            Type typeOfT = new TypeToken<List<PasswordBean>>() {
            }.getType();
            String data = new String(buffer, "utf-8");
            data = SymEncrypt.decrypt(data, Myapplication.getPassWord(), "AES");
            List<PasswordBean> dataList = new Gson().fromJson(data, typeOfT);
            new PwdDao(context).addPasswordMessages(dataList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

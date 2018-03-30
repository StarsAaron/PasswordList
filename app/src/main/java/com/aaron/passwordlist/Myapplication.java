package com.aaron.passwordlist;

import android.app.Application;

public class Myapplication extends Application {
    private static final String decryptType = "AES";  // 加密类型
    private static String password  = "";             // 密码
    private static int userId;                        // 当前用户ID

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("---------application start");
    }

    public void setPassword(String password){
        this.password = password;
    }

    public static String getPassWord(){
        return password;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        Myapplication.userId = userId;
    }

    public static String getDecryptType() {
        return decryptType;
    }
}

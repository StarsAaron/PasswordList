package com.aaron.passwordlist;

import android.app.Application;

/**
 *
 * Created by Aaron on 2015/6/30.
 */
public class Myapplication extends Application {
    private static String password  = "";

    private static final String decryptType = "AES";

    public Myapplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("---------application start");
    }

    public void setPassword(String password){
        this.password = password;
    }

    public static String getPassWord (){
        return password;
    }

    public static String getDecryptType() {
        return decryptType;
    }
}

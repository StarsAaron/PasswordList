package com.aaron.passwordlist.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.aaron.keyboardlibrary.KeyboardTouchListener;
import com.aaron.keyboardlibrary.KeyboardUtil;
import com.aaron.passwordlist.R;
import com.aaron.passwordlist.bean.UserBean;
import com.aaron.passwordlist.db.dao.UserDao;

/**
 * 注册页面
 */
public class RegistActivity extends Activity {
    private static final String rug_password = "^(?![^a-zA-Z]+$)(?!\\D+$).{6,}$";//密码长度至少6
    private EditText edt_password;
    private EditText edt_password2;
    private Button btn_regist;//确定按钮
    private String password;
    private String password2;
    private UserDao userDao = null;
    private KeyboardUtil keyboardUtil;
    private LinearLayout rootView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initView();
        initMoveKeyBoard();
    }

    private void initView() {
        edt_password = (EditText)findViewById(R.id.edt_password);
        edt_password2 = (EditText)findViewById(R.id.edt_password2);
        btn_regist = (Button)findViewById(R.id.btn_regist);

        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInput()){
                    insertUserDataToDb();
                }
            }
        });
        rootView = (LinearLayout) findViewById(R.id.rootView);
        scrollView = (ScrollView) findViewById(R.id.sv_main);
    }
    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, rootView, scrollView);
        keyboardUtil.hideSystemKeyBoard();
        edt_password.setOnTouchListener(new KeyboardTouchListener(keyboardUtil
                , KeyboardUtil.INPUTTYPE_ABC, -1));
        edt_password2.setOnTouchListener(new KeyboardTouchListener(keyboardUtil
                , KeyboardUtil.INPUTTYPE_ABC, -1));
    }

    private void insertUserDataToDb(){
        UserBean userBean = new UserBean();
        userBean.userName = "admin";
        userBean.userPassword = password;
        userDao = new UserDao(RegistActivity.this);
        if(userDao.insertUserMessageToDb(userBean)> 0){
            toast("创建成功！");
            saveUserPasswordToSp();
            jumpToLoginActivity();
        }else {
            toast("创建失败！");
        }
    }

    private void saveUserPasswordToSp(){
        SharedPreferences sp = getSharedPreferences("msg", Context.MODE_PRIVATE);
        sp.edit().putString("pass", password).commit();
    }

    private void jumpToLoginActivity(){
        Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean checkInput() {
        password = edt_password.getText().toString().trim();
        password2 = edt_password2.getText().toString().trim();
        if(TextUtils.isEmpty(password)){
            edt_password.setError("密码不能为空！");
            return false;
        }
        if(!password.matches(rug_password)){
            Toast.makeText(RegistActivity.this,"密码必须英文字母加数字至少6位",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(password2)){
            edt_password2.setError("请再次输入密码！");
            return false;
        }
        if(!password2 .equals(password)){
            edt_password2.setError("两次密码不一致！");
            return false;
        }
        return true;
    }
}

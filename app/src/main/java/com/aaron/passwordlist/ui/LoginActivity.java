package com.aaron.passwordlist.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aaron.passwordlist.Myapplication;
import com.aaron.passwordlist.R;
import com.aaron.passwordlist.db.dao.UserDao;
import com.aaron.passwordlist.util.KeyboardUtil;
import com.aaron.passwordlist.util.Md5Util;

/**
 * 登录页面
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private Button btn_login,btn_finger;  // 按钮
    private EditText edt_login_password;  // 密码输入框
    private UserDao userDao = null;       // 用户信息
    private Myapplication myapplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//关闭系统默认输入法
        myapplication = (Myapplication) getApplication();
        initView();
        initData();
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        edt_login_password = (EditText) findViewById(R.id.edt_login_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_finger = (Button) findViewById(R.id.btn_finger);

//        edt_login_password.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);//设置输入法为空
        edt_login_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardUtil(LoginActivity.this, LoginActivity.this, edt_login_password).showKeyboard();
                return true;
            }
        });
        btn_login.setOnClickListener(this);
        btn_finger.setOnClickListener(this);
    }

    /**
     * 初始化页面显示数据
     */
    private void initData() {
        userDao = new UserDao(LoginActivity.this);
        if(!userDao.anyoneExit()){
            Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String pass = edt_login_password.getText().toString().trim();
                if (checkInput(pass)) {
                    if (userDao.checkUser(LoginActivity.this, Md5Util.md5Arithmetic(Md5Util.md5Arithmetic(pass)))) {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                        // 保存数据
                        myapplication.setPassword(pass);//临时保存
                        SharedPreferences sp = getSharedPreferences("msg", Context.MODE_PRIVATE);
                        sp.edit().putString("pass", pass).commit();

                        // 跳转到主页
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        edt_login_password.setError("密码错误");
                    }
                }
                break;
            case R.id.btn_finger:
                showFragmentDialog();
                break;
        }
    }

    /**
     * 显示按指纹对话框
     */
    private void showFragmentDialog(){
        FingerDialogFragment fingerDialogFragment = new FingerDialogFragment();
        fingerDialogFragment.show(getFragmentManager(), "fingerFragment");
        fingerDialogFragment.setmFragmentCallBack(new FingerDialogFragment.Callback() {
            @Override
            public void onSuccess() {
                SharedPreferences sp = getSharedPreferences("msg", Context.MODE_PRIVATE);
                String pass = sp.getString("pass","");

                if(!TextUtils.isEmpty(pass)){
                    myapplication.setPassword(pass);//临时保存
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "获取不到本地数据，请使用密码登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(LoginActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 检查输入数据
     * @param password
     * @return
     */
    private boolean checkInput(String password) {
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

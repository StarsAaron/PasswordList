package com.aaron.passwordlist.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.aaron.keyboardlibrary.KeyboardTouchListener;
import com.aaron.keyboardlibrary.KeyboardUtil;
import com.aaron.passwordlist.Myapplication;
import com.aaron.passwordlist.R;
import com.aaron.passwordlist.bean.UserBean;
import com.aaron.passwordlist.db.dao.UserDao;

/**
 * 登录页面
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private Button btn_login, btn_finger;  // 按钮
    private EditText edt_login_password;  // 密码输入框
    private UserDao userDao = null;       // 用户信息
    private Myapplication myapplication;
    private KeyboardUtil keyboardUtil;
    private LinearLayout rootView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myapplication = (Myapplication) getApplication();
        initView();
        initMoveKeyBoard();
        isHadRegist();
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        edt_login_password = (EditText) findViewById(R.id.edt_login_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_finger = (Button) findViewById(R.id.btn_finger);

        btn_login.setOnClickListener(this);
        btn_finger.setOnClickListener(this);

        rootView = (LinearLayout) findViewById(R.id.rootView);
        scrollView = (ScrollView) findViewById(R.id.sv_main);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (keyboardUtil.isShow) {
                keyboardUtil.hideSystemKeyBoard();
                keyboardUtil.hideAllKeyBoard();
                keyboardUtil.hideKeyboardLayout();
            } else {
                return super.onKeyDown(keyCode, event);
            }

            return false;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, rootView, scrollView);
        edt_login_password.setOnTouchListener(new KeyboardTouchListener(keyboardUtil
                , KeyboardUtil.INPUTTYPE_ABC, -1));
    }

    /**
     * 初始化页面显示数据
     */
    private void isHadRegist() {
        userDao = new UserDao(LoginActivity.this);
        if (!userDao.anyoneExist()) {
            Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
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
                    checkUserPassword();
                }
                break;
            case R.id.btn_finger:
                showFragmentDialog();
                break;
        }
    }

    private void checkUserPassword() {
        String pass = edt_login_password.getText().toString().trim();
        UserBean userBean = new UserBean();
        userBean.userName = "admin";
        userBean.userPassword = pass;
        if (userDao.checkUserMessage(userBean)) {
            toast("登录成功");
            saveTempData(pass);
            jumpToMainActivity();
        } else {
            edt_login_password.setError("密码错误");
        }
    }

    private void saveTempData(String pass) {
        myapplication.setPassword(pass);//临时保存
        SharedPreferences sp = getSharedPreferences("msg", Context.MODE_PRIVATE);
        sp.edit().putString("pass", pass).commit();
    }

    /**
     * 显示按指纹对话框
     */
    private void showFragmentDialog() {
        FingerDialogFragment fingerDialogFragment = new FingerDialogFragment();
        fingerDialogFragment.show(getFragmentManager(), "fingerFragment");
        fingerDialogFragment.setmFragmentCallBack(new FingerDialogFragment.Callback() {
            @Override
            public void onSuccess() {
                SharedPreferences sp = getSharedPreferences("msg", Context.MODE_PRIVATE);
                String pass = sp.getString("pass", "");

                if (!TextUtils.isEmpty(pass)) {
                    myapplication.setPassword(pass);//临时保存
                    toast("登录成功");
                    jumpToMainActivity();
                } else {
                    toast("获取不到本地数据，请使用密码登录");
                }
            }

            @Override
            public void onError() {
                toast("成功");
            }
        });
    }

    private void jumpToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, ShowActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean checkInput(String password) {
        if (TextUtils.isEmpty(password)) {
            toast("密码不能为空");
            return false;
        }
        return true;
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}

package com.aaron.passwordlist.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aaron.passwordlist.R;
import com.aaron.passwordlist.bean.UserBean;
import com.aaron.passwordlist.db.dao.UserDao;
import com.aaron.passwordlist.util.KeyboardUtil;
import com.aaron.passwordlist.util.Md5Util;

/**
 * 注册页面
 */
public class RegistActivity extends Activity {
    private static final String  rug_email = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
    private static final String rug_password = "^(?![^a-zA-Z]+$)(?!\\D+$).{6,}$";//密码长度至少6
    private EditText edt_password,edt_password2;
    private Button btn_regist;//确定按钮
    private String password,password2,email;
    private UserDao userDao = null;
    private KeyboardUtil keyboardUtil1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initView();
    }

    private void initView() {
        edt_password = (EditText)findViewById(R.id.edt_password);
        edt_password2 = (EditText)findViewById(R.id.edt_password2);
        btn_regist = (Button)findViewById(R.id.btn_regist);

        keyboardUtil1 = new KeyboardUtil(RegistActivity.this, RegistActivity.this, edt_password);
        edt_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                keyboardUtil1.setEditText(edt_password);
                keyboardUtil1.showKeyboard();
                return true;
            }
        });
        edt_password2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                keyboardUtil1.setEditText(edt_password2);
                keyboardUtil1.showKeyboard();
                return true;
            }
        });
        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInput()){
                    UserBean userBean = new UserBean();
                    userBean.password = password;
                    userBean.email = email;
                    userDao = new UserDao(RegistActivity.this);
                    if(userDao.addUser(userBean)== 1){
                        // 保存用户数据
                        SharedPreferences sp = getSharedPreferences("msg", Context.MODE_PRIVATE);
                        sp.edit().putString("pass", password).commit();

                        // 跳转到登录页面
                        Toast.makeText(RegistActivity.this,"创建成功！",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(RegistActivity.this,"创建失败！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 检查输入是否合法
     * @return
     */
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
//        if(email .equals("")){
//            edt_email.setError("请输入备份邮箱");
//            return false;
//        }
//        Pattern p = Pattern.compile(rug_email);
//        Matcher m = p.matcher(email);
//        if(!m.matches()){
//            edt_email.setError("地址格式不正确");
//            return false;
//        }
        return true;
    }
}

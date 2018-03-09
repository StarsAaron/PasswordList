package com.aaron.passwordlist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aaron.passwordlist.R;
import com.aaron.passwordlist.bean.PasswordBean;
import com.aaron.passwordlist.db.dao.PwdDao;

/**
 * 添加记录页面
 */
public class AddActivity extends AppCompatActivity {
    private EditText edt_keyword, edt_account, edt_nicname, edt_password, edt_tip;
    private Button btn_add;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("添加记录");
//        //返回按钮点击事件
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edt_keyword = (EditText)findViewById(R.id.edt_keyword);
        edt_account = (EditText)findViewById(R.id.edt_account);
        edt_nicname = (EditText)findViewById(R.id.edt_nicname);
        edt_password = (EditText)findViewById(R.id.edt_password);
        edt_tip = (EditText)findViewById(R.id.edt_tip);
        btn_add = (Button)findViewById(R.id.btn_add);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = edt_keyword.getText().toString();
                String account = edt_account.getText().toString();
                String password = edt_password.getText().toString();
                if (key == null || key.equals("")) {
                    Toast.makeText(getApplicationContext(), "关键字是搜索时必须的", Toast.LENGTH_SHORT).show();
                } else if (account == null || account.equals("")) {
                    Toast.makeText(getApplicationContext(), "账号为空", Toast.LENGTH_SHORT).show();
                } else if (password == null || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "密码为空", Toast.LENGTH_SHORT).show();
                } else {
                    PasswordBean passwordBean = new PasswordBean();
                    passwordBean.mg_keyWord = edt_keyword.getText().toString();
                    passwordBean.mg_account = edt_account.getText().toString();
                    passwordBean.mg_nicname = edt_nicname.getText().toString();
                    try {
                        passwordBean.mg_password = edt_password.getText().toString();
                        System.out.println("---------" + passwordBean.mg_password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    passwordBean.mg_tip = edt_tip.getText().toString();
                    PwdDao pwdDao = new PwdDao(getApplicationContext());
                    switch (pwdDao.addAccount(passwordBean)) {
                        case 0x124:
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                            break;
                        case 0x125:
                            Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                            break;
                        case 0x126:
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

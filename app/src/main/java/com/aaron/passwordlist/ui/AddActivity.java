package com.aaron.passwordlist.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aaron.passwordlist.Myapplication;
import com.aaron.passwordlist.R;
import com.aaron.passwordlist.bean.PasswordBean;
import com.aaron.passwordlist.db.dao.PwdDao;

/**
 * 添加记录页面
 */
public class AddActivity extends AppCompatActivity {
    private EditText edt_keyword;
    private EditText edt_account;
    private EditText edt_tip;
    private Button btn_add;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
    }

    private void initView() {
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

        edt_keyword = (EditText) findViewById(R.id.edt_keyword);
        edt_account = (EditText) findViewById(R.id.edt_account);
        edt_tip = (EditText) findViewById(R.id.edt_tip);
        btn_add = (Button) findViewById(R.id.btn_add);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    insertPasswordMessageToDb();
                }
            }
        });
    }

    private boolean checkInput() {
        String key = edt_keyword.getText().toString();
        String account = edt_account.getText().toString();
        if (TextUtils.isEmpty(key)) {
            toast("关键字是搜索时必须的");
            return false;
        }
        if (TextUtils.isEmpty(account)) {
            toast("账号为空");
            return false;
        }
        return true;
    }

    private void insertPasswordMessageToDb(){
        PasswordBean passwordBean = new PasswordBean();
        passwordBean.pwdkeyword = edt_keyword.getText().toString();
        passwordBean.pwdAccount = edt_account.getText().toString();
        passwordBean.pwdTip = edt_tip.getText().toString();
        passwordBean.userKey = Myapplication.getUserId();
        PwdDao pwdDao = new PwdDao(getApplicationContext());
        long rowsAffectedResult = pwdDao.addPasswordMessage(passwordBean);
        if(rowsAffectedResult <= 0){
            toast("添加失败");
        }else{
            toast("添加成功");
        }
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

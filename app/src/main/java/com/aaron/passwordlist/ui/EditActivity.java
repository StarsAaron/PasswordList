package com.aaron.passwordlist.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditActivity extends AppCompatActivity {
    private EditText edit_keyword;
    private EditText edit_account;
    private EditText edit_tip;
    private Button btn_edit_add;
    private Toolbar toolbar;
    private PasswordBean passwordBean;

    public static void jumpToEditActivity(Context context,PasswordBean passwordBean){
        Intent intent = new Intent(context,EditActivity.class);
        intent.putExtra("passwordbean",passwordBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("修改记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_keyword = (EditText) findViewById(R.id.edit_keyword);
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_tip = (EditText) findViewById(R.id.edit_tip);
        btn_edit_add = (Button) findViewById(R.id.btn_edit_add);

        btn_edit_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    changePasswordMessageToDb();
                }
            }
        });

        passwordBean = (PasswordBean)getIntent().getSerializableExtra("passwordbean");
        if(passwordBean != null){
            edit_keyword.setText(passwordBean.pwdkeyword);
            edit_account.setText(passwordBean.pwdAccount);
            edit_tip.setText(passwordBean.pwdTip);
        }else{
            toast("获取不到数据");
            finish();
        }
    }

    private boolean checkInput() {
        String key = edit_keyword.getText().toString();
        String account = edit_account.getText().toString();
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

    private void changePasswordMessageToDb(){
        passwordBean.pwdkeyword = edit_keyword.getText().toString();
        passwordBean.pwdAccount = edit_account.getText().toString();
        passwordBean.pwdTip = edit_tip.getText().toString();
        PwdDao pwdDao = new PwdDao(getApplicationContext());
        long rowsAffectedResult = pwdDao.changePasswordMessage(passwordBean);
        if(rowsAffectedResult <= 0){
            toast("修改失败");
        }else{
            toast("修改成功");
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

package com.aaron.passwordlist.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aaron.passwordlist.MyConstant;
import com.aaron.passwordlist.R;
import com.aaron.passwordlist.adapter.TestStackAdapter;
import com.aaron.passwordlist.bean.PasswordBean;
import com.aaron.passwordlist.db.dao.PwdDao;
import com.aaron.passwordlist.util.DateUtils;
import com.aaron.passwordlist.util.FileUtil;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.loopeer.cardstack.CardStackView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.aaron.passwordlist.MyConstant.dialogContentBgColor;
import static com.aaron.passwordlist.MyConstant.dialogTitleBarColor;
import static com.aaron.passwordlist.R.id.cardStackView;


public class MainActivity extends AppCompatActivity implements CardStackView.ItemExpendListener,TestStackAdapter.MyListener {
    public static Integer[] TEST_DATAS = new Integer[]{
            R.color.color_1,
            R.color.color_2,
            R.color.color_3,
            R.color.color_4,
            R.color.color_5,
            R.color.color_6,
            R.color.color_7,
            R.color.color_8,
            R.color.color_9,
            R.color.color_10,
            R.color.color_11,
            R.color.color_12,
            R.color.color_13,
            R.color.color_14,
            R.color.color_15,
            R.color.color_16,
            R.color.color_17,
            R.color.color_18,
            R.color.color_19,
            R.color.color_20,
            R.color.color_21,
            R.color.color_22,
            R.color.color_23,
            R.color.color_24,
            R.color.color_25,
            R.color.color_26
    };

    private PwdDao pwdDao;
    private List<PasswordBean> passwordBeans;
    private View tv_empty_pwd;
    private CardStackView mStackView;
    private static final int REQUEST_FILE_PATH = 1000;     // 请求文件路径
    private static final int REQUEST_DIR_PATH = 2000;      // 请求文件夹路径
    private String keyWork = "";                           //搜索关键字
    private static final String searchKey = "searchKey";

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0x100:
                    getData("");
                    break;
                case 0x101:
                case 0x102:
                    Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 0x103:
                    getData((String)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

        tv_empty_pwd = findViewById(R.id.empty_view);
        mStackView = (CardStackView) findViewById(cardStackView);
        if(savedInstanceState != null){
            keyWork = savedInstanceState.getString(searchKey);
        }
        getData(keyWork);
    }

    /**
     * 获取数据
     * @param key 关键字
     */
    private void getData(String key){
        if(pwdDao == null){
            pwdDao = new PwdDao(getApplicationContext());
        }
        passwordBeans = pwdDao.getAccount(key);
        if (passwordBeans.isEmpty()) {
            tv_empty_pwd.setVisibility(View.VISIBLE);
            mStackView.setVisibility(View.GONE);
        } else {
            tv_empty_pwd.setVisibility(View.GONE);
            mStackView.setVisibility(View.VISIBLE);
            TestStackAdapter mTestStackAdapter = new TestStackAdapter(this, Arrays.asList(TEST_DATAS));
            mStackView.setAdapter(mTestStackAdapter);
            mStackView.setItemExpendListener(this);
            mTestStackAdapter.updateData(passwordBeans);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_export) {
            new LFilePicker()
                    .withActivity(MainActivity.this)
                    .withRequestCode(REQUEST_DIR_PATH)
                    .withTitle("请选择导出文件夹")
                    .withIconStyle(Constant.ICON_STYLE_BLUE)
                    .withChooseType(Constant.CHOOSE_DIR)
                    .withMutilyMode(false)
                    .start();
            return true;
        }else if(id == R.id.action_import){
            new LFilePicker()
                    .withActivity(MainActivity.this)
                    .withRequestCode(REQUEST_FILE_PATH)
                    .withTitle("请选择导入的文件")
                    .withIconStyle(Constant.ICON_STYLE_BLUE)
                    .withChooseType(Constant.CHOOSE_FILE)
                    .withMutilyMode(false)
                    .start();
        }else if(id == R.id.action_clear){
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(MainActivity.this);
            dialogBuilder
                    .withTitle("Tip")
                    .withMessage("确定要清空数据记录吗?")
                    .withDialogColor(dialogContentBgColor)
                    .withTitleColor(dialogTitleBarColor)
                    .withDividerColor("#11000000")
                    .withMessageColor("#FFFFFFFF")
                    .withEffect(Effectstype.Fadein)
                    .withButton1Text("确定")
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PwdDao pwdDao = new PwdDao(MainActivity.this);
                            pwdDao.deleteAllAccount();
                            myHandler.sendEmptyMessage(0x100);
                            dialogBuilder.dismiss();
                        }
                    })
                    .withButton2Text("取消")
                    .setButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    }).show();
        }else if(id == R.id.action_search){
            //查找
            //1获取一个对话框的创建器
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            //2所有builder设置一些参数
            LinearLayout linearLayout = new LinearLayout(MainActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(layoutParams);

            final EditText editText = new EditText(MainActivity.this);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams2.weight = 1;
            editText.setLayoutParams(layoutParams2);

            Button btnSearch = new Button(MainActivity.this);
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(dp2px(38),dp2px(38));
            btnSearch.setLayoutParams(layoutParams3);
            btnSearch.setTextColor(Color.parseColor("#424242"));
            btnSearch.setBackgroundResource(R.mipmap.ic_search);

            linearLayout.addView(editText);
            linearLayout.addView(btnSearch);

            builder.setCancelable(true);
            builder.setView(linearLayout);
            final AlertDialog dialog = builder.create();
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "搜索中...", Toast.LENGTH_SHORT).show();
                    keyWork = editText.getText().toString().trim();
                    dialog.dismiss();
                    Message message = myHandler.obtainMessage();
                    message.obj = keyWork;
                    message.what = 0x103;
                    myHandler.sendMessage(message);
                }
            });
            dialog.show();
        }else if(id == R.id.action_about){
            StringBuffer message = new StringBuffer();
            message.append("作者：Aaron\n邮箱：103514303@qq.com\n版本：V");
            try {
                String version = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
                message.append(version);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            NiftyDialogBuilder about = NiftyDialogBuilder.getInstance(MainActivity.this);
            about.withTitle("关于")
                    .withMessage(message.toString())
                    .withDialogColor(dialogContentBgColor)
                    .withTitleColor(dialogTitleBarColor)
                    .withDividerColor("#11000000")
                    .withMessageColor("#FFFFFFFF")
                    .withEffect(Effectstype.Fadein)
                    .show();
        }
        return true;
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    private int dp2px(final float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { //备份
            List<String> list = data.getStringArrayListExtra("paths");
            final String path = list.get(0);
            if (requestCode == REQUEST_DIR_PATH) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String fileName = "密码管理器_"+ DateUtils.getDateDayTime2(System.currentTimeMillis());
                        FileUtil fileUtil = new FileUtil(getApplicationContext());
                        if (fileUtil.backupDatabase(MyConstant.PW_DBName,  path + File.separator + fileName)) {
                            Message message = myHandler.obtainMessage();
                            message.obj = "备份成功！";
                            message.what = 0x101;
                            myHandler.sendMessage(message);
                        } else {
                            Message message = myHandler.obtainMessage();
                            message.obj = "备份失败！";
                            message.what = 0x102;
                            myHandler.sendMessage(message);
                        }
                    }
                }).start();
            }

            if(requestCode == REQUEST_FILE_PATH){//恢复
                final NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(MainActivity.this);
                dialogBuilder
                        .withTitle("Tip")
                        .withMessage("恢复的数据会把当前应用的数据都覆盖掉，确定要恢复数据吗？")
                        .withDialogColor(dialogContentBgColor)
                        .withTitleColor(dialogTitleBarColor)
                        .withDividerColor("#11000000")
                        .withMessageColor("#FFFFFFFF")
                        .withEffect(Effectstype.Fadein)
                        .withButton1Text("确定")
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        File file = new File(path);
                                        FileUtil fileUtil = new FileUtil(getApplicationContext());
                                        if (file.exists()) {
                                            if (fileUtil.restoteDatabase(MyConstant.PW_DBName, path)) {
                                                Message message = myHandler.obtainMessage();
                                                message.obj = "恢复成功！";
                                                message.what = 0x100;
                                                myHandler.sendMessage(message);
                                            } else {
                                                Message message = myHandler.obtainMessage();
                                                message.obj = "恢复失败！";
                                                message.what = 0x102;
                                                myHandler.sendMessage(message);
                                            }
                                        } else {
                                            Message message = myHandler.obtainMessage();
                                            message.obj = "文件不存在！";
                                            message.what = 0x102;
                                            myHandler.sendMessage(message);
                                        }
                                    }
                                }).start();
                                dialogBuilder.dismiss();
                            }
                        })
                        .withButton2Text("取消")
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        }).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("searchKey",keyWork);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getData(keyWork);
    }

    @Override
    public void onItemExpend(boolean expend) {
    }

    @Override
    public void changeDataSuccessed() {
        getData("");
    }

    @Override
    public void showMsg(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}

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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aaron.passwordlist.MyConstant;
import com.aaron.passwordlist.Myapplication;
import com.aaron.passwordlist.R;
import com.aaron.passwordlist.bean.PasswordBean;
import com.aaron.passwordlist.db.dao.PwdDao;
import com.aaron.passwordlist.util.DateUtils;
import com.aaron.passwordlist.util.FileUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.io.File;
import java.util.List;

import static com.aaron.passwordlist.MyConstant.dialogContentBgColor;
import static com.aaron.passwordlist.MyConstant.dialogTitleBarColor;

public class ShowActivity extends AppCompatActivity {
    private static final int REQUEST_FILE_PATH = 1000;     // 请求文件路径
    private static final int REQUEST_DIR_PATH = 2000;      // 请求文件夹路径
    private static final String searchKey = "searchKey";
    private String keyWork = "";                           //搜索关键字
    private PwdDao pwdDao;
    private List<PasswordBean> passwordBeans;
    private View emptyView;
    private RecyclerView recycleView;
    private BaseQuickAdapter baseQuickAdapter;

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x100:
                    getDataFromDb("");
                    break;
                case 0x101:
                case 0x102:
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 0x103:
                    getDataFromDb((String) msg.obj);
                    break;
            }
        }
    }

    private MyHandler myHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        initView();
        restoreKeyWorkFromLifeBundle(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDb(keyWork);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        emptyView = findViewById(R.id.empty_view);
        recycleView = findViewById(R.id.recycleView);
    }

    private void restoreKeyWorkFromLifeBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            keyWork = savedInstanceState.getString(searchKey);
        }
    }

    private void getDataFromDb(String key) {
        if (pwdDao == null) {
            pwdDao = new PwdDao(getApplicationContext());
        }
        String userId = String.valueOf(Myapplication.getUserId());
        passwordBeans = pwdDao.getPasswordMessage(userId,key);
        setRecycleViewAdapter();
    }

    private void setRecycleViewAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        baseQuickAdapter = new BaseQuickAdapter<PasswordBean, BaseViewHolder>(R.layout.item_listview, passwordBeans) {
            @Override
            protected void convert(BaseViewHolder helper, PasswordBean item) {
                helper.setText(R.id.tv_item_account, item.pwdAccount);
                helper.setText(R.id.tv_item_tip, item.pwdTip);
                helper.addOnClickListener(R.id.imgbtn_edit);
            }
        };
        baseQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                EditActivity.jumpToEditActivity(ShowActivity.this,passwordBeans.get(position));
            }
        });
        recycleView.setAdapter(baseQuickAdapter);
        baseQuickAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.emptyview,null));
    }

    private void jumpToEditActivity(){

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
            showChooseOutputPathDialog();
        } else if (id == R.id.action_import) {
            showChooseInputPathDialog();
        } else if (id == R.id.action_clear) {
            showCleanAllDataDialog();
        } else if (id == R.id.action_search) {
            showSearchDialog();
        } else if (id == R.id.action_about) {
            showAppMessageDialog();
        }
        return true;
    }

    private void showChooseOutputPathDialog() {
        new LFilePicker()
                .withActivity(ShowActivity.this)
                .withRequestCode(REQUEST_DIR_PATH)
                .withTitle("请选择导出文件夹")
                .withIconStyle(Constant.ICON_STYLE_BLUE)
                .withChooseType(Constant.CHOOSE_DIR)
                .withMutilyMode(false)
                .start();
    }

    private void showChooseInputPathDialog() {
        new LFilePicker()
                .withActivity(ShowActivity.this)
                .withRequestCode(REQUEST_FILE_PATH)
                .withTitle("请选择导入的文件")
                .withIconStyle(Constant.ICON_STYLE_BLUE)
                .withChooseType(Constant.CHOOSE_FILE)
                .withMutilyMode(false)
                .start();
    }

    private void showCleanAllDataDialog() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(ShowActivity.this);
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
                        PwdDao pwdDao = new PwdDao(ShowActivity.this);
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
    }

    private void showSearchDialog() {
        //查找
        //1获取一个对话框的创建器
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
        //2所有builder设置一些参数
        LinearLayout linearLayout = new LinearLayout(ShowActivity.this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        final EditText editText = new EditText(ShowActivity.this);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.weight = 1;
        editText.setLayoutParams(layoutParams2);

        Button btnSearch = new Button(ShowActivity.this);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(dp2px(38), dp2px(38));
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
                Toast.makeText(ShowActivity.this, "搜索中...", Toast.LENGTH_SHORT).show();
                keyWork = editText.getText().toString().trim();
                dialog.dismiss();
                Message message = myHandler.obtainMessage();
                message.obj = keyWork;
                message.what = 0x103;
                myHandler.sendMessage(message);
            }
        });
        dialog.show();
    }

    private void showAppMessageDialog() {
        StringBuffer message = new StringBuffer();
        message.append("作者：Aaron\n邮箱：103514303@qq.com\n版本：V");
        try {
            String version = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            message.append(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        NiftyDialogBuilder about = NiftyDialogBuilder.getInstance(ShowActivity.this);
        about.withTitle("关于")
                .withMessage(message.toString())
                .withDialogColor(dialogContentBgColor)
                .withTitleColor(dialogTitleBarColor)
                .withDividerColor("#11000000")
                .withMessageColor("#FFFFFFFF")
                .withEffect(Effectstype.Fadein)
                .show();
    }

    private int dp2px(final float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private String provideFileName(){
        return "密码管理器_" + DateUtils.getDateDayTime2(System.currentTimeMillis());
    }

    private void doBackupJop(final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtil fileUtil = new FileUtil(getApplicationContext());
                final boolean result = fileUtil.backupDatabase(MyConstant.PW_DBName
                        , filePath + File.separator + provideFileName());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            toast("备份成功！");
                        } else {
                            toast("备份失败！");
                        }
                    }
                });
            }
        }).start();
    }

    private void doRestoreJop(final String filePath) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(ShowActivity.this);
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
                                File file = new File(filePath);
                                FileUtil fileUtil = new FileUtil(getApplicationContext());
                                String resultMessage;
                                if (file.exists()) {
                                    if (fileUtil.restoteDatabase(MyConstant.PW_DBName, filePath)) {
                                        resultMessage = "恢复成功！";
                                    } else {
                                        resultMessage = "恢复失败！";
                                    }
                                } else {
                                    resultMessage = "恢复失败！";
                                }
                                final String finalMsg = resultMessage;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast(finalMsg);
                                    }
                                });
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

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            List<String> list = data.getStringArrayListExtra("paths");
            final String path = list.get(0);
            if (requestCode == REQUEST_DIR_PATH) {
                doBackupJop(path);//备份
            }
            if (requestCode == REQUEST_FILE_PATH) {
                doRestoreJop(path);//恢复
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("searchKey", keyWork);
    }
}

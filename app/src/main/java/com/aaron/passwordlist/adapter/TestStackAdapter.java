package com.aaron.passwordlist.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.passwordlist.R;
import com.aaron.passwordlist.bean.PasswordBean;
import com.aaron.passwordlist.db.dao.PwdDao;
import com.aaron.passwordlist.ui.LoginActivity;
import com.aaron.passwordlist.ui.MainActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.util.List;

import static com.aaron.passwordlist.MyConstant.dialogContentBgColor;
import static com.aaron.passwordlist.MyConstant.dialogTitleBarColor;

/**
 * Created by Aaron on 2017/8/24.
 */

public class TestStackAdapter extends StackAdapter<PasswordBean> {
    private List<Integer> colorData ;
    private Context context;

    public TestStackAdapter(Context context, List<Integer> colorData) {
        super(context);
        this.colorData = colorData;
        this.context = context;
    }

    @Override
    public void bindView(PasswordBean data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(context,data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        return new ColorItemViewHolder(view,colorData);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_card_item;
    }

    private static class ColorItemViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle;
        ImageView mEdit;
        EditText edt_item_key,edt_item_account,edt_item_nicname,myedit,edt_item_tip;
        LinearLayout ll_button;
        Button btn_OK,btn_chg_cancel,btn_del;

        List<Integer> colorData;

        public ColorItemViewHolder(View view,List<Integer> colorData) {
            super(view);
            this.colorData = colorData;
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle =(TextView)view.findViewById(R.id.text_list_card_title);
            mEdit = (ImageView)view.findViewById(R.id.iv_edit);
            edt_item_key = (EditText)view.findViewById(R.id.edt_item_key);
            edt_item_account = (EditText)view.findViewById(R.id.edt_item_account);
            edt_item_nicname = (EditText)view.findViewById(R.id.edt_item_nicname);
            myedit = (EditText)view.findViewById(R.id.myedit);
            edt_item_tip = (EditText)view.findViewById(R.id.edt_item_tip);

            ll_button = (LinearLayout) view.findViewById(R.id.ll_button);
            btn_OK = (Button)view.findViewById(R.id.btn_OK);
            btn_chg_cancel = (Button)view.findViewById(R.id.btn_chg_cancel);

            btn_del = (Button)view.findViewById(R.id.btn_del);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(final Context context, final PasswordBean pb, final int position) {
            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setEditable(edt_item_key.isEnabled()?false:true);
                }
            });
            btn_OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String key = edt_item_key.getText().toString();
                    String account = edt_item_account.getText().toString();
                    String nicname = edt_item_nicname.getText().toString();
                    String password = myedit.getText().toString();
                    String tip = edt_item_tip.getText().toString();

                    if(key == null || key.equals("")){
                        ((MyListener)context).showMsg("关键字是搜索时必须的");
                    }else if(account == null || account.equals("")){
                        ((MyListener)context).showMsg("账号为空");
                    }else if(password == null || password.equals("")){
                        ((MyListener)context).showMsg("密码为空");
                    }else{
                        PwdDao pwdDao = new PwdDao(context);
                        PasswordBean savePb = new PasswordBean();
                        savePb.mg_id = pb.mg_id;
                        savePb.mg_keyWord = key;
                        savePb.mg_account = account;
                        savePb.mg_nicname = nicname;
                        savePb.mg_password = password;
                        savePb.mg_tip = tip;
                        switch(pwdDao.chgAccount(savePb)){
                            case 0x124:
                                ((MyListener)context).showMsg("保存成功");
                                ((MyListener)context).changeDataSuccessed();
                                setEditable(false);
                                break;
                            case 0x125:
                                ((MyListener)context).showMsg("保存失败");
                                break;
                            case 0x126:
                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                                break;
                        }
                    }
                }
            });
            btn_chg_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edt_item_key.setText(pb.mg_keyWord);
                    edt_item_account.setText(pb.mg_account);
                    edt_item_nicname.setText(pb.mg_nicname);
                    myedit.setText(pb.mg_password);
                    edt_item_tip.setText(pb.mg_tip);
                    setEditable(false);
                }
            });
            btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                    dialogBuilder
                            .withTitle("Tip")
                            .withMessage("确定要删除记录吗?")
                            .withDialogColor(dialogContentBgColor)
                            .withTitleColor(dialogTitleBarColor)
                            .withDividerColor("#11000000")
                            .withMessageColor("#FFFFFFFF")
                            .withEffect(Effectstype.Fadein)
                            .withButton1Text("确定")
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PwdDao pwdDao = new PwdDao(context);
                                    if(pwdDao.deleteAccount(pb.mg_id)){
                                        ((MyListener)context).changeDataSuccessed();
                                    }else{
                                        ((MyListener)context).showMsg("删除失败");
                                    }
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
            });

            //设置标题颜色
            int colorId = colorData.get(position%(colorData.size()-1));
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(),colorId), PorterDuff.Mode.SRC_IN);
            //设置标题
            mTextTitle.setText(pb.mg_nicname);
            edt_item_key.setText(pb.mg_keyWord);
            edt_item_account.setText(pb.mg_account);
            edt_item_nicname.setText(pb.mg_nicname);
            myedit.setText(pb.mg_password);
            edt_item_tip.setText(pb.mg_tip);
        }

        //设置按钮栏是否可见
        private void setEditable(boolean is){
            edt_item_key.setEnabled(is);
            edt_item_account.setEnabled(is);
            edt_item_nicname.setEnabled(is);
            myedit.setEnabled(is);
            edt_item_tip.setEnabled(is);
            if(is){
                ll_button.setVisibility(View.VISIBLE);
            }else{
                ll_button.setVisibility(View.GONE);
            }
        }
    }



    //数据监听器
    public interface MyListener{
        // 修改数据成功，通知刷新页面
        void changeDataSuccessed();
        //输出提示信息
        void showMsg(String str);
    }
}

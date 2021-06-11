package com.aaron.passwordlist.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.passwordlist.R;
import com.xuelianx.fingerlib.FingerprintIdentify;
import com.xuelianx.fingerlib.base.BaseFingerprint;

/**
 * 指纹提示对话框
 * Created by Aaron on 2018/3/9.
 * <p>
 * <p>
 * 使用：
 *
 * private void showFragmentDialog() {
 *         FingerDialogFragment fingerDialogFragment = new FingerDialogFragment();
 *         fingerDialogFragment.show(getFragmentManager(), "fingerFragment");
 *         fingerDialogFragment.setmFragmentCallBack(new FingerDialogFragment.Callback() {
 *             @Override
 *             public void onSuccess() {
 *                 SharedPreferences sp = getSharedPreferences("msg", Context.MODE_PRIVATE);
 *                 String pass = sp.getString("pass", "");
 *
 *                 if (!TextUtils.isEmpty(pass)) {
 *                     myapplication.setPassword(pass);//临时保存
 *                     toast("登录成功");
 *                     jumpToMainActivity();
 *                 } else {
 *                     toast("获取不到本地数据，请使用密码登录");
 *                 }
 *             }
 *
 *             @Override
 *             public void onError() {
 *                 toast("成功");
 *             }
 *         });
 *     }
 */
public class FingerDialogFragment extends DialogFragment {
    Dialog mDialog;
    TextView tv_msg;
    ImageView iv;

    private Callback mCallback;

    private FingerprintIdentify mFingerprintIdentify;

    private static final int MAX_AVAILABLE_TIMES = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mDialog == null) {
            mDialog = new Dialog(getActivity(), R.style.petgirls_dialog);
            mDialog.setContentView(R.layout.fragment_dialog_finger);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.getWindow().setGravity(Gravity.CENTER);
            mDialog.setCanceledOnTouchOutside(true);
            View view = mDialog.getWindow().getDecorView();
            tv_msg = (TextView) view.findViewById(R.id.tv_dialog_msg);
            iv = (ImageView) view.findViewById(R.id.iv);

            mFingerprintIdentify = new FingerprintIdentify(getActivity().getApplicationContext(), new BaseFingerprint.FingerprintIdentifyExceptionListener() {
                @Override
                public void onCatchException(Throwable exception) {
//                    Toast.makeText(getActivity(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            });
            if (mFingerprintIdentify != null && mFingerprintIdentify.isHardwareEnable()) {
                if (!mFingerprintIdentify.isRegisteredFingerprint()) {
                    Toast.makeText(getActivity(), "请先进入手机--设置，录入至少一个指纹", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            } else {
                Toast.makeText(getActivity(), "硬件不支持", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            start();
        }

        return mDialog;
    }


    public void setmFragmentCallBack(Callback mFragmentCallBack) {
        this.mCallback = mFragmentCallBack;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void start() {

        mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, new BaseFingerprint.FingerprintIdentifyListener() {
            @Override
            public void onSucceed() {
                if (mCallback != null) {
                    mCallback.onSuccess();
                }
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }

            @Override
            public void onNotMatch(int availableTimes) {
                tv_msg.setTextColor(getResources().getColor(R.color.color_FB544B));
                if (availableTimes == 0) {
                    tv_msg.setText("指纹验证失败");
                } else {
                    tv_msg.setText("指纹验证错误，请重试");
                }
                shake(iv);
                shake(tv_msg);
            }

            @Override
            public void onFailed(boolean isDeviceLocked) {
                if (isDeviceLocked) {
                    tv_msg.setText("指纹验证失败");
                } else {
                    tv_msg.setText("指纹验证失败");
                }

                tv_msg.setTextColor(getResources().getColor(R.color.color_FB544B));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                            mCallback.onError();
                        }
                    }
                }, 1000);
            }

            @Override
            public void onStartFailedByDeviceLocked() {
                tv_msg.setText("指纹验证太过频繁，请稍后重试");
                tv_msg.setTextColor(getResources().getColor(R.color.color_FB544B));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                            mCallback.onError();
                        }
                    }
                }, 5000);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintIdentify.cancelIdentify();
    }

    private void shake(View v) {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        v.startAnimation(shake);
    }


    public interface Callback {

        void onSuccess();

        void onError();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFingerprintIdentify.cancelIdentify();
        mCallback = null;
    }
}

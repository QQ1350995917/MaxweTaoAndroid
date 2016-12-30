package org.maxwe.tao.proxy.android.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.maxwe.tao.proxy.android.BaseActivity;
import org.maxwe.tao.proxy.android.R;
import org.maxwe.tao.proxy.android.utils.CellPhoneUtils;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Pengwei Ding on 2016-12-30 13:04.
 * Email: www.dingpengwei@foxmail.com www.dingpegnwei@gmail.com
 * Description: TODO
 */
@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {
    @ViewInject(R.id.et_act_register_cellphone)
    private EditText et_act_register_cellphone;
    @ViewInject(R.id.et_act_register_cellphone_code)
    private EditText et_act_register_cellphone_code;
    @ViewInject(R.id.et_act_register_password)
    private EditText et_act_register_password;
    @ViewInject(R.id.et_act_register_password_confirm)
    private EditText et_act_register_password_confirm;
    @ViewInject(R.id.bt_act_cellphone_code)
    private Button bt_act_cellphone_code;

    private String cellphoneOfGetCode = null;
    private int DELAY = 60;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what > 0) {
                RegisterActivity.this.bt_act_cellphone_code.setText(msg.what + RegisterActivity.this.getString(R.string.string_get_cellphone_code_delay));
            } else {
                RegisterActivity.this.bt_act_cellphone_code.setClickable(true);
                RegisterActivity.this.bt_act_cellphone_code.setText(RegisterActivity.this.getString(R.string.string_get_cellphone_code));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Event(value = R.id.bt_act_register_back, type = View.OnClickListener.class)
    private void onBackAction(View view) {
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.setResult(LoginActivity.RESPONSE_CODE_FAIL);
        this.finish();
    }

    @Event(value = R.id.bt_act_cellphone_code, type = View.OnClickListener.class)
    private void onCellphoneCodeAction(View view) {
        String cellphone = et_act_register_cellphone.getText().toString();
        if (TextUtils.isEmpty(cellphone) || !CellPhoneUtils.isCellphone(cellphone)) {
            Toast.makeText(this, this.getString(R.string.string_toast_cellphone), Toast.LENGTH_SHORT).show();
            return;
        }

        this.cellphoneOfGetCode = cellphone;

        this.bt_act_cellphone_code.setClickable(false);
        final int delay = DELAY;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = delay; i >= 0; i--) {
                    handler.sendEmptyMessage(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Event(value = R.id.bt_act_register, type = View.OnClickListener.class)
    private void onRegisterAction(View view) {
        String cellphone = et_act_register_cellphone.getText().toString();
        String cellphoneCode = et_act_register_cellphone_code.getText().toString();
        String password = et_act_register_password.getText().toString();
        String passwordConfirm = et_act_register_password_confirm.getText().toString();
        if (TextUtils.isEmpty(cellphone) || !CellPhoneUtils.isCellphone(cellphone)) {
            Toast.makeText(this, this.getString(R.string.string_toast_cellphone), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(cellphoneCode)) {
            Toast.makeText(this, this.getString(R.string.string_toast_cellphone_code), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.equals(cellphone, cellphoneOfGetCode)) {
            Toast.makeText(this, this.getString(R.string.string_toast_cellphone_different), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, this.getString(R.string.string_toast_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(this, this.getString(R.string.string_toast_password_confirm), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.equals(password, passwordConfirm)) {
            Toast.makeText(this, this.getString(R.string.string_toast_password_different), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("t","token");
        this.setResult(LoginActivity.RESPONSE_CODE_SUCCESS,intent);
        this.finish();
    }
}

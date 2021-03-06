package org.maxwe.tao.android.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.maxwe.tao.android.R;
import org.maxwe.tao.android.activity.BaseFragmentActivity;
import org.maxwe.tao.android.activity.ExistDialog;
import org.maxwe.tao.android.activity.LoginActivity;
import org.maxwe.tao.android.agent.AgentFragment;
import org.maxwe.tao.android.code.ActCodeFragment;
import org.maxwe.tao.android.mine.MineFragment;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.IOException;

/**
 * 默认显示已经被激活的状态，在访问状态下进行校验
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_PROXY = 0;
    private static final int REQUEST_CODE_TRADE = 1;

    private ActCodeFragment codeFragment;
    private Fragment agentFragment;
    private MineFragment mineFragment;

    @ViewInject(R.id.rg_act_navigate)
    private RadioGroup rg_act_navigate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int index = 0; index < this.rg_act_navigate.getChildCount(); index++) {
            this.rg_act_navigate.getChildAt(index).setOnClickListener(this);
        }
        this.setCurrentFragment(R.id.rb_act_main_active_code);
        this.onCheckNewVersion();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof RadioButton) {
            this.setCurrentFragment(v.getId());
        }
    }

    private void setCurrentFragment(int index) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        this.hideAllFragments(fragmentTransaction);
        switch (index) {
            case R.id.rb_act_main_active_code:
                if (this.codeFragment == null) {
                    this.codeFragment = new ActCodeFragment();
                    fragmentTransaction.add(R.id.fl_act_content, this.codeFragment);
                } else {
                    fragmentTransaction.show(this.codeFragment);
                    this.codeFragment.resetCodesStatus();
                }
                break;
            case R.id.rb_act_main_agent:
                if (this.agentFragment == null) {
                    this.agentFragment = new AgentFragment();
                    fragmentTransaction.add(R.id.fl_act_content, this.agentFragment);
                } else {
                    fragmentTransaction.show(this.agentFragment);
                }
                break;
            case R.id.rb_act_main_mine:
                if (this.mineFragment == null) {
                    this.mineFragment = new MineFragment();
                    fragmentTransaction.add(R.id.fl_act_content, this.mineFragment);
                } else {
                    fragmentTransaction.show(this.mineFragment);
                    this.mineFragment.resetCodesStatus();
                }
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideAllFragments(FragmentTransaction fragmentTransaction) {
        if (this.codeFragment != null) {
            fragmentTransaction.hide(this.codeFragment);
        }
        if (this.agentFragment != null) {
            fragmentTransaction.hide(this.agentFragment);
        }
        if (this.mineFragment != null) {
            fragmentTransaction.hide(this.mineFragment);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PROXY:
                if (resultCode == LoginActivity.RESPONSE_CODE_SUCCESS) {

                }
                break;
            case REQUEST_CODE_TRADE:
                if (resultCode == LoginActivity.RESPONSE_CODE_SUCCESS) {

                }
                break;
            default:
                break;
        }
    }

    protected int getVersionCode() {
        try {
            String packageName = this.getPackageName();
            int versionCode = this.getPackageManager().getPackageInfo(packageName, 0).versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ExistDialog.Builder builder = new ExistDialog.Builder(this);
            builder.setMessage("亲，确定退出吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    MainActivity.this.finish();
                }
            });

            builder.setNegativeButton("取消",
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

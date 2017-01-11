package org.maxwe.tao.android.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import org.maxwe.tao.android.BaseFragment;
import org.maxwe.tao.android.Constants;
import org.maxwe.tao.android.INetWorkManager;
import org.maxwe.tao.android.NetworkManager;
import org.maxwe.tao.android.R;
import org.maxwe.tao.android.account.model.SessionModel;
import org.maxwe.tao.android.activity.LoginActivity;
import org.maxwe.tao.android.activity.ModifyActivity;
import org.maxwe.tao.android.main.MainActivity;
import org.maxwe.tao.android.utils.SharedPreferencesUtils;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

/**
 * Created by Pengwei Ding on 2017-01-11 16:29.
 * Email: www.dingpengwei@foxmail.com www.dingpegnwei@gmail.com
 * Description: TODO
 */
@ContentView(R.layout.fragment_mine)
public class MineFragment extends BaseFragment {
    public static final int REQUEST_CODE_MODIFY_PASSWORD = 3;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_MODIFY_PASSWORD:
                if (resultCode == LoginActivity.RESPONSE_CODE_SUCCESS) {
                    onModifyPasswordSuccessCallback((SessionModel) data.getSerializableExtra(Constants.KEY_INTENT_SESSION));
                }
                break;
            default:
                break;
        }
    }

    @Event(value = R.id.bt_frg_mine_modify_password, type = View.OnClickListener.class)
    private void onModifyPasswordAction(View view) {
        Intent intent = new Intent(this.getContext(), ModifyActivity.class);
        this.startActivityForResult(intent, REQUEST_CODE_MODIFY_PASSWORD);
    }

    @Event(value = R.id.bt_frg_mine_exit, type = View.OnClickListener.class)
    private void onExitAction(View view) {
        SessionModel sessionModel = SharedPreferencesUtils.getSession(MineFragment.this.getContext());
        try {
            sessionModel.setSign(sessionModel.getEncryptSing());
            String url = this.getString(R.string.string_url_domain) + this.getString(R.string.string_url_account_logout);
            NetworkManager.requestByPost(url, sessionModel, new INetWorkManager.OnNetworkCallback() {
                @Override
                public void onSuccess(String result) {
                    SharedPreferencesUtils.clearSession(MineFragment.this.getContext());
                    Intent intent = new Intent(MineFragment.this.getContext(), LoginActivity.class);
                    MineFragment.this.getActivity().startActivity(intent);
                    MineFragment.this.getActivity().finish();
                }

                @Override
                public void onLoginTimeout(String result) {
                    SharedPreferencesUtils.clearSession(MineFragment.this.getContext());
                    Intent intent = new Intent(MineFragment.this.getContext(), LoginActivity.class);
                    MineFragment.this.getActivity().startActivity(intent);
                    MineFragment.this.getActivity().finish();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    SharedPreferencesUtils.clearSession(MineFragment.this.getContext());
                    Intent intent = new Intent(MineFragment.this.getContext(), LoginActivity.class);
                    MineFragment.this.getActivity().startActivity(intent);
                    MineFragment.this.getActivity().finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MineFragment.this.getContext(), "请求失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void onModifyPasswordSuccessCallback(SessionModel sessionModel) {
        SharedPreferencesUtils.saveSession(this.getContext(), sessionModel);
    }
}
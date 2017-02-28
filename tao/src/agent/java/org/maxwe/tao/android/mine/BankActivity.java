package org.maxwe.tao.android.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.maxwe.tao.android.Constants;
import org.maxwe.tao.android.INetWorkManager;
import org.maxwe.tao.android.NetworkManager;
import org.maxwe.tao.android.R;
import org.maxwe.tao.android.account.agent.AgentEntity;
import org.maxwe.tao.android.account.agent.AgentModel;
import org.maxwe.tao.android.account.agent.BankModel;
import org.maxwe.tao.android.account.model.TokenModel;
import org.maxwe.tao.android.activity.BaseActivity;
import org.maxwe.tao.android.response.IResponse;
import org.maxwe.tao.android.utils.SharedPreferencesUtils;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;

/**
 * Created by Pengwei Ding on 2017-01-14 15:39.
 * Email: www.dingpengwei@foxmail.com www.dingpegnwei@gmail.com
 * Description: TODO
 */
@ContentView(R.layout.activity_bank)
public class BankActivity extends BaseActivity {

    private AgentModel agentEntityModel;

    @ViewInject(R.id.tv_act_bank_no_data)
    private TextView tv_act_bank_no_data;

    @ViewInject(R.id.ll_act_bank_display)
    private LinearLayout ll_act_bank_display;
    @ViewInject(R.id.tv_act_bank_true_name)
    private TextView tv_act_bank_true_name;
    @ViewInject(R.id.tv_act_bank_zhifubao)
    private TextView tv_act_bank_zhifubao;

    @ViewInject(R.id.ll_act_bank_bind)
    private LinearLayout ll_act_bank_bind;
    @ViewInject(R.id.et_act_bank_zhifubao)
    private EditText et_act_bank_true_name;
    @ViewInject(R.id.et_act_bank_true_name)
    private EditText et_act_bank_zhifubao;
    @ViewInject(R.id.et_act_bank_password)
    private EditText et_act_bank_password;

    @ViewInject(R.id.bt_act_bank_action)
    private Button bt_act_bank_action;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Serializable serializableExtra = this.getIntent().getSerializableExtra(Constants.KEY_INTENT_AGENT);
        if (serializableExtra != null && serializableExtra instanceof AgentModel) {
            this.agentEntityModel = (AgentModel) serializableExtra;
            AgentEntity agentEntity = this.agentEntityModel.getAgentEntity();
            if (agentEntity != null && TextUtils.isEmpty(agentEntity.getZhifubao())) {
                showBindView();
            }
            if (agentEntity != null && !TextUtils.isEmpty(agentEntity.getZhifubao())) {
                showDisplayView(agentEntityModel.getAgentEntity());
            }
        }
    }

    private void showBindView() {
        this.tv_act_bank_no_data.setVisibility(View.GONE);
        this.ll_act_bank_display.setVisibility(View.GONE);
        this.ll_act_bank_bind.setVisibility(View.VISIBLE);
    }

    private void showDisplayView(AgentEntity agentEntity) {
        this.tv_act_bank_no_data.setVisibility(View.GONE);
        this.ll_act_bank_display.setVisibility(View.VISIBLE);
        this.ll_act_bank_bind.setVisibility(View.GONE);

        this.tv_act_bank_true_name.setText(this.getString(R.string.string_your_true_name) + agentEntity.getTrueName());
        this.tv_act_bank_zhifubao.setText(this.getString(R.string.string_your_zhifubao) + agentEntity.getZhifubao());
    }

    private void onResponseSuccess(BankModel bankModel) {
        this.tv_act_bank_no_data.setVisibility(View.GONE);
        this.ll_act_bank_display.setVisibility(View.VISIBLE);
        this.ll_act_bank_bind.setVisibility(View.GONE);

        AgentEntity agentEntity = new AgentEntity();
        agentEntity.setTrueName(bankModel.getTrueName());
        agentEntity.setZhifubao(bankModel.getZhifubao());
        this.agentEntityModel.getAgentEntity().setZhifubao(bankModel.getZhifubao());
        this.agentEntityModel.getAgentEntity().setTrueName(bankModel.getTrueName());

        this.tv_act_bank_true_name.setText(this.getString(R.string.string_your_true_name) + agentEntity.getTrueName());
        this.tv_act_bank_zhifubao.setText(this.getString(R.string.string_your_zhifubao) + agentEntity.getZhifubao());
    }

    private void onResponseError() {
        this.bt_act_bank_action.setClickable(true);
    }

    @Event(value = R.id.bt_act_bank_back, type = View.OnClickListener.class)
    private void onBackAction(View view) {
        this.onBackPressed();
    }

    @Event(value = R.id.bt_act_bank_action, type = View.OnClickListener.class)
    private void onBindZhiFuBaoAction(View view) {
        String trueName = this.et_act_bank_true_name.getText().toString();
        String zhifubao = this.et_act_bank_zhifubao.getText().toString();
        String password = this.et_act_bank_password.getText().toString();
        if (TextUtils.isEmpty(trueName)) {
            Toast.makeText(this, R.string.string_my_true_name, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(zhifubao)) {
            Toast.makeText(this, R.string.string_my_zhifubao, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6 || password.length() > 12) {
            Toast.makeText(this, R.string.string_input_account_password, Toast.LENGTH_SHORT).show();
            return;
        }

        this.bt_act_bank_action.setClickable(false);
        try {
            TokenModel session = SharedPreferencesUtils.getSession(this);
            BankModel bankModel = new BankModel(session, trueName, zhifubao);
            bankModel.setVerification(password);
            bankModel.setSign(session.getEncryptSing());
            String url = this.getString(R.string.string_url_domain) + this.getString(R.string.string_url_account_bank);
            NetworkManager.requestByPost(url, bankModel, new INetWorkManager.OnNetworkCallback() {
                @Override
                public void onSuccess(String result) {
                    BankModel responseModel = JSON.parseObject(result, BankModel.class);
                    onResponseSuccess(responseModel);
                }

                @Override
                public void onLoginTimeout(String result) {
                    onResponseError();
                    SharedPreferencesUtils.clearSession(BankActivity.this);
                    Toast.makeText(BankActivity.this, R.string.string_toast_timeout, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    onResponseError();
                    Toast.makeText(BankActivity.this, R.string.string_toast_network_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onOther(int code, String result) {
                    if (code == IResponse.ResultCode.RC_ACCESS_BAD_2.getCode()) {
                        Toast.makeText(BankActivity.this, R.string.string_bank_error, Toast.LENGTH_SHORT).show();
                    }
                    onResponseError();
                }
            });
        } catch (Exception e) {
            this.bt_act_bank_action.setClickable(true);
            e.printStackTrace();
            Toast.makeText(this, "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
}

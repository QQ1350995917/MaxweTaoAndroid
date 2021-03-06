package org.maxwe.tao.android.account.user;


import org.maxwe.tao.android.account.model.AuthenticateModel;
import org.maxwe.tao.android.account.model.TokenModel;
import org.maxwe.tao.android.utils.StringUtils;

/**
 * Created by Pengwei Ding on 2017-03-07 17:29.
 * Email: www.dingpengwei@foxmail.com www.dingpegnwei@gmail.com
 * Description: 用户激活请求模型
 */
public class UserActiveRequestModel extends AuthenticateModel {
    private String actCode;

    public UserActiveRequestModel() {
        super();
    }

    public UserActiveRequestModel(TokenModel tokenModel) {
        super(tokenModel);
    }

    public UserActiveRequestModel(TokenModel tokenModel, String authenticatePassword,String actCode) {
        super(tokenModel, authenticatePassword);
        this.actCode = actCode;
    }

    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public boolean isUserActiveRequestParamsOk(){
        if (StringUtils.isEmpty(actCode)){
            return false;
        }
        if (this.getActCode().length() != 8){
            return false;
        }
        return super.isAuthenticateParamsOk();
    }
}

package org.maxwe.tao.android.account.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.apache.commons.codec.binary.Base64;
import org.maxwe.tao.android.utils.CellPhoneUtils;
import org.maxwe.tao.android.utils.CryptionUtils;
import org.maxwe.tao.android.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by Pengwei Ding on 2017-01-09 18:26.
 * Email: www.dingpengwei@foxmail.com www.dingpegnwei@gmail.com
 * Description: token模型
 */
public class TokenModel implements Serializable {
    private String t;//token字符串
    private int id;//用户ID
    private String cellphone;//电话号码
    private int apt; // 登录类型,在内存中标记token的类型
    private String sign;

    public TokenModel() {
        super();
    }

    public TokenModel(TokenModel tokenModel) {
        this.t = tokenModel.getT();
        this.id = tokenModel.getId();
        this.cellphone = tokenModel.getCellphone();
        this.apt = tokenModel.getApt();
    }

    public TokenModel(String t, int id, String cellphone, int apt) {
        this.t = t;
        this.id = id;
        this.cellphone = cellphone;
        this.apt = apt;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public int getApt() {
        return apt;
    }

    public void setApt(int apt) {
        this.apt = apt;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "SessionModel{" +
                ", id='" + id + '\'' +
                ", cellphone='" + cellphone + '\'' +
                ", apt=" + apt +
                '}';
    }

    @JSONField(serialize = false)
    public String getEncryptSing() throws Exception {
        if (this.getId() == 0 || !CellPhoneUtils.isCellphone(this.getCellphone())) {
            return null;
        }
        String password = (this.getCellphone() + new StringBuffer(this.getCellphone()).reverse()).substring(1, 17);//生成的ID是11位，补全16位密码
        String content = this.getId() + "-" + System.currentTimeMillis() + "-" + this.getCellphone();
        String encodeContent = new String(Base64.encodeBase64(content.getBytes()));
        byte[] encryptResult = CryptionUtils.encryptCustomer(encodeContent, password);
        String encryptResultStr = CryptionUtils.parseByte2HexStr(encryptResult);
        return encryptResultStr;
    }

    public boolean isCellphoneParamsOk() {
        if (!CellPhoneUtils.isCellphone(this.getCellphone())) {
            return false;
        }
        return true;
    }

    @JSONField(serialize = false)
    public boolean isDecryptSignOK() throws Exception {
        String password = (this.getCellphone() + new StringBuffer(this.getCellphone()).reverse()).substring(1, 17);//生成的ID是11位，补全16位密码
        byte[] decryptResult = CryptionUtils.decryptCustomer(CryptionUtils.parseHexStr2Byte(this.getSign()), password);
        byte[] decode = Base64.decodeBase64(decryptResult);
        String[] split = new String(decode).split("-");
        if (split == null || split.length != 3) {
            return false;
        }
        if (StringUtils.equals(split[0], this.getId() + "")
                && System.currentTimeMillis() - Long.parseLong(split[1]) < 60 * 1000
                && StringUtils.equals(split[2], this.getCellphone())
                ) {
            return true;
        }
        return false;
    }

    @JSONField(serialize = false)
    public boolean isTokenParamsOk() {
        if (!StringUtils.isEmpty(this.getT())
                && this.getId() > 0
                && CellPhoneUtils.isCellphone(this.getCellphone())
                && !StringUtils.isEmpty(this.getSign())
                && (this.getApt() == 1 || this.getApt() == 2)) {
            return true;
        } else {
            return false;
        }
    }
}

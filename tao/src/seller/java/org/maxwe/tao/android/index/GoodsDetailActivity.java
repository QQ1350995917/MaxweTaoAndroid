package org.maxwe.tao.android.index;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.maxwe.tao.android.INetWorkManager;
import org.maxwe.tao.android.NetworkManager;
import org.maxwe.tao.android.R;
import org.maxwe.tao.android.account.model.SessionModel;
import org.maxwe.tao.android.author.AuthorActivity;
import org.maxwe.tao.android.activity.BaseActivity;
import org.maxwe.tao.android.author.BrandActivity;
import org.maxwe.tao.android.api.Position;
import org.maxwe.tao.android.goods.GoodsConvertResponseModel;
import org.maxwe.tao.android.goods.GoodsEntity;
import org.maxwe.tao.android.goods.TaoConvertRequestModel;
import org.maxwe.tao.android.goods.TaoConvertResponseModel;
import org.maxwe.tao.android.goods.TaoPwdRequestModel;
import org.maxwe.tao.android.utils.SharedPreferencesUtils;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;

/**
 * Created by Pengwei Ding on 2017-02-12 13:03.
 * Email: www.dingpengwei@foxmail.com www.dingpegnwei@gmail.com
 * Description: TODO
 */
@ContentView(R.layout.activity_goods_detail)
public class GoodsDetailActivity extends BaseActivity {
    public static final String KEY_GOODS = "goodsEntity";
    private static final int CODE_REQUEST_AUTHOR = 0;
    private static final int CODE_REQUEST_BRAND = 1;
    @ViewInject(R.id.iv_act_goods_detail_image)
    private SimpleDraweeView iv_act_goods_detail_image;
    @ViewInject(R.id.tv_act_goods_detail_title)
    private TextView tv_act_goods_detail_title;
    @ViewInject(R.id.tv_act_goods_detail_brokerage)
    private TextView tv_act_goods_detail_brokerage;
    @ViewInject(R.id.tv_act_goods_detail_brokerage_got)
    private TextView tv_act_goods_detail_brokerage_got;

    @ViewInject(R.id.iv_act_goods_detail_coupon_container)
    private RelativeLayout iv_act_goods_detail_coupon_container;
    @ViewInject(R.id.iv_act_goods_detail_coupon_price)
    private TextView iv_act_goods_detail_coupon_price;

    @ViewInject(R.id.tv_act_goods_detail_final_price)
    private TextView tv_act_goods_detail_final_price;
    @ViewInject(R.id.tv_act_goods_detail_sale)
    private TextView tv_act_goods_detail_sale;

    @ViewInject(R.id.tv_act_goods_detail_coupon_counter)
    private TextView tv_act_goods_detail_coupon_counter;

    @ViewInject(R.id.tv_act_goods_detail_coupon_get)
    private TextView tv_act_goods_detail_coupon_get;

    @ViewInject(R.id.tv_act_goods_detail_coupon_left)
    private TextView tv_act_goods_detail_coupon_left;

    @ViewInject(R.id.tv_act_goods_detail_coupon_time_line)
    private TextView tv_act_goods_detail_coupon_time_line;

    @ViewInject(R.id.bt_act_goods_detail_get_link)
    private Button bt_act_goods_detail_get_link;
    @ViewInject(R.id.tv_act_goods_detail_get_link_result)
    private TextView tv_act_goods_detail_get_link_result;
    @ViewInject(R.id.ll_act_goods_detail_space_holder)
    private LinearLayout ll_act_goods_detail_space_holder;
    @ViewInject(R.id.ll_act_goods_detail_action_holder)
    private LinearLayout ll_act_goods_detail_action_holder;

    private GoodsEntity goodsEntity = null;
    private TaoPwdEntity taoPwdEntity = null;


    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(GoodsDetailActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(GoodsDetailActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(GoodsDetailActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.goodsEntity = (GoodsEntity) this.getIntent().getExtras().get(KEY_GOODS);
        this.iv_act_goods_detail_image.setImageURI(Uri.parse(goodsEntity.getPict_url()));
        this.tv_act_goods_detail_title.setText(goodsEntity.getTitle());
        this.tv_act_goods_detail_brokerage.setText(goodsEntity.getCommission_rate() + "%");
        this.tv_act_goods_detail_brokerage_got.setText("赚" + new DecimalFormat("###.00").format(Float.parseFloat(goodsEntity.getZk_final_price()) * Float.parseFloat(goodsEntity.getCommission_rate()) / 100) + "元");
        if (Long.parseLong(goodsEntity.getCoupon_info()) > 0){
            iv_act_goods_detail_coupon_container.setVisibility(View.VISIBLE);
            iv_act_goods_detail_coupon_price.setText(Long.parseLong(goodsEntity.getCoupon_info()) + "元");
        }
        this.tv_act_goods_detail_final_price.setText("￥" + goodsEntity.getZk_final_price());
        this.tv_act_goods_detail_sale.setText("月销:" + goodsEntity.getVolume());
        this.tv_act_goods_detail_coupon_counter.setText(goodsEntity.getCoupon_total_count() + "");
        this.tv_act_goods_detail_coupon_get.setText((goodsEntity.getCoupon_total_count() - goodsEntity.getCoupon_remain_cou()) + "");
        this.tv_act_goods_detail_coupon_left.setText(goodsEntity.getCoupon_remain_cou() + "");
        this.tv_act_goods_detail_coupon_time_line.setText(goodsEntity.getCoupon_end_time());
    }

    @Event(value = R.id.bt_act_goods_detail_get_link, type = View.OnClickListener.class)
    private void onTaoPwdAction(View view) {
        AuthorActivity.requestTaoLoginStatus(this, new AuthorActivity.TaoLoginStatusCallback() {
            @Override
            public void onNeedLoginCallback() {
                Intent intent = new Intent(GoodsDetailActivity.this, AuthorActivity.class);
                intent.putExtra(AuthorActivity.KEY_INTENT_OF_STATE_CODE, 1234);
                GoodsDetailActivity.this.startActivityForResult(intent, GoodsDetailActivity.this.CODE_REQUEST_AUTHOR);
            }

            @Override
            public void onNeedBrandCallback() {
                Toast.makeText(GoodsDetailActivity.this, "请选择推广位", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GoodsDetailActivity.this, BrandActivity.class);
                GoodsDetailActivity.this.startActivityForResult(intent, GoodsDetailActivity.this.CODE_REQUEST_BRAND);
            }

            @Override
            public void onNeedOkCallback() {
                requestTaoPwd();
            }

            @Override
            public void onNeedErrorCallback() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.CODE_REQUEST_AUTHOR) {
            if (resultCode == AuthorActivity.CODE_RESULT_OF_AUTHOR_SUCCESS) {
                onTaoPwdAction(null);
            } else {
                Toast.makeText(GoodsDetailActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == this.CODE_REQUEST_BRAND) {
            if (resultCode == BrandActivity.CODE_RESULT_SUCCESS) {
                requestTaoPwd();
            } else if (resultCode == BrandActivity.CODE_RESULT_FAIL) {
                Toast.makeText(GoodsDetailActivity.this, "您尚未请选择推广位,请您选择推广位", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }





    @Event(value = R.id.bt_act_goods_detail_back, type = View.OnClickListener.class)
    private void onModifyBackAction(View view) {
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    private void onRequestTaoPwd() {
        this.bt_act_goods_detail_get_link.setClickable(false);
    }

    private void onResponseTaoPwdError() {
        this.bt_act_goods_detail_get_link.setClickable(true);
    }

    /**
     * 长按复制淘口令
     * @param view
     * @return
     */
    @Event(value = R.id.tv_act_goods_detail_get_link_result, type = View.OnLongClickListener.class)
    private boolean onLinkPwdCopyAction(View view) {
        ClipboardManager cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        if (view.getTag() != null) {
            cm.setText(tv_act_goods_detail_get_link_result.getTag().toString());
            Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * 复制全部文案
     * @param view
     * @return
     */
    @Event(value = R.id.bt_act_goods_detail_copy, type = View.OnClickListener.class)
    private void onCopyAction(View view) {
        ClipboardManager cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(tv_act_goods_detail_get_link_result.getText());
        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
        intent.setData(Uri.parse(this.goodsEntity.getCoupon_click_url())); //为Intent设置DATA属性
        startActivity(intent);
    }


    /**
     * 分享
     * @param view
     * @return
     */
    @Event(value = R.id.bt_act_goods_detail_share, type = View.OnClickListener.class)
    private void onShareAction(View view) {
        new ShareAction(GoodsDetailActivity.this).withTitle(BaseActivity.getEMOJIStringByUnicode(0x1F4E7))
                .withText(tv_act_goods_detail_get_link_result.getText().toString())
                .withTargetUrl(this.goodsEntity.getItem_url())
                .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                .setCallback(umShareListener).open();
    }

    private void onResponseTaoConvertSuccess(GoodsConvertResponseModel goodsConvertResponseModel) {
        this.bt_act_goods_detail_get_link.setClickable(false);
        this.bt_act_goods_detail_get_link.setVisibility(View.GONE);
        this.tv_act_goods_detail_get_link_result.setVisibility(View.VISIBLE);
        this.ll_act_goods_detail_space_holder.setVisibility(View.VISIBLE);
        this.ll_act_goods_detail_action_holder.setVisibility(View.VISIBLE);

        if (goodsConvertResponseModel != null) {
            TaoConvertResponseModel convert = goodsConvertResponseModel.getConvert();
            GoodsEntity goodsEntity = goodsConvertResponseModel.getGoodsEntity();
            if (convert != null && goodsEntity != null) {
                if (!TextUtils.isEmpty(convert.getShortLinkUrl()) &&
                        !TextUtils.isEmpty(convert.getTaoToken())) {
                    String copyText =
                            BaseActivity.getEMOJIStringByUnicode(0x270C) + goodsEntity.getTitle() + "\r\n" +
                                    "【领券】" + this.goodsEntity.getCoupon_click_url() + "\r\n" +
                                    "【价格】￥" + goodsEntity.getZk_final_price() + "\r\n" +
                                    BaseActivity.getEMOJIStringByUnicode(0x1F446) + "长按复制后打开" +
                                    BaseActivity.getEMOJIStringByUnicode(0x1F4F1) + "淘宝" +
                                    BaseActivity.getEMOJIStringByUnicode(0x1F449) +
                                    convert.getTaoToken() +
                                    BaseActivity.getEMOJIStringByUnicode(0x1F448) + "\r\n";
                    tv_act_goods_detail_get_link_result.setText(copyText);
                    tv_act_goods_detail_get_link_result.setTag(convert.getTaoToken());
                } else {
                    tv_act_goods_detail_get_link_result.setText("该链接不被支持或已经下架");
                }
            } else {
                tv_act_goods_detail_get_link_result.setText("该链接不被支持或已经下架");
            }
        } else {
            tv_act_goods_detail_get_link_result.setText("该链接不被支持或已经下架");
        }
    }

    private void requestTaoPwd() {
        onRequestTaoPwd();

        Position currentPP = SharedPreferencesUtils.getCurrentPP(this);
        TaoConvertRequestModel taoConvertRequestModel = new TaoConvertRequestModel();

        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie(AuthorActivity.URL_LOGIN_MESSAGE);

        taoConvertRequestModel.setCookie(cookie);
        taoConvertRequestModel.setSiteid(currentPP.getSiteId());
        taoConvertRequestModel.setAdzoneid(currentPP.getId());
        taoConvertRequestModel.setPromotionURL(goodsEntity.getItem_url());

        String url = this.getString(R.string.string_url_domain) + this.getString(R.string.string_url_tao_convert);
        SessionModel sessionModel = SharedPreferencesUtils.getSession(this);
        taoConvertRequestModel.setT(sessionModel.getT());
        taoConvertRequestModel.setMark(sessionModel.getMark());
        taoConvertRequestModel.setCellphone(sessionModel.getCellphone());
        taoConvertRequestModel.setApt(this.getResources().getInteger(R.integer.integer_app_type));
        try {
            taoConvertRequestModel.setSign(sessionModel.getEncryptSing());
            NetworkManager.requestByPost(url, taoConvertRequestModel, new INetWorkManager.OnNetworkCallback() {
                @Override
                public void onSuccess(String result) {
                    GoodsConvertResponseModel goodsConvertResponseModel = JSON.parseObject(result, GoodsConvertResponseModel.class);
                    onResponseTaoConvertSuccess(goodsConvertResponseModel);
                }

                @Override
                public void onLoginTimeout(String result) {
                    SharedPreferencesUtils.clearSession(GoodsDetailActivity.this);
                    SharedPreferencesUtils.clearAuthor(GoodsDetailActivity.this);
                    Toast.makeText(GoodsDetailActivity.this, R.string.string_toast_timeout, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onEmptyResult(String result) {
                    super.onEmptyResult(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(GoodsDetailActivity.this, R.string.string_toast_network_error, Toast.LENGTH_SHORT).show();
                    onResponseTaoPwdError();
                }

                @Override
                public void onOther(int code, String result) {
                    Toast.makeText(GoodsDetailActivity.this, R.string.string_toast_reset_password_error, Toast.LENGTH_SHORT).show();
                    onResponseTaoPwdError();
                }
            });
        } catch (Exception e) {
            onResponseTaoPwdError();
            Toast.makeText(GoodsDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

//        String currentKeeperId = SharedPreferencesUtils.getCurrentKeeperId(this);
//        Position currentPP = SharedPreferencesUtils.getCurrentPP(this);
//        TaoPwdRequestModel taoPwdRequestModel = new TaoPwdRequestModel();
//
//        taoPwdRequestModel.setText(goodsEntity.getTitle());
//        taoPwdRequestModel.setUrl(goodsEntity.getItem_url() + "&pid=mm_" + currentKeeperId + "_" + currentPP.getSiteId() + "_" + currentPP.getId());
//        taoPwdRequestModel.setUser_id(Long.parseLong(currentKeeperId));
//
//        String url = this.getString(R.string.string_url_domain) + this.getString(R.string.string_url_tao_pwd);
//        SessionModel sessionModel = SharedPreferencesUtils.getSession(this);
//        taoPwdRequestModel.setT(sessionModel.getT());
//        taoPwdRequestModel.setMark(sessionModel.getMark());
//        taoPwdRequestModel.setCellphone(sessionModel.getCellphone());
//        taoPwdRequestModel.setApt(this.getResources().getInteger(R.integer.integer_app_type));
//        try {
//            taoPwdRequestModel.setSign(sessionModel.getEncryptSing());
//            NetworkManager.requestByPost(url, taoPwdRequestModel, new INetWorkManager.OnNetworkCallback() {
//                @Override
//                public void onSuccess(String result) {
//                    onResponseTaoPwdSuccess(result);
//                }
//
//                @Override
//                public void onLoginTimeout(String result) {
//                    SharedPreferencesUtils.clearSession(GoodsDetailActivity.this);
//                    SharedPreferencesUtils.clearAuthor(GoodsDetailActivity.this);
//                    Toast.makeText(GoodsDetailActivity.this, R.string.string_toast_timeout, Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onError(Throwable ex, boolean isOnCallback) {
//                    Toast.makeText(GoodsDetailActivity.this, R.string.string_toast_network_error, Toast.LENGTH_SHORT).show();
//                    onResponseTaoPwdError();
//                }
//
//                @Override
//                public void onOther(int code, String result) {
//                    Toast.makeText(GoodsDetailActivity.this, R.string.string_toast_reset_password_error, Toast.LENGTH_SHORT).show();
//                    onResponseTaoPwdError();
//                }
//            });
//        } catch (Exception e) {
//            onResponseTaoPwdError();
//            Toast.makeText(this, "请求失败", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
    }


    private Bitmap drawableToBitamp(Drawable drawable) {
        Bitmap bitmap = null;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

}
package com.daqsoft.thetravelcloudwithculture.home.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.ToastUtils;
import com.daqsoft.provider.MainARouterPath;
import com.daqsoft.thetravelcloudwithculture.R;

/**
 * Created by Wolf on 2019/11/21.
 * Describe:设置选项
 */
public class HomeButtomView extends RelativeLayout {

    private  boolean canClick=true;
    private    LinearLayout   ll_roots;

    public static  final  String   LINK_WB="https://m.weibo.cn/status/4504793157337227?";
    public static  final  String   LINK_WX="https://mp.weixin.qq.com/s/Q1pD3Tedg27cYt5fkOsuQA";
    public static  final  String   LINK_XJ="https://mp.weixin.qq.com/s/72vCzMl1pMFpJMaW85R2OA";
    public static  final  String   LINK_DY="https://www.iesdouyin.com/share/video/6874455152003190029/?region=CN&mid=6874455151617641224&u_code=2jb8gfb4c35d&titleType=title&timestamp=1601029293&utm_campaign=client_share&app=aweme&utm_medium=ios&tt_from=copy&utm_source=copy";
    public static  final  String   LINK_KS="https://c.kuaishou.com/fw/photo/3x9hptkiiawdxyk?fid=1839524343&cc=share_copylink&followRefer=151&shareMethod=TOKEN&docId=0&kpn=KUAISHOU&subBiz=PHOTO&photoId=3x9hptkiiawdxyk&shareId=263892819867&shareToken=X70XSNUWC96R2kN_A&shareResourceType=PHOTO_SELF&userId=3xrb7p49gadxe4k&shareType=2&et=1_i%2F2000113983721029313_f85&shareMode=APP&groupName=&originShareId=263892819867&appType=1&shareObjectId=36688114611&shareUrlOpened=0&timestamp=1601176996689";

    public HomeButtomView(Context context) {
        this(context, null);
    }

    public HomeButtomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeButtomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_sheet_view,this);
        ll_roots = view.findViewById(R.id.ll_roots);
        initView(view);
    }


    private void initView(View view) {
        LinearLayout ll_Root = view.findViewById(R.id.ll_roots);
        for (int i = 0; i < ll_Root.getChildCount(); i++) {
            int finalI = i;
            ll_Root.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(canClick){
                        onClickPosition(finalI);
                    }
                }
            });

        }

    }
    private void onClickPosition(int i) {
        switch (i){
            case 0:
                goUrl("sinaweibo://userinfo?nick=乌鲁木齐市文旅局");
                break;
            case 1:
//                Toast.makeText(BaseApplication.context, "1.搜索公众号“乌鲁木齐文旅局”；2.点击“关注公众号”", Toast.LENGTH_SHORT).show();
//                gpWeiXin();
                goWebView(LINK_WX,"官方微信");
                break;
            case 2:
//                Toast.makeText(BaseApplication.context, "1.搜索公众号“一域游新疆”；2.点击“关注公众号”", Toast.LENGTH_SHORT).show();
//                gpWeiXin();
                goWebView(LINK_XJ,"一城游新疆");
                break;
            case 3:
                goUrl("snssdk1128://user/profile/2713219165857869");
                break;
            case 4:
                goUrl("kwai://profile/1839524343");
                break;

        }
    }
//    private void onClickPosition1(int i) {
//        switch (i){
//            case 0:
//                goWebView(LINK_WB,"官方微博");
//                break;
//            case 1:
//                goWebView(LINK_WX,"官方微信");
//                break;
//            case 2:
//                goWebView(LINK_XJ,"一域游新疆");
//                break;
//            case 3:
//                goWebView(LINK_DY,"官方抖音");
//                break;
//            case 4:
//                goWebView(LINK_KS,"官方快手");
//                break;
//
//        }
//    }


    public void  gpWeiXin(){
        try {
            Intent intent = new Intent(BaseApplication.context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
            BaseApplication.context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(BaseApplication.context, "请先安装微信APP", Toast.LENGTH_SHORT).show();
        }
    }
    public void  goUrl(String uri){
        try {
            Uri result = Uri.parse(uri);
            Intent intent = new Intent(Intent.ACTION_VIEW, result);
            getContext().startActivity(intent);
        } catch (Exception e) {
            //sinaweibo://userinfo?nick=
            if (uri.startsWith("sinaweibo://")) {
                Toast.makeText( BaseApplication.context, "请先安装新浪微博APP", Toast.LENGTH_SHORT).show();
            } else if (uri.startsWith("snssdk1128://")) {
                //snssdk1128://user/profile/2713219165857869
                Toast.makeText( BaseApplication.context, "请先安装抖音APP", Toast.LENGTH_SHORT).show();
            } else if (uri.startsWith("kwai://")) {
                //kwai://profile/1839524343
                Toast.makeText( BaseApplication.context, "请先安装快手APP", Toast.LENGTH_SHORT).show();
            } else if (uri.startsWith("snssdk143://")) {
                Toast.makeText( BaseApplication.context, "请先安装今日头条APP", Toast.LENGTH_SHORT).show();
            } else if (uri.startsWith("cover://cn.thecover.www/")) {
                Toast.makeText( BaseApplication.context, "请先安装封面新闻APP", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void   goWebView(String url,String title){
        ARouter.getInstance()
                .build(MainARouterPath.MAIN_MY_WEB)
                .withString("title", title)
                .withString("url", url)
                .navigation();
    }

    public void setCanClick(boolean canClick) {
        if(ll_roots==null){
            return;
        }
        if(canClick){
            ll_roots.setVisibility(VISIBLE);
        }else {
            ll_roots.setVisibility(GONE);
        }
    }
}

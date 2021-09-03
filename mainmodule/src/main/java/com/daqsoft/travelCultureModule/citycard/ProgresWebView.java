package com.daqsoft.travelCultureModule.citycard;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.daqsoft.mainmodule.R;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ProgresWebView extends WebView {
    private Context context;
    private ProgressBar progressView;//进度条
    public ProgresWebView(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public ProgresWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    public ProgresWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initView();
    }
    private  void initView(){
        progressView=new ProgressBar(context);
        progressView.setIndeterminate(false);
        BeanUtils.setFieldValue(progressView, "mOnlyIndeterminate", new Boolean(false));
        progressView.setIndeterminate(false);
        progressView.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_shape));
        progressView.setIndeterminateDrawable(getResources().getDrawable(android.R.drawable.progress_indeterminate_horizontal));
        progressView.setMinimumHeight(20);
        progressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, 4)));
        progressView.setMax(100);
        progressView.setIndeterminate(false);
        addView(progressView);
        initWebViewSettings();
        setWebChromeClient(new MyWebCromeClient());

    }
    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setSavePassword(false);

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
    }
    private class MyWebCromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                //加载完毕进度条消失
                progressView.setVisibility(View.GONE);
            } else {
                //更新进度
                progressView.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }
    /**
     * dp转换成px
     *
     * @param context Context
     * @param dp      dp
     * @return px值
     */
    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    public static class BeanUtils {
        private BeanUtils() {
        }
        /**
         * 直接设置对象属性值,无视private/protected修饰符,不经过setter函数.
         */
        public static void setFieldValue(final Object object, final String fieldName, final Object value) {
            Field field = getDeclaredField(object, fieldName);
            if (field == null)
                throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
            makeAccessible(field);
            try {
                field.set(object, value);
            } catch (IllegalAccessException e) {
                Log.e("zbkc", "", e);
            }
        }
        /**
         * 循环向上转型,获取对象的DeclaredField.
         */
        protected static Field getDeclaredField(final Object object, final String fieldName) {
            return getDeclaredField(object.getClass(), fieldName);
        }
        /**
         * 循环向上转型,获取类的DeclaredField.
         */
        @SuppressWarnings("unchecked")
        protected static Field getDeclaredField(final Class clazz, final String fieldName) {
            for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
                try {
                    return superClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    // Field不在当前类定义,继续向上转型
                }
            }
            return null;
        }
        /**
         * 强制转换fileld可访问.
         */
        protected static void makeAccessible(Field field) {
            if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
                field.setAccessible(true);
            }
        }
    }

}

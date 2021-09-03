package com.daqsoft.provider.view.web;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.provider.R;


public class MWebChromeClient extends WebChromeClient {

    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean dialog,
                                  boolean userGesture, Message resultMsg) {
        return super.onCreateWindow(view, dialog, userGesture, resultMsg);
    }

    /**
     * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message,
                             final JsResult result) {

        Toast.makeText(BaseApplication.context,message,Toast.LENGTH_SHORT).show();
        result.cancel();
        return true;
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url,
                                    String message, JsResult result) {
        return super.onJsBeforeUnload(view, url, message, result);
    }

    /**
     * 覆盖默认的window.confirm展示界面，避免title里显示为“：来自file:////”
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message,
                               final JsResult result) {
        final AlertDialog dialog = new AlertDialog.Builder(BaseApplication.context).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_wap);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView tvDescription = (TextView) window.findViewById(R.id.tv_dialog_wap_content);
        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_wap_cancel);
        Button btnSure = (Button) window.findViewById(R.id.btn_dialog_wap_sure);
        tvDescription.setText(message);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                result.cancel();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                result.confirm();
            }
        });
        return true;
    }

    /**
     * 覆盖默认的window.prompt展示界面，避免title里显示为“：来自file:////”
     * window.prompt('请输入您的域名地址', '618119.com');
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message,
                              String defaultValue, final JsPromptResult result) {
        final AlertDialog dialog = new AlertDialog.Builder(BaseApplication.context).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_wap);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView tvDescription = (TextView) window.findViewById(R.id.tv_dialog_wap_content);
        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_wap_cancel);
        Button btnSure = (Button) window.findViewById(R.id.btn_dialog_wap_sure);
        tvDescription.setText(message);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                result.cancel();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                result.confirm();
            }
        });
        return true;
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onRequestFocus(WebView view) {
        super.onRequestFocus(view);
    }

    private OpenFileChooserCallBack mOpenFileChooserCallBack;

    public MWebChromeClient() {
        super();
    }

    public MWebChromeClient(OpenFileChooserCallBack openFileChooserCallBack) {
        mOpenFileChooserCallBack = openFileChooserCallBack;
    }


    //For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mOpenFileChooserCallBack.openFileChooserCallBack(uploadMsg, acceptType);
    }


    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }


    // For Android  > 4.1.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }


    // For Android > 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams
            fileChooserParams) {
        mOpenFileChooserCallBack.openFileChooserCallBack5(uploadMsg, "");
        //openFileChooser(uploadMsg, "");
        return true;
    }


    public interface OpenFileChooserCallBack {
        void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType);

        void openFileChooserCallBack5(ValueCallback<Uri[]> uploadMsg, String acceptType);
    }


}

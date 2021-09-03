/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.daqsoft.provider.zxing;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.alibaba.android.arouter.launcher.ARouter;
import com.daqsoft.baselib.utils.AppUtils;
import com.daqsoft.baselib.utils.StringUtil;
import com.daqsoft.baselib.utils.ToastUtils;
import com.daqsoft.provider.ARouterPath;
import com.daqsoft.provider.R;
import com.daqsoft.provider.zxing.camera.CameraManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

    private final CaptureActivity activity;
    private final DecodeThread decodeThread;
    private State state;
    private final CameraManager cameraManager;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    CaptureActivityHandler(CaptureActivity activity,
                           Collection<BarcodeFormat> decodeFormats,
                           Map<DecodeHintType, ?> baseHints,
                           String characterSet,
                           CameraManager cameraManager) {
        this.activity = activity;
        decodeThread = new DecodeThread(activity, decodeFormats, baseHints, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS;
            activity.handleDecode((Result) message.obj);
            if (message.obj != null) {
                Result data = (Result) message.obj;

                Map<String, String> map = StringUtil.INSTANCE.getUrlPramNameAndValue(data.getText());
                if (map == null || map.isEmpty()) {
                    dealResult(data);
                } else {
                    if (map.containsKey("operation") && map.containsKey("resourceType") && map.containsKey("resourceId")) {
                        if (map.get("operation").equals("writerOff")) {
                            if (AppUtils.INSTANCE.isLogin()) {
                                activity.getWriterOffList(map.get("resourceType"), map.get("resourceId"));
                            } else {
                                ToastUtils.showUnLoginMsg();
                                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                        .navigation();
                            }
                        } else {
                            dealResult(data);
                        }

                    } else {
                        dealResult(data);
                    }
                }
            }

        } else if (message.what == R.id.decode_failed) {// We're decoding as fast as possible, so when one decode fails, start another.
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        } else if (message.what == R.id.return_scan_result) {

//            activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
//            activity.finish();
        }
    }

    private void dealResult(Result data) {
        String content = data.getText();
        if (content != null && content.startsWith("http")) {
            ARouter.getInstance()
                    .build(ARouterPath.Provider.WEB_ACTIVITY)
                    .withString("mTitle", "")
                    .withString("html", content)
                    .navigation();
        } else {
            ToastUtils.showMessage("" + data.getText());
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    restartPreviewAndDecode();
                } catch (Exception e) {

                }
            }
        }, 5000);
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            activity.drawViewfinder();
        }
    }

}

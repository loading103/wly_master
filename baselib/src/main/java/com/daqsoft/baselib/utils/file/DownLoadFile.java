package com.daqsoft.baselib.utils.file;

import android.os.Environment;
import android.util.Log;

import com.daqsoft.baselib.utils.SPUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;


public class DownLoadFile {

    public static String Video_PATH= Environment.getExternalStorageDirectory() + "/daqsoft/";
    public DownLoadFile() {
        File file = new File(Video_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    public  void downloadAdFile(final String videoUrl, String videoFileName, final int type) {
        if(type==1){
            SPUtils.getInstance().put(SPUtils.SpNameConfig.SPLASH_VIDEO_SUCCESS_V, false);
        }else {
            SPUtils.getInstance().put(SPUtils.SpNameConfig.SPLASH_VIDEO_SUCCESS_H, false);
        }
        final String path=Video_PATH+videoFileName;
        FileDownloader.getImpl().create(videoUrl)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e("pending-----","pending");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.e("connected-----",soFarBytes/totalBytes+ "--=FarBytes="+soFarBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e("progress-----",(soFarBytes*1.0f/totalBytes) *100+"%");
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        Log.e("blockComplete-----","");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        Log.e("retry-----","");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.e("completed-----","下载完成");
                        if(type==1){
                            SPUtils.getInstance().put(SPUtils.SpNameConfig.SPLASH_VIDEO_SUCCESS_V, true);
                        }else {
                            SPUtils.getInstance().put(SPUtils.SpNameConfig.SPLASH_VIDEO_SUCCESS_H, true);
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e("paused-----","");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.e("error-----",e.getMessage());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.e("warn-----","warn");
                    }
                }).start();

    }

}

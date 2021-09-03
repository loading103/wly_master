package me.nereo.multi_image_selector.upload;


import android.content.Context;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.daqsoft.baselib.utils.SPUtils;
import com.daqsoft.baselib.utils.StringUtil;
import com.google.gson.Gson;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.bean.ImgBean;
import me.nereo.multi_image_selector.utils.BitmapUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import timber.log.Timber;

/**
 * 图片上传文件上传类
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2018/10/27 0027
 * @since JDK 1.8
 */
public class FileUpload {
    /**
     * 单文件上传
     *
     * @param context
     * @param image
     */
    public static void uploadFile(final String url, final Context context, final Image image, final FileBack back, final ProgressBar progressBar) {
        final RxAppCompatActivity activity = (RxAppCompatActivity) context;
        progressBar.setMax(100);
        if (image != null) {
            File tempFile = new File(image.path);
            if (!tempFile.exists()) {
                activity.runOnUiThread(() -> back.result(null));
                return;
            }
            // 异步上传文件
            Disposable disposable = Observable.just(tempFile)
                    .map(f -> {
                        try {
                            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS).build();
                            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                            builder.addFormDataPart("path", "LargeLineTubePicture");
                            if (image.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {

                                String newName = context.getCacheDir().getAbsolutePath() + File.separator + image.name;
                                String path = BitmapUtils.compressImage(image.path, newName, 480, 800, 50);
                                if (TextUtils.isEmpty(path)) {
                                    return "-1";
                                }
                                File file = new File(path);
                                if (!file.exists()) {
                                    return "文件" + file.getAbsolutePath() + "不存在";
                                }
                                MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
                                String name = file.getName();
                                name = name.replace(" ", "").replace(",", "_");

                                builder.addFormDataPart("Filedata", name, new UploadRequestBody(MEDIA_TYPE_PNG, file, (totalBytes, remainingBytes, done) -> {
                                    Log.e("TAG",
                                            "" + (int) ((totalBytes - remainingBytes) * 100 / totalBytes));
                                    activity.runOnUiThread(() -> {
                                        progressBar.setProgress((int) ((totalBytes - remainingBytes) * 100 / totalBytes));
                                        if (done) {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }));
                            } else if (image.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                                MediaType MEDIA_TYPE = MediaType.parse("video/*");
                                String name = tempFile.getName();
                                name = name.replace(" ", "").replace(",", "_");
                                int lastPos = name.lastIndexOf(".");
                                String type = name.substring(lastPos);
                                builder.addFormDataPart("Filedata", "video_" + StringUtil.INSTANCE.getRandomString(8) + "_" + System.currentTimeMillis() + type, new UploadRequestBody(MEDIA_TYPE, tempFile, (totalBytes, remainingBytes, done) -> {
                                    Log.e("TAG", "" + (int) ((totalBytes - remainingBytes) * 100 / totalBytes));
                                    activity.runOnUiThread(() -> {
                                        progressBar.setProgress((int) ((totalBytes - remainingBytes) * 100 / totalBytes));
                                        if (done) {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }));
                            } else {
                                MediaType MEDIA_TYPE = MediaType.parse("audio/*");
                                String name = tempFile.getName();
                                name = name.replace(" ", "").replace(",", "_");
                                builder.addFormDataPart("Filedata", URLEncoder.encode(name, "utf-8"), new UploadRequestBody(MEDIA_TYPE, tempFile, (totalBytes, remainingBytes, done) -> {
                                    Log.e("TAG", "" + (int) ((totalBytes - remainingBytes) * 100 / totalBytes));
                                    activity.runOnUiThread(() -> {
                                        progressBar.setProgress((int) ((totalBytes - remainingBytes) * 100 / totalBytes));
                                        if (done) {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }));
                            }
                            builder.addFormDataPart("key", SPUtils.getInstance().getString(SPUtils.Config.OSS_KEY));
                            RequestBody requestBody = builder.build();
                            Request.Builder RequestBuilder = new Request.Builder();
                            RequestBuilder.url(url + "upload");
                            RequestBuilder.post(requestBody);
                            Request request = RequestBuilder.build();
                            String result = client.newCall(request).execute().body().string();
                            Log.e("TAG", "" + result);
                            Gson gson = new Gson();
                            ImgBean bean = gson.fromJson(result, ImgBean.class);
                            return bean;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "-1";
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(bean -> {
                        if (bean instanceof ImgBean) {
                            ImgBean imgBean = (ImgBean) bean;
                            if (imgBean.getError() != 0) {
                                activity.runOnUiThread(() -> back.result(null));
                            } else {
                                activity.runOnUiThread(() -> back.result(imgBean));
                            }
                        } else {
                            activity.runOnUiThread(() -> back.result(null));
                        }

                    }, e -> {
                        Timber.e(e.getMessage());
                        activity.runOnUiThread(() -> back.result(null));
                    });
        }


    }

    /**
     * 上传图片文件
     *
     * @param filePathList
     */
    public static void uploadFile(final String url, final Context context, final ArrayList<Image> filePathList, final UploadFileBack back) {
        final RxAppCompatActivity activity = (RxAppCompatActivity) context;
        if (filePathList == null || filePathList.isEmpty()) {
            activity.runOnUiThread(() -> back.result(""));
            return;
        }
        final List<ImgBean> list = new ArrayList<>();
        final List<String> resultList = new ArrayList<String>();
        for (final Image image : filePathList) {
            if (image != null) {
                File tempFile = new File(image.path);
                if (!tempFile.exists()) {
                    activity.runOnUiThread(() -> back.result("文件" + tempFile.getAbsolutePath() + "不存在"));
                    return;
                }
                // 异步上传文件
                Disposable disposable = Observable.just(tempFile)
                        .map(f -> {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                                builder.addFormDataPart("path", "LargeLineTubePicture");
                                if (image.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                                    String newName = context.getCacheDir().getAbsolutePath() + File.separator + f.getName();
                                    String path = BitmapUtils.compressImage(image.path, newName, 480, 800, 50);
                                    if (TextUtils.isEmpty(path)) {
                                        return "-1";
                                    }
                                    File file = new File(path);
                                    if (!file.exists()) {
                                        return "文件" + file.getAbsolutePath() + "不存在";
                                    }
                                    MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
                                    String name = file.getName();
                                    name = name.replace(" ", "").replace(",", "_");

                                    builder.addFormDataPart("Filedata", URLEncoder.encode(name, "utf-8"), RequestBody.create(MEDIA_TYPE_PNG, file));
                                } else if (image.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                                    MediaType MEDIA_TYPE = MediaType.parse("video/*");
                                    String name = tempFile.getName();
                                    name = name.replace(" ", "").replace(",", "_");
                                    builder.addFormDataPart("Filedata", URLEncoder.encode(name, "utf-8"), RequestBody.create(MEDIA_TYPE, tempFile));
                                } else {
                                    MediaType MEDIA_TYPE = MediaType.parse("audio/*");
                                    String name = tempFile.getName();
                                    name = name.replace(" ", "").replace(",", "_");
                                    builder.addFormDataPart("Filedata", URLEncoder.encode(name, "utf-8"), RequestBody.create(MEDIA_TYPE, tempFile));
                                }

                                builder.addFormDataPart("key", SPUtils.getInstance().getString(SPUtils.Config.OSS_KEY));
                                RequestBody requestBody = builder.build();
                                Request.Builder RequestBuilder = new Request.Builder();
                                // 添加URL地址 "http://file.geeker.com.cn/upload"
                                RequestBuilder.url(url + "upload");
                                RequestBuilder.post(requestBody);
                                Request request = RequestBuilder.build();
                                String result = client.newCall(request).execute().body().string();
                                Log.e("TAG", "" + result);
                                Gson gson = new Gson();
                                ImgBean bean = gson.fromJson(result, ImgBean.class);
                                return bean;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return "-1";
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(bean -> {
                            if (bean instanceof ImgBean) {
                                ImgBean imgBean = (ImgBean) bean;
                                list.add(imgBean);
                                resultList.add(imgBean.getUrl());
                                if (imgBean.getError() != 0) {
                                    activity.runOnUiThread(() -> back.result("-1"));
                                }
                                // 判断是否是最后的一次
                                if (list.size() >= filePathList.size()) {
                                    final StringBuffer sb = new StringBuffer();
                                    for (ImgBean tempBean : list) {
                                        sb.append(tempBean.getUrl());
                                        sb.append(",");
                                    }
                                    sb.deleteCharAt(sb.length() - 1);
                                    // 上传成功之后回调
                                    activity.runOnUiThread(() -> {
                                        back.result(sb.toString());
                                        back.resultList(resultList);
                                    });
                                }
                            } else {
                                activity.runOnUiThread(() -> back.result("-1"));
                            }

                        });
            }

        }

    }

    /**
     * 上传单个图片
     *
     * @param filePathList
     */
    public static void uploadFilePath(final String url, final Context context,
                                      final ArrayList<String> filePathList,
                                      final UploadFileBack back) {
        final RxAppCompatActivity activity = (RxAppCompatActivity) context;
        if (filePathList == null || filePathList.isEmpty()) {
            activity.runOnUiThread(() -> back.result(""));
            return;
        }
        final List<ImgBean> list = new ArrayList<>();
        final List<String> resultList = new ArrayList<String>();
        for (final String image : filePathList) {
            if (image != null) {
                File tempFile = new File(image);
                if (!tempFile.exists()) {
                    activity.runOnUiThread(() -> back.result("文件" + tempFile.getAbsolutePath() + "不存在"));
                    return;
                }
                // 异步上传文件
                Disposable disposable = Observable.just(tempFile)
                        .map(f -> {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                                builder.addFormDataPart("path", "LargeLineTubePicture");
                                String newName = context.getCacheDir().getAbsolutePath() + File.separator + f.getName();
                                String path = BitmapUtils.compressImage(image, newName, 480, 800, 50);
                                if (TextUtils.isEmpty(path)) {
                                    return "-1";
                                }
                                File file = new File(path);
                                if (!file.exists()) {
                                    return "文件" + file.getAbsolutePath() + "不存在";
                                }
                                MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
                                String name = file.getName();
                                name = name.replace(" ", "").replace(",", "_");

                                builder.addFormDataPart("Filedata", URLEncoder.encode(name, "utf-8"), RequestBody.create(MEDIA_TYPE_PNG, file));
                                builder.addFormDataPart("key", SPUtils.getInstance().getString(SPUtils.Config.OSS_KEY));
                                RequestBody requestBody = builder.build();
                                Request.Builder RequestBuilder = new Request.Builder();
                                // 添加URL地址 "http://file.geeker.com.cn/upload"
                                RequestBuilder.url(url);
                                RequestBuilder.post(requestBody);
                                Request request = RequestBuilder.build();
                                String result = client.newCall(request).execute().body().string();
                                Log.e("TAG", "" + result);
                                Gson gson = new Gson();
                                ImgBean bean = gson.fromJson(result, ImgBean.class);
                                return bean;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return "-1";
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(bean -> {
                            if (bean instanceof ImgBean) {
                                ImgBean imgBean = (ImgBean) bean;
                                list.add(imgBean);
                                resultList.add(imgBean.getFileUrl());
                                if (imgBean.getError() != 0) {
                                    activity.runOnUiThread(() -> back.result("-1"));
                                }
                                // 判断是否是最后的一次
                                if (list.size() >= filePathList.size()) {
                                    final StringBuffer sb = new StringBuffer();
                                    for (ImgBean tempBean : list) {
                                        sb.append(tempBean.getUrl());
                                        sb.append(",");
                                    }
                                    sb.deleteCharAt(sb.length() - 1);
                                    // 上传成功之后回调
                                    activity.runOnUiThread(() -> {
                                        back.result(sb.toString());
                                        back.resultList(resultList);
                                    });
                                }
                            } else {
                                activity.runOnUiThread(() -> back.result("-1"));
                            }

                        });
            }

        }

    }
//    /**
//     * 上传图片文件
//     * 子线程持有context引用，可能会引发内存泄漏,
//     * 该方法会造成本身已经上传完成了，但是由于是多线程数据不同同步而造成的上传失败；
//     * @param filePathList
//     */
//    public static void uploadFile(final Context context, final List<String> filePathList, final UploadFileBack back) {
//        final RxAppCompatActivity activity = (RxAppCompatActivity) context;
//        if (filePathList == null || filePathList.isEmpty()) {
//            activity.runOnUiThread(() -> back.result(""));
//            return;
//        }
//        final List<ImgBean> list = new ArrayList<>();
//        final List<String> resultList = new ArrayList<String>();
//        for (final String filePath : filePathList) {
//            new Thread(() -> {
//                try {
//                    if (!TextUtils.isEmpty(filePath)) {
//                        File tempFile = new File(filePath);
//                        if (!tempFile.exists()) {
//                            activity.runOnUiThread(() -> back.result("-1"));
//                            return;
//                        }
//                        OkHttpClient client = new OkHttpClient();
//                        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//                        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
//                        String newName = context.getCacheDir().getAbsolutePath() + File.separator + tempFile.getName();
//                        String path = BitmapUtils.compressImage(filePath, newName, 480, 800, 50);
//                        if (TextUtils.isEmpty(path)) {
//                            activity.runOnUiThread(() -> back.result("-1"));
//                            return;
//                        }
//                        File file = new File(path);
//                        builder.addFormDataPart("path", "LargeLineTubePicture");
//                        builder.addFormDataPart("Filedata", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
//                        RequestBody requestBody = builder.build();
//                        Request.Builder RequestBuilder = new Request.Builder();
//                        // 添加URL地址 "http://file.geeker.com.cn/upload"
//                        RequestBuilder.url(URLConstants.BASE_UPFILE_URL + "upload");
//                        RequestBuilder.post(requestBody);
//                        Request request = RequestBuilder.build();
//                        String result = client.newCall(request).execute().body().string();
//                        Log.e("TAG",""+result);
//                        Gson gson = new Gson();
//                        ImgBean bean = gson.fromJson(result, ImgBean.class);
//                        // 此处会造成数据不同步
//                        list.add(bean);
//                        resultList.add(bean.getFileUrl());
//                        if (bean.getError() != 0) {
//                            activity.runOnUiThread(() -> back.result("-1"));
//                        }
//                        // 判断是否是最后的一次
//                        if (list.size() >= filePathList.size()) {
//                            final StringBuffer sb = new StringBuffer();
//                            for (ImgBean tempBean : list) {
//                                sb.append(tempBean.getFileUrl());
//                                sb.append(",");
//                            }
//                            sb.deleteCharAt(sb.length() - 1);
//                            // 上传成功之后回调
//                            activity.runOnUiThread(() -> {
//                                back.result(sb.toString());
//                                back.resultList(resultList);
//                            });
//
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    activity.runOnUiThread(() -> back.result("-1"));
//                }
//            }).start();
//        }
//
//    }

//       /**
    //     * 上传文件yi
    //     * @param filePathList
    //     */
//    public static void uploadAudioFile(final Context context, final String filePathList, final UploadFileBack back) {
//        final RxAppCompatActivity activity = (RxAppCompatActivity) context;
//        if (filePathList == null || filePathList.isEmpty()) {
//            activity.runOnUiThread(()-> back.result(""));
//            return;
//        }
//
//        File file = new File(filePathList);
//        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Builder builder = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("path", "LargeLineTube")
//                .addFormDataPart("Filedata", System.currentTimeMillis() + ".mp3", fileBody);
//        List<MultipartBody.Part> parts = builder.build().parts();
//        Disposable disposable = RetrofitHelper.getUpApiService().upImg(parts)
//                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(upImgEntity-> back.result(upImgEntity.getFileUrl())
//                        ,e-> back.result("-1"));
//    }


    public interface UploadFileBack {
        void result(String value);

        void resultList(List<String> value);
    }

    public interface FileBack {
        void result(ImgBean imgBean);

        void progress(int progress);
    }


}

package com.daqsoft.baselib.net;

import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.SPUtils;

import java.io.IOException;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * @author ihsan on 09/02/2017.
 */

public class HeaderInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        String accessToken = SPUtils.getInstance().getString(SPUtils.Config.TOKEN);
        Request request = chain.request();

        HttpUrl.Builder httpUrlBuilder = Objects.requireNonNull(request.url()
                .newBuilder(request.url().toString()))
                .addQueryParameter("token", accessToken)
                .addQueryParameter("source", "android")
                .addQueryParameter("siteCode", BaseApplication.siteCode);


        request = request.newBuilder().url(httpUrlBuilder.build()).build();
//        Timber.e("%s请求数据  ", request.url());
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.peekBody(1024 * 1024);
//        Timber.e("======================================================");
//        Timber.e("headers:%s", request.headers().toString());
//        Timber.e("url:%s", response.request().url()+"-----");
//        Timber.e("responseBody:%s", responseBody.string());
//        Timber.e("======================================================");
        Timber.e(request.url()+"返回数据 : \n"+ responseBody.string());
        return response;
    }
}

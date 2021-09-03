package com.daqsoft.provider.network.net;

import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.SPUtils;
import com.daqsoft.provider.SPKey;

import java.io.IOException;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * @author PuHua
 * @des 小电商的头部信息
 * @date
 */

public class ElectronicHeaderInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        String accessToken = SPUtils.getInstance().getString(SPUtils.Config.TOKEN);
        String sessionId = SPUtils.getInstance().getString("sessionId");
        String domain = SPUtils.getInstance().getString(SPKey.DOMAIN);

        String ds = domain.replace("http://", "");
        Request request = chain.request();
        HttpUrl.Builder httpUrlBuilder = Objects.requireNonNull(request.url()
                .newBuilder(request.url().toString()))
                .addQueryParameter("siteCode", BaseApplication.siteCode);

        request = request.newBuilder()
                .addHeader("domain", ds)
                .addHeader("sessionId",sessionId)
                .url(httpUrlBuilder.build()).build();
        Response response = chain.proceed(request);
        Timber.e("\n" + request.headers().toString());
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        Timber.e(response.request().url() + "\n" + responseBody.string());
        return response;
    }

}

package com.daqsoft.baselib.net.gsonTypeAdapters;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 数据解析工厂
 */
public class FaultToleranceConvertFactory extends Converter.Factory {


    @SuppressWarnings("ConstantConditions")
    public static FaultToleranceConvertFactory create() {

        return new FaultToleranceConvertFactory();
    }

    public static FaultToleranceConvertFactory create(Gson gson) {

        return new FaultToleranceConvertFactory(gson);
    }

    private final Gson gson;

    private FaultToleranceConvertFactory() {
        this.gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapterFactory(new GsonTypeAdapterFactory())
//                .registerTypeAdapter(String.class,new StringNullAdapter())
//                .registerTypeAdapterFactory(new ObjectAdapterFactory())
                .create();
    }

    private FaultToleranceConvertFactory(Gson gson) {
        this.gson = gson;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {

        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));

        return new ToleranceResponseBodyConverter(gson, adapter);
    }

}

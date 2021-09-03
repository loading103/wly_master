package com.daqsoft.baselib.net.gsonTypeAdapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import timber.log.Timber;

/**
 * @Description list为空字符串时容错处理类
 * @ClassName ToleranceResponseBodyConverter
 * @Author PuHua
 * @Time 2019/11/14 11:19
 * @Version 1.0
 */
public class ToleranceResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public ToleranceResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override public T convert(ResponseBody value) throws IOException {
        String originalBody = value.string();
        Pattern p = Pattern.compile("\"data\":\"\"");
        Matcher m = p.matcher(originalBody);
        String result = m.replaceAll("\"data\":null");
//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            Timber.e(adapter.getClass().getName());
            return adapter.fromJson(result);
        }finally {
            value.close();
        }
    }
}

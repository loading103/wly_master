package com.daqsoft.baselib.net.gsonTypeAdapters;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @Description 字符串 null 转""
 * @ClassName StringNullAdapter
 * @Author LUOYI
 * @Time 2020/6/24 14:46
 * @Version 1.0
 */
public class StringNullAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter writer, String value) throws IOException {
        if (value == null) {
            writer.value("");
            return;
        }
        writer.value(value);
    }

    @Override
    public String read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return "";
        }
        return reader.nextString();
    }
}

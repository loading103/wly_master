package com.daqsoft.baselib.net.gsonTypeAdapters;

/**
 * @Description XXXXX
 * @ClassName ListTypeAdapterFactory
 * @Author PuHua
 * @Time 2019/11/14 15:30
 * @Version 1.0
 */


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Description list为空字符串时容错处理
 * @ClassName ToleranceResponseBodyConverter
 * @Author PuHua
 * @Time 2019/11/14 11:19
 * @Version 1.0
 */
public final class ObjectAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();

        Class<? super T> rawType = typeToken.getRawType();
        if (!Object.class.isAssignableFrom(rawType)) {
            return null;
        }

//        Type elementType = $Gson$Types.getRawType(rawType);
//        Log.e("TAGTYPE", elementType.toString());
//        TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
//        if (elementType == String.class) {
//            return (TypeAdapter<T>) new StringNullAdapter();
//        } else if (elementType == Double.class) {
//            return (TypeAdapter<T>) new DoubleNullAdapter();
//        } else if (elementType == List.class) {
//            @SuppressWarnings({"unchecked", "rawtypes"})(gson, elementType, elementTypeAdapter)
//            TypeAdapter<T> result = new ListTypeAdapterFactory.Adapter<T>(gson, elementType, elementTypeAdapter);
//            return result;
//        } else if (elementType == Object.class) {
//            @SuppressWarnings({"unchecked", "rawtypes"})
//            TypeAdapter<T> result = new ObjectAdapterFactory.Adapter(gson, elementType, elementTypeAdapter);
//            return result;
//        } else {
            return null;
//        }
    }

    class DoubleNullAdapter extends TypeAdapter<Double> {

        @Override
        public void write(JsonWriter out, Double value) throws IOException {
            out.value(value);
        }

        @Override
        public Double read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.STRING) {
                in.skipValue();
                return 0.0;
            }
            Double d = Double.valueOf(in.nextString());
            return d;
        }
    }

    class StringNullAdapter extends TypeAdapter<String> {

        @Override
        public void write(JsonWriter out, String value) throws IOException {
            out.value(value);
        }

        @Override
        public String read(JsonReader in) throws IOException {
            return in.nextString();
        }
    }


    /**
     * 定义解析类型
     *
     * @param <E>
     */
    private static final class Adapter<E> extends TypeAdapter<E> {
        // 防止循环调用read方法
        private final TypeAdapter<E> elementTypeAdapter;


        public Adapter(Gson context, Type elementType,
                       TypeAdapter<E> elementTypeAdapter) {
            this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(context, elementTypeAdapter, elementType);

        }

        @Override
        public E read(JsonReader in) throws IOException {

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            E instance = null;

            if (in.peek() == JsonToken.STRING) {
                instance = elementTypeAdapter.read(in);
            } else {
                try {
                    // 正常解析数组
                    in.beginObject();
                    while (in.hasNext()) {
                        instance = elementTypeAdapter.read(in);
                    }
                    in.endObject();
                } catch (IllegalStateException e) {
                    // 主要是除了异常之后就跳过这条数据，输出对象为null
                    in.skipValue();
                    e.printStackTrace();
//                return null;
                }
            }


            return instance;
        }

        @Override
        public void write(JsonWriter out, E collection) throws IOException {
            if (collection == null) {
                out.nullValue();
                return;
            }

            out.beginObject();

            elementTypeAdapter.write(out, collection);

            out.endObject();
        }
    }

    /**
     * @param <T>
     */
    static final class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {
        private final Gson context;
        private final TypeAdapter<T> delegate;
        private final Type type;

        TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter<T> delegate, Type type) {
            this.context = context;
            this.delegate = delegate;
            this.type = type;
        }

        @Override
        public T read(JsonReader in) throws IOException {

            return delegate.read(in);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void write(JsonWriter out, T value) throws IOException {
            // Order of preference for choosing type adapters
            // First preference: a type adapter registered for the runtime type
            // Second preference: a type adapter registered for the declared type
            // Third preference: reflective type adapter for the runtime type (if it is a sub class of the declared type)
            // Fourth preference: reflective type adapter for the declared type

            TypeAdapter chosen = delegate;
            Type runtimeType = getRuntimeTypeIfMoreSpecific(type, value);
            if (runtimeType != type) {
                TypeAdapter runtimeTypeAdapter = context.getAdapter(TypeToken.get(runtimeType));
                if (!(runtimeTypeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter)) {
                    // The user registered a type adapter for the runtime type, so we will use that
                    chosen = runtimeTypeAdapter;
                } else if (!(delegate instanceof ReflectiveTypeAdapterFactory.Adapter)) {
                    // The user registered a type adapter for Base class, so we prefer it over the
                    // reflective type adapter for the runtime type
                    chosen = delegate;
                } else {
                    // Use the type adapter for runtime type
                    chosen = runtimeTypeAdapter;
                }
            }
            chosen.write(out, value);
        }

        /**
         * Finds a compatible runtime type if it is more specific
         */
        private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
            if (value != null
                    && (type == Object.class || type instanceof TypeVariable<?> || type instanceof Class<?>)) {
                type = value.getClass();
            }
            return type;
        }
    }

}

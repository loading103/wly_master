package com.daqsoft.baselib.net.gsonTypeAdapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @Description XXXXX
 * @ClassName ListTypeAdapter
 * @Author PuHua
 * @Time 2020/2/21 10:47
 * @Version 1.0
 */
public class ListTypeAdapter<E> extends TypeAdapter<Collection<E>> {
    // 防止循环调用read方法
    private final TypeAdapter<E> elementTypeAdapter;


    public ListTypeAdapter(Gson context, Type elementType,
                   TypeAdapter<E> elementTypeAdapter) {
        this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(context, elementTypeAdapter, elementType);

    }

    @Override
    public Collection<E> read(JsonReader in) throws IOException {

        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        Collection<E> collection = new ArrayList<>();
        try {
            // 正常解析数组
            in.beginArray();
            while (in.hasNext()) {
                E instance = elementTypeAdapter.read(in);
                collection.add(instance);
            }
            in.endArray();
        } catch (IllegalStateException e) {
            // 主要是除了异常之后就跳过这条数据，输出对象为null
            in.skipValue();
            e.printStackTrace();
            return null;
        }

        return collection;
    }

    @Override
    public void write(JsonWriter out, Collection<E> collection) throws IOException {
        if (collection == null) {
            out.nullValue();
            return;
        }

        out.beginArray();
        for (E element : collection) {
            elementTypeAdapter.write(out, element);
        }
        out.endArray();
    }


    /**
     *
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
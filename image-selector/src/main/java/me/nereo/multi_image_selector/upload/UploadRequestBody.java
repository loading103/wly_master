package me.nereo.multi_image_selector.upload;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * @Description 文件上传的body, 可以反馈当前上传的进度
 * @ClassName UploadRequestBody
 * @Author PuHua
 * @Time 2019/11/28 15:27
 * @Version 1.0
 */
public class UploadRequestBody extends RequestBody {
    MediaType contentType;
    File file;
    ProgressListener listener;

    UploadRequestBody(final MediaType contentType, final File file, final ProgressListener listener) {
        this.contentType = contentType;
        this.file = file;
        this.listener = listener;
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source;
        try {
            source = Okio.source(file);
            Buffer buf = new Buffer();
            Long remaining = contentLength();
            for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                sink.write(buf, readCount);
                listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    interface ProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }
}

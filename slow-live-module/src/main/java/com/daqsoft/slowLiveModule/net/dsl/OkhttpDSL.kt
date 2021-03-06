package com.daqsoft.slowLiveModule.net.dsl


import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.slowLiveModule.bothNotNull
import okhttp3.*
import okhttp3.internal.Util
import okio.BufferedSink
import okio.Okio
import okio.Source
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.experimental.and


/**
 * okhttp 的 dsl
 *
 * Created by wongxd on 2018/06/08.
 *            https://github.com/wongxd
 *            wxd1@live.com
 *
 */


internal object BaseOkHttpHelper {

    internal class BaseOkHttpHelperLooper private constructor(looper: Looper) : Handler(looper) {
        companion object {
            private val instance = BaseOkHttpHelperLooper(Looper.getMainLooper())

            fun runOnUiThread(block: () -> Unit) {
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    Runnable { block.invoke() }.run()
                } else {
                    instance.post { block.invoke() }
                }

            }
        }
    }

    /**
     * 用于监听上传进度
     */
    internal class FileProgressRequestBody(
        val file: File,
        val contentType: String,
        // progress  totalLength ,index
        val listener: (Int, Long, Int) -> Unit,
        val totalFileLength: Long,
        val index: Int
    ) : RequestBody() {


        override fun contentLength(): Long {
            return file.length()
        }

        override fun contentType(): MediaType? {
            return MediaType.parse(contentType)
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            var source: Source? = null
            try {
                source = Okio.source(file)
                var total: Long = 0
                source?.let { _ ->
                    var read: Long = 0
                    while ((source.read(sink.buffer(), SEGMENT_SIZE.toLong())).also { read = it } != -1L) {
                        total += read
                        sink.flush()

                        val d =
                            BigDecimal(total / file.length().toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP)
                                .toDouble()

                        val progress = d * 100

                        BaseOkHttpHelperLooper.runOnUiThread {
                            listener.invoke(progress.toInt(), totalFileLength, index)
                        }

                    }
                }
            } finally {
                Util.closeQuietly(source)
            }
        }

        companion object {


            /**
             * 这是一个示例
             *
             * @param url
             * @param uploadName
             * @param filePath
             * @param fileName
             * @return
             */
            fun generateRequest(
                url: String,
                uploadName: String,
                filePath: String,
                fileName: String,
                // progress  totalLength ,index
                listener: (Int, Long, Int) -> Unit,
                totalFileLength: Long,
                index: Int
            ): Request {
                // 构造上传请求，模拟表单提交文件

                val formData = String.format("form-data;name=%s; filename=%s", uploadName, fileName)
                val filePart = FileProgressRequestBody(
                    File(fileName),
                    "application/octet-stream",
                    listener,
                    totalFileLength,
                    index
                )
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addPart(Headers.of("Content-Disposition", formData), filePart)
                    .build()

                // 创建Request对象
                return Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

            }

            val SEGMENT_SIZE = 2 * 1024 // okio.Segment.SIZE
        }

    }

    /**
     * 通用的http请求封装，不包含任何个性化参数
     */
    fun baseOkhttp(wrap: RequestWrapper, isDownload: Boolean = false) {
        Thread(Runnable { onExecute(wrap, isDownload) }).start()
    }


    private fun onExecute(wrap: RequestWrapper, isDownload: Boolean = false) {


        val headers: Headers = Headers.of(wrap.headers)

        var req: Request? = null

        val formBody = FormBody.Builder()

        val sb = StringBuilder()

        wrap.params.keys.forEach { key ->
            formBody.add(key, wrap.params[key])

            sb.append(key + "--" + wrap.params[key])
            sb.append("\r\n")
        }


        val builder: RequestBody =

            if (wrap.jsonParam.isNotBlank()) {
                //首先判断 jsonParam 是否为空，由于 jsonParam 与 paramsMap 不可能同时存在，所以先判断mJsonStr
                val JSON = MediaType.parse("application/json; charset=utf-8")//数据类型为json格式，
                RequestBody.create(JSON, wrap.jsonParam)//json数据，

            } else if (wrap.imgs.isNotEmpty() || wrap.files.isNotEmpty()) {
                val muti: MultipartBody.Builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addPart(formBody.build())


                var totalFileLength = 0L
                wrap.imgs.forEach { img ->
                    totalFileLength += img.value.length()
                }

                wrap.files.forEach { file ->
                    totalFileLength += file.value.length()
                }


                var index = 0

                for (img in wrap.imgs) {
//                    muti.addFormDataPart(
//                        img.key, img.value.name,
//                        RequestBody.create(MediaType.parse("image/*"), img.value)
//                    )

                    val formData = String.format("form-data;name=%s; filename=%s", img.key, img.value.name)
                    val filePart = FileProgressRequestBody(
                        img.value,
                        "application/octet-stream",
                        wrap._uploadFile,
                        totalFileLength,
                        index
                    )
                    muti.addPart(Headers.of("Content-Disposition", formData), filePart)

                    index++
                }


                for (file in wrap.files) {
//                    muti.addFormDataPart(
//                        file.key, file.value.name,
//                        RequestBody.create(MediaType.parse("file/*"), file.value)
//                    )

                    val formData = String.format("form-data;name=%s; filename=%s", file.key, file.value.name)
                    val filePart = FileProgressRequestBody(
                        file.value,
                        "application/octet-stream",
                        wrap._uploadFile,
                        totalFileLength,
                        index
                    )
                    muti.addPart(Headers.of("Content-Disposition", formData), filePart)

                    index++
                }

                muti.build()
            } else {
                formBody.build()
            }


        when (wrap.method.trim().toUpperCase()) {
            "GET" -> {

                val getUrlSB = java.lang.StringBuilder()

                getUrlSB.append(wrap.url)
                getUrlSB.append("?")


                wrap.params.keys.forEach { key ->

                    getUrlSB.append(key + "=" + wrap.params[key])
                    sb.append("&")
                }


                val getUrl = getUrlSB.substring(0, getUrlSB.lastIndex)

                com.daqsoft.slowLiveModule.net.logI("OKHttpDsl-getUrl:  $getUrl")

                req = Request.Builder().url(getUrl.toString()).headers(headers).build()
            }

            //只做了post
            else -> {
                try {
                    com.daqsoft.slowLiveModule.net.logI("OKHttpDsl-postUrl:  ${wrap.url} ")
                    req = Request.Builder().url(wrap.url).headers(headers).post(builder).build()
                } catch (e: Exception) {
                    e.printStackTrace()
                    wrap._fail(-1, e.message ?: "服务器内部错误")
                    return
                }
            }
        }

        val http = OkHttpClient.Builder().connectTimeout(wrap.timeout, TimeUnit.SECONDS).build()
        http.newCall(req!!).enqueue(object : Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                val msg = e?.message ?: "服务器内部错误"
                BaseOkHttpHelperLooper.runOnUiThread {
                    if (wrap.IS_SHOW_MSG) {
                        ToastUtils.showMessage(msg)
                    }
                    wrap._finish.invoke()
                    wrap._fail.invoke(-1, msg)
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (isDownload) {
                    bothNotNull(call, response) { a, b ->
                        dealDownloadFile(
                            a,
                            b,
                            wrap
                        )
                    }
                } else {
                    BaseOkHttpHelperLooper.runOnUiThread { wrap._finish.invoke() }
                    response?.let {
                        val res = response.body()?.string() ?: ""


                        if (res.isNotBlank()) {
                            BaseOkHttpHelperLooper.runOnUiThread {
                                wrap._originResponse.invoke(res)
                            }
                            try {
                                val json = JSONObject(res)
                                val errCode = json.optInt(wrap.RESPONSE_CODE_NAME)
                                val errMsg = json.optString(wrap.RESPONSE_MSG_NAME)

                                BaseOkHttpHelperLooper.runOnUiThread {
                                    if (errCode != wrap.SUCCESS_CODE) {
                                        if (wrap.IS_SHOW_MSG) {
                                            ToastUtils.showMessage(errMsg)
                                        }
                                        if (errCode == wrap.TOKEN_LOST_CODE) {
                                            //token失效
                                            wrap._tokenLost.invoke(errMsg)

                                        } else
                                            wrap._fail.invoke(errCode, errMsg)
                                    } else {
                                        wrap._success.invoke(res)
                                        wrap._successWithMsg.invoke(res, errMsg)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            wrap._fail.invoke(-1, "服务器响应异常")
                        }
                    }
                }
            }
        })
    }


    private fun dealDownloadFile(call: Call, response: Response, wrap: RequestWrapper) {

        //将返回结果转化为流，并写入文件
        try {
            var len = 0
            val buf = ByteArray(8 * 1024)
            val inputStream = response.body()?.byteStream()
            val total = response.body()?.contentLength() ?: 0

            /**
             * 写入本地文件
             */
            val responseFileName = getHeaderFileName(response)
            var file: File? = null
            /**
             *如果服务器没有返回的话,使用自定义的文件名字
             */

            val dir = File(wrap.downloadPath)

            if (!dir.exists()) {
                dir.mkdirs()
            }


            if (responseFileName.isBlank()) {
                file = File(dir, wrap.downloadFileName)
            } else {
                file = File(dir, responseFileName)
            }

            val fileOutputStream = FileOutputStream(file)
            var sum: Long = 0
            while ((inputStream?.read(buf))?.also { len = it } != -1) {
                fileOutputStream.write(buf, 0, len)
                sum += len
                val progress = (sum * 1.0f / total * 100).toInt() // 下载中
                BaseOkHttpHelperLooper.runOnUiThread {
                    wrap._downloadFile.invoke(
                        progress,
                        total,
                        null
                    )
                }
            }

            BaseOkHttpHelperLooper.runOnUiThread {
                wrap._finish()
                wrap._downloadFile.invoke(100, total, file)
            }

            fileOutputStream.flush()
            fileOutputStream.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            BaseOkHttpHelperLooper.runOnUiThread {

                wrap._finish.invoke()

                val msg = "下载失败"
                if (wrap.IS_SHOW_MSG) {
                    ToastUtils.showMessage(msg)
                }
                wrap._fail.invoke(-1, msg)
            }

        }
    }


    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private fun getHeaderFileName(response: Response): String {
        var dispositionHeader = response.header("Content-Disposition")
        if (!TextUtils.isEmpty(dispositionHeader)) {
            dispositionHeader?.replace("attachment;filename=", "")
            dispositionHeader?.replace("filename*=utf-8", "")
            val strings = dispositionHeader?.split("; ") ?: emptyList()
            if (strings.size > 1) {
                dispositionHeader = strings[1].replace("filename=", "")
                dispositionHeader = dispositionHeader.replace("\"", "")
                return dispositionHeader
            }
            return ""
        }
        return ""
    }


}


/**
 * 使用 Map按key进行升序排序后,取出value进行MD5加密
 * @param map
 * @return
 */
internal fun getSign(map: Map<String, String>, appKey: String): String {
    if (map.isEmpty()) {
        return ""
    }
    val sortMap = TreeMap<String, String>(MapKeyComparator())
    sortMap.putAll(map)

    val sb = StringBuffer()
    for (key in sortMap.keys) {
        sb.append(sortMap[key])
    }
    sb.append(appKey)

    return MD5.md5(sb.toString())
}

//比较器类
private class MapKeyComparator : Comparator<String> {
    override fun compare(str1: String, str2: String): Int {
        return str1.compareTo(str2)
    }
}

/**
 * Created by wongxd on 2018/06/05.
 */
internal object MD5 {

    fun md5(string: String): String {
        if (TextUtils.isEmpty(string)) {
            return ""
        }
        var md5: MessageDigest? = null
        try {
            md5 = MessageDigest.getInstance("MD5")
            val bytes = md5!!.digest(string.toByteArray())
            val result = StringBuilder()
            for (b in bytes) {
                var temp = Integer.toHexString((b and 0xff.toByte()).toInt())
                if (temp.length == 1) {
                    temp = "0$temp"
                }
                result.append(temp)
            }
            return result.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }
}






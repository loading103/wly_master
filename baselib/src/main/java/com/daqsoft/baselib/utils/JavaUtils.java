package com.daqsoft.baselib.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;

import com.daqsoft.baselib.adapter.RecyclerViewAdapter;
import com.daqsoft.baselib.base.BaseResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.misc.BASE64Decoder;

/**
 * Java语言的工具类
 * <p>
 * 主要用于一些用Kotlin语言不太好处理的情况，如泛型情况
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-7-5
 * @since JDK 1.8.0_191
 */
public class JavaUtils {

    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    public static void pageDeal(Integer page, BaseResponse response, RecyclerViewAdapter adapter) {
        if (page == null) {
            return;
        }
        if (page == 1) {
            adapter.clear();
        }
        if (response.getPage() == null) {
            adapter.loadEnd();
            return;
        }
        if (response.getPage().getCurrPage() < response.getPage().getTotalPage()) {
            adapter.loadComplete();
        } else {
            adapter.loadEnd();
        }
    }

    /**
     * 高德地图验证sha值
     *
     * @param context
     * @return
     */
    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置一个字符串中部分字体颜色改变
     *
     * @param content    内容
     * @param colorId    颜色
     * @param startIndex 从第几个开始
     * @param endIndex   到第几个结束
     * @return
     */
    public static SpannableString setTextColor(String content, int colorId, int startIndex, int
            endIndex) {
        SpannableString result = new SpannableString(content);
        result.setSpan(new ForegroundColorSpan(colorId), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return result;
    }

    /**
     * 去除带html标签的内容中，去掉<img>图片标签
     *
     * @param content 内容
     * @return
     */
    public static String deleteHtmlImg(String content) {
        // 匹配img标签的正则表达式
        String regxpForImgTag = "<img\\s[^>]+/>";
        Pattern pattern = Pattern.compile(regxpForImgTag);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String temp = matcher.group();
            content = content.replace(temp, "");
        }
        return Html.fromHtml(content).toString().trim();
    }

    /**
     * file转为bitmap(网上发现的另一种写法，主要区别在与这种是计算得到的scale)
     */
    public static Bitmap fileToBitmap(String filePath) throws IOException {
        Bitmap b = null;
        int IMAGE_MAX_SIZE = 600;

        File f = new File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException();
        }
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis;
        int scale = 1;
        // 如果图片过大，可能导致Bitmap对象装不下图片
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            // scale的计算
            scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        // 以一定的比例转为bitmap
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        fis = new FileInputStream(f);
        b = BitmapFactory.decodeStream(fis, null, o2);
        fis.close();
        return b;
    }

    /**
     * bitmap保存为file
     */
    public static void bitmapToFile(String filePath, Bitmap bitmap, int quality) throws IOException {
        if (bitmap != null) {
            File file = new File(filePath.substring(0,
                    filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
            bos.flush();
            bos.close();
        }
    }

    /**
     * 图片文件转化成base64字符串
     */
    public static String fileToBase64(String imgFile) {
        InputStream in = null;
        String base64 = null;
        // 读取图片字节数组
        try {
            if (imgFile == null || "".equals(imgFile)) {
                imgFile = "uploaddir/file/default.png";
            }
            in = new FileInputStream(imgFile);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭输入流
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    /**
     * base64字符串转化成文件（图片）
     */
    public static boolean base64ToFile(String imgStr, String filePath) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    // 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(filePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}

package me.nereo.multi_image_selector.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

/**
 * @Description 获取视频文件的缩略图
 * @ClassName ThumbnilUtits
 * @Author PuHua
 * @Time 2019/6/21 15:37
 * @Version 1.0
 */
public class ThumbnilUtits {

    public static Bitmap getThumbnil(String filePath,int kind){
        Bitmap bitmap=null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (filePath.startsWith("http://")
                || filePath.startsWith("https://")
                || filePath.startsWith("widevine://")) {
            retriever.setDataSource(filePath,new Hashtable<String,String>());
        }else {
            retriever.setDataSource(filePath);
        }
        bitmap =retriever.getFrameAtTime(-1);
        if (bitmap==null){
            return null;
        }
        if (kind== MediaStore.Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width= bitmap.getWidth();
            int height= bitmap.getHeight();
            int max =Math.max(width, height);
            if(max >512) {
                float scale=512f / max;
                int w =Math.round(scale * width);
                int h =Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap,w, h, true);
            }
        } else if (kind== MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    96,
                    96,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    public static byte[] getThumbnilBaos(String filePath, int kind){
        Bitmap bitmap=null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (filePath.startsWith("http://")
                || filePath.startsWith("https://")
                || filePath.startsWith("widevine://")) {
            retriever.setDataSource(filePath,new Hashtable<String,String>());
        }else {
            retriever.setDataSource(filePath);
        }
        bitmap =retriever.getFrameAtTime(-1);
        if (bitmap==null){
            return null;
        }
        if (kind== MediaStore.Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width= bitmap.getWidth();
            int height= bitmap.getHeight();
            int max =Math.max(width, height);
            if(max >512) {
                float scale=512f / max;
                int w =Math.round(scale * width);
                int h =Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap,w, h, true);
            }
        } else if (kind== MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    96,
                    96,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes=baos.toByteArray();
        bitmap.recycle();
        return bytes;
    }
}

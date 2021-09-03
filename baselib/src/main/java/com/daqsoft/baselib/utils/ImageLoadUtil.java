package com.daqsoft.baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.widgets.timepicker.IMDensityUtil;

import daqsoft.com.baselib.R;


/**
 * @author jinxin
 * 剑之所指，心之所向
 * @date 2019/8/4
 *加载图片类
 */
public class ImageLoadUtil {
    /**
     * 聊天列表图片自适应
     */
    public static  void glidePersonLoadUrl(final Context context, final String url , final ImageView imageView) {
        Glide.with(context)
                .asBitmap()//强制Glide返回一个Bitmap对象
                .load(url)
                .fitCenter()
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .error(R.mipmap.placeholder_img_fail_240_180)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        setLayoutManager(context, width, height, imageView, url);
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    public static void  setLayoutManager(Context context,int w,int h ,ImageView view,String url){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width=Math.max(Math.min(IMDensityUtil.dip2px(context,300),w), IMDensityUtil.dip2px(context,150));
        params.height= (int) (1.0f * h/w*params.width);
        view.setLayoutParams(params);
    }



    /**
     * 聊天列表图片自适应
     */
    public static  void glideStageManage(final String url , final ImageView imageView) {

        Glide.with(BaseApplication.context)
                .asBitmap()//强制Glide返回一个Bitmap对象
                .load(url)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .error(R.mipmap.placeholder_img_fail_240_180)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        setStageLayoutManager( width, height, imageView, url);
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    public static void  setStageLayoutManager(int w,int h ,ImageView view,String url){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width=IMDensityUtil.getScreenWidth(BaseApplication.context) / 2 - IMDensityUtil.dip2px(BaseApplication.context,20f);
        params.height= (int) (1.0f * h/w*params.width);
        view.setLayoutParams(params);
    }


    public static void CommonGifLoadCp(Context context, String url, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.mipmap.placeholder_img_fail_240_180)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(context)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .apply(options)
                .into(imageView);

    }
}

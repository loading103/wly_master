package com.daqsoft.travelCultureModule.hotActivity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daqsoft.baselib.utils.GlideModuleUtil;
import com.daqsoft.baselib.utils.StringUtil;
import com.daqsoft.baselib.utils.Utils;
import com.daqsoft.baselib.widgets.ArcImageView;
import com.daqsoft.mainmodule.R;
import com.daqsoft.provider.databinding.ItemMuiltImageViewBinding;
import com.daqsoft.provider.utils.ThumbnilUtits;
import com.daqsoft.baselib.widgets.ArcImageView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description 不定图片个数显示控件
 * @ClassName MuitImageView
 * @Author PuHua
 * @Time 2020/1/17 15:01
 * @Version 1.0
 */
public class MuiltImageView extends RelativeLayout {
    ItemMuiltImageViewBinding binding;
    Context context;

    public MuiltImageView(Context context) {
        super(context);
        initView(context);
    }

    public MuiltImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MuiltImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化页面
     *
     * @param context
     */
    private void initView(Context context) {
        this.context = context;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_muilt_image_view,
                this,
                false
        );
        addView(binding.getRoot());
    }

    public void setImages(List<String> images) {

        if (images.size() == 1) {
            binding.vThreeImage.setVisibility(GONE);
            binding.aiImageOne.setVisibility(VISIBLE);
            GlideModuleUtil.INSTANCE.loadDqImageWaterMark(images.get(0), binding.aiImageOne, R.mipmap.placeholder_img_fail_240_180, R.mipmap.placeholder_img_fail_240_180);
        } else if (images.size() == 2) {
            binding.vThreeImage.setVisibility(VISIBLE);
            binding.aiImageOne.setVisibility(GONE);
            binding.aiImage2.setVisibility(VISIBLE);
            binding.aiImage3.setVisibility(GONE);
            GlideModuleUtil.INSTANCE.loadDqImageWaterMark(images.get(0), binding.aiImage1, R.mipmap.placeholder_img_fail_240_180, R.mipmap.placeholder_img_fail_240_180);
            GlideModuleUtil.INSTANCE.loadDqImageWaterMark(images.get(1), binding.aiImage2, R.mipmap.placeholder_img_fail_240_180, R.mipmap.placeholder_img_fail_240_180);
        } else if (images.size() >= 3) {
            binding.vThreeImage.setVisibility(VISIBLE);
            binding.aiImageOne.setVisibility(GONE);
            binding.aiImage2.setVisibility(VISIBLE);
            binding.aiImage3.setVisibility(VISIBLE);
            GlideModuleUtil.INSTANCE.loadDqImageWaterMark(images.get(0), binding.aiImage1, R.mipmap.placeholder_img_fail_240_180, R.mipmap.placeholder_img_fail_240_180);
            GlideModuleUtil.INSTANCE.loadDqImageWaterMark(images.get(1), binding.aiImage2, R.mipmap.placeholder_img_fail_240_180, R.mipmap.placeholder_img_fail_240_180);
            GlideModuleUtil.INSTANCE.loadDqImageWaterMark(images.get(2), binding.aiImage3, R.mipmap.placeholder_img_fail_240_180, R.mipmap.placeholder_img_fail_240_180);
        } else {
            binding.getRoot().setVisibility(GONE);
        }
    }


    public void setCover(String url) {
        binding.vThreeImage.setVisibility(GONE);
        binding.aiImageOne.setVisibility(VISIBLE);
        GlideModuleUtil.INSTANCE.loadDqImageWaterMark(url, 670, 336, binding.aiImageOne, R.mipmap.placeholder_img_fail_240_180, R.mipmap.placeholder_img_fail_240_180);
    }

    @SuppressLint("CheckResult")
    public void setVideoImage(String url) {
        // 当位视频时取第一帧
        binding.vThreeImage.setVisibility(GONE);
        binding.aiImageOne.setVisibility(VISIBLE);
//        Observable.just(url)
//                .map(new Function<String, byte[]>() {
//                    @Override
//                    public byte[] apply(String s) throws Exception {
//                        return ThumbnilUtits.getThumbnilBaos(s, MediaStore.Images.Thumbnails.MICRO_KIND);
//
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<byte[]>() {
//                    @Override
//                    public void accept(byte[] bytes) throws Exception {
//                        Glide.with(binding.aiImage1)
//                                .load(bytes)
//                                .into(binding.aiImage1);
//                    }
//                });
        GlideModuleUtil.INSTANCE.loadDqImage(StringUtil.INSTANCE.getVideoCoverUrl(url), binding.aiImageOne, R.mipmap.placeholder_img_fail_240_180, R.mipmap.placeholder_img_fail_240_180);
    }

}

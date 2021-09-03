package com.daqsoft.provider.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.daqsoft.baselib.utils.StringUtil;
import com.daqsoft.provider.R;
import com.daqsoft.provider.databinding.ItemMuiltImageViewBinding;

import java.util.List;

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
        binding.getRoot().setVisibility(View.VISIBLE);
        if (images.size() == 1) {
            binding.vThreeImage.setVisibility(GONE);
            binding.aiImageOne.setVisibility(VISIBLE);
            Glide.with(binding.aiImageOne)
                    .load(images.get(0))
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(binding.aiImageOne);
        } else if (images.size() == 2) {
            binding.vThreeImage.setVisibility(VISIBLE);
            binding.aiImageOne.setVisibility(GONE);
            binding.aiImage2.setVisibility(VISIBLE);
            binding.aiImage3.setVisibility(GONE);
            Glide.with(binding.aiImage1)
                    .load(images.get(0))
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(binding.aiImage1);
            Glide.with(binding.aiImage2)
                    .load(images.get(1))
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(binding.aiImage2);
        } else if (images.size() >= 3) {
            binding.vThreeImage.setVisibility(VISIBLE);
            binding.aiImageOne.setVisibility(GONE);
            binding.aiImage2.setVisibility(VISIBLE);
            binding.aiImage3.setVisibility(VISIBLE);
            Glide.with(binding.aiImage1)
                    .load(images.get(0))
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(binding.aiImage1);
            Glide.with(binding.aiImage2)
                    .load(images.get(1))
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(binding.aiImage2);
            Glide.with(binding.aiImage3)
                    .load(images.get(2))
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(binding.aiImage3);
        } else {
            binding.getRoot().setVisibility(GONE);
        }
    }



    public void setCover(String url) {
        binding.vThreeImage.setVisibility(GONE);
        binding.aiImageOne.setVisibility(VISIBLE);
        Glide.with(binding.aiImageOne)
                .load(url)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(binding.aiImageOne);
    }

    @SuppressLint("CheckResult")
    public void setVideoImage(String url) {
        binding.getRoot().setVisibility(View.VISIBLE);
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
        Glide.with(binding.aiImageOne)
                .load(StringUtil.INSTANCE.getVideoCoverUrl(url))
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(binding.aiImageOne);
    }

}

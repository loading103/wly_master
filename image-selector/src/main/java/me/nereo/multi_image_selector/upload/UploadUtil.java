package me.nereo.multi_image_selector.upload;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.MultiFileSelectorActivity;
import me.nereo.multi_image_selector.MultiVideoSelectorActivity;
import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.adapter.ImageListAdapter;
import me.nereo.multi_image_selector.bean.Constrant;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.bean.ImgBean;
import me.nereo.multi_image_selector.view.FileSelectWindow;

public class UploadUtil {

    /**
     * 最多选择图片数量
     */
    private int picNumber = 20;
    /**
     * 最多选择视频数量
     */
    private int videoNumber = 1;

    private int haveSelectVideoNumber = 0;

    public List<Image> mImages = new ArrayList<>();

    private Activity mContext;
    // Constrant.ADD_IMAGE 为展示图片 Constrant.ADD_VIDEO 为展示视频
    private int type = Constrant.ADD_IMAGE;
    private String[] items = {"图片", "视频"};

    private FileSelectWindow window;
    /**
     * 2020-11-26日 更换
     * http://file.geeker.com.cn/
     */
    private String baseUrl = "https://file.geeker.com.cn/";
    //是否单选,默认多选
    private boolean isSingle = false;

    /**
     * 是否显示删除按钮
     */
    boolean isShowDelete = true;

    boolean isNeedShowSelect = true;

    int SELECT_MODE = 0;

    public UploadUtil(FragmentActivity context) {
        mContext = context;
        View listContent = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pop_select_file, null, false);
        window = new FileSelectWindow(context, listContent, items, item -> {
            if (item.equals(items[0])) {
                choicePicture
                        (picNumber + haveSelectVideoNumber - mImages.size
                                (), Constrant.ADD_IMAGE);
            } else {
                if (videoNumber > haveSelectVideoNumber) {
                    choiceVideo(videoNumber - haveSelectVideoNumber
                            , Constrant.ADD_VIDEO);
                } else {
                    Toast.makeText(context, "非常抱歉，限制最大视频选择数目为" + videoNumber,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }
    public void setmImages(List<Image> mImages) {
        this.mImages = mImages;
    }

    public void setMaxVideoNumber(int maxVideoNumber) {
        this.videoNumber = maxVideoNumber;
    }

    public void setHaveSelectVideoNumber(int number) {
        this.haveSelectVideoNumber = number;
    }

    public void showSelect(View view, Activity activity) {
        if (isNeedShowSelect) {
            window.showBottom(view, activity);
        } else {
            if (SELECT_MODE == 0) {
                choicePicture
                        (picNumber + haveSelectVideoNumber - mImages.size
                                (), Constrant.ADD_IMAGE);
            } else {
                if (videoNumber > haveSelectVideoNumber) {
                    choiceVideo(videoNumber - haveSelectVideoNumber
                            , Constrant.ADD_VIDEO);
                } else {
                    Toast.makeText(mContext, "非常抱歉，限制最大视频选择数目为" + videoNumber,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void setPicNumber(int number) {
        this.picNumber = number;
    }

    public void setIsNeedShowSelect(boolean showSelect) {
        this.isNeedShowSelect = showSelect;
    }

    public void setUploadMode(int mode) {
        this.SELECT_MODE = mode;
    }

    public void init(RecyclerView recyclerView, FragmentActivity context, boolean isShowDelete) {

    }

    public void onActivityResult(ArrayList<Image> list) {
//        initGridView(list,isShowDelete);
    }


    /**
     * 从手机相册中选择
     */
    private void choicePicture(int num, int t) {
        if(picNumber - haveSelectVideoNumber - mImages.size()>0){
            num=picNumber  - mImages.size();
        }else {
            Toast.makeText(mContext, "非常抱歉，限制最大选择数目为"+picNumber+"个", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(mContext, MultiFileSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiFileSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大图片选择数量
        intent.putExtra(MultiFileSelectorActivity.EXTRA_SELECT_COUNT, num);
        // 设置模式 (支持 单选/MultiFileSelectorActivity.MODE_SINGLE 或者 多选/MultiFileSelectorActivity
        // .MODE_MULTI)
        intent.putExtra(MultiFileSelectorActivity.EXTRA_SELECT_MODE, isSingle ? MultiFileSelectorActivity
                .MODE_SINGLE : MultiFileSelectorActivity
                .MODE_MULTI);
        this.type = Constrant.ADD_IMAGE;
        intent.putExtra("TYPE", type);
        // 默认选择
//        if (mImages != null && mImages.size() > 0) {
//            intent.putParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, (ArrayList<? extends Parcelable>) mImages);
//        }
        mContext.startActivityForResult(intent, t);
    }

    /**
     * 选择视频
     */
    private void choiceVideo(int num, int t) {
        if(mImages.size()>=picNumber) {
            Toast.makeText(mContext, "非常抱歉，限制最大选择数目为"+picNumber+"个", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(mContext, MultiVideoSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiFileSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大视频选择数量
        intent.putExtra(MultiFileSelectorActivity.EXTRA_SELECT_COUNT, num);
        // 设置模式 (支持 单选/MultiFileSelectorActivity.MODE_SINGLE 或者 多选/MultiFileSelectorActivity
        // .MODE_MULTI)
        intent.putExtra(MultiFileSelectorActivity.EXTRA_SELECT_MODE, isSingle ? MultiFileSelectorActivity
                .MODE_SINGLE : MultiFileSelectorActivity
                .MODE_MULTI);
        this.type = Constrant.ADD_VIDEO;
        intent.putExtra("TYPE", type);
        // 默认选择
//        if (mImages != null && mImages.size() > 0) {
//            intent.putParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, (ArrayList<? extends Parcelable>) mImages);
//        }
        mContext.startActivityForResult(intent, t);
    }

    /**
     * 上传单个文件
     *
     * @param data
     * @param fileBack
     * @param progressBar
     */
    public void uploadFile(Image data, FileUpload.FileBack fileBack, ProgressBar progressBar) {
        FileUpload.uploadFile(baseUrl, mContext, data, fileBack, progressBar);
    }

    /**
     * 上传多个文件
     *
     * @param images
     * @param back
     */
    public void uploadFiles(ArrayList<Image> images, FileUpload.UploadFileBack back) {
        FileUpload.uploadFile(baseUrl, mContext, images, back);
    }
}

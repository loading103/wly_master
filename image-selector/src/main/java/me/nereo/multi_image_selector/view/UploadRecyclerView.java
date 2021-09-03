package me.nereo.multi_image_selector.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.daqsoft.baselib.widgets.layoutmanager.ExStaggeredGridLayoutManager;
import com.daqsoft.baselib.widgets.layoutmanager.FullyGridLayoutManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.MultiFileSelectorActivity;
import me.nereo.multi_image_selector.adapter.ImageListAdapter;
import me.nereo.multi_image_selector.bean.Constrant;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.bean.ImgBean;
import me.nereo.multi_image_selector.upload.UploadUtil;
import me.nereo.multi_image_selector.utils.FileUtils;

/**
 * @Description 上传图片所使用的recyclerView
 * @ClassName UploadRecyclerView
 * @Author PuHua
 * @Time 2020/2/22 11:44
 * @Version 1.0
 */
public class UploadRecyclerView extends RecyclerView implements ImageListAdapter.OnAddClickListener {

    @Override
    public boolean canScrollVertically(int direction) {
//        return super.canScrollVertically(direction);
        return false;
    }

    /**
     * 最多选择图片数量
     */
    private int picNumber = 20;
    /**
     * 最大选择视频数目
     */
    public int maxPicVideoNum = 1;
    /**
     * 已选择的视频数目
     */
    public int picVideoNumBer = 0;
    /**
     * 原有数据（上传成功的数据）
     */
    private List<Image> originData = new ArrayList<>();

    private List<Image> pictureList;

    private FragmentActivity mContext;
    // 0 为展示图片 1 为展示视频
    private int type = 0;
    /**
     * 文件上传基本路径
     * http://file.geeker.com.cn
     */
    private String baseUrl = "https://file.geeker.com.cn/";

    UploadUtil uploadUtil;

    public void setShowDelete(boolean showDelete) {
        isShowDelete = showDelete;
    }

    /**
     * 是否显示删除按钮
     */
    boolean isShowDelete = true;
    /**
     * 是否显示添加按钮
     */
    boolean isNeedAddPic = true;

    public boolean isNeedShowSelect = true;

    public int SELECT_MODE = -1;

    public void setPicNumber(int number) {
        this.picNumber = number;
        if (uploadUtil != null) {
            uploadUtil.setPicNumber(number);
        }
    }

    public void setMaxVideoNumer(int number) {
        this.maxPicVideoNum = number;
        if (uploadUtil != null) {
            uploadUtil.setMaxVideoNumber(number);
        }
    }

    public void setIsNeedShowSelect(boolean flag) {
        this.isNeedShowSelect = flag;
        if (uploadUtil != null) {
            uploadUtil.setIsNeedShowSelect(flag);
        }
    }

    public void setSelectMode(int mode) {
        this.SELECT_MODE = mode;
        if (uploadUtil != null) {
            uploadUtil.setUploadMode(mode);
        }
    }

    public ImageListAdapter imageAdapter;


    public UploadRecyclerView(@NonNull Context context) {
        super(context);
    }

    public UploadRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UploadRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(FragmentActivity context, boolean isShowDelete, boolean isShowAdd) {
        initPhotoError();
        uploadUtil = new UploadUtil(context);
        uploadUtil.setPicNumber(picNumber);
        uploadUtil.setMaxVideoNumber(maxPicVideoNum);
        uploadUtil.setIsNeedShowSelect(isNeedShowSelect);
        uploadUtil.setUploadMode(SELECT_MODE);
        isNeedAddPic = isShowAdd;
        mContext = context;
        FullyGridLayoutManager linearLayoutManager = new FullyGridLayoutManager(context, 4,
                FullyGridLayoutManager.VERTICAL, false);
        setLayoutManager(linearLayoutManager);
        pictureList = new ArrayList<>();
        imageAdapter = new ImageListAdapter(baseUrl, mContext, picNumber, isShowDelete, isShowAdd, isNeedShowSelect, SELECT_MODE);
        this.isShowDelete = isShowDelete;
        if (isShowAdd) {
            pictureList.add(new Image("", "", 0));
        }
        imageAdapter.setOnAddClickListener(this);
        imageAdapter.setData(pictureList);

        setAdapter(imageAdapter);
    }

    public void onActivityResult(ArrayList<Image> list) {
        initGridView(list, isShowDelete);
    }

    public void insertAtFirst(Image image) {
        pictureList.add(0, image);
        if (image.type == 1) {
            picVideoNumBer = +1;
        }

        Image needRemoveItem = null;
        for (Image item : pictureList) {
            if (item.path.equals("")) {
                needRemoveItem = item;
            }
        }

        if (needRemoveItem != null) {
            pictureList.remove(needRemoveItem);
        }

        if (pictureList.size() < (picNumber + maxPicVideoNum) && isShowDelete && isNeedAddPic) {
            pictureList.add(new Image("", "", 0));
        }
        if (!isNeedAddPic) {
            imageAdapter.mImages.clear();
            imageAdapter.mImages.addAll(pictureList);
        }
        imageAdapter.notifyDataSetChanged();
    }

    public void addVideo(Image video) {
        pictureList.add(0, video);
        if (video.type == 1) {
            picVideoNumBer = +1;
        }
        Image needRemoveItem = null;
        for (Image item : pictureList) {
            if (item.path.equals("")) {
                needRemoveItem = item;
            }
        }
        if (needRemoveItem != null) {
            pictureList.remove(needRemoveItem);
        }

        if (pictureList.size() < maxPicVideoNum && isShowDelete && isNeedAddPic) {
            pictureList.add(new Image("", "", 0));
        }
        if (!isNeedAddPic) {
            imageAdapter.mImages.clear();
            imageAdapter.mImages.addAll(pictureList);
        }
        imageAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化gridView
     */
    public void initGridView(List<Image> imageList, boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        imageAdapter.setShowDelete(isShowDelete);
        if (pictureList.size() >= 1 && pictureList.get(pictureList.size() - 1).path.equals("") && isNeedAddPic) {
            pictureList.remove(pictureList.size() - 1);
        }
        pictureList.addAll(imageList);

        if (pictureList.size() < (picNumber + picVideoNumBer) && isShowDelete && isNeedAddPic) {
            pictureList.add(new Image("", "", 0));
        }
        if (!isNeedAddPic) {
            imageAdapter.mImages.clear();
            imageAdapter.mImages.addAll(pictureList);
        }
        imageAdapter.notifyDataSetChanged();

    }


    public void addData(List<Image> imageList) {
//        if ()
        for (Image pictureEntity : pictureList) {
            if (pictureEntity.path.equals("")) {
                pictureList.remove(pictureEntity);
            }
        }

    }

    public String getPath() {
        StringBuilder path = new StringBuilder();
        if (!imageAdapter.uploadBackList.isEmpty()) {
            for (Image im :
                    imageAdapter.uploadBackList) {
                path.append(im.path).append(",");
            }

        } else {
            for (Image im : pictureList) {
                if (!im.path.equals(""))
                    path.append(im.path).append(",");
            }
        }
        if (path.length() > 0) {
            path.deleteCharAt(path.length() - 1);
        }
        return path.toString();
    }

    public void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }


    @Override
    public void onAddClick(ImageView image) {
        List<Image> images = pictureList;
        if (images.size() > 0 && images.get(images.size() - 1).path.equals("")) {
            images.remove(images.size() - 1);
        }
        uploadUtil.setmImages(images);
        uploadUtil.setHaveSelectVideoNumber(picVideoNumBer);
        uploadUtil.showSelect(image, mContext);
    }

    public void showSelectImages() {
        List<Image> images = pictureList;
        if (images.size() > 0 && images.get(images.size() - 1).path.equals("")) {
            images.remove(images.size() - 1);
        }
        uploadUtil.setmImages(images);
        uploadUtil.setHaveSelectVideoNumber(picVideoNumBer);
        uploadUtil.showSelect(this, mContext);
    }

    @Override
    public void onDelCick(Image image, int index) {
        // 同步删除数据
        if (picVideoNumBer > 0 && image.type == 1) {
            picVideoNumBer = picVideoNumBer - 1;
        }
        if (pictureList != null && pictureList.size() > 0 && index < pictureList.size())
            pictureList.remove(index);
    }
}

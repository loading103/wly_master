package me.nereo.multi_image_selector.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daqsoft.baselib.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.commonsdk.debug.E;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.nereo.multi_image_selector.BigImgActivity;
import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.bean.ImgBean;
import me.nereo.multi_image_selector.upload.FileUpload;
import me.nereo.multi_image_selector.utils.FileUtils;
import me.nereo.multi_image_selector.video.PlayerActivity;
import timber.log.Timber;

/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class ImageListAdapter extends RecyclerView.Adapter {

    /**
     * 默认图片
     */
    private int defult = R.drawable.story_add_pic;
    private FragmentActivity mContext;
    /**
     * 最多选择图片数量
     */
    private int picNumber = 20;
    private LayoutInflater mInflater;

    public List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    private Image addImage;


    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    private OnAddClickListener onAddClickListener;

    /**
     * 用來存放上傳發揮的數據
     */
    public List<Image> uploadBackList = new ArrayList<>();
    /**
     * 控件默认的选图模式
     */
    private int type;
    private String baseUrl = "";
    /**
     * 是否显示删除按钮
     */
    boolean isShowDelete = true;

    boolean isShowAddPic = true;
    boolean isNeedShowSelect = true;

    int SELECT_MODE = 0;

    private String[] items = {"图片", "视频"};

    public ImageListAdapter(String url, FragmentActivity context, int picNumber, boolean isShowDelete, boolean isShowAddPic, boolean isNeedShowSelect, int mode) {
        this.baseUrl = url;
        mContext = context;

        this.isShowDelete = isShowDelete;
        this.isNeedShowSelect = isNeedShowSelect;
        this.SELECT_MODE = mode;
        setIsHasAdd(isShowAddPic);
        addImage = new Image("", "", 0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            width = wm.getDefaultDisplay().getWidth();
        }
        this.picNumber = picNumber;

    }

    /**
     * 选择某个图片，改变选择状态
     *
     * @param image
     */
    public void select(Image image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    public void setIsHasAdd(Boolean isHasAdd) {
        isShowAddPic = isHasAdd;
    }

    private Image getImageByPath(String path) {
        if (mImages != null && mImages.size() > 0) {
            for (Image image : mImages) {
                if (image.path.equalsIgnoreCase(path)) {
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     *
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();
        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 设置数据集
     */
    public void setData(Image image) {
        mSelectedImages.clear();
        if (image != null) {
            if (mImages != null) {
                mImages.add(image);
            } else {
                mImages = new ArrayList<>();
            }
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder;

        View view = mInflater.inflate(R.layout.complaint_imageview_layout, parent, false);
        holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            ((ViewHolder) holder).bindData(mImages.get(position));
        }
    }


    public void setShowDelete(boolean showDelete) {
        isShowDelete = showDelete;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView delete;
        View mask;
        TextView fileName;
        ImageView itemVideo;
        TextView itemAudio;
        ProgressBar progressBar;
        TextView uploadFail;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.rounded_item_image);
            delete = view.findViewById(R.id.item_imgage_delete);
            if (isShowDelete) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }
            mask = view.findViewById(R.id.mask);
            fileName = view.findViewById(R.id.file_name);
            itemAudio = view.findViewById(R.id.item_audio);
            itemVideo = view.findViewById(R.id.item_video);
            progressBar = view.findViewById(R.id.progress_bar_h);
            uploadFail = view.findViewById(R.id.item_upload);
            view.setTag(this);
        }


        void bindData(final Image data) {
            if (data == null) return;

            if (data.path.equals("")) {
                // 展示添加按钮
                bindDefaultAddIcon();
            } else {
                // 是否展示删除按钮
                bindShowDeleteIcon();
                if (!data.path.startsWith("http")) {
                    // 为本地地址时先上传文件
                    bindUpload(data);
                } else {
                    progressBar.setVisibility(View.GONE);
                    mask.setVisibility(View.GONE);
                }

                // 为网络图片时直接展示
                if (FileUtils.isVideo(data.path)) {
                    // 展示视频
                    bindVideo(data);
                } else if (FileUtils.isAudio(data.path)) {
                    // 音频
                } else {
                    // 图片
                    bindImage(data);

                }
                uploadFail.setVisibility(View.GONE);

                uploadFail.setOnClickListener(v -> {
                    uploadFail.setVisibility(View.GONE);
                    bindUpload(data);
                });

                delete.setOnClickListener(v -> {
                    if (!data.path.equals("")) {
                        int index = mImages.indexOf(data);
                        if (onAddClickListener != null)
                            onAddClickListener.onDelCick(data, index);
                        mImages.remove(data);
                        uploadFail.setVisibility(View.GONE);
                        uploadBackList.remove(data);
//                        notifyItemRemoved(index);
                        notifyDataSetChanged();
                    }


                    refreshItemTypeStatus();
                    if (!isHadAddItem && isShowAddPic) {
                        mImages.add(new Image("", "", 0));
                        try {
                            notifyItemInserted(mImages.size() - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            notifyDataSetChanged();
                        }
                    }

////                    if (!(hadImgNum == picNumber && isHadVideoItem && isHadAddItem)) {
////                        mImages.add(new Image("", "", 0));
//////                        notifyItemInserted(mImages.size() - 1);
////                    }


//                    notifyDataSetChanged();
                });
            }
        }

        /**
         * 展示添加按钮
         */
        private void bindDefaultAddIcon() {
            itemVideo.setVisibility(View.GONE);
            itemAudio.setVisibility(View.GONE);
            if (isNeedShowSelect) {
                image.setImageResource(defult);
            } else {
                if (SELECT_MODE == 0) {
                    image.setImageResource(R.drawable.vote_join_button_upload_pic);
                } else if (SELECT_MODE == 1) {
                    image.setImageResource(R.drawable.vote_join_button_upload_video);
                } else {
                    image.setImageResource(defult);
                }
            }
            delete.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mask.setVisibility(View.GONE);
            image.setOnClickListener(v -> {
                RxPermissions rxPermission = new RxPermissions(mContext);
                Disposable disposable = rxPermission.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            // 成功
                            if (aBoolean) {
                                if (onAddClickListener != null)
                                    onAddClickListener.onAddClick(image);
                            } else {
//                              ToastUtil.getInstance.s("未获取相关权限");
                            }

                        });
            });
        }

        /**
         * 展示图片
         *
         * @param data
         */
        public void bindImage(final Image data) {
            itemVideo.setVisibility(View.GONE);
            itemAudio.setVisibility(View.GONE);
            Glide.with(mContext)
                    .load(data.path)
                    .into(image);

            image.setOnClickListener(l -> {
                Intent intent = new Intent(mContext, BigImgActivity.class);
                intent.putExtra("position", getAdapterPosition());

                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < mImages.size(); i++) {
                    list.add(mImages.get(i).path);
                }

                intent.putStringArrayListExtra("imgList", list);
                mContext.startActivity(intent);
            });
        }

        private boolean isHadAddItem = false;
        private boolean isHadVideoItem = false;
        private int hadImgNum = 0;

        private void refreshItemTypeStatus() {
            isHadAddItem = false;
            isHadVideoItem = false;
            hadImgNum = 0;
            for (Image image : mImages) {
                if (FileUtils.isVideo(image.path)) {
                    isHadVideoItem = true;
                } else if (image.path.equals("")) {
                    isHadAddItem = true;
                } else {
                    hadImgNum++;
                }
            }
        }


        /**
         * 展示上传文件
         *
         * @param data
         */
        void bindUpload(final Image data) {
            progressBar.setVisibility(View.VISIBLE);
            mask.setVisibility(View.VISIBLE);
            Timber.e(data.path);
            FileUpload.uploadFile(baseUrl, mContext, data, new FileUpload.FileBack() {
                @Override
                public void result(ImgBean imgBean) {
                    if (imgBean != null) {
                        uploadFail.setVisibility(View.GONE);
                        mask.setVisibility(View.GONE);
                        if (imgBean.getError() == 0) {
                            data.path = imgBean.getUrl();
                            data.status = 200;
                            for (int i = 0; i < uploadBackList.size(); i++) {
                                Image bean = uploadBackList.get(i);
                                if (bean.path != data.path) {
                                    uploadBackList.add(data);
                                }
                            }
                        }

//                        refreshItemTypeStatus();
//                        if (!isHadAddItem) {
//                            mImages.add(new Image("", "", 0));
//                            try {
//                                notifyItemInserted(mImages.size() - 1);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                notifyDataSetChanged();
//                            }
//                        }

//            if (hadImgNum == picNumber && mImages.size() >= picNumber + 1 && !isHadVideoItem) {
//                ToastUtils.showMessage("最多上传" + picNumber + "张图片和1个视频");
//                int index = mImages.indexOf(data);
//                mImages.remove(data);
////                notifyItemRemoved(index);
//                notifyDataSetChanged();
//                return;
//               }

                    } else {
                        uploadFail.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void progress(int progress) {

                }
            }, progressBar);
        }

        /**
         * 展示删除按钮
         */
        void bindShowDeleteIcon() {
            if (isShowDelete) {
                // 视频
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }
        }

        /**
         * 展示视频
         *
         * @param data
         */
        void bindVideo(final Image data) {
            itemVideo.setVisibility(View.VISIBLE);
            itemAudio.setVisibility(View.GONE);
            image.setOnClickListener(l -> {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("title", "视频播放");
                intent.putExtra("url", data.path);
                mContext.startActivity(intent);
            });
            // 异步加载视频缩略图
            Glide.with(mContext)
                    .setDefaultRequestOptions(new RequestOptions()
                            .frame(1000000)
                            .centerCrop()
                    )
                    .load(data.path)
                    .into(image);
        }


    }


    /**
     * 文件大小转化工具
     *
     * @param size
     * @return
     */
    public String readableFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public interface OnAddClickListener {
        void onAddClick(ImageView image);

        void onDelCick(Image image, int index);
    }


}

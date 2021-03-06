package me.nereo.multi_image_selector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.components.support.RxFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.nereo.multi_image_selector.adapter.FolderAdapter;
import me.nereo.multi_image_selector.adapter.ImageGridAdapter;
import me.nereo.multi_image_selector.bean.Folder;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.utils.FileUtils;
import me.nereo.multi_image_selector.utils.ScreenUtils;

/**
 * ????????????Fragment
 * Created by Nereo on 2015/4/7.
 */
public class MultiFileSelectorFragment extends RxFragment {

    public static final String TAG = "me.nereo.multi_image_selector.MultiFileSelectorFragment";

    private static final String KEY_TEMP_FILE = "key_temp_file";

    /**
     * ???????????????????????????int??????
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * ?????????????????????int??????
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * ?????????????????????boolean??????
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * ????????????????????????
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
    /**
     * ??????
     */
    public static final int MODE_SINGLE = 0;
    /**
     * ??????
     */
    public static final int MODE_MULTI = 1;
    // ??????loader??????
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    // ???????????????????????????
    private static final int REQUEST_CAMERA = 100;


    // ????????????
    private ArrayList<Image> resultList = new ArrayList<>();

    // ????????????
    private ArrayList<String> timeList = new ArrayList<>();
    // ???????????????
    private ArrayList<Folder> mResultFolder = new ArrayList<>();

    // ??????Grid
    private GridView mGridView;
    private Callback mCallback;

    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;

    private ListPopupWindow mFolderPopupWindow;

    // ??????
    private TextView mCategoryText;
    // ????????????
    private Button mPreviewBtn;
    // ??????View
    private View mPopupAnchorView;

    private int mDesireImageCount;

    private boolean hasFolderGened = false;
    private boolean mIsShowCamera = false;

    private File mTmpFile;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback " +
                    "interface...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_multi_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ??????????????????
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);

        // ??????????????????
        final int mode = getArguments().getInt(EXTRA_SELECT_MODE);

        // ????????????
        if (mode == MODE_MULTI) {
            ArrayList<Image> tmp = getArguments().getParcelableArrayList(EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.size() > 0) {
                resultList = tmp;
            }
        }

        // ?????????????????????
        mIsShowCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);
        mImageAdapter = new ImageGridAdapter(getActivity(), mIsShowCamera, 3);
        // ???????????????????????????
        mImageAdapter.showSelectIndicator(mode == MODE_MULTI);

        mPopupAnchorView = view.findViewById(R.id.footer);

        mCategoryText = (TextView) view.findViewById(R.id.category_btn);
        // ??????????????????????????????
        mCategoryText.setText(R.string.folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

        mPreviewBtn = (Button) view.findViewById(R.id.preview);
        // ?????????????????????????????????
        if (resultList == null || resultList.size() <= 0) {
            mPreviewBtn.setText(R.string.preview);
            mPreviewBtn.setEnabled(false);
        }
        mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO ??????
            }
        });

        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setAdapter(mImageAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mImageAdapter.isShowCamera()) {
                    // ????????????????????????????????????Grid???????????????????????????????????????
                    if (i == 0) {
                        showCameraAction();
                    } else {
                        // ????????????
                        Image image = (Image) adapterView.getAdapter().getItem(i);
                        selectImageFromGrid(image, mode);
                    }
                } else {
                    // ????????????
                    Image image = (Image) adapterView.getAdapter().getItem(i);
                    selectImageFromGrid(image, mode);
                }
            }
        });
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_FLING) {
                    Picasso.with(view.getContext()).pauseTag(TAG);
                } else {
                    Picasso.with(view.getContext()).resumeTag(TAG);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mFolderAdapter = new FolderAdapter(getActivity());
    }

    /**
     * ???????????????ListView
     */
    private void createPopupFolderList() {
        Point point = ScreenUtils.getScreenSize(getActivity());
        int width = point.x;
        int height = (int) (point.y * (4.5f / 8.0f));
        mFolderPopupWindow = new ListPopupWindow(getActivity());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height);
        mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (mFolderAdapter.getSelectIndex() != i) {
                    mFolderAdapter.setSelectIndex(i);

                    final int index = i;
                    final AdapterView v = adapterView;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFolderPopupWindow.dismiss();

                            if (index == 0) {
                                getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                                mCategoryText.setText(R.string.folder_all);
                                if (mIsShowCamera) {
                                    mImageAdapter.setShowCamera(true);
                                } else {
                                    mImageAdapter.setShowCamera(false);
                                }
                            } else {
                                Folder folder = (Folder) v.getAdapter().getItem(index);
                                if (null != folder) {
                                    mImageAdapter.setData(folder.images);
                                    mCategoryText.setText(folder.name);
                                    // ??????????????????
                                    if (resultList != null && resultList.size() > 0) {
                                        mImageAdapter.setDefaultSelected(resultList);
                                    }
                                }
                                mImageAdapter.setShowCamera(false);
                            }

                            // ????????????????????????
                            mGridView.smoothScrollToPosition(0);
                        }
                    }, 100);
                }


            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_TEMP_FILE, mTmpFile);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mTmpFile = (File) savedInstanceState.getSerializable(KEY_TEMP_FILE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // ????????????????????????
        //new LoadImageTask().execute();
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // ??????????????????????????????????????????
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mTmpFile);
                    }
                }
            } else {
                while (mTmpFile != null && mTmpFile.exists()) {
                    boolean success = mTmpFile.delete();
                    if (success) {
                        mTmpFile = null;
                    }
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mFolderPopupWindow != null) {
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * ????????????
     */
    private void showCameraAction() {
        // ????????????????????????
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // ??????????????????????????????????????????
            // ??????????????????
            try {
                mTmpFile = FileUtils.createTmpFile(getActivity(), "");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ??????????????????
     *
     * @param image
     */
    private void selectImageFromGrid(Image image, int mode) {
        if (image != null) {
            // ????????????
            if (mode == MODE_MULTI) {
                if (resultList.contains(image)) {
                    timeList.remove(image.time);
                    resultList.remove(image);
                    if (resultList.size() != 0) {
                        mPreviewBtn.setEnabled(true);
                        mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + resultList.size() + ")");
                    } else {
                        mPreviewBtn.setEnabled(false);
                        mPreviewBtn.setText(R.string.preview);
                    }
                    if (mCallback != null) {
                        mCallback.onImageUnselected(image);
                        mCallback.onImageUnselectedTime(image.path, MultiImageSelectorActivity
                                .getDateLongToString(image.time, "yyyy-MM-dd"));

                    }
                } else {
                    // ????????????????????????
                    if (mDesireImageCount == resultList.size()) {
                        Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resultList.add(image);
                    timeList.add(image.time + "");
                    mPreviewBtn.setEnabled(true);
                    mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + resultList.size() + ")");
                    if (mCallback != null) {
                        mCallback.onImageSelected(image);
                        mCallback.onImageSelectedTime(image.path, MultiFileSelectorActivity.getDateLongToString
                                (image.time, "yyyy-MM-dd"));
                        Log.e("time", image.time + "");
                    }
                }
                mImageAdapter.select(image);
            } else if (mode == MODE_SINGLE) {
                // ????????????
                if (mCallback != null) {
                    mCallback.onSingleImageSelected(image);
                }
            }
        }
    }

    String[] MEDIA_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.PARENT};

    private boolean fileExist(String path) {
        if (!TextUtils.isEmpty(path)) {
            return new File(path).exists();
        }
        return false;
    }

    private final LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri queryUri = MediaStore.Files.getContentUri("external");
            if (id == LOADER_ALL) {
                String selection = /*MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + " OR " +*/ MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                CursorLoader cursorLoader = new CursorLoader(
                        getActivity(),
                        queryUri,
                        MEDIA_PROJECTION,
                        selection,
                        null, // Selection args (none).
                        MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
                );

                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        queryUri, MEDIA_PROJECTION,
                        MEDIA_PROJECTION[4] + ">0 AND " + MEDIA_PROJECTION[0] + " like '%" + args.getString("path") +
                                "%'",
                        null, MEDIA_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if (data.getCount() > 0) {
                    getThumbNil(data);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    private void getThumbNil(Cursor data) {
        // ??????????????????
        Disposable disposable = Observable.just("")
                .map(s -> {
                    List<Image> images = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(MEDIA_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(MEDIA_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(MEDIA_PROJECTION[2]));
                        long id = data.getLong(data.getColumnIndexOrThrow(MEDIA_PROJECTION[5]));
                        int mime = data.getInt(data.getColumnIndexOrThrow(MEDIA_PROJECTION[3]));
                        int size = data.getInt(data.getColumnIndexOrThrow(MEDIA_PROJECTION[4]));
                        if (size > 0) {
                            Image image = null;
                            if (fileExist(path)) {
                                image = new Image(path, name, dateTime);
                                image.id = id;
                                image.mediaType = mime;
                                image.size = size;
                                image.path = path;
                            }
                            if (!hasFolderGened) {
                                // ?????????????????????
                                File folderFile = new File(path).getParentFile();
                                if (folderFile != null && folderFile.exists()) {
                                    String fp = folderFile.getAbsolutePath();
                                    Folder f = getFolderByPath(fp);
                                    if (f == null) {
                                        Folder folder = new Folder();
                                        folder.name = folderFile.getName();
                                        folder.path = fp;
                                        folder.cover = image;
                                        List<Image> imageList = new ArrayList<>();
                                        imageList.add(image);
                                        folder.images = imageList;
                                        mResultFolder.add(folder);
                                    } else {
                                        f.images.add(image);
                                    }
                                }
                            }
                            if(image!=null){
                                images.add(image);
                            }
                        }

                    } while (data.moveToNext());
                    return images;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(ProgressUtils.applyProgressBar(getActivity(), true))
                .compose(bindUntilEvent(FragmentEvent.DESTROY)).subscribe(images -> {
//                    ProgressUtils.dismissProgress();
                    mImageAdapter.setData(images);
                    // ??????????????????
                    if (resultList != null && resultList.size() > 0) {
                        mImageAdapter.setDefaultSelected(resultList);
                    }

                    if (!hasFolderGened) {
                        mFolderAdapter.setData(mResultFolder);
                        hasFolderGened = true;
                    }
                });
    }

    private Folder getFolderByPath(String path) {
        if (mResultFolder != null) {
            for (Folder folder : mResultFolder) {
                if (TextUtils.equals(folder.path, path)) {
                    return folder;
                }
            }
        }
        return null;
    }

    /**
     * ????????????
     */
    public interface Callback {
        void onSingleImageSelected(Image path);

        void onImageSelected(Image path);

        void onImageUnselected(Image path);

        void onCameraShot(File imageFile);

        void onImageSelectedTime(String path, String time);

        void onImageUnselectedTime(String path, String time);
    }
}

package me.nereo.multi_image_selector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.nereo.multi_image_selector.bean.Image;

/**
 * 文件选择，包括音频，视频，图片
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class MultiVideoSelectorActivity extends FragmentActivity implements MultiVideoSelectorFragment.Callback {

    /**
     * 最大图片选择次数，int类型，默认9
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT_TIME = "select_result_time";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;
    /**
     * 选择的图片集合
     */
    private ArrayList<Image> resultList = new ArrayList<>();
    /**
     * 选择的图片集合
     */
    private HashMap<String, String> timeMap = new HashMap<>();
    private ArrayList<String> timeList = new ArrayList<>();
    private Button mSubmitButton;
    private int mDefaultCount;
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getParcelableArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
            if (resultList.size() > 0) {
                resultList.remove(resultList.size() - 1);
            }
        }
        type = intent.getIntExtra("TYPE", 0);

        Bundle bundle = new Bundle();
        bundle.putInt(MultiVideoSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiVideoSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiVideoSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putParcelableArrayList(MultiVideoSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiVideoSelectorFragment.class.getName(), bundle))
                .commit();

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // 完成按钮
        mSubmitButton = (Button) findViewById(R.id.commit);
        if (resultList == null || resultList.size() <= 0) {
            mSubmitButton.setText(R.string.action_done);
            mSubmitButton.setEnabled(false);
        } else {
            updateDoneText();
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resultList != null && resultList.size() > 0) {
                    // 返回已选择的图片数据
                    Intent data = new Intent();
                    data.putParcelableArrayListExtra(EXTRA_RESULT, resultList);
                    data.putStringArrayListExtra(EXTRA_RESULT_TIME, timeList);
                    setResult(0, data);
                    finish();
                }
            }
        });
    }

    private void updateDoneText() {
        mSubmitButton.setText(String.format("%s(%d/%d)",
                getString(R.string.action_done), resultList.size(), mDefaultCount));
    }

    @Override
    public void onSingleImageSelected(Image path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putParcelableArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(Image path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if (resultList.size() > 0) {
            updateDoneText();
            if (!mSubmitButton.isEnabled()) {
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(Image path) {
        if (resultList.contains(path)) {
            resultList.remove(path);
        }
        updateDoneText();
        // 当为选择图片时候的状态
        if (resultList.size() == 0) {
            mSubmitButton.setText(R.string.action_done);
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {

            // notify system
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

            Intent data = new Intent();
            Image image = new Image(imageFile.getAbsolutePath(), imageFile.getName(), 0);
            resultList.add(image);
            data.putParcelableArrayListExtra(EXTRA_RESULT, resultList);
            setResult(0, data);
            finish();
        }
    }

    @Override
    public void onImageSelectedTime(String path, String time) {
        if (timeMap.get(path) == null
                || timeMap.get(path).equals("")) {
            timeMap.put(path, time);
        }
        timeList.clear();
        for (Map.Entry<String, String> entry : timeMap.entrySet()) {
            timeList.add(initTime(entry.getKey() + ""));
        }
        Log.e("time", timeList.toString());
        initTime(path);
    }

    public String initTime(String path) {
        File file = new File(path);
        Long fileTime = file.lastModified();
        return getDateLongToString(fileTime, "yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void onImageUnselectedTime(String path, String time) {
        if (timeMap.get(path)!=null&&!timeMap.get(path).equals("")) {
            timeMap.remove(path);
        }
        timeList.clear();
        for (Map.Entry<String, String> entry : timeMap.entrySet()) {
            timeList.add(initTime(entry.getKey()));
        }
    }

    /**
     * 时间戳转成相应的格式字符串
     *
     * @param mill      时间戳
     * @param strScheme 格式
     * @return
     */
    public static final String getDateLongToString(long mill, String strScheme) {
        String strReturn = null;
        Date now = new Date(mill);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(strScheme);
            strReturn = sdf.format(now);
        } catch (Exception e) {
            strReturn = "";
        }
        return strReturn;
    }

}

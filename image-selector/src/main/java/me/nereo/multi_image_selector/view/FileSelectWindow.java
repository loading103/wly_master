package me.nereo.multi_image_selector.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daqsoft.baselib.adapter.RecyclerViewAdapter;
import com.daqsoft.baselib.base.AppManager;
import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.databinding.ItemSelectFileBinding;
import me.nereo.multi_image_selector.databinding.PopSelectFileBinding;

/**
 * @Description XXXXX
 * @ClassName FileSelectWindow
 * @Author PuHua
 * @Time 2020/2/27 15:03
 * @Version 1.0
 */
public class FileSelectWindow extends PopupWindow {
    private RecyclerViewAdapter<ItemSelectFileBinding, String> adapter;
    RecyclerView recyclerView;
    String[] datas;
    WindowDataBack windowDataBack;

    public FileSelectWindow(FragmentActivity activity, View contentView, String[] data,
                            WindowDataBack back) {
        super(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        recyclerView = contentView.findViewById(R.id.window_list_choose_recycler);
        this.datas = data;
        this.windowDataBack = back;
        // 设置背景
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88000000")));
        // 设置是否能够响应外部点击事件
        setOutsideTouchable(true);
        // 设置能否响应点击事件
        setTouchable(true);
        setFocusable(true);
        initRecyclerView();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowBackGroud(activity, 1f);
            }
        });
    }


    public FileSelectWindow(Activity activity, View contentView, String[] data,
                            WindowDataBack back) {
        super(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        recyclerView = contentView.findViewById(R.id.window_list_choose_recycler);
        this.datas = data;
        this.windowDataBack = back;
        // 设置背景
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88000000")));
        // 设置是否能够响应外部点击事件
        setOutsideTouchable(true);
        // 设置能否响应点击事件
        setTouchable(true);
        setFocusable(true);
        initRecyclerView();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowBackGroud(activity, 1f);
            }
        });
    }


    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(BaseApplication.context,
                RecyclerView.VERTICAL, false));

        adapter = new RecyclerViewAdapter<ItemSelectFileBinding, String>(R.layout.item_select_file) {


            @Override
            public void setVariable(@NotNull final ItemSelectFileBinding mBinding,
                                    final int position, @NotNull final String item) {

                mBinding.tvName.setText(item);

                mBinding.tvName.setOnClickListener(view -> {
                    if (windowDataBack != null) {
                        windowDataBack.select(item);
                        dismiss();
                    }
                });
            }
        };
        ArrayList<String> ite = new ArrayList<>();
        Collections.addAll(ite, datas);

        adapter.add(ite);
        recyclerView.setAdapter(adapter);
    }

    public interface WindowDataBack {
        void select(String item);
    }

    public void show(View mView) {

        showAsDropDown(mView);
    }

    public void showBottom(View mView, Activity activity) {
        setWindowBackGroud(activity, 0.8f);
        showAtLocation(mView, Gravity.BOTTOM, 0, 0);
    }


    private void setWindowBackGroud(Activity activity, Float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;
        if (alpha == 1f) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }
}

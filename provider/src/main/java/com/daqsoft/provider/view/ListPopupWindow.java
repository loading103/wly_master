package com.daqsoft.provider.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daqsoft.baselib.adapter.RecyclerViewAdapter;
import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.provider.R;
import com.daqsoft.provider.databinding.ItemListPopupWindowBinding;
import com.daqsoft.provider.databinding.ListPopupWindowBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @Description 列表类型的pop弹窗
 * @ClassName ListPopupWindow
 * @Author PuHua
 * @Time 2019/9/2 16:08
 * @Version 1.0
 */
public class ListPopupWindow<T> extends BasePopupWindow {
    private RecyclerViewAdapter<ItemListPopupWindowBinding, T> adapter;
    private View mView;
    RecyclerView recyclerView;
    List<T> datas;
    WindowDataBack windowDataBack;
    RecyclerView.LayoutManager manager;
    // 用来记录上次选中的item;
    public TextView previous;

    public static <T> ListPopupWindow getInstance(View view, List<T> datas, WindowDataBack windowDataBack) {
        LayoutInflater inflater = LayoutInflater.from(BaseApplication.context);
        View listContent = inflater.inflate(R.layout.list_popup_window, null, false);
        ListPopupWindow listPopupWindow = new ListPopupWindow(listContent, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, datas, true);
        listPopupWindow.mView = view;
        listPopupWindow.windowDataBack = windowDataBack;
        listPopupWindow.initRecyclerView();
        return listPopupWindow;
    }

    public static <T> ListPopupWindow getInstance(View view, List<T> datas, int height, WindowDataBack windowDataBack) {
        LayoutInflater inflater = LayoutInflater.from(BaseApplication.context);
        View listContent = inflater.inflate(R.layout.list_popup_window, null, false);
        ListPopupWindow listPopupWindow = new ListPopupWindow(listContent, LinearLayout.LayoutParams.MATCH_PARENT,
                height, datas, true);
        listPopupWindow.mView = view;
        listPopupWindow.windowDataBack = windowDataBack;
        listPopupWindow.initRecyclerView();
        return listPopupWindow;
    }

    public static <T> ListPopupWindow getInstance(RecyclerView.LayoutManager manager, View view, List<T> datas, WindowDataBack windowDataBack) {
        LayoutInflater inflater = LayoutInflater.from(BaseApplication.context);
        View listContent = inflater.inflate(R.layout.list_popup_window, null, false);
        ListPopupWindow listPopupWindow = new ListPopupWindow(listContent, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, datas, true);
        listPopupWindow.mView = view;
        listPopupWindow.windowDataBack = windowDataBack;
        listPopupWindow.manager = manager;
        listPopupWindow.initRecyclerView();
        return listPopupWindow;
    }

    private ListPopupWindow(View contentView, int width, int height, List<T> datas, boolean focusable) {
        super(contentView, width, height, focusable);
        this.datas = datas;
        // 设置背景
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置是否能够响应外部点击事件
        setOutsideTouchable(true);
        // 设置能否响应点击事件
        setTouchable(true);
        setFocusable(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mView.setSelected(false);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = contentView.findViewById(R.id.window_list_choose_recycler);
        recyclerView.setLayoutManager(manager == null ? new LinearLayoutManager(BaseApplication.context) : manager);

        adapter = new RecyclerViewAdapter<ItemListPopupWindowBinding, T>(R.layout.item_list_popup_window) {


            @Override
            public void setVariable(@NotNull final ItemListPopupWindowBinding mBinding, final int position, @NotNull final T item) {

                mBinding.tvItem.setSelected(false);
                mBinding.tvItem.setText(item.toString());
                mBinding.tvItem.setGravity(Gravity.LEFT);

                mBinding.tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mBinding.tvItem.isSelected()) {
                            mBinding.tvItem.setSelected(false);
                        } else {
                            mBinding.tvItem.setSelected(true);
                            if (previous != null && previous != mBinding.tvItem)
                                previous.setSelected(false);
                            previous = mBinding.tvItem;
                            if (windowDataBack != null) {
                                windowDataBack.select(item);
                            }
                            dismiss();
                        }

                    }
                });
            }
        };
        adapter.emptyViewShow = false;
        adapter.add(datas);
        recyclerView.setAdapter(adapter);
    }

    public void updateDatas(List<T> datas) {
        if (datas != null && !datas.isEmpty()) {
            adapter.clear();
            adapter.add(datas);
        }
    }

    public void clearData() {
        adapter.clear();
    }

    public interface WindowDataBack<T> {
        void select(T item);
    }

    public void show() {
        resetDarkPosition();
        darkBelow(mView);
        showAsDropDown(mView);
    }

    public void showBottom() {
        resetDarkPosition();
        darkFillScreen();
        showAtLocation(mView, Gravity.BOTTOM, 0, 0);
    }

}

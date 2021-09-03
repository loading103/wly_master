package com.daqsoft.guidemodule.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daqsoft.baselib.adapter.RecyclerViewAdapter;
import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.guidemodule.R;
import com.daqsoft.guidemodule.databinding.GuideLayoutLabelSelectBinding;
import com.daqsoft.provider.bean.ResourceTypeLabel;
import com.daqsoft.provider.databinding.ItemListPopupWindowBinding;
import com.daqsoft.provider.databinding.ItemListPopupWindowLeftBinding;
import com.daqsoft.provider.view.BasePopupWindow;
import com.jakewharton.rxbinding2.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @Description 两级选择pop
 * @ClassName GuideGuideSecondSelectPopupWindow
 * @Author Wongxd
 * @Time 2020/4/8 10:12
 */
public class GuideSecondSelectPopupWindow extends BasePopupWindow {


    private GuideLayoutLabelSelectBinding mBinding;


    private WindowDataBack windowDataBack;

    public static GuideSecondSelectPopupWindow getInstance(Context context, Boolean multiSelect,
                                                           WindowDataBack back) {
        return new GuideSecondSelectPopupWindow(back, multiSelect,
                (GuideLayoutLabelSelectBinding) DataBindingUtil.inflate(
                        LayoutInflater.from(context),
                        R.layout.guide_layout_label_select,
                        null,
                        false
                ));
    }

    private RecyclerViewAdapter<ItemListPopupWindowLeftBinding, ResourceTypeLabel> firstAdapter;
    private RecyclerViewAdapter<ItemListPopupWindowBinding, ResourceTypeLabel> secendAdapter;

    public List<ResourceTypeLabel> getFirstData() {
        return firstData;
    }

    public void setFirstData(List<ResourceTypeLabel> firstData) {
        this.firstData = firstData;
        if (firstAdapter != null) {
            firstAdapter.clear();
            firstAdapter.add(firstData);
        }
    }

    public void setSecendData() {

        if (secendAdapter != null) {
            secendAdapter.clear();
            secendAdapter.add(secondData.get(0));
        }
    }

    public List<List<ResourceTypeLabel>> getSecondData() {
        return secondData;
    }

    public void setSecondData(List<List<ResourceTypeLabel>> secondData) {
        this.secondData = secondData;
        if (secendAdapter != null) {
            secendAdapter.clear();
            secendAdapter.add(firstData);
        }
    }

    public void addSecencData(List<ResourceTypeLabel> data) {
        this.secondData.add(data);
    }

    public void addAll(List<List<ResourceTypeLabel>> data) {
        this.secondData.addAll(data);
    }

    private List<ResourceTypeLabel> firstData = new ArrayList<>();
    private List<List<ResourceTypeLabel>> secondData = new ArrayList<>();


    @SuppressLint("CheckResult")
    private GuideSecondSelectPopupWindow(WindowDataBack back, Boolean multiSelect,
                                         GuideLayoutLabelSelectBinding binding) {
        super(binding.getRoot(), LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);
        mBinding = binding;
        this.windowDataBack = back;
        setOutsideTouchable(true);
        if (multiSelect) {
            mBinding.llBottom.setVisibility(View.GONE);
        } else {
            mBinding.llBottom.setVisibility(View.GONE);
        }

        RxView.clicks(mBinding.tvReset)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
            }
        });
        RxView.clicks(mBinding.tvSure)
                // 1秒内不可重复点击或仅响应一次事件
                .throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

                HashMap<String, String> pa = new HashMap<>();
                for (int i = 0; i <
                        secondData.size(); i++) {
                    List<ResourceTypeLabel> list = secondData.get(i);
                    for (ResourceTypeLabel r :
                            list) {
                        if (r.getSelect()) {
                            pa.put(firstData.get(i).getId(), r.getId());
                            continue;
                        }
                    }
                }
                if (windowDataBack != null) {
                    windowDataBack.select(pa);
                }
            }
        });
        setData();
    }

    public int firstPostion = 0;

    public void setData() {
        mBinding.rvFirst.setLayoutManager(new LinearLayoutManager(BaseApplication.context,
                RecyclerView.VERTICAL, false));

        firstAdapter = new RecyclerViewAdapter<ItemListPopupWindowLeftBinding, ResourceTypeLabel>(com.daqsoft.provider.R.layout.item_list_popup_window_left) {

            @Override
            public void setVariable(@NotNull final ItemListPopupWindowLeftBinding mBinding, final int position, @NotNull final ResourceTypeLabel item) {
                mBinding.tvItem.setSelected(firstPostion == position);
                mBinding.tvItem.setText(item.toString());
                mBinding.tvItem.setGravity(Gravity.LEFT);

                mBinding.tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firstPostion = position;
                        notifyItemRangeChanged(0, getData().size(), "updateSelectPos");
                        secendAdapter.clear();
                        secendAdapter.add(secondData.get(position));
                    }
                });
            }

            @Override
            public void payloadUpdateUi(@NotNull ItemListPopupWindowLeftBinding mBinding, int position, @NotNull List<Object> payloads) {
                if (payloads.get(0) == "updateSelectPos") {
                    mBinding.tvItem.setSelected(firstPostion == position);
                }
            }

        };

        mBinding.rvFirst.setAdapter(firstAdapter);

        mBinding.rvSecend.setLayoutManager(new LinearLayoutManager(BaseApplication.context,
                RecyclerView.VERTICAL, false));

        secendAdapter = new RecyclerViewAdapter<ItemListPopupWindowBinding, ResourceTypeLabel>(com.daqsoft.provider.R.layout.item_list_popup_window) {


            @Override
            public void setVariable(@NotNull final ItemListPopupWindowBinding mBinding, final int position, @NotNull final ResourceTypeLabel item) {
                mBinding.tvItem.setSelected(item.getSelect());
                mBinding.tvItem.setText(item.toString());
                mBinding.tvItem.setGravity(Gravity.LEFT);

                mBinding.tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < getData().size(); i++) {
                            getData().get(i).setSelect(i == position);
                        }
                        notifyItemRangeChanged(0, getData().size(), "updateSelectPos");
                        if (windowDataBack != null) {
                            HashMap<String, String> param = new HashMap<>();
                            for (int i = 0; i < getFirstData().size(); i++) {
                                ResourceTypeLabel lab = getFirstData().get(i);
                                param.put(lab.getId(), getSecondDataSelectValue(i));
                            }
                            windowDataBack.select(param);
                        }

                        dismiss();
                    }
                });
            }

            @Override
            public void payloadUpdateUi(@NotNull ItemListPopupWindowBinding mBinding, int position, @NotNull List<Object> payloads) {
                if (payloads.get(0) == "updateSelectPos") {
                    mBinding.tvItem.setSelected(getData().get(position).getSelect());
                }
            }
        }

        ;
        mBinding.rvSecend.setAdapter(secendAdapter);

    }

    private String getSecondDataSelectValue(int pos) {
        String result = "";
        if (!secondData.isEmpty() && pos < secondData.size()) {
            List<ResourceTypeLabel> list = secondData.get(pos);
            if (list == null || list.isEmpty()) {
                return result;
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getSelect()) {
                    result = list.get(i).getId();
                    break;
                }
            }
        }
        return result;
    }


    public void getMultiSelect() {

    }

    public void show(View mView) {
        resetDarkPosition();
        darkBelow(mView);
        showAsDropDown(mView);
    }

    public interface WindowDataBack {
        void select(HashMap<String, String> item);
    }
}

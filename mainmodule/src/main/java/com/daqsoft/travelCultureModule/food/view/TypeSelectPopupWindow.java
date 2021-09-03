package com.daqsoft.travelCultureModule.food.view;

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
import com.daqsoft.mainmodule.R;
import com.daqsoft.mainmodule.databinding.LayoutFoodSelectTypesBinding;
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

public class TypeSelectPopupWindow extends BasePopupWindow {


    private LayoutFoodSelectTypesBinding mBinding;

    public Boolean getmMultiSelect() {
        return mMultiSelect;
    }

    public void setmMultiSelect(Boolean mMultiSelect) {
        this.mMultiSelect = mMultiSelect;
    }

    /**
     * 判断是否时多选
     */
    private Boolean mMultiSelect = false;

    private WindowDataBack windowDataBack;

    private Context mContext;

    private String type = "";

    private String eqt = "";

    private int firstSelect = 0;
    private HashMap<Integer, Integer> selectPosMap = new HashMap<>();

    public static TypeSelectPopupWindow getInstance(Context context, Boolean multiSelect,
                                                    WindowDataBack back) {
        return new TypeSelectPopupWindow(back, multiSelect,
                (LayoutFoodSelectTypesBinding) DataBindingUtil.inflate(
                        LayoutInflater.from(context),
                        R.layout.layout_food_select_types,
                        null,
                        false
                ), context);
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
        if (firstData != null) {
            for (int i = 0; i < firstData.size(); i++) {
                selectPosMap.put(i, 0);
            }
        }
    }

    public void setSecendData() {

        if (secendAdapter != null) {
            secendAdapter.clear();
            secendAdapter.add(secondData);
        }
    }

    public List<ResourceTypeLabel> getSecondData() {
        return secondData;
    }

    public void setSecondData(List<ResourceTypeLabel> secondData) {
        this.secondData = secondData;
        if (secendAdapter != null) {
            secendAdapter.clear();
            if (secondData != null) {
                secendAdapter.add(secondData);
            }
        }
    }

    public void addSecencData(ResourceTypeLabel data) {
        this.secondData.add(data);
    }

    public void addAll(List<ResourceTypeLabel> data) {
        this.secondData.addAll(data);
    }

    private List<ResourceTypeLabel> firstData = new ArrayList<>();
    private List<ResourceTypeLabel> secondData = new ArrayList<>();


    @SuppressLint("CheckResult")
    private TypeSelectPopupWindow(WindowDataBack back, Boolean multiSelect,
                                  LayoutFoodSelectTypesBinding binding, Context context) {
        super(binding.getRoot(), LinearLayout.LayoutParams.MATCH_PARENT
                , context.getResources().getDimensionPixelSize(R.dimen.dp_250), false);
        this.mMultiSelect = multiSelect;
        mBinding = binding;
        this.windowDataBack = back;
        this.mContext = context;
        setOutsideTouchable(true);
        setData();
    }

    public int firstPostion = 0;

    public void setData() {
        mBinding.rvFirst.setLayoutManager(new LinearLayoutManager(BaseApplication.context,
                RecyclerView.VERTICAL, false));

        firstAdapter = new RecyclerViewAdapter<ItemListPopupWindowLeftBinding, ResourceTypeLabel>(R.layout.item_list_popup_window_left) {
            // 用来记录上次选中的item;

            @Override
            public void setVariable(@NotNull final ItemListPopupWindowLeftBinding mBinding, final int position, @NotNull final ResourceTypeLabel item) {
                if (firstSelect == position) {
                    mBinding.tvItem.setSelected(true);
                } else {
                    mBinding.tvItem.setSelected(false);
                }
                mBinding.tvItem.setText(item.toString());
                mBinding.tvItem.setGravity(Gravity.LEFT);

                mBinding.tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firstSelect = position;
                        notifyDataSetChanged();
                        secendAdapter.clear();
                        List<ResourceTypeLabel> temp = new ArrayList<>();
                        temp.add(0, new ResourceTypeLabel("", "", "", "", "不限","","",""));
                        if (firstData.get(position).getChildList() != null) {
                            temp.addAll(firstData.get(position).getChildList());
                        }
                        secondData.clear();
                        if (temp != null) {
                            secondData.addAll(temp);
                        }
                        secendAdapter.clear();
                        secendAdapter.add(secondData);
                    }
                });
            }
        }

        ;
        mBinding.rvFirst.setAdapter(firstAdapter);

        mBinding.rvSecend.setLayoutManager(new

                LinearLayoutManager(BaseApplication.context,
                RecyclerView.VERTICAL, false));

        secendAdapter = new RecyclerViewAdapter<ItemListPopupWindowBinding, ResourceTypeLabel>(R.layout.item_list_popup_window) {
            // 用来记录上次选中的item;
//            TextView previous;
//            ChildRegion previousLabel;

            @Override
            public void setVariable(@NotNull final ItemListPopupWindowBinding mBinding, final int position, @NotNull final ResourceTypeLabel item) {

                if (selectPosMap.get(firstSelect) == position) {
                    mBinding.tvItem.setSelected(true);
                } else {
                    mBinding.tvItem.setSelected(false);
                }

                mBinding.tvItem.setText(item.toString());
                mBinding.tvItem.setGravity(Gravity.LEFT);

                mBinding.tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectPosMap.put(firstSelect, position);
                        notifyDataSetChanged();
                        if (firstSelect == 0) {
                            type = secondData.get(position).getId();
                        } else {
                            eqt = secondData.get(position).getId();
                        }
//                        ResourceTypeLabel regin = secondData.get(position);
//                        if (windowDataBack != null) {
//                            windowDataBack.select(regin, firstSelect);
//                        }
//                        dismiss();
                    }

                });
            }
        }

        ;
        mBinding.rvSecend.setAdapter(secendAdapter);
        RxView.clicks(mBinding.tvReset)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        // 重置所有选择
                        if (firstData != null) {
                            selectPosMap.clear();
                            for (int i = 0; i < firstData.size(); i++) {
                                selectPosMap.put(i, 0);
                            }
                        }
                        type = "";
                        eqt = "";
                        secendAdapter.notifyDataSetChanged();
                        mBinding.rvSecend.smoothScrollToPosition(0);
                    }
                });
        RxView.clicks(mBinding.tvSure)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (windowDataBack != null) {
                            windowDataBack.select(type, eqt);
                        }
                        dismiss();
                    }
                });
    }

    private void resetAllSecondSelectStatus() {
        if (secondData != null) {
            for (int i = 0; i < secondData.size(); i++) {
                secondData.get(i).setSelect(false);
            }
        }
    }


    public void getMultiSelect() {

    }

    public void show(View mView) {
        resetDarkPosition();
        darkBelow(mView);
        showAsDropDown(mView);
    }

    public interface WindowDataBack {
        void select(String type, String eqt);

        void reset();
    }
}

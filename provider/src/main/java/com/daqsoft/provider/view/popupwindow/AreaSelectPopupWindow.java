package com.daqsoft.provider.view.popupwindow;

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
import com.daqsoft.provider.R;
import com.daqsoft.baselib.bean.ChildRegion;
import com.daqsoft.provider.databinding.ItemAreaPopLeftBinding;
import com.daqsoft.provider.databinding.ItemAreaPopRightBinding;
import com.daqsoft.provider.databinding.LayoutSecondAreaSelectBinding;
import com.daqsoft.provider.view.BasePopupWindow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AreaSelectPopupWindow extends BasePopupWindow {


    private LayoutSecondAreaSelectBinding mBinding;

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

    private int selectCityPos = 0;
    private int selectAreaPos = 0;
    private int tempSelectCityPos = 0;

    public static AreaSelectPopupWindow getInstance(Context context, Boolean multiSelect,
                                                    WindowDataBack back) {
        return new AreaSelectPopupWindow(back, multiSelect,
                (LayoutSecondAreaSelectBinding) DataBindingUtil.inflate(
                        LayoutInflater.from(context),
                        R.layout.layout_second_area_select,
                        null,
                        false
                ), context);
    }

    private RecyclerViewAdapter<ItemAreaPopLeftBinding, ChildRegion> firstAdapter;
    private RecyclerViewAdapter<ItemAreaPopRightBinding, ChildRegion> secendAdapter;

    public List<ChildRegion> getFirstData() {
        return firstData;
    }

    /**
     * 默认选中
     */
    public void defSelected(int position) {
        this.tempSelectCityPos = position;
    }

    public void setFirstData(List<ChildRegion> firstData) {
        this.firstData = firstData;
        if (firstAdapter != null) {
            firstAdapter.clear();
            firstAdapter.add(firstData);
        }
    }

    public void setSecendData() {

        if (secendAdapter != null) {
            secendAdapter.clear();
            secendAdapter.add(secondData);
        }
    }

    public List<ChildRegion> getSecondData() {
        return secondData;
    }

    public void setSecondData(List<ChildRegion> secondData) {
        this.secondData = secondData;
        if (secendAdapter != null) {
            secendAdapter.clear();
            if (secondData != null) {
                secendAdapter.add(secondData);
            }
        }
    }

    public void addSecencData(ChildRegion data) {
        this.secondData.add(data);
    }

    public void addAll(List<ChildRegion> data) {
        this.secondData.addAll(data);
    }

    private List<ChildRegion> firstData = new ArrayList<>();
    private List<ChildRegion> secondData = new ArrayList<>();


    @SuppressLint("CheckResult")
    private AreaSelectPopupWindow(WindowDataBack back, Boolean multiSelect, LayoutSecondAreaSelectBinding binding, Context context) {
        super(binding.getRoot(), LinearLayout.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelSize(R.dimen.dp_250), false);
        this.mMultiSelect = multiSelect;
        mBinding = binding;
        this.windowDataBack = back;
        this.mContext = context;
        setOutsideTouchable(true);
        setData();
    }

    public int firstPostion = 0;

    public void setData() {
        mBinding.rvCity.setLayoutManager(new LinearLayoutManager(BaseApplication.context, RecyclerView.VERTICAL, false));

        firstAdapter = new RecyclerViewAdapter<ItemAreaPopLeftBinding, ChildRegion>(R.layout.item_area_pop_left) {
            // 用来记录上次选中的item;
            @Override
            public void setVariable(@NotNull final ItemAreaPopLeftBinding mBinding, final int position, @NotNull final ChildRegion item) {
                if (tempSelectCityPos == position) {
                    mBinding.tvItem.setSelected(true);
                } else {
                    mBinding.tvItem.setSelected(false);
                }
                mBinding.tvItem.setText(item.toString());
                mBinding.tvItem.setGravity(Gravity.LEFT);

                mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tempSelectCityPos = position;
                        notifyDataSetChanged();
                        secendAdapter.clear();
                        List<ChildRegion> temp = firstData.get(position).getSubList();
                        secondData.clear();
                        secondData.add(0, new ChildRegion("", "不限", "", "",
                                new ArrayList<ChildRegion>(), 0, ""));
                        if (temp == null || temp.isEmpty()) {
                            selectCityPos = tempSelectCityPos;
                            selectAreaPos = 0;
                            if (windowDataBack != null) {
                                windowDataBack.select(item);
                            }
                            dismiss();
                        } else {
                            secondData.addAll(temp);
                        }
                        secendAdapter.clear();
                        secendAdapter.add(secondData);
                    }
                });
            }
        };

        mBinding.rvCity.setAdapter(firstAdapter);

        mBinding.rvArea.setLayoutManager(new LinearLayoutManager(BaseApplication.context, RecyclerView.VERTICAL, false));

        secendAdapter = new RecyclerViewAdapter<ItemAreaPopRightBinding, ChildRegion>(com.daqsoft.provider.R.layout.item_area_pop_right) {
            // 用来记录上次选中的item;
//            TextView previous;
//            ChildRegion previousLabel;

            @Override
            public void setVariable(@NotNull final ItemAreaPopRightBinding mBinding, final int position, @NotNull final ChildRegion item) {
                if (selectAreaPos == position && tempSelectCityPos == selectCityPos) {
                    mBinding.tvItem.setSelected(true);
                } else {
                    mBinding.tvItem.setSelected(false);
                }

                mBinding.tvItem.setText(item.toString());
                mBinding.tvItem.setGravity(Gravity.LEFT);

                mBinding.tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectCityPos = tempSelectCityPos;
                        selectAreaPos = position;
                        notifyDataSetChanged();
                        ChildRegion regin = secondData.get(position);
                        String siteId = regin.getSiteId();
                        if (siteId == null || siteId.isEmpty()) {
                            regin = firstData.get(selectCityPos);
                            regin.setRegion("");
                        }
                        if (windowDataBack != null) {
                            windowDataBack.select(regin);
                        }
                        dismiss();
                    }

                });
            }
        };

        mBinding.rvArea.setAdapter(secendAdapter);

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
        void select(ChildRegion region);
    }
}

package com.daqsoft.travelCultureModule.country.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.utils.TextUtils;
import com.daqsoft.baselib.adapter.RecyclerViewAdapter;
import com.daqsoft.baselib.base.BaseApplication;
import com.daqsoft.baselib.widgets.timepicker.IMDensityUtil;
import com.daqsoft.mainmodule.R;
import com.daqsoft.provider.databinding.ItemListPopupWindowBinding;
import com.daqsoft.provider.databinding.ItemPopupPlayChooseBinding;
import com.daqsoft.provider.databinding.LayoutPlayChooseBinding;
import com.daqsoft.provider.view.BasePopupWindow;
import com.daqsoft.travelCultureModule.country.bean.ResourceTypeLabel;
import com.jakewharton.rxbinding2.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class PlayChoosePopupWindow extends BasePopupWindow {

    private Context mContext;

    private LayoutPlayChooseBinding mBinding;

    private WindowDataBack windowDataBack;

    private ResourceTypeLabel bean1;
    private ResourceTypeLabel bean2;
    private ResourceTypeLabel bean3;
    private ResourceTypeLabel bean4;

    private RecyclerViewAdapter<ItemPopupPlayChooseBinding, ResourceTypeLabel> mAdapter1;
    private RecyclerViewAdapter<ItemPopupPlayChooseBinding, ResourceTypeLabel> mAdapter2;
    private RecyclerViewAdapter<ItemPopupPlayChooseBinding, ResourceTypeLabel> mAdapter3;
    private RecyclerViewAdapter<ItemPopupPlayChooseBinding, ResourceTypeLabel> mAdapter4;

    public static PlayChoosePopupWindow getInstance(Context context, WindowDataBack back) {
        return new PlayChoosePopupWindow(back, (LayoutPlayChooseBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_play_choose, null, false), context);
    }

    public void setdata(List<ResourceTypeLabel> data1,List<ResourceTypeLabel> data2,List<ResourceTypeLabel> data3,List<ResourceTypeLabel> data4) {

        if (mAdapter1 != null && data1!=null) {
            mAdapter1.clear();
            mAdapter1.add(data1);
            mBinding.ivTitle1.setVisibility(View.VISIBLE);
            mBinding.tvTitle1.setVisibility(View.VISIBLE);
            mBinding.recyclerView1.setVisibility(View.VISIBLE);
        }else {
            mBinding.ivTitle1.setVisibility(View.GONE);
            mBinding.tvTitle1.setVisibility(View.GONE);
            mBinding.recyclerView1.setVisibility(View.GONE);
        }
        if (mAdapter2 != null && data2!=null ) {
            mAdapter2.clear();
            mAdapter2.add(data2);
            mBinding.ivTitle2.setVisibility(View.VISIBLE);
            mBinding.tvTitle2.setVisibility(View.VISIBLE);
            mBinding.recyclerView2.setVisibility(View.VISIBLE);
        }else {
            mBinding.ivTitle2.setVisibility(View.GONE);
            mBinding.tvTitle2.setVisibility(View.GONE);
            mBinding.recyclerView2.setVisibility(View.GONE);
        }
        if (mAdapter3 != null  && data3!=null) {
            mAdapter3.clear();
            mAdapter3.add(data3);
            mBinding.ivTitle3.setVisibility(View.VISIBLE);
            mBinding.tvTitle3.setVisibility(View.VISIBLE);
            mBinding.recyclerView3.setVisibility(View.VISIBLE);
        }else {
            mBinding.ivTitle3.setVisibility(View.GONE);
            mBinding.tvTitle3.setVisibility(View.GONE);
            mBinding.recyclerView3.setVisibility(View.GONE);
        }
        if (mAdapter4 != null  && data4!=null) {
            mAdapter4.clear();
            mAdapter4.add(data4);
            mBinding.ivTitle4.setVisibility(View.VISIBLE);
            mBinding.tvTitle4.setVisibility(View.VISIBLE);
            mBinding.recyclerView4.setVisibility(View.VISIBLE);
        }else {
            mBinding.ivTitle4.setVisibility(View.GONE);
            mBinding.tvTitle4.setVisibility(View.GONE);
            mBinding.recyclerView4.setVisibility(View.GONE);
        }
    }





    @SuppressLint("CheckResult")
    private PlayChoosePopupWindow(WindowDataBack back,  LayoutPlayChooseBinding binding, Context context) {
        super(binding.getRoot(), LinearLayout.LayoutParams.MATCH_PARENT, (int)(IMDensityUtil.getScreenHeight(context)*0.7f), false);
        mBinding = binding;
        this.windowDataBack = back;
        this.mContext = context;
        setOutsideTouchable(true);
        setData();
    }


    @SuppressLint("CheckResult")
    public void setData() {
        initRecycleView();

        mBinding.ivTitle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBinding.recyclerView1.getVisibility()==View.VISIBLE){
                    mBinding.recyclerView1.setVisibility(View.GONE);
                    mBinding.ivTitle1.setImageResource(R.mipmap.map_arrow_down);
                }else {
                    mBinding.recyclerView1.setVisibility(View.VISIBLE);
                    mBinding.ivTitle1.setImageResource(R.mipmap.map_arrow_up);
                }
            }
        });
        mBinding.ivTitle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBinding.recyclerView2.getVisibility()==View.VISIBLE){
                    mBinding.recyclerView2.setVisibility(View.GONE);
                    mBinding.ivTitle2.setImageResource(R.mipmap.map_arrow_down);
                }else {
                    mBinding.recyclerView2.setVisibility(View.VISIBLE);
                    mBinding.ivTitle2.setImageResource(R.mipmap.map_arrow_up);
                }
            }
        });
        mBinding.ivTitle3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBinding.recyclerView3.getVisibility()==View.VISIBLE){
                    mBinding.recyclerView3.setVisibility(View.GONE);
                    mBinding.ivTitle3.setImageResource(R.mipmap.map_arrow_down);
                }else {
                    mBinding.recyclerView3.setVisibility(View.VISIBLE);
                    mBinding.ivTitle3.setImageResource(R.mipmap.map_arrow_up);
                }
            }
        });
        mBinding.ivTitle4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBinding.recyclerView4.getVisibility()==View.VISIBLE){
                    mBinding.recyclerView4.setVisibility(View.GONE);
                    mBinding.ivTitle4.setImageResource(R.mipmap.map_arrow_down);
                }else {
                    mBinding.recyclerView4.setVisibility(View.VISIBLE);
                    mBinding.ivTitle4.setImageResource(R.mipmap.map_arrow_up);
                }
            }
        });


        RxView.clicks(mBinding.tvCancel)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        // 重置所有选择
                        bean1=null;
                        bean2=null;
                        bean3=null;
                        bean4=null;
                        mAdapter1.notifyDataSetChanged();
                        mAdapter2.notifyDataSetChanged();
                        mAdapter3.notifyDataSetChanged();
                        mAdapter4.notifyDataSetChanged();
                        if (windowDataBack != null) {
                            windowDataBack.reset();
                        }
                        dismiss();

                    }
                });
        RxView.clicks(mBinding.tvEnsure)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        dismiss();
                        windowDataBack.select(bean1,bean2,bean3,bean4);
                    }
                });
    }

    private void initRecycleView() {

        mAdapter1 = new RecyclerViewAdapter<ItemPopupPlayChooseBinding, ResourceTypeLabel>(R.layout.item_popup_play_choose) {
            // 用来记录上次选中的item;
            @Override
            public void setVariable(@NotNull final ItemPopupPlayChooseBinding itemBinding, final int position, @NotNull final ResourceTypeLabel item) {

                itemBinding.tvTag.setText(item.getLabelName());
                if(bean1==null){
//                    itemBinding.tvTag.setSelected(position==0);
                    itemBinding.tvTag.setSelected(false);
                }else {
                    itemBinding.tvTag.setSelected(!TextUtils.isEmpty(bean1.getId()) && bean1.getId()==item.getId());
                }
                itemBinding.tvTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bean1=item;
                        notifyDataSetChanged();
                    }
                });
            }
        };
        mBinding.recyclerView1.setLayoutManager(new  GridLayoutManager(BaseApplication.context, 3, GridLayoutManager.VERTICAL, false));
        mBinding.recyclerView1.setAdapter(mAdapter1);
        mBinding.recyclerView1.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.bottom = IMDensityUtil.dip2px(mContext,15);
                    }
                });

        mAdapter2 = new RecyclerViewAdapter<ItemPopupPlayChooseBinding, ResourceTypeLabel>(R.layout.item_popup_play_choose) {
            // 用来记录上次选中的item;
            @Override
            public void setVariable(@NotNull final ItemPopupPlayChooseBinding itemBinding, final int position, @NotNull final ResourceTypeLabel item) {

                itemBinding.tvTag.setText(item.getLabelName());
                if(bean2==null){
                    itemBinding.tvTag.setSelected(false);
                }else {
                    itemBinding.tvTag.setSelected(!TextUtils.isEmpty(bean2.getId()) && bean2.getId()==item.getId());
                }
                itemBinding.tvTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bean2=item;
                        notifyDataSetChanged();
                    }
                });
            }
        };
        mBinding.recyclerView2.setLayoutManager(new  GridLayoutManager(BaseApplication.context, 3, GridLayoutManager.VERTICAL, false));
        mBinding.recyclerView2.setAdapter(mAdapter2);
        mBinding.recyclerView2.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.bottom = IMDensityUtil.dip2px(mContext,15);
                    }
                });

        mAdapter3 = new RecyclerViewAdapter<ItemPopupPlayChooseBinding, ResourceTypeLabel>(R.layout.item_popup_play_choose) {
            // 用来记录上次选中的item;
            @Override
            public void setVariable(@NotNull final ItemPopupPlayChooseBinding itemBinding, final int position, @NotNull final ResourceTypeLabel item) {

                itemBinding.tvTag.setText(item.getLabelName());
                if(bean3==null){
                    itemBinding.tvTag.setSelected(false);
                }else {
                    itemBinding.tvTag.setSelected(!TextUtils.isEmpty(bean3.getId()) && bean3.getId()==item.getId());
                }
                itemBinding.tvTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bean3=item;
                        notifyDataSetChanged();
                    }
                });
            }
        };
        mBinding.recyclerView3.setLayoutManager(new  GridLayoutManager(BaseApplication.context, 3, GridLayoutManager.VERTICAL, false));
        mBinding.recyclerView3.setAdapter(mAdapter3);
        mBinding.recyclerView3.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.bottom = IMDensityUtil.dip2px(mContext,15);
                    }
                });

        mAdapter4 = new RecyclerViewAdapter<ItemPopupPlayChooseBinding, ResourceTypeLabel>(R.layout.item_popup_play_choose) {
            // 用来记录上次选中的item;
            @Override
            public void setVariable(@NotNull final ItemPopupPlayChooseBinding itemBinding, final int position, @NotNull final ResourceTypeLabel item) {

                itemBinding.tvTag.setText(item.getLabelName());
                if(bean4==null){
//                    itemBinding.tvTag.setSelected(position==0);
                    itemBinding.tvTag.setSelected(false);
                }else {
                    itemBinding.tvTag.setSelected(!TextUtils.isEmpty(bean4.getId()) && bean4.getId()==item.getId());
                }
                itemBinding.tvTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bean4=item;
                        notifyDataSetChanged();
                    }
                });
            }
        };
        mBinding.recyclerView4.setLayoutManager(new  GridLayoutManager(BaseApplication.context, 3, GridLayoutManager.VERTICAL, false));
        mBinding.recyclerView4.setAdapter(mAdapter4);
        mBinding.recyclerView4.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.bottom = IMDensityUtil.dip2px(mContext,15);
                    }
                });

    }


    public void show(View mView) {
        resetDarkPosition();
        darkBelow(mView);
        showAsDropDown(mView);
    }

    public interface WindowDataBack {

        void select(ResourceTypeLabel bean1, ResourceTypeLabel bean2, ResourceTypeLabel bean3, ResourceTypeLabel bean4);

        void reset();
    }
}

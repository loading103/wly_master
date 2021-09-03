package com.daqsoft.travelCultureModule.specialty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.daqsoft.baselib.bean.ChildRegion;
import com.daqsoft.mainmodule.R;
import com.daqsoft.mainmodule.databinding.LayoutResourceTypeSelectBinding;
import com.daqsoft.provider.bean.ResourceTypeLabel;
import com.daqsoft.provider.bean.ValueKeyBean;
import com.daqsoft.provider.service.GaoDeLocation;
import com.daqsoft.provider.view.ListPopupWindow;
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow;
import com.daqsoft.travelCultureModule.specialty.viewmodel.SpecialtyListViewModel;
import com.daqsoft.travelCultureModule.resource.SecondSelectPopupWindow;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @Description 资源条件选择器
 * @ClassName ResourceTypeSelectView
 * @Author luoyi
 * @Time 2020/3/30 14:00
 * @Version 1.1
 */
public class SpecialTypeSelectView extends LinearLayout {
    private FragmentActivity fragmentActivity;

    // 排序
    ArrayList sorts = new ArrayList();
    LayoutResourceTypeSelectBinding mBinding;

    private String selfLat = "", selfLon = "", regionId;
    private GaoDeLocation gaoDeLocation=null;
    public void setModel(String regionId, SpecialtyListViewModel model, FragmentActivity activity) {
        this.model = model;
        if (regionId != null) {
            this.regionId = regionId;
        } else {
            this.regionId = "";
        }
        this.fragmentActivity = activity;
    }

    SpecialtyListViewModel model;


    /**
     * 弹窗
     */
    private AreaSelectPopupWindow areaListPopupWindow = null;
    /**
     * 排序
     */
    private ListPopupWindow sortListPopupWindow = null;

    private SecondSelectPopupWindow secondSelectPopupWindow = null;

    public void setmOnTypeSelectListener(OnTypeSelectListener mOnTypeSelectListener) {
        this.mOnTypeSelectListener = mOnTypeSelectListener;
    }

    private OnTypeSelectListener mOnTypeSelectListener;

    public SpecialTypeSelectView(Context context) {
        super(context);
        init(context);
    }

    public SpecialTypeSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpecialTypeSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("CheckResult")
    public void init(final Context context) {
        sorts.add(new ValueKeyBean("不限", "", true));
        sorts.add(new ValueKeyBean("可订优先","orderStatus",false));
        sorts.add(new ValueKeyBean("距离优先", "disNum", false));
        sorts.add(new ValueKeyBean("推荐优先", "recommendHomePage ", false));
        sorts.add(new ValueKeyBean("人气优先", "hot", false));

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_resource_type_select, null, false);
        addView(mBinding.getRoot());

        RxView.clicks(mBinding.tvArea)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object n) throws Exception {
                        if (areaListPopupWindow != null) {
                            areaListPopupWindow.show(mBinding.getRoot());
                        }
                        if (sortListPopupWindow != null) {
                            sortListPopupWindow.dismiss();
                        }
                        if (secondSelectPopupWindow != null) {
                            secondSelectPopupWindow.dismiss();
                        }
                    }
                });
        RxView.clicks(mBinding.tvSort)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (sortListPopupWindow != null) {
                            sortListPopupWindow.show();
                            // 设置箭头向上
                        }
                        if (areaListPopupWindow != null) {
                            areaListPopupWindow.dismiss();
                        }
                        if (secondSelectPopupWindow != null) {
                            secondSelectPopupWindow.dismiss();
                        }
                    }
                });
        RxView.clicks(mBinding.tvType)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object resourceTypeLabel) throws Exception {
                        if (secondSelectPopupWindow != null) {
                            secondSelectPopupWindow.show(mBinding.tvType);
                            // 设置箭头向上
                        }
                        if (sortListPopupWindow != null) {
                            sortListPopupWindow.dismiss();
                        }
                        if (areaListPopupWindow != null) {
                            areaListPopupWindow.dismiss();
                        }
                    }
                });

        // 排序方式
        sortListPopupWindow = ListPopupWindow.getInstance(mBinding.tvArea, sorts, new ListPopupWindow.WindowDataBack() {
            @Override
            public void select(final Object item) {
                final ValueKeyBean bean = (ValueKeyBean) item;
                mBinding.tvSort.setText(bean.getName());
                if (mOnTypeSelectListener != null) {
                    mOnTypeSelectListener.OnSortTypeSelected(bean);
                }

            }
        });

        secondSelectPopupWindow = SecondSelectPopupWindow.getInstance(context, true,
                new SecondSelectPopupWindow.WindowDataBack() {
                    @Override
                    public void select(HashMap<String, String> item) {
                        if (mOnTypeSelectListener != null) {
                            mOnTypeSelectListener.onTypesSelected(item);
                        }
                    }
                });

    }


    public void getData(ArrayList<ResourceTypeLabel> firstData,
                        ArrayList<ResourceTypeLabel> secondData) {
        secondSelectPopupWindow.setFirstData(firstData);
        secondSelectPopupWindow.addSecencData(secondData);
        secondSelectPopupWindow.setSecendData();
//        if (model != null && fragmentActivity != null) {
//            gaoDeLocation= new GaoDeLocation();
//            gaoDeLocation.init(fragmentActivity, new GaoDeLocation.OnGetCurrentLocationLisner() {
//                @Override
//                public void onResult(String adCode, String result, double lat, double lon, String adcode) {
//                    getChildRegions(lat, lon);
//                }
//
//                @Override
//                public void onError(String errorMsg) {
//
//                }
//
//            });
//        }
        model.getAreas().observe(fragmentActivity, new Observer<List<ChildRegion>>() {
            @Override
            public void onChanged(List<ChildRegion> childRegions) {
                childRegions.add(0, new ChildRegion("", "地区", "", "", new ArrayList<ChildRegion>(), 0, "0"));
                areaListPopupWindow = AreaSelectPopupWindow.getInstance(
                        mBinding.getRoot().getContext(), false, new AreaSelectPopupWindow.WindowDataBack() {
                            @Override
                            public void select(ChildRegion region) {
                                mBinding.tvArea.setText("" + region.getName());
                                if (mOnTypeSelectListener != null) {
                                    mOnTypeSelectListener.OnAreaSelected(region);
                                }
                            }
                        }
                );
                for (int index = 0; index < childRegions.size(); index++) {
                    ChildRegion bean = childRegions.get(index);
                    if (bean.getRegion().equals(regionId)) {
                        mBinding.tvArea.setText(bean.getName());
                        areaListPopupWindow.defSelected(index);
                        break;
                    }
                }
                areaListPopupWindow.setFirstData(childRegions);
                ArrayList<ChildRegion> temp = new ArrayList<ChildRegion>();
                temp.add(0, new ChildRegion("", "不限", "", "", new ArrayList<ChildRegion>(), 0, ""));
                areaListPopupWindow.setSecondData(temp);
            }
        });
        model.getSelectLabels().observe(fragmentActivity, new Observer<List<List<ResourceTypeLabel>>>() {
            @Override
            public void onChanged(List<List<ResourceTypeLabel>> lists) {
                secondSelectPopupWindow.addAll(lists);
            }
        });


        model.getAreas();
    }

    public  void getChildRegions(double lat, double lon) {
        if(model!=null) {
            final HashMap<String, String> param = new HashMap<>();

            selfLat = String.valueOf(lat);
            selfLon = String.valueOf(lon);
            param.put("lat", String.valueOf(lat));
            param.put("lng", String.valueOf(lon));
            param.put("sortType", "disNum");
            model.getChildRegions();
            model.getSelectLabel();
        }
    }


    public interface OnTypeSelectListener {

        /**
         * 区域类型选中
         *
         * @param region
         */
        void OnAreaSelected(ChildRegion region);

        /**
         * 排序类型选中
         *
         * @param bean
         */
        void OnSortTypeSelected(ValueKeyBean bean);

        /**
         * 分类选中
         *
         * @param item
         */
        void onTypesSelected(HashMap<String, String> item);


    }
}


package com.daqsoft.travelCultureModule.resource.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.LayoutScenicSpotBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ScenicTags
import com.daqsoft.provider.bean.Spots
import com.daqsoft.provider.bean.TourBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.travelCultureModule.resource.adapter.ScenicSpotAdapter
import com.daqsoft.travelCultureModule.resource.adapter.ScenicSpotPagerAdapter
import com.daqsoft.travelCultureModule.resource.fragment.ScenicSpotFragment

/**
 * @Description 逛景点view
 * @ClassName   ScenicSpotView
 * @Author      luoyi
 * @Time        2020/4/2 9:33
 */
class ScenicSpotView : FrameLayout {

    var mContext: Context? = null
    var binding: LayoutScenicSpotBinding? = null
    var scenicSpotsAdapter: ScenicSpotPagerAdapter? = null
    var mDatasScenicFrags: MutableList<ScenicSpotFragment> = mutableListOf()
    var fragmentmanger: FragmentManager? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_scenic_spot,
            this,
            false
        )
        addView(binding!!.root)
    }

    public fun setData(datas: MutableList<Spots>, fragmentManager: FragmentManager, name: String?, imageUrl: String?, scenicTags: ScenicTags?, scenicId: Int) {
        this.fragmentmanger = fragmentManager
        updateUi(datas, name, imageUrl, scenicTags, scenicId)
    }

    public fun setTour(tour: TourBean?) {
        if (tour != null) {
            binding?.vToTourGuide?.visibility = View.VISIBLE
            binding?.vTourGuideContent?.visibility = View.VISIBLE
            var imageUrl = ""
            if (!tour.images.isNullOrEmpty()) {
                var imageList = tour.images?.split(",")
                if (!imageList.isNullOrEmpty()) {
                    imageUrl = imageList[0]
                }
            }
            Glide.with(mContext!!)
                .load(imageUrl)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(binding!!.imgScenicSpotMap)
            var tagSpots = ""
            if (tour.spotsNum!=null&&tour.spotsNum!! > 0) {
                tagSpots = "景点·" + tour.spotsNum
            }
            var tagsRouts = ""
            if (tour.routeNum!=null&&tour.routeNum!! > 0) {
                tagsRouts = "线路·" + tour.routeNum
            }
            var tagsSparks = ""
            if (tour.parkNum != null && tour.parkNum!! > 0) {
                tagsSparks = "停车场·" + tour.parkNum
            }
            var tagsToilents = ""
            if (tour.toiletNum!=null&&tour.toiletNum!! > 0) {
                tagsToilents = "厕所·" + tour.toiletNum
            }

            val info = DividerTextUtils.convertString(
                StringBuilder(), tagSpots, tagsRouts, tagsSparks, tagsToilents
            )
            if (!info.isNullOrEmpty()) {
                binding?.txtScenicSpotNum?.visibility = View.VISIBLE
                binding?.txtScenicSpotNum?.text = info
            } else {
                binding?.txtScenicSpotNum?.visibility = View.GONE
            }
            binding?.vToTourGuide?.onNoDoubleClick {
                ARouter.getInstance().build(ARouterPath.GuideModule.GUIDE_TOUR_ACTIVITY)
                    .withString("tourId", tour.id)
                    .navigation()
            }
        } else {
            binding?.vToTourGuide?.visibility = View.GONE
            binding?.vTourGuideContent?.visibility = View.GONE
        }
    }

    private fun updateUi(datas: MutableList<Spots>, name: String?, imageUrl: String?, scenicTags: ScenicTags?, scenicId: Int) {
        binding?.vSpotsContent?.visibility = View.VISIBLE
        if (scenicTags != null && !datas.isNullOrEmpty()) {
            scenicTags.spotNum = "${datas.size}个景点"
        }
        if (!datas.isNullOrEmpty()) {
            var count = datas.size
            var pageSize = 4
            var pageCount = (count + pageSize - 1) / pageSize
            for (index in 1..pageCount) {
                var start = (index - 1) * pageSize
                var max = start + pageSize
                var end = if (max > count) {
                    count
                } else {
                    max
                }
                var data = datas.subList(start, end)

                mDatasScenicFrags.add(ScenicSpotFragment.newInstance(data, name, imageUrl, "", scenicTags, scenicId))
            }
            if (pageCount == 1) {
                binding?.circleIndicator?.visibility = View.GONE
            } else {
                binding?.circleIndicator?.total = pageCount
                binding?.circleIndicator?.binViewPager(binding?.vpScenicSpots)
            }
            scenicSpotsAdapter = ScenicSpotPagerAdapter(mDatasScenicFrags, fragmentmanger!!)
            binding?.vpScenicSpots?.adapter = scenicSpotsAdapter
            binding?.vpScenicSpots?.offscreenPageLimit = pageCount

        }
    }

    /**
     * 隐藏景点内容
     */
    public fun hideSpotsContent() {
        binding?.vSpotsContent?.visibility = View.GONE
    }

}
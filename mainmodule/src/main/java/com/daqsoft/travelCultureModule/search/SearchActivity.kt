package com.daqsoft.travelCultureModule.search


import android.annotation.SuppressLint
import android.os.Build
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivitySearchBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.base.ActivityType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.travelCultureModule.search.bean.SearchBean
import com.daqsoft.travelCultureModule.search.vm.SearchActivityViewModel
import org.jetbrains.anko.toast

/**
 * @des 全局搜索
 * @author 秦文肖
 * @Date 2020/4/13 14:52
 */
@Route(path = MainARouterPath.MAIN_ALL_SEARCH)
class SearchActivity : TitleBarActivity<ActivitySearchBinding, SearchActivityViewModel>() {
    override fun getLayout(): Int = R.layout.activity_search

    override fun setTitle(): String = "搜索"

    override fun injectVm(): Class<SearchActivityViewModel> = SearchActivityViewModel::class.java

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    override fun initView() {
        mBinding.etAllSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var content = mBinding.etAllSearch.text.toString().trim()
                if (content.isNullOrEmpty()) {
                    mBinding.llSearchList.removeAllViews()
                    mBinding.etAllSearch.text.clear()
                    mBinding.llAllSearchTop.visibility = View.VISIBLE
                    reloadData()
                } else {
                    mModel.getSearchList("1", "50", content, "")
                }
            }

        })
//        mBinding.etAllSearch!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH||actionId==EditorInfo
//                    .IME_ACTION_UNSPECIFIED||actionId==EditorInfo.IME_ACTION_DONE) {
//                var content= mBinding.etAllSearch.text.toString()
//                mModel.getSearchList("1","10",content,"")
//            }
//            false
//        })
        mBinding.tvAllSearchCancle.setOnClickListener {
            mBinding.llSearchList.removeAllViews()
            mBinding.etAllSearch.text.clear()
            mBinding.llAllSearchTop.visibility = View.VISIBLE
            reloadData()
        }
        mBinding.ivAllSearchDelete.setOnClickListener {
            mModel.clearSearchRcord()
        }
        mModel.clear.observe(this, Observer { reloadData() })
        mModel.searchList.observe(this, Observer {
            mBinding.llSearchList.removeAllViews()
            if (it.size == 0) {
                mBinding.llAllSearchTop.visibility = View.VISIBLE
                toast("暂无数据")
                return@Observer
            }
            mBinding.llAllSearchTop.visibility = View.GONE
            var content = mBinding.etAllSearch.text.toString()
            var lay = layoutInflater.inflate(R.layout.layout_search_type, null)
            var tvSearvhType1 = lay.findViewById<TextView>(R.id.tv_searvh_type)
            var tvSearvhList = lay.findViewById<TextView>(R.id.tv_searvh_list)
            var ivSearvhType = lay.findViewById<ImageView>(R.id.iv_searvh_type)
            var isNormalTyle = true
            var path = ""
            if (content == "美食") {
                tvSearvhType1.text = "全部美食"
                path = MainARouterPath.MAIN_FOOD_LS
                tvSearvhList.text = "美食列表"
                ivSearvhType.setImageResource(R.mipmap.add_food)
                mBinding.llSearchList.addView(lay)
            } else if (content == "酒店") {
                tvSearvhType1.text = "全部酒店"
                path = ZARouterPath.ZMAIN_HOTEL_LIST
                tvSearvhList.text = "酒店列表"
                ivSearvhType.setImageResource(R.mipmap.add_hotel)
                mBinding.llSearchList.addView(lay)
            } else if (content == "攻略") {
                tvSearvhType1.text = "全部攻略"
                path = MainARouterPath.MAIN_STRATEGY_FIND
                tvSearvhList.text = "攻略列表"
                ivSearvhType.setImageResource(R.mipmap.search_lxss_gn)
                mBinding.llSearchList.addView(lay)
            } else if (content == "景区") {
                tvSearvhType1.text = "全部景区"
                path = MainARouterPath.MAINE_SCENIC_LIST
                tvSearvhList.text = "景区列表"
                ivSearvhType.setImageResource(R.mipmap.add_scenic)
                mBinding.llSearchList.addView(lay)
            } else if (content == "社团") {
                tvSearvhType1.text = "全部社团"
                path = MainARouterPath.MAIN_CLUB
                tvSearvhList.text = "社团列表"
                ivSearvhType.setImageResource(R.mipmap.search_lxss_st)
                mBinding.llSearchList.addView(lay)
            } else if (content == "场馆") {
                tvSearvhType1.text = "全部场所"
                path = ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY
                tvSearvhList.text = "场所列表"
                ivSearvhType.setImageResource(R.mipmap.add_cultural_venues)
                mBinding.llSearchList.addView(lay)
            }else if(content == "非遗"){
                tvSearvhType1.text = "品非遗"
                path = ARouterPath.LegacyModule.LEGACY_HOME_ACTIVITY
                tvSearvhList.text = "品非遗"
                ivSearvhType.setImageResource(R.mipmap.search_lxss_st)
                mBinding.llSearchList.addView(lay)
            } else if(content == "娱乐"||content == "娱乐场所"){
                tvSearvhType1.text = "全部娱乐场所"
                path = MainARouterPath.MAIN_PLAYGROUND_LS
                tvSearvhList.text = "娱乐场所列表"
                ivSearvhType.setImageResource(R.mipmap.add_entertainment)
                mBinding.llSearchList.addView(lay)
            }else if(content == "特产"){
                tvSearvhType1.text = "全部特产"
                path = MainARouterPath.SPECIAL_BASELIST
                tvSearvhList.text = "特产列表"
                ivSearvhType.setImageResource(R.mipmap.add_speciality)
                mBinding.llSearchList.addView(lay)
            }else if(content == "研学基地" ){
                tvSearvhType1.text = "全部研学基地"
                path = MainARouterPath.RESEARCH_BASELIST
                tvSearvhList.text = "研学基地列表"
                ivSearvhType.setImageResource(R.mipmap.add_study_base)
                mBinding.llSearchList.addView(lay)
            }




            else {
                isNormalTyle = false
            }
            tvSearvhType1.onNoDoubleClick {
                if (!path.isNullOrEmpty()) {
                    ARouter.getInstance().build(path)
                        .navigation()
                }
            }
            for (postion in it.indices) {
                if (postion == 0 && !isNormalTyle) {
                    var item = layoutInflater.inflate(R.layout.item_search_tag_first, null)
                    var tvStTitle = item.findViewById<TextView>(R.id.tv_st_title)
                    var tvStContent = item.findViewById<TextView>(R.id.tv_st_content)
                    var tvStType = item.findViewById<TextView>(R.id.tv_st_type)
                    var ivStLogo = item.findViewById<ImageView>(R.id.iv_st_logo)
                    setImageUrlqwx(
                        ivStLogo,
                        it[postion].image,
                        AppCompatResources.getDrawable(
                            BaseApplication.context,
                            R.drawable.placeholder_img_fail_240_180
                        ),
                        5
                    )
                    tvStTitle.text = it[0].name.replace("<em>", "").replace("</em>", "")
                    if (it[0].summary == "") tvStContent.visibility = View.GONE
                    tvStContent.text = it[0].summary
                    tvStType.text = ResourceType.getName(it[0].resourceType)
                    mBinding.llSearchList.addView(item)
                    var searchBean = it[0]
                    item.setOnClickListener {
                        itemOnClick(searchBean)
                    }
                } else {
                    var item = layoutInflater.inflate(R.layout.item_search_all, null)
                    var tvSearvhName = item.findViewById<TextView>(R.id.tv_search_item_name)
                    var tvSearvhType = item.findViewById<TextView>(R.id.tv_search_item_type)
                    var text = it[postion].name.replace("<em>", "").replace("</em>", "")
                    // text="美食123美食456美食"
                    val style = SpannableStringBuilder(text)
                    var reslt = style.split(content)
                    var cur = 0
                    for (position in reslt.indices) {
                        cur += reslt[position].length
                        if (position == reslt.size - 1) {
                        } else {
                            style.setSpan(
                                ForegroundColorSpan(resources.getColor(R.color.color_36cd64)),
                                cur,
                                content.length + cur,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )     //设置指定位置文字的颜色
                            cur += content.length
                        }
                    }
                    tvSearvhName.text = style
                    tvSearvhType.text = ResourceType.getName(it[postion].resourceType)
                    var searchBean = it[postion]
                    item.setOnClickListener {
                        mModel.saveSearchRcord(
                            searchBean.name.replace("<em>", "").replace(
                                "</em>",
                                ""
                            )
                        )
                        itemOnClick(searchBean)
                    }
                    mBinding.llSearchList.addView(item)
                }

            }
        })
        mModel.searchRecordList.observe(this, Observer {
            mBinding.flSearchRecord.removeAllViews()
            if (it.size > 0) {
                mBinding.llSearchHistory.visibility = View.VISIBLE
                for (position in it.indices) {
                    var parame = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    var tv = TextView(this)
                    var name = it[position]
                    if (it[position].length > 8) {
                        tv.text = it[position].substring(0, 8) + "..."
                    } else {
                        tv.text = it[position]
                    }
                    tv.textSize = 13.toFloat()
                    tv.background = resources.getDrawable(R.drawable.main_shape_search_all_f5)

                    tv.setPadding(
                        resources.getDimensionPixelOffset(R.dimen.dp_12),
                        resources.getDimensionPixelOffset(R.dimen.dp_9),
                        resources.getDimensionPixelOffset(R.dimen.dp_12),
                        resources.getDimensionPixelOffset(R.dimen.dp_9)
                    )
                    parame.rightMargin = resources.getDimensionPixelOffset(R.dimen.dp_8)
                    parame.bottomMargin = resources.getDimensionPixelOffset(R.dimen.dp_8)
                    tv.setOnClickListener {
                        mBinding.etAllSearch.setText("${name}")
                    }
                    mBinding.flSearchRecord.addView(tv, parame)
                }
            }else{
                mBinding.llSearchHistory.visibility = View.GONE
            }
        })
    }

    override fun initData() {
        mModel.getSearchRecordList("10", "")
    }

    /**
     * 点击事件（item）
     * 数据源 searchBean
     */
    fun itemOnClick(searchBean: SearchBean) {
        when (searchBean.resourceType) {
            // 景区
            ResourceType.CONTENT_TYPE_SCENERY ->
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", searchBean.resourceId.toString())
                    .navigation()
            // 景点
            ResourceType.CONTENT_TYPE_SCENIC_SPOTS -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_SPOT_DETAI)
                    .withString("id", searchBean.resourceId.toString())
                    .navigation()
            }
            // 景观点
            ResourceType.CONTENT_TYPE_RURAL_SPOTS -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_SCENIC_SPOT_ACTIVITY)
                    .withString("id", searchBean.resourceId.toString())
                    .navigation()
            }
            // 酒店
            ResourceType.CONTENT_TYPE_HOTEL -> {
                ARouter.getInstance()
                    .build(ZARouterPath.ZMAIN_HOTEL_DETAIL)
                    .withString("id", searchBean.resourceId)
                    .navigation()
            }
            // 场馆
            ResourceType.CONTENT_TYPE_VENUE -> {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.VENUES_DETAILS_ACTIVITY)
                    .withString("id", searchBean.resourceId)
                    .navigation()
            }
            // 美食(餐饮)
            ResourceType.CONTENT_TYPE_RESTAURANT -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_FOOD_DETAIL)
                    .withString("id", searchBean.resourceId)
                    .navigation()
            }
            // 内容
            ResourceType.CONTENT -> {
                if (searchBean.contentType.equals("IMAGE")) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                        .withString("id", searchBean.resourceId)
                        .navigation()
                } else {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", searchBean.resourceId)
                        .withString("contentTitle", "资讯详情")
                        .navigation()
                }
            }
            // 故事
            ResourceType.CONTENT_TYPE_STORY -> {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_STORY_DETAIL)
                    .withString("id", searchBean.resourceId)
                    .withInt("type", 1)
                    .navigation()
            }
            // 农家乐
            ResourceType.CONTENT_TYPE_AGRITAINMENT -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL)
                    .withString("id", searchBean.resourceId)
                    .navigation()
            }
            // 城市名片
            ResourceType.CONTENT_TYPE_SITE_INDEX -> {
                if (searchBean.contentType == "TOURISM") {
                    // 乡村游
                    ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_TOUR_LIST)
                        .withString("jumpSiteId", searchBean.siteId ?: "")
                        .navigation()
                } else {
                    ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                        .withString("id", searchBean.siteId)
                        .navigation()
                }

            }
            // 品牌信息
            ResourceType.CONTENT_TYPE_BRAND -> {
                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                    .withString("id", searchBean.resourceId)
                    .navigation();
            }
            // 大讲堂
            ResourceType.CONTENT_TYPE_COURSE->{
                MainARouterPath.toLectureHallDetail(searchBean.resourceId)
            }
            // 活动
            ResourceType.CONTENT_TYPE_ACTIVITY -> {
                if (!searchBean.jumpUrl.isNullOrEmpty()) {
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.WEB_ACTIVITY)
                        .withString("mTitle", searchBean.name)
                        .withString("html", searchBean.jumpUrl)
                        .navigation()
                } else {
                    when (searchBean.contentType) {
                        // 志愿活动
                        ActivityType.ACTIVITY_TYPE_VOLUNT -> {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
//                                    .build( MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
                                .withString("id", searchBean.resourceId)
                                .navigation()
                        }
                        // 预订活动
                        ActivityType.ACTIVITY_TYPE_RESERVE -> {
                            // 预订
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_HOT_ACITITY)
                                .withString("id", searchBean.resourceId)
                                .withString("region", searchBean.region)
                                .navigation()
                        }
                        else -> {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_HOT_ACITITY)
                                .withString("id", searchBean.resourceId)
                                .navigation()
                        }

                    }
                }
            }
            // 活动室
            ResourceType.CONTENT_TYPE_ACTIVITY_SHIU -> {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance()
                        .build(ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL)
                        .withString("id", searchBean.resourceId)
                        .navigation()
                } else {
                    ToastUtils.showMessage("预订活动室，必须登录~")
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
            // 乡村详情
            ResourceType.CONTENT_TYPE_COUNTRY -> {
                ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_COUNTRY_DETAIL_ACTIVITY)
                    .withString("id", searchBean.resourceId)
                    .navigation();
            }
            // 品非遗基地
            ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE,ResourceType.CONTENT_TYPE_HERITAGE_TEACHING_BASE ->
                ARouter.getInstance()
                    .build(ARouterPath.LegacyModule.LEGACY_EXPERIENCE_DETAIL_ACTIVITY)
                    .withString("id",searchBean.resourceId)
                    .withString("type",searchBean.resourceType)
                    .navigation()
            // 品非遗项目
            ResourceType.CONTENT_TYPE_HERITAGE_ITEM ->
                ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_Smrity_DETAIL_ACTIVITY)
                    .withString("id",searchBean.resourceId)
                    .navigation()
            // 品非遗传承人
            ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE ->
                ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY)
                    .withString("id",searchBean.resourceId)
                    .navigation()
            ResourceType.CONTENT_TYPE_ASSOCIATION ->
                ARouter.getInstance().build(MainARouterPath.MAIN_CLUB_INFO)
                    .withInt("id", searchBean.resourceId.toInt())
                    .navigation()
            ResourceType.CONTENT_TYPE_ENTERTRAINMENT ->
                ARouter.getInstance().build(MainARouterPath.MAIN_PLAYGROUND_DETAIL)
                    .withString("id", searchBean.resourceId)
                    .navigation()
            ResourceType.CONTENT_TYPE_RESEARCH_BASE ->
                ARouter.getInstance()
                    .build(MainARouterPath.RESEARCH_DETAIL)
                    .withString("id",  searchBean.resourceId)
                    .navigation()
            ResourceType.CONTENT_TYPE_SPECIALTY ->
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SPECIAL_DETAIL)
                    .withString("id",  searchBean.resourceId)
                    .navigation()
        }
    }


}
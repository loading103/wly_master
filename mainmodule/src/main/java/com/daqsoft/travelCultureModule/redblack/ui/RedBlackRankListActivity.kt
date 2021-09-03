package com.daqsoft.travelCultureModule.redblack.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainRedblacklAreaListActivityBinding
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.ZARouterPath.RED_BLACK_AREA_LIST
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.travelCultureModule.redblack.adapter.RedBlackAreaListAdapter
import com.daqsoft.travelCultureModule.redblack.bean.AreaListBeanItem
import com.daqsoft.travelCultureModule.redblack.viewmodle.RedBlackAreaListViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import kotlinx.android.synthetic.main.layout_redblack_head1.*

/**
 * 红黑榜地区列表
 */
@Route(path = RED_BLACK_AREA_LIST)
class RedBlackRankListActivity : TitleBarActivity<MainRedblacklAreaListActivityBinding, RedBlackAreaListViewModel>() {
    /**
     * 用户当前位置
     */
    var selfLocation: LatLng? = null

    /**站点 Id*/
    @JvmField
    @Autowired
    var topTitle: String = ""

    @JvmField
    @Autowired
    var siteId: String? = ""

    @JvmField
    @Autowired
    var isArea: Boolean? = true

    var region: String? = null

    var type: String? = "1"

    var adapter: RedBlackAreaListAdapter? = null    //  地区综合排名


    override fun getLayout(): Int = R.layout.main_redblackl_area_list_activity

    override fun setTitle(): String {
        return if (topTitle.isNullOrEmpty()) "景区榜单" else topTitle
    }

    override fun injectVm(): Class<RedBlackAreaListViewModel> = RedBlackAreaListViewModel::class.java

    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.vm = mModel
        initSmartRefresh()
        adapterAddView()
        initViewMolde()
        mBinding.rvActivity!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvActivity!!.visibility = View.VISIBLE
        mBinding.rvActivity!!.adapter = adapter
    }
    private fun initSmartRefresh() {
        adapter = RedBlackAreaListAdapter()
        mBinding.smartRefreshLayout.apply {
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setOnRefreshListener {
                mModel.pageManager.initPageIndex()
                region?.let { it1 -> mModel.getAreaListData(it1,false) }
            }

            setOnLoadMoreListener {
                mModel.pageManager.nexPageIndex
                region?.let { it1 -> mModel.getAreaListData(it1,false) }
            }
        }
    }

    private fun initViewMolde() {
        region = SPUtils.getInstance().getString(SPKey.SITE_REGION)
        // 背景图片和内容
        mModel.bgBean.observe(this, Observer {
            iv_bg?.let { it1 ->
                Glide.with(this).load(it.backgroundImg).placeholder(R.mipmap.placeholder_img_fail_h300).into(it1)
                tsv?.text=it.summary
                setTitleContent(it.name)
            }
        })

        // 地区列表
        mModel.areadatas.observe(this, Observer {
            if (mModel.pageManager.isFirstIndex){
                if(it!=null && it.size>0) {
                    setTopViewData(it[0])
                    mBinding.smartRefreshLayout.finishRefresh()
                    adapter?.setNewData(it.subList(1, it.size))
                    if (it.size != mModel.pageManager.pageSize) {
                        mBinding.smartRefreshLayout.setNoMoreData(true)
                    } else {
                        mBinding.smartRefreshLayout.setNoMoreData(false)
                    }
                }else{
                    val emptyView=LayoutInflater.from(this).inflate(R.layout.layout_adapter_theme_empty,null)
                    emptyView.findViewById<TextView>(R.id.empty_content).setTextColor(resources.getColor(R.color.white))
                    adapter?.emptyView = emptyView
                }
            } else{
                adapter?.addData(it)
                mBinding.smartRefreshLayout.finishLoadMore()
                if(it.size<mModel.pageManager.pageSize){
                    mBinding.smartRefreshLayout.setNoMoreData(true)
                }else{
                    mBinding.smartRefreshLayout.setNoMoreData(false)
                }
            }
        })
    }



    override fun initData() {
        mModel.pageManager.initPageIndex()
        mModel.getBgUrlData()
        region?.let { mModel.getAreaListData(it,true) }
    }

    /**
     * 加入头部和空布局
     */
    private fun adapterAddView() {
        var viewHead: View = LayoutInflater.from(this).inflate(R.layout.layout_redblack_head1, null)
        viewHead.findViewById<FrameLayout>(R.id.fl_root).visibility=View.INVISIBLE
        adapter?.addHeaderView(viewHead)
    }

    private fun setTopViewData(bean: AreaListBeanItem) {
        fl_root.visibility=View.VISIBLE
        rating_bar_des?.rating= bean.totalAvg.toFloat()
        tv_area?.text=bean.regionName
        tv_score?.text="综合评分 ：${bean.totalAvg}"
        tv_area_score.text=bean.scenic.toString()
        tv_hotel_score?.text=bean.hotel.toString()
        tv_food_score?.text=bean.dining.toString()
        tv_njl_score?.text=bean.agr.toString()
        iv_content?.let {
            Glide.with(this)
                .load(bean?.image)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(it)
        }


        sl_area?.setOnClickListener{
            ARouter.getInstance().build(ZARouterPath.RED_BLACK_LIST)
                .withString("type",ResourceType.CONTENT_TYPE_SCENERY)
                .withString("itemTitle",bean.regionName)
                .withString("itemRegion",bean.region)
                .navigation()
        }
        sl_hotel?.setOnClickListener{
            ARouter.getInstance().build(ZARouterPath.RED_BLACK_LIST)
                .withString("type",ResourceType.CONTENT_TYPE_HOTEL)
                .withString("itemTitle",bean.regionName)
                .withString("itemRegion",bean.region)
                .navigation()
        }
        sl_food?.setOnClickListener{
            ARouter.getInstance().build(ZARouterPath.RED_BLACK_LIST)
                .withString("type",ResourceType.CONTENT_TYPE_RESTAURANT)
                .withString("itemTitle",bean.regionName)
                .withString("itemRegion",bean.region)
                .navigation()
        }
        sl_njl?.setOnClickListener{
            ARouter.getInstance().build(ZARouterPath.RED_BLACK_LIST)
                .withString("type",ResourceType.CONTENT_TYPE_AGRITAINMENT)
                .withString("itemTitle",bean.regionName)
                .withString("itemRegion",bean.region)
                .navigation()
        }
    }

}


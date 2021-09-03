package com.daqsoft.travelCultureModule.redblack.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.LetterSpacingTextView
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainRedblacklListActivityBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.ZARouterPath
import com.daqsoft.provider.ZARouterPath.RED_BLACK_LIST
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.travelCultureModule.redblack.adapter.RedBlackListAdapter
import com.daqsoft.travelCultureModule.redblack.bean.ResoureListBeanItem
import com.daqsoft.travelCultureModule.redblack.viewmodle.RedBlackListViewModel
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 红黑榜资源列表
 */
@Route(path = RED_BLACK_LIST)
class RedBlacklRankDetailListActivity : TitleBarActivity<MainRedblacklListActivityBinding, RedBlackListViewModel>() {
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
    var type: String =""

    @JvmField
    @Autowired
    var itemTitle: String? =null

    @JvmField
    @Autowired
    var itemRegion: String=""

    var adapter: RedBlackListAdapter? = null    //  地区综合排名
    var  typeBg: String = ""
    var path: String = ""

    var  sortType="desc"    //desc 倒序 asc正序
    override fun getLayout(): Int = R.layout.main_redblackl_list_activity

    override fun setTitle(): String {
        return if (topTitle.isNullOrEmpty()) "景区榜单" else topTitle
    }

    override fun injectVm(): Class<RedBlackListViewModel> = RedBlackListViewModel::class.java

    @SuppressLint("CheckResult")
    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.vm = mModel
        initSmartRefresh()
        initViewMolde()

        adapter = RedBlackListAdapter(type,title)
        adapterAddView()
        mBinding.rvActivity!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvActivity!!.visibility = View.VISIBLE
        mBinding.rvActivity!!.adapter = adapter
    }


    private fun initSmartRefresh() {
        mBinding.smartRefreshLayout.apply {
            setRefreshFooter(ClassicsFooter(context).setDrawableSize(20f))
            setEnableLoadMore(true)
            setOnRefreshListener {
                mModel.pageManager.initPageIndex()
                mModel.getResoureListData(itemRegion, type,sortType,false)
            }

            setOnLoadMoreListener {
                mModel.pageManager.nexPageIndex
                mModel.getResoureListData(itemRegion, type,sortType,false)
            }
        }
    }

    private fun initViewMolde() {
        //背景图片和内容
        mModel.bgBean.observe(this, Observer {
            mIvBg?.let { it1 ->
                Glide.with(this).load(it.backgroundImg).placeholder(R.mipmap.placeholder_img_fail_h300).into(it1)
                mTvTop?.text=it.summary
                setTitleContent(it.name)
            }
        })

        //资源列表
        mModel.resoureListBean.observe(this, Observer {
            if (mModel.pageManager.isFirstIndex){
                mBinding.smartRefreshLayout.finishRefresh()
                if(it!=null && it.size>0){
                    mBinding.llEmpty.llRppt.visibility=View.GONE
                    setTopViewData(it[0])
                    adapter?.setNewData(it.subList(1,it.size))
                    if(it.size!=mModel.pageManager.pageSize){
                        mBinding.smartRefreshLayout.setNoMoreData(true)
                    }else{
                        mBinding.smartRefreshLayout.setNoMoreData(false)
                    }
                }else{
                    setTopViewData(null)
                    mBinding.llEmpty.llRppt.visibility=View.VISIBLE
                    mBinding.llEmpty.emptyContent.setTextColor(resources.getColor(R.color.white))
//                    val emptyView=LayoutInflater.from(this).inflate(R.layout.layout_adapter_theme_empty,null)
//                    emptyView.findViewById<TextView>(R.id.empty_content).setTextColor(resources.getColor(R.color.white))
//                    adapter?.emptyView = emptyView
                }

            } else{
                mBinding.smartRefreshLayout.finishLoadMore()
                adapter?.addData(it)
                if(it.size<mModel.pageManager.pageSize){
                    mBinding.smartRefreshLayout.setNoMoreData(true)
                }
            }
        })
    }


    override fun initData() {
        mModel.pageManager.initPageIndex()
        when(type){
            ResourceType.CONTENT_TYPE_SCENERY-> typeBg="jqbd"
            ResourceType.CONTENT_TYPE_HOTEL-> typeBg="jdbd"
            ResourceType.CONTENT_TYPE_RESTAURANT-> typeBg="cybd"
            ResourceType.CONTENT_TYPE_AGRITAINMENT-> typeBg="njlbd"
        }
        mModel.getBgUrlData(typeBg)
        mModel.getResoureListData(itemRegion, type,sortType,true)

    }

    /**
     * 加入头部和空布局
     */

    var mIvBg: ImageView?=null
    var mIvLogo: RelativeLayout?=null
    var mTvTop: LetterSpacingTextView?=null
    var mTvArea: TextView?=null
    var mTvScore: TextView?=null
    var mbar: RatingBar?=null
    var mIvContent: ImageView?=null
    var mTvTag: TextView?=null
    var mTvComment: TextView?=null
    var rl_root: RelativeLayout?=null
    var item_1: FrameLayout?=null

    private fun adapterAddView() {
        var viewHead: View= LayoutInflater.from(this).inflate(R.layout.layout_redblack_head2, null)
        rl_root=  viewHead.findViewById(R.id.rl_root)
        rl_root?.visibility=View.INVISIBLE
        mIvBg=  viewHead.findViewById(R.id.iv_bg)
        mIvLogo=  viewHead.findViewById(R.id.iv_logo)
        mTvTop=  viewHead.findViewById(R.id.tsv)
        mTvArea=  viewHead.findViewById(R.id.tv_area)
        mbar=  viewHead.findViewById(R.id.rating_bar_des)
        mTvScore=  viewHead.findViewById(R.id.tv_score)
        mIvContent=  viewHead.findViewById(R.id.iv_content)
        mTvTag=  viewHead.findViewById(R.id.tv_tag)
        mTvComment=  viewHead.findViewById(R.id.tv_comment)
        item_1=  viewHead.findViewById(R.id.item_1)
        adapter?.addHeaderView(viewHead)
        when(type){
            ResourceType.CONTENT_TYPE_SCENERY-> {
                mIvLogo?.setBackgroundResource(R.mipmap.rank_scenic_title)
                path=MainARouterPath.MAIN_SCENIC_DETAIL
            }
            ResourceType.CONTENT_TYPE_HOTEL-> {
                mIvLogo?.setBackgroundResource(R.mipmap.rank_hotel_title)
                path=ZARouterPath.ZMAIN_HOTEL_DETAIL
            }
            ResourceType.CONTENT_TYPE_RESTAURANT-> {
                mIvLogo?.setBackgroundResource(R.mipmap.rank_food_title)
                path=MainARouterPath.MAIN_FOOD_DETAIL
            }
            ResourceType.CONTENT_TYPE_AGRITAINMENT-> {
                mIvLogo?.setBackgroundResource(R.mipmap.rank_village_title)
                path=ARouterPath.CountryModule.COUNTRY_HAPPINESS_DETAIL
            }
        }
    }
    private fun setTopViewData(bean: ResoureListBeanItem?) {
        rl_root?.visibility=View.VISIBLE
        if(bean==null){
            item_1?.visibility=View.INVISIBLE
            return
        } else{
            item_1?.visibility=View.VISIBLE
        }
        item_1?.setOnClickListener{
            ARouter.getInstance()
                .build(path)
                .withString("id", bean?.id.toString())
                .navigation()
            }
        mbar?.rating= bean?.numAvg?.toFloat()!!
        mTvArea?.text=bean?.name
        mTvComment?.text="${bean.commentNum}条评论"
        mTvTag?.text=bean?.level
        mTvScore?.text="综合评分${bean?.numAvg}"
        if (!bean.images.isNullOrEmpty()) {
            var url = bean?.images?.split(",")?.get(0)
            mIvContent?.let {
                Glide.with(this)
                    .load(url)
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .error(R.mipmap.placeholder_img_fail_240_180)
                    .into(it)

            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getResoureListData(itemRegion, type,sortType,false)
    }
}


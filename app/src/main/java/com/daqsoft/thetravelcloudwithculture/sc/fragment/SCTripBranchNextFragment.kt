package com.daqsoft.thetravelcloudwithculture.sc.fragment

import android.annotation.SuppressLint
import android.os.Handler
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.databinding.MainItemHotActivityClassifyBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.BrandMDD
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.bean.LineTagBean
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeTripBranchNextScBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeBranchScBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeCityBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeLineScBinding
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.jakewharton.rxbinding2.view.RxView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @des 安逸四川和智能行程
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class SCTripBranchNextFragment :
    BaseFragment<FragmentHomeTripBranchNextScBinding, SCHappySiChuanFragmentViewModel>() {

    override fun getLayout(): Int = R.layout.fragment_home_trip_branch_next_sc


    /**
     * 城市名片的适配器
     */
    var cityAdapter =
        object : RecyclerViewAdapter<ItemHomeCityBinding, BrandMDD>(R.layout.item_home_city) {
            @SuppressLint("CheckResult")
            override fun setVariable(mBinding: ItemHomeCityBinding, position: Int, item: BrandMDD) {
                mBinding.info = item.slogan
                mBinding.name = item.name
                mBinding.url = item.images
                mBinding.root.onModuleNoDoubleClick(
                    resources.getString(R.string.home_city_name),
                    ProviderApi.REGION_MAIN_COLUMNS
                ) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                        .withString("id", item.id.toString())
                        .withString("region", item.region)
                        .navigation()
                }
            }
        }

    /**
     * 印象天府
     */
    private var yxAdapter = object :
        RecyclerViewAdapter<ItemHomeBranchScBinding, HomeBranchBean>(R.layout.item_home_branch_sc) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemHomeBranchScBinding,
            position: Int,
            item: HomeBranchBean
        ) {
            Glide.with(context!!)
                .load(StringUtil.getImageUrlWatermark(item.brandImage, 335, 268))
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .error(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.image)
            mBinding.tvBrandName.text = item.name
            mBinding.tvBrandInfo.text = item.slogan
            if (!item.relationResourceNameStr.isNullOrEmpty()) {
                val strs = item.relationResourceNameStr.split(",")
                if (strs.size == 1) {
                    mBinding.tvRelation.text = strs[0]
                } else {
                    mBinding.tvRelation.text = strs[0]
                    mBinding.tvRelation1.text = strs[1]
                }
            } else {
                mBinding.llRelation.visibility = View.GONE
            }
            mBinding.root.onModuleNoDoubleClick(
                resources.getString(R.string.home_branch_name),
                ProviderApi.REGION_MAIN_COLUMNS
            ) {
                ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            }

        }

    }

    /**
     * 精品线路的适配器
     */
    var lineAdapter = object :
        RecyclerViewAdapter<ItemHomeLineScBinding, HomeContentBean>(R.layout.item_home_line_sc) {
        override fun setVariable(
            mBinding: ItemHomeLineScBinding,
            position: Int,
            item: HomeContentBean
        ) {
            if (!item.tagName.isNullOrEmpty()) {
                mBinding.info = item.tagName[0]
                mBinding.tvLineInfo.visibility = View.VISIBLE
            } else {
                mBinding.tvLineInfo.visibility = View.GONE
            }
            mBinding.name = item.title
            var imageUrl: String = item.getContentCoverImageUrl()
            GlideModuleUtil.loadDqImageWaterMark(imageUrl,context!!,mBinding.imgLineSc)
//            Glide.with(context!!)
//                .load(imageUrl)
//                .placeholder(R.mipmap.placeholder_img_fail_240_180)
//                .into(mBinding.imgLineSc)
            mBinding.root.onModuleNoDoubleClick(
                resources.getString(R.string.home_line_name),
                ProviderApi.REGION_MAIN_COLUMNS
            ) {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id", item.id.toString())
                    .withString("contentTitle", "资讯详情")
                    .navigation()
            }
        }

    }

    /**
     * 精品路线
     */
    private val lineTypeAdapter = object :
        RecyclerViewAdapter<MainItemHotActivityClassifyBinding, LineTagBean>(com.daqsoft.mainmodule.R.layout.main_item_hot_activity_classify) {
        var currentType: LineTagBean? = null

        override fun setVariable(
            mBinding: MainItemHotActivityClassifyBinding,
            position: Int,
            item: LineTagBean
        ) {
            mBinding.label.text = item.tagName
            mBinding.label.isSelected = item.select

            var d = RxView.clicks(mBinding.root)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    run {
                        if (currentType != item) {
                            item.select = true
                            if (currentType != null) {
                                currentType!!.select = false
                            }
                            currentType = item
                            mModel.lineType = item.tagId
                            selectToTypePos(position)
                            showLoadingDialog()
                            lineAdapter?.clear()
                            mModel.getContentList()
                            notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    /**
     *  选择点击的class
     */
    fun selectToTypePos(position: Int) {
        mBinding?.rvLineType.smoothScrollToPosition(position)
    }

    override fun initData() {
        mModel.getBranchList()
//        mModel.getContentList()
        mModel.getLineTypeList()
    }

    public fun getCityList() {
        mModel.getCitiyList()
    }

    private  var isfirst:Boolean=true
    override fun notifyData() {
        super.notifyData()
        // 品牌数据回调
        mModel.homeBranchBeanList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llHomeBrand.visibility = View.VISIBLE
                yxAdapter.add(it)
                if(it.size>1 && isfirst){
                    isfirst=false
                    showStartScroll(it,1)
                }
            } else {
                mBinding.llHomeBrand.visibility = View.GONE
            }
        })

        // 精品线路
        mModel.homeLineList.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                mBinding.llHomeLine.visibility = View.VISIBLE
                lineAdapter.clear()
                lineAdapter.add(it)
                lineAdapter.notifyDataSetChanged()
            } else {
                mBinding.llHomeLine.visibility = View.GONE
            }
        })
        mBinding.llHomeLine.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                .withString("channelCode", "lineChannel")
                .navigation()
        }
        // 城市名片
        mModel.mddCityList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llHomeCity.visibility = View.VISIBLE
                cityAdapter.clear()
                cityAdapter.add(it)
                cityAdapter.notifyDataSetChanged()
            } else {
                mBinding.llHomeCity.visibility = View.GONE
            }
        })

        mModel.lineTypeList.observe(this, Observer {
            dissMissLoadingDialog()
            if (it.size > 0) {
                val item = it[0]
                item.select = true
                mModel.lineType = item.tagId
                lineTypeAdapter.clear()
                lineTypeAdapter.add(it)
                lineTypeAdapter.currentType = item
                mBinding.rvLineType.visibility = View.VISIBLE
                mModel.getContentList()
            } else {
                mBinding.rvLineType.visibility = View.GONE
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    private fun showStartScroll(it: MutableList<HomeBranchBean>, position: Int) {
        var  index=position
        Handler().postDelayed({
            if(index==it.size){
                index=0
            }
//            ( mBinding.rvHomeBrand.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
            mBinding.rvHomeBrand.smoothScrollToPosition(index)
            if(activity!=null && !activity?.isFinishing!!){
                index += 1
                showStartScroll(it,index)

            }
        },5000)
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.vm = mModel
        // 城市名片
        mBinding.recyclerHomeCity.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = cityAdapter
        }
        // 线路类型
        val lineTypeLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvLineType.layoutManager = lineTypeLayoutManager
        mBinding.rvLineType.adapter = lineTypeAdapter

        lineAdapter.emptyViewShow = false

        // 精品线路
        mBinding.recyclerHomeLine.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = lineAdapter
        }
        // 城市名片
        mBinding.llHomeCityMore.onModuleNoDoubleClick(
            resources.getString(R.string.home_city_name),
            ProviderApi.REGION_MAIN_COLUMNS
        ) {
            ARouter.getInstance().build(MainARouterPath.MAIN_MDD_LIST).navigation()
        }
        // 品牌
        mBinding.llHomeBrandMore.onModuleNoDoubleClick(
            resources.getString(R.string.home_branch_name),
            ProviderApi.REGION_MAIN_COLUMNS
        ) {
            ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_LIST).navigation()
        }

        mBinding.rvHomeBrand.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = yxAdapter
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 刷新数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: RefreshHomeEvent) {
        initData()
    }

    override fun injectVm(): Class<SCHappySiChuanFragmentViewModel> =
        SCHappySiChuanFragmentViewModel::class.java

}

/**
 * @des 安逸四川和智能行程的viewMode
 * @author PuHua
 * @Date 2019/12/5 17:54
 */
class SCHappySiChuanFragmentViewModel : BaseViewModel() {
    /**
     * 品牌展播
     */
    var homeBranchBeanList = MutableLiveData<MutableList<HomeBranchBean>>()

    /**
     * 下一站出发的城市列表
     */
    var homeCityList = MutableLiveData<MutableList<HomeBranchBean>>()

    /**
     * 获取城市列表
     */
    var mddCityList = MutableLiveData<MutableList<BrandMDD>>()

    /**
     * 精品线路
     */
    var homeLineList = MutableLiveData<MutableList<HomeContentBean>>()

    /**
     * 线路类型
     */
    var lineTypeList = MutableLiveData<MutableList<LineTagBean>>()

    /**
     * 选中的精品线路类型
     */
    var lineType = ""

    /**
     * 跳转到品牌列表
     */
    var gotoBranchListActivity: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_BRANCH_LIST)
                .navigation()
        }
    }

    /**
     * 跳转到品牌列表
     */
    var gotoCityListActivity: ReplyCommand = object :
        ReplyCommand {
        override fun run() {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_MDD_LIST)
                .navigation()
        }
    }

    /**
     * 品牌展播列表
     */
    fun getBranchList() {
        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        // queryType  【选填】默认为1 1 首页/品牌名片 2 城市名片 3 随机推荐
        param["queryType"] = "1"
        // 活动类型id
        param["pageSize"] = "10"
        HomeRepository.service.getBranchList(param)
            .excute(object : BaseObserver<HomeBranchBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeBranchBean>) {
                    homeBranchBeanList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取城市名片
     */
    fun getCitiyList() {
        mPresenter.value?.loading = false
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        var siteRegion: String? = SPUtils.getInstance().getString(SPKey.SITE_REGION)
        var type: String = "CITY"
        if (!siteRegion.isNullOrEmpty()) {
            if (siteRegion.endsWith("000")) {
                type = "CITY"
            } else {
                type = "county"
            }
        }
        HomeRepository.service.getMDDCity("50", type, siteId)
            .excute(object : BaseObserver<BrandMDD>(mPresenter) {
                override fun onSuccess(response: BaseResponse<BrandMDD>) {
                    mddCityList.postValue(response.datas)
                }
            })
    }

    /**
     * 获取内容（精品线路等）
     */
    fun getContentList() {
        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        param["channelCode"] = Constant.HOME_CONTENT_TYPE_lineChannel
        param["pageSize"] = "4"
        param["currPage"] = "1"
        param["tagId"] = lineType
        HomeRepository.service.getHomeContentList(param)
            .excute(object : BaseObserver<HomeContentBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeContentBean>) {
                    homeLineList.postValue(response.datas)

                }

                override fun onFailed(response: BaseResponse<HomeContentBean>) {
                    mError.postValue(response)
                }
            })
    }

    /**
     * 获取精品线路类型列表
     */
    fun getLineTypeList() {
        mPresenter.value?.loading = false
        HomeRepository.service.getHomeLineTagList("lineChannel")
            .excute(object : BaseObserver<LineTagBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<LineTagBean>) {
                    lineTypeList.postValue(response.datas)
                }

            })
    }

}
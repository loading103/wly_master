package com.daqsoft.thetravelcloudwithculture.ws

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.databinding.MainItemHotActivityClassifyBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.rxCommand.ReplyCommand
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.extend.onModuleNoDoubleClick
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentHomeTripBranchNextWsBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeBranchWsBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeLineXjBinding
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.thetravelcloudwithculture.ui.event.RefreshHomeEvent
import com.daqsoft.thetravelcloudwithculture.ui.viewholder.RotesImaqeHolder
import com.daqsoft.thetravelcloudwithculture.ws.adapter.WSHomeCityAdapter
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable.interval
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @des ????????????  ????????????
 * @author PuHua
 * @Date 2019/12/5 17:52
 */
class WsTripBranchNextFragment :
    BaseFragment<FragmentHomeTripBranchNextWsBinding, WsHappySiChuanFragmentViewModel>() {

    override fun getLayout(): Int = R.layout.fragment_home_trip_branch_next_ws


    var citys = mutableListOf<BrandMDD>()

    val cityAdapter by lazy { WSHomeCityAdapter() }

    /**
     * ????????????????????????
     */
    var lineAdapter = object :
        RecyclerViewAdapter<ItemHomeLineXjBinding, HomeContentBean>(R.layout.item_home_line_xj) {
        override fun setVariable(
            mBinding: ItemHomeLineXjBinding,
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
            mBinding.url = item?.getContentCoverImageUrl()
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id", item.id.toString())
                    .withString("contentTitle", "????????????")
                    .navigation()
            }
        }

    }


    /**
     * ????????????
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
     *  ???????????????class
     */
    fun selectToTypePos(position: Int) {
        mBinding?.rootView.rvLineType.smoothScrollToPosition(position)
    }

    override fun initData() {
        mModel.getBranchList()
        mModel.getCitiyList()
        mModel.getLineTypeList()
    }

    var index = 0
    var isContinue = true

    // ???????????????
    lateinit var subscribe: Disposable

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg != null) {
                when (msg.what) {
                    1 -> {
                        index++
//                        if(index>=cityAdapter!!.count){
//                            index=0
//                        }
//                        mBinding.pagerHomeCity.currentItem = index
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        subscribe.dispose()//????????????

    }

    override fun onResume() {
        super.onResume()
        subscribe = interval(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (isContinue) {
                    handler.sendEmptyMessage(1)
                }
            }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        if (!subscribe.isDisposed) {
            subscribe.dispose()
        }
        super.onDestroy()
    }

    override fun notifyData() {
        super.notifyData()
        // ??????????????????
        mModel.homeBranchBeanList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {

                mBinding.llHomeBrand.visibility = View.VISIBLE
                yxAdapter.clear()
                if (it.size > 6)
                    yxAdapter.add(it.subList(0, 6));
                else
                    yxAdapter.add(it);

            } else {
                mBinding.llHomeBrand.visibility = View.GONE
            }
        })

        // ????????????
        mModel.homeLineList.observe(this, Observer {
            dissMissLoadingDialog()
            if (!it.isNullOrEmpty()) {
                if (it.size > 0) {
                    mBinding.datas = it
                    setSplashAdsView(it)
                } else {
                    mBinding.rootView.rlRoot.visibility = View.GONE
                }
            } else {
                mBinding.rootView.rlRoot.visibility = View.GONE
            }
        })

//         ????????????
        mModel.mddCityList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llHomeCity.visibility = View.VISIBLE
                mBinding.cityCardRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                mBinding.cityCardRecyclerView.adapter = cityAdapter
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
                mBinding?.rootView.rvLineType.visibility = View.VISIBLE
                mModel.getContentList()
            } else {
                mBinding?.rootView.rvLineType.visibility = View.GONE
            }
        })
    }

    /**
     * ??????????????????banner
     */
    private fun setSplashAdsView(adInfos: MutableList<HomeContentBean>) {
        mBinding.llHomeLine.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                .withString("channelCode", "lineChannel")
                .withString("topTitle", "????????????")
                .navigation()
        }
        mBinding.rootView.tvCard1.setOnClickListener{
            mBinding.rootView.cbBanner.setCurrentItem(0,true)
        }
        mBinding.rootView.tvCard2.setOnClickListener{
            mBinding.rootView.cbBanner.setCurrentItem(1,true)
        }
        mBinding.rootView.tvCard3.setOnClickListener{
            mBinding.rootView.cbBanner.setCurrentItem(2,true)
        }

        mBinding.rootView.cbBanner?.setPages(object : CBViewHolderCreator {
            override fun createHolder(itemView: View?): RotesImaqeHolder? {
                return activity?.let { RotesImaqeHolder(itemView, it) }
            }

            override fun getLayoutId(): Int {
                return R.layout.item_rotes_ads
            }
        }, adInfos as MutableList<HomeContentBean>)?.setCanLoop(true)?.startTurning(5000)?.startTurning()
            ?.setOnItemClickListener { pos ->
                try {
                    if (pos < adInfos.size) {
                        var adInfo = adInfos[pos]
                        if (adInfo != null) {
                            ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                                .withString("id", adInfo.id.toString())
                                .withString("contentTitle", "????????????")
                                .navigation()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }?.setPageIndicatorPadding(0, 0, 0, resources.getDimension(R.dimen.dp_20).toInt())
            ?.setPointViewVisible(false)?.onPageChangeListener = object : com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(index: Int) {
                mBinding.rootView.tvBoutiqueTitle.text=adInfos[index].title
                mBinding.rootView.tvBoutiqueTime.text="???????????????"+adInfos[index].title
                mBinding.rootView.tvBoutiqueInfo.text=adInfos[index].summary
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

            }

        }
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        // ????????????
//        mBinding.recyclerHomeLine.apply {
//            layoutManager = GridLayoutManager(context, 2)
//            adapter = lineAdapter
//        }

        // ????????????
        val lineTypeLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        mBinding?.rootView.rvLineType.layoutManager = lineTypeLayoutManager
        mBinding?.rootView.rvLineType.adapter = lineTypeAdapter
        val branchName = getString(R.string.home_branch_name)
        if (branchName.isNullOrEmpty()) {
            val set = ConstraintSet()

            mBinding.tvBrandTitleImg.visibility = View.VISIBLE
            mBinding.tvBrandTitle.visibility = View.GONE
        } else {
            mBinding.tvBrandTitle.visibility = View.VISIBLE
            mBinding.tvBrandTitleImg.visibility = View.GONE

        }
        // ????????????
        mBinding.llHomeCityMore.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_MDD_LIST) .withString("toptitle", "???????????? City Logo").navigation()
        }
        // ??????
        mBinding.llHomeBrandMore.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_LIST).navigation()
        }

        mBinding.rvHomeBrand.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = yxAdapter
        }
    }

    /**
     * ????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: RefreshHomeEvent) {
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun injectVm(): Class<WsHappySiChuanFragmentViewModel> =
        WsHappySiChuanFragmentViewModel::class.java

}

/**
 * @des ??????????????????????????????viewMode
 * @author PuHua
 * @Date 2019/12/5 17:54
 */
class WsHappySiChuanFragmentViewModel : BaseViewModel() {
    /**
     * ????????????
     */
    var homeBranchBeanList = MutableLiveData<MutableList<HomeBranchBean>>()

    /**
     * ??????????????????????????????
     */
    var homeCityList = MutableLiveData<MutableList<HomeBranchBean>>()

    /**
     * ??????????????????
     */
    var mddCityList = MutableLiveData<MutableList<BrandMDD>>()

    /**
     * ????????????
     */
    var homeLineList = MutableLiveData<MutableList<HomeContentBean>>()

    /**
     * ????????????
     */
    var lineTypeList = MutableLiveData<MutableList<LineTagBean>>()

    /**
     * ???????????????????????????
     */
    var lineType = ""

    /**
     * ?????????????????????
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
     * ?????????????????????
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
     * ??????????????????
     */
    fun getBranchList() {
        mPresenter.value?.loading = true
        val param = HashMap<String, String>()
        // queryType  ?????????????????????1 1 ??????/???????????? 2 ???????????? 3 ????????????
        param["queryType"] = "1"
        // ????????????id
        param["pageSize"] = "6"
        param["currPage"] = "1"
        HomeRepository.service.getBranchList(param)
            .excute(object : BaseObserver<HomeBranchBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeBranchBean>) {
                    homeBranchBeanList.postValue(response.datas)
                }
            })
    }

    /**
     * ??????????????????
     */
    fun getCitiyList() {
        mPresenter.value?.loading = true
        var siteId = SPUtils.getInstance().getString(SPKey.SITE_ID)
        HomeRepository.service.getMDDCity("50", "ALL", siteId)
            .excute(object : BaseObserver<BrandMDD>(mPresenter) {
                override fun onSuccess(response: BaseResponse<BrandMDD>) {
                    mddCityList.postValue(response.datas)
                }
            })
    }

    /**
     * ?????????????????????????????????
     */
    fun getContentList() {
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
            })
    }

    /**
     * ??????????????????????????????
     */
    fun getLineTypeList() {
        HomeRepository.service.getHomeLineTagList("lineChannel")
            .excute(object : BaseObserver<LineTagBean>() {
                override fun onSuccess(response: BaseResponse<LineTagBean>) {
                    lineTypeList.postValue(response.datas)
                }

            })
    }
}

/**
 * ????????????
 */
private var yxAdapter = object :
    RecyclerViewAdapter<ItemHomeBranchWsBinding, HomeBranchBean>(R.layout.item_home_branch_ws) {
    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: ItemHomeBranchWsBinding,
        position: Int,
        item: HomeBranchBean
    ) {
        Glide.with(context!!)
            .load(StringUtil.getImageUrlWatermark(item.brandImage))
            .placeholder(R.mipmap.placeholder_img_fail_240_180)
            .error(R.mipmap.placeholder_img_fail_240_180)
            .into(mBinding.image)
        mBinding.tvBrandName.text = "#" + item.name

        mBinding.root.onModuleNoDoubleClick(
            "??????????????????",
            ProviderApi.REGION_MAIN_COLUMNS
        ) {
            ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                .withString("id", item.id)
                .navigation()
        }

    }

}
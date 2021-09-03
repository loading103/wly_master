package com.daqsoft.thetravelcloudwithculture.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.utils.TextUtils
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.AnimatorUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.StyleTemplate
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.event.UpdateTokenEvent
import com.daqsoft.provider.net.TemplateApi
import com.daqsoft.provider.uiTemplate.banner.AdsBannerAdapter
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.FragmentMineNewBinding
import com.daqsoft.thetravelcloudwithculture.ui.adapter.*
import com.daqsoft.thetravelcloudwithculture.ui.vm.MineFragmentNewVm
import kotlinx.android.synthetic.main.fragment_mine_new.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 个人中心模块化
 */
class MineFragmentNew : BaseFragment<FragmentMineNewBinding, MineFragmentNewVm>() {

    lateinit var delegateAdapter: DelegateAdapter

    var bannerAdapterMaps: MutableList<AdsBannerAdapter> = mutableListOf()

    private val mineInfoAdapter: MineInfoAdapter by lazy { MineInfoAdapter(SingleLayoutHelper()) }
    private val realNameAdapter: RealNameAdapter by lazy { RealNameAdapter(SingleLayoutHelper()) }
    private val mineStoryAdapter: MineStoryAdapter by lazy { MineStoryAdapter(SingleLayoutHelper()) }
    lateinit var  animatorUtil: AnimatorUtil

    private var showNotice=true

    override fun getLayout(): Int {
        return R.layout.fragment_mine_new
    }

    override fun injectVm(): Class<MineFragmentNewVm> {
        return MineFragmentNewVm::class.java
    }


    override fun initData() {
        mModel.getMineTemplate()
//        setUserInfoView()
    }

    override fun onResume() {
        super.onResume()
        setUserInfoView()
    }

    private fun setUserInfoView() {
        mModel.siteInfo()
        mModel.getPersonTemplate()
        mModel.getMineCenterInfo()
        EventBus.getDefault().post(UpdateTokenEvent())
        if (AppUtils.isLogin()) {
            mModel.getCurrPoint()
            mModel.getHotStoryList()
            mModel.getPointTaskInfo()
            // todo 四川通知未打开
//            if(BaseApplication.appArea=="xj" || BaseApplication.appArea=="test" || BaseApplication.appArea=="sc"){
            mModel.getNoNumber1()
            mModel.getNoNumberList()
//            }

        }
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        animatorUtil=AnimatorUtil(activity)
        initSmatrLayout()
        initViewModel()
        initRecycleView()
    }


    /**
     * 初始化 下拉刷新
     */
    private fun initSmatrLayout() {
        mBinding.smartRefreshLayout.setOnRefreshListener {
            mModel.getMineTemplate()
            setUserInfoView()
        }
    }

    /**
     * 初始化 recycle view
     */
    private fun initRecycleView() {
        with(mBinding.rvHomeTemplate) {
            // 复用池大小
            val viewPool = RecyclerView.RecycledViewPool()
            setRecycledViewPool(viewPool)
            viewPool.setMaxRecycledViews(0, 20)
            viewPool.setMaxRecycledViews(13, 20)
            viewPool.setMaxRecycledViews(12, 20)

            // layoutManager
            val virtualLayoutManager = VirtualLayoutManager(context)
            layoutManager = virtualLayoutManager
            // adapter
            delegateAdapter = DelegateAdapter(virtualLayoutManager, false)
            adapter = delegateAdapter
        }
    }

    private fun initViewModel() {
        // 个人模板
        mModel.mineTemplateLd.observe(this, Observer {
            mBinding.smartRefreshLayout.finishRefresh()
            delegateAdapter.clear()
            delegateAdapter.notifyDataSetChanged()
            delegateAdapter.addAdapter(mineInfoAdapter)
            if (!it.isNullOrEmpty()) {
                for (index in it.indices) {
                    var template = it[index]
                    template?.let { obj ->
                        if (!obj.moduleType.isNullOrEmpty()) {
                            buildIndexTemplateView(obj, index)

                        }
                    }
                }
            }
        })
        mModel.noReadNumber.observe(this, Observer {
            if(!TextUtils.isEmpty(it)){
                mineInfoAdapter.setNumber(it)
                mineInfoAdapter.notifyDataSetChanged()
            }

        })
        mModel.noReadList.observe(this, Observer {
            if(!showNotice || it==null){
                return@Observer
            }
            showNotice=false
            if(it.size >0)  {
                animatorUtil.performAnim(true, mBinding.llNotice)
                mBinding.tvNotice.list=it
                mBinding.tvNotice.startScroll()

            }else{
                mBinding.llNotice.visibility=View.GONE
            }
        })

        mBinding.ivClose.onNoDoubleClick {
            showNotice=false
            animatorUtil.performAnim(false, mBinding.llNotice)
        }

        mineInfoAdapter.setData(object : MineInfoAdapter.GetData {
            override fun getData(str: String) {
                when (str) {
                    "1" -> mModel.getCurrPoint()
                    "2" -> {
                        showLoadingDialog()
                        mModel.getCheckIn()
                    }
                }
            }
        })

        // 个人信息模板
        mModel.personTemplate.observe(this, Observer {
            mineInfoAdapter.dataPersonInfoTemplate = it
            delegateAdapter.notifyItemChanged(0)
        })


        // 个人信息
        mModel.info.observe(this, Observer {
            mineInfoAdapter.dataUserInfo = it.data
            delegateAdapter.notifyItemChanged(0)
        })

        // 积分
        mModel.point.observe(this, Observer {
            mineInfoAdapter.dataUserCurrPoint = it
            delegateAdapter.notifyItemChanged(0)
        })

        // 签到任务
        mModel.taskInfo.observe(this, Observer {
            mineInfoAdapter.dataUserPointTask = it
            delegateAdapter.notifyItemChanged(0)
        })

        // 签到结果
        mModel.checkInResult.observe(this, Observer {
            dissMissLoadingDialog()
            mineInfoAdapter.dataCheckIn = it.code ?: -1
            delegateAdapter.notifyItemChanged(0)
        })

        // 站点
        mModel.siteBean.observe(this, Observer {
            mineInfoAdapter.dataSiteInfo = it
            delegateAdapter.notifyItemChanged(0)
        })

        //故事
        mModel.storyList.observe(this, Observer {
            mineStoryAdapter.datas.clear()
            mineStoryAdapter.datas.addAll(it)
            mineStoryAdapter.notifyDataSetChanged()
        })
    }

    /**
     * @param obj 样式实体
     * @param pos 位置信息
     */
    private fun buildIndexTemplateView(obj: StyleTemplate, pos: Int) {
        when (obj.moduleType) {
            // 普通菜单
            TemplateApi.MoudleType.menu -> {
                if (!obj.layoutDetails.isNullOrEmpty()) {
                    val adapter = MineMenuAdapter(SingleLayoutHelper())
                    delegateAdapter.addAdapter(adapter)
                    val commonTemplate = mutableListOf<CommonTemlate>()
                    commonTemplate.clear()
                    for (item in obj.layoutDetails) {
                        if (item.menuCode == "INHERITOR") {
                            if (AppUtils.isLogin() && !SPUtils.getInstance().getString(SPKey.HERITAGE_PEOPLEID).isNullOrEmpty()) {
                                commonTemplate.add(item)
                            }
//                        } else if (item.menuCode == "ROBOT") {
//                            if (SPUtils.getInstance().getBoolean(SPKey.SITE_IT_ROBOT, false)) {
//                                commonTemplate.add(item)
//                            }
                        } else {
                            commonTemplate.add(item)
                        }
                    }
                    adapter.addAll(commonTemplate)
                }
            }
            // 顶部菜单
            TemplateApi.MoudleType.topMenu -> {
                if (!obj.layoutDetails.isNullOrEmpty()) {
                    val adapter = MineTopMenuAdapter(SingleLayoutHelper())
                    delegateAdapter.addAdapter(adapter)
                    adapter.addAll(obj.layoutDetails)
                }
                delegateAdapter.addAdapter(realNameAdapter)
            }
            // 底部菜单
            TemplateApi.MoudleType.bottomMenu -> {
            }
            // 轮播图
            TemplateApi.MoudleType.carousel -> {
                if (!obj.layoutDetails.isNullOrEmpty()) {
                    val adapter: AdsBannerAdapter = AdsBannerAdapter(LinearLayoutHelper()).apply {
                        adses.clear()
                        adses.addAll(obj.layoutDetails)
                    }
                    bannerAdapterMaps.add(adapter)
                    delegateAdapter.addAdapter(adapter)
                }
            }
            // 时光故事
            TemplateApi.MoudleType.customize -> {
                if (obj.layoutDetail?.type == "myStory") {
                    delegateAdapter.addAdapter(mineStoryAdapter)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateLoginStatusChange(event: LoginStatusEvent) {
        event?.run {
            // 退出登录
            if (status == LoginStatusEvent.EXIT_LOGIN) {
                mModel.getMineTemplate()
                if(mineStoryAdapter!=null && mineStoryAdapter.datas!=null){
                    mineStoryAdapter.datas.clear()
                    mineStoryAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
package com.daqsoft.thetravelcloudwithculture.ui

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.android.CommonURL
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.version.VersionCheck
import com.daqsoft.integralmodule.repository.IntegralRepository
import com.daqsoft.integralmodule.repository.bean.SiteInfoBean
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.AdvCodeType
import com.daqsoft.provider.base.PublishChannel
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.event.LoginStatusEvent
import com.daqsoft.provider.event.UndateFinishEvent
import com.daqsoft.provider.event.UpdateTokenEvent
import com.daqsoft.provider.event.UpdateWebViewEvent
import com.daqsoft.provider.event.UpdateWebViewPlaceEvent
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.net.TemplateRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.net.TravelCloudRespository
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.provider.utils.dialog.PrivacyStatementDialog
import com.daqsoft.provider.view.web.ProgressWebView
import com.daqsoft.provider.view.web.WebUtils
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ActivityMainBinding
import com.daqsoft.thetravelcloudwithculture.jpush.NotifyMessageOpenHelper
import com.daqsoft.thetravelcloudwithculture.ui.utils.JumpUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import timber.log.Timber


/**
 * ?????????
 * @author ?????????
 * @version 1.0.0
 * @date 2019-9-24
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.MainModule.MAIN_ACTIVITY)
class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

    @JvmField
    @Autowired
    var notifyBundle : Bundle ? = null


    var privacyStatementDialog: PrivacyStatementDialog? = null

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    companion object {
        /**
         * ????????????
         */
        const val ACTION_TYPE = "action_type"

        /**
         *????????????
         */
        const val ACTION_ADS_OBJ = "ads_obj"

        /**
         * ????????????
         */
        const val TYPE_ADS = "type_ads"
    }

    var isNeedChangeTab = false

    var changeTabPos = 0

    private val fragments = mutableListOf<Fragment>()

    private var mMenuTabNames: MutableList<String> = mutableListOf()

    private var beginTransaction = supportFragmentManager.beginTransaction()

    /**
     * ?????????webview
     */
    private  lateinit var  mWebView:ProgressWebView
    private  var locationBean :LocationBean ?= null

    override fun injectVm(): MainActivityViewModel =
        ViewModelProvider.NewInstanceFactory().create(MainActivityViewModel::class.java)

    override fun setViewModel() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun initData() {
        checkToRedirect()
        initParam()
//        mModel.getAPPMenus(Constant.HOME_MENU_BOTTOM)
        mModel.siteInfo()
        mModel.refreshUserInfo()
        mModel.getOssKey()
        mModel.getSplashAds()
        getLocationData()
        initwebView(false)

    }


    /**
     * ?????????????????????
     */
    private fun initParam() {
        Timber.e("initParam")
        if(notifyBundle==null){
            Timber.e("initParam==null")
        }
        notifyBundle?.let {
            val notifyExtra = it.getParcelable<MyNotificationExtra>("notifyExtra")
            Timber.e("initParam---------"+Gson().toJson(notifyExtra))
            if (notifyExtra != null){
                NotifyMessageOpenHelper.pageJump(notifyExtra)
            }
        }
    }


    /**
     * ????????????
     */
    private fun getLocationData() {
        GaoDeLocation.getInstance().init(this,null, object : GaoDeLocation.OnGetCurrentLocationLisner1 {
            override fun onResult(
                adCode: String,
                adress: String,
                lat: Double,
                lon: Double,
                city: String) {
                locationBean = LocationBean(adCode,adress,lat,lon,city)
            }
            override fun onError(errorMsg: String) {
            }
        })
    }

    /**
     * ????????????????????????
     */
    private fun initwebView(isAgin:Boolean) {
        try {
            if(isAgin){
                mBinding.llRoot.removeAllViews()
                var js = "window.localStorage.setItem('cloudNewToken','')"
                mWebView?.evaluateJavascript(js, null);
                mWebView?.destroy()
            }
            mWebView = ProgressWebView(this,null)
            val params = LinearLayout.LayoutParams(1, 1)
            mWebView.layoutParams = params
            val volunteerStatus = SPUtils.getInstance().getString(SPKey.VOLUNTEER, "0")
            var url =""
            if( SPUtils.getInstance().getString(SPKey.H5_DOMAIN).isEmpty() ||  SPUtils.getInstance().getString(SPUtils.Config.TOKEN).isEmpty()){
                return
            }
            if (volunteerStatus == "1") {
                url =  SPUtils.getInstance().getString(SPKey.H5_DOMAIN) + "/#/volunteers"+"?source=app&appToken=" + SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
            } else {
                url = SPUtils.getInstance().getString(SPKey.H5_DOMAIN) + "/#/volunteer-service"+ "?source=app&appToken=" + SPUtils.getInstance().getString(SPUtils.Config.TOKEN)
            }
            WebUtils.setWebInfo2(mWebView, StringUtil.getHttpsUrl(url), this,false,null)
            mBinding.llRoot.addView(mWebView)
        }catch (e :Exception){
        }
    }


    private fun checkVersionUpdate() {
        if (!BaseApplication.isDebug) {
            VersionCheck.checkUpdate(this, CommonURL.VERSION_URL, CommonURL.APP_VERSION_SYS_ID)
        }
    }

    override fun onResume() {
        super.onResume()
        JPushInterface.setBadgeNumber(this,0)
        if (isNeedChangeTab) {
            isNeedChangeTab = false
            mBinding.sllNavigation.setCurrentSelectPostion(changeTabPos)
            switch(changeTabPos)
        }
    }

    /**
     * ??????????????????
     */
    fun changeTab(index: Int) {
        mBinding.sllNavigation.setCurrentSelectPostion(index)
        switch(index)
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        mModel.bottomMenus.observe(this, Observer {
            if (it != null) {

                for (element in it) {
                    var fragment = JumpUtils.homeFragmentUtils(element)
                    fragments.add(fragment)
                    beginTransaction.add(R.id.mFrameLayout, fragment)
                    beginTransaction.setMaxLifecycle(fragment, Lifecycle.State.CREATED);
                    mMenuTabNames.add("" + element.name)
                }
                mBinding.sllNavigation.addMenus(it)
                beginTransaction.commit()
                switch(0)
            }
        })
        mModel.siteBean.observe(this, Observer {
            if (it != null) {
                if (it.useNewMenu) {
                    mModel.getAppNewMenu()
                } else {
                    mModel.getAPPMenus(Constant.HOME_MENU_BOTTOM)
                }
            }
            checkVersionUpdate()
        })

        mModel.bottomTemplateMenus.observe(this, Observer {
            if (it != null && !it.layoutDetails.isNullOrEmpty()) {
                //                mMenuTabNames.addAll(it)
                for (element in it.layoutDetails) {
                    var fragment = JumpUtils.homeFragmentUtils(element)
                    fragments.add(fragment)
                    beginTransaction.add(R.id.mFrameLayout, fragment)
                    beginTransaction.setMaxLifecycle(fragment, Lifecycle.State.CREATED);
                }
                mBinding.sllNavigation.addNewMenus(it.layoutDetails)
                beginTransaction.commit()
                switch(0)
            }
        })
        mBinding.sllNavigation.setOnPageSelect { switch(it) }
        showPrivacyStatement()
    }


    /**
     * ????????????????????????
     */
    private fun showPrivacyStatement() {

        var isFirst = SPUtils.getInstance().getInt(SPUtils.Config.APP_IS_FIRST_LOAD, 0)
        if (isFirst == 0) {
            Handler().postDelayed({
                if (privacyStatementDialog == null) {
                    privacyStatementDialog = PrivacyStatementDialog(this@MainActivity)
                    privacyStatementDialog!!.show()
                }
            },2000)

        }
    }

    fun switch(position: Int) {
        if (position !in fragments.indices) {
            Timber.e("??????????????????")
            return
        }
        // ?????? ??????????????????
        if (position in mMenuTabNames.indices) {
            var item = mMenuTabNames[position]
            item?.let {
                StatisticsRepository.instance.statistcsModuleClick(
                    it,
                    ProviderApi.REGION_TABBAR
                )
            }
        }
        beginTransaction = supportFragmentManager.beginTransaction()
        for (i in fragments.indices) {
            if (position == i) {
                beginTransaction.show(fragments[i])
                beginTransaction.setMaxLifecycle(fragments[i], Lifecycle.State.RESUMED)
            } else {
                beginTransaction.hide(fragments[i])
            }
        }

        beginTransaction.commit()
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun changeTab(event: ChangeTabEvent) {
        var tempPos = getPositionByCode(event.code)
        if (tempPos != -1) {



            if (event.position != 2) {
                isNeedChangeTab = true
                changeTabPos = tempPos
                AppManager.instance.finishAllActivityExceptThis(this)
            } else {
                runOnUiThread {
                    try {
                        changeTab(tempPos)
                    } catch (e: Exception) {

                    }

                }
            }
        } else {
            if (event.code == "TIME") {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_TIME)
                    .navigation()
            } else if (event.code == "ACTIVITY") {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_ACTIVITY_LS)
                    .navigation()
            }

        }
    }

    private  var needData:Boolean=true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeTab(event: LoginStatusEvent) {
        needData=true
        initwebView(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun finished(event: UndateFinishEvent) {
        Handler().postDelayed({
            finish()
        },400)

    }
    /**
     * ?????????????????????-????????????-??????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setmWebViewToken(event: UpdateWebViewEvent) {
        if(mWebView!=null && !event.token.isNullOrEmpty()) {
            var js = "window.localStorage.setItem('cloudNewToken','" + event.token + "')"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                mWebView.evaluateJavascript(js, null);
            }
        }
    }
    /**
     * ??????(?????????????????????)
     */
    @SuppressLint("ObsoleteSdkInt")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setWebViewPlace(event: UpdateWebViewPlaceEvent) {
        if(mWebView!=null && locationBean!=null){
            var place: String = Gson().toJson(locationBean)
            var js = "window.localStorage.setItem('cloudLocation','$place')"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && needData==true) {
                needData=false
                mWebView.evaluateJavascript(js, null)
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun finish(event: UndateFinishEvent) {
        Handler().postDelayed({
            finish()
        },400)

    }
    /**
     * ?????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refresh(event: UpdateTokenEvent) {
        Log.e("------","?????????????????????")
        mModel.refreshUserInfo()
    }
    fun getPositionByCode(code: String): Int {
        if ((mModel.bottomMenus.value?.size ?: 0) > 0) {
            for (index in mModel.bottomMenus.value!!.indices) {
                if (mModel.bottomMenus.value!![index].menuCode == code) {
                    return index
                }
            }
        }
        return -1
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        try {
            EventBus.getDefault().unregister(this)
            privacyStatementDialog = null
        } catch (e: Exception) {
        }
        super.onDestroy()

    }

    var firstBackClickTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - firstBackClickTime > 2000) {
            toast(getString(R.string.quit_comfirm)).setGravity(Gravity.CENTER, 0, 0)
            firstBackClickTime = System.currentTimeMillis()
            return
        } else {
            super.onBackPressed()
        }
    }

    private fun checkToRedirect() {
        try {
            var bundle: Bundle? = intent?.getBundleExtra("bundle")
            if(notifyBundle==null){
                notifyBundle = intent?.getBundleExtra("notifyBundle")
            }

            if (bundle != null) {
                var actionType: String? = bundle.getString(MainActivity.ACTION_TYPE)
                when (actionType) {
                    MainActivity.TYPE_ADS -> {
                        // ?????? ????????????
                        var adInfo: AdInfo? = bundle.getParcelable(MainActivity.ACTION_ADS_OBJ)
                        if (adInfo != null) {
                            MenuJumpUtils.adJump(adInfo)
                        }
                    }
                    else -> {
                        // ????????????????????????
                    }
                }
            }
        } catch (e: java.lang.Exception) {

        }
    }


    override fun onStop() {
        super.onStop()
        Thread { // ?????????
            val safe = AntiHijackingUtil.checkActivity(applicationContext)
            // ????????????
            val isHome = AntiHijackingUtil.isHome(applicationContext)
            // ????????????
            val isReflectScreen = AntiHijackingUtil.isReflectScreen(applicationContext)
            // ??????????????????????????????
            if (!safe && !isHome && !isReflectScreen) {
                Looper.prepare()
                try {
                    val packageManager: PackageManager = packageManager
                    val packageInfo: PackageInfo = packageManager.getPackageInfo(getPackageName(), 0)
                    val labelRes: Int = packageInfo.applicationInfo.labelRes
                    ToastUtils.showMessage("${resources.getString(labelRes)}??????????????????")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                Looper.loop()
            }
        }.start()
    }


}

/**
 * @des mainActivity???viewModel
 * @author PuHua
 * @Date 2020/1/7 9:09
 */
class MainActivityViewModel : BaseViewModel() {
    /**
     * ????????????
     */
    var bottomMenus = MutableLiveData<MutableList<HomeMenu>>()

    /**
     * ???????????????
     */
    var bottomTemplateMenus = MutableLiveData<StyleTemplate>()

    /**
     * ????????????
     */
    fun getAPPMenus(location: String) {
        mPresenter.value?.loading = true

        HomeRepository.service.getAPPMenuList(Constant.HOME_CLIENT_TYPE, location).excute(object :
            BaseObserver<HomeMenu>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeMenu>) {
                bottomMenus.postValue(response.datas)
            }
        })
    }

    fun getAppNewMenu() {
        TemplateRepository.instance.service.getIndexBottomMenuTemplate()
            .excute(object : BaseObserver<StyleTemplate>() {
                override fun onSuccess(response: BaseResponse<StyleTemplate>) {
                    bottomTemplateMenus.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<StyleTemplate>) {
                    bottomTemplateMenus.postValue(null)
                }
            })
    }

    /**
     * ????????????
     */
    var siteBean = MutableLiveData<SiteInfoBean>()

    /**
     * ??????????????????
     */
    fun siteInfo() {
        mPresenter.value?.isNeedRecyleView = false
        mPresenter.value?.loading = true
        IntegralRepository().siteInfo()
            .excute(object : BaseObserver<SiteInfoBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SiteInfoBean>) {
                    if (response.code == 0 && response.data != null) {

                        SPUtils.getInstance().put(SPKey.SHOP_CODE, response.data?.shopCode)
                        SPUtils.getInstance().put(SPKey.SHOP_URL, response.data?.shopUrl)
                        SPUtils.getInstance().put(SPKey.SITE_CODE, response.data?.siteCode)
                        SPUtils.getInstance().put(SPKey.H5_DOMAIN, response.data?.h5Domain)
                        // ??????id
                        SPUtils.getInstance().put(SPKey.SITE_ID, response.data?.id.toString())
                        SPUtils.getInstance().put(SPKey.SITE_REGION, response.data?.region)
                        if (response.data?.serviceRobot != null) {
                            SPUtils.getInstance()
                                .put(SPKey.SITE_IT_ROBOT, response.data?.serviceRobot!!)
                        }
                        if (response?.data != null) {
                            BaseApplication.isMustUpdate = response.data!!.mandatoryUpdate
                        }
                        if (!response.data?.watermark.isNullOrEmpty()) {
                            SPUtils.getInstance()
                                .put(SPKey.SITE_WATERMARK, response.data?.watermark)
                        }
                    }
                    siteBean.postValue(response.data)
                }

                override fun onFailed(response: BaseResponse<SiteInfoBean>) {
                    siteBean.postValue(null)
                }
            })
    }

    /**
     * ??????osskey
     */
    fun getOssKey() {
        TravelCloudRespository().ossService.getOssKeys()
            .excute(object : BaseObserver<String>(mPresenter) {
                override fun onSuccess(response: BaseResponse<String>) {
                    if (!response.data.isNullOrEmpty()) {
                        var key = SM4Util.decryptByEcb(response.data)
                        if (!key.isNullOrEmpty()) {
                            SPUtils.getInstance().put(SPUtils.Config.OSS_KEY, key)
                        }
                    }
                }

            })
    }

    /**
     * ??????app????????????
     */
    fun getSplashAds() {
        mPresenter?.value?.loading = false
        HomeRepository.service.getHomeAd(PublishChannel.MICRO_SITE, AdvCodeType.APP_SPLASH_ADV)
            .excute(object : BaseObserver<HomeAd>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeAd>) {
                    var homeAd: HomeAd? = response.data
                    if (homeAd != null) {
                        if (!homeAd.adInfo.isNullOrEmpty()) {
                            // ????????????
                            try {
                                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).clear()
                                // ???adinfo??????????????????gson??????
                                var adInfoJson: String =
                                    GsonBuilder().create().toJson(homeAd.adInfo)
                                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS)
                                    .put(SPUtils.Config.APP_SPLASH_IMAGES, adInfoJson)
                                // ????????????
                                var screenHeight = Utils.getHeightInPx(BaseApplication.context!!)
                                var screenWidth = Utils.getWidthInPx(BaseApplication.context!!)
                                for (item in homeAd.adInfo) {
                                    DownLoadFileUtil.downNetworkImage(
                                        item.imgUrl,
                                        screenWidth,
                                        screenHeight
                                    )
                                }
                            } catch (e: java.lang.Exception) {
                                // ????????????
                                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).clear()
                            }
                        } else {
                            // ????????????
                            SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).clear()
                        }
                    }
                }
            })
    }

    /**
     * ??????????????????
     */
    fun refreshUserInfo() {
        mPresenter?.value?.loading = false
        UserRepository().userService
            .refreshToken()
            .excute(object : BaseObserver<UserLogin>() {
                override fun onSuccess(response: BaseResponse<UserLogin>) {
                    SPUtils.getInstance()
                        .put(SPKey.USER_CENTER_TOKEN, response.data?.userCenterToken)
                    SPUtils.getInstance().put(SPKey.UUID, response.data?.unid)
                    SPUtils.getInstance().put(SPKey.SITEID, response.data?.siteId ?: -1)
                    SPUtils.getInstance().put(SPKey.ENCRYPTION, response.data?.encryption ?: "")
                    SPUtils.getInstance().put(SPKey.VIPID, response.data?.vipId ?: -1)
                    SPUtils.getInstance().put(SPKey.UID, response.data?.unid)
                    SPUtils.getInstance().put(SPKey.PHONE, response.data?.phone)
                    SPUtils.getInstance().put(SPKey.USERCENTERTOKEN, response.data?.userCenterToken)
                    SPUtils.getInstance().put(SPKey.HEAD_URL, response.data?.headUrl)
                    SPUtils.getInstance().put(SPKey.VOLUNTEER,response.data?.volunteerStatus.toString())
                    SPUtils.getInstance().put(SPKey.VOLUNTEER_TEAM,response.data?.volunteerTeamStatus.toString())
                    SPUtils.getInstance().put(SPKey.REALNAMESTATUS, response.data?.realNameStatus ?: 0)


                }
            })
    }


    init {
        getPullDownToRefreshTip()
    }

    /**
     * ??????????????????
     */
    private fun getPullDownToRefreshTip() {
        HomeRepository.service.getPullDownToRefreshTip()
            .excute(object : BaseObserver<PullDownToRefreshTip>() {
                override fun onSuccess(response: BaseResponse<PullDownToRefreshTip>) {
                    response.data?.let {
                        BaseApplication.refreshTips = it.content
                        BaseApplication.pandaRefreshHeader!!.setTips(it.content)
                        SPUtils.getInstance().put(Constant.MAIN_TIP,it.content)
                    }
                }

                override fun onFailed(response: BaseResponse<PullDownToRefreshTip>) {
                    BaseApplication.refreshTips = ""
                }
            })
    }


}

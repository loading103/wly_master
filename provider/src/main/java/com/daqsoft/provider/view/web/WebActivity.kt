package com.daqsoft.provider.view.web

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.net.toUri
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.utils.StringUtil.getHtml
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.KeyBoardListener
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.dialog.QrCodeDialog
import com.daqsoft.baselib.widgets.timepicker.IMDensityUtil
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.base.WebSiteAouter
import com.daqsoft.provider.bean.LocationBean
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.databinding.ActivityWebBinding
import com.daqsoft.provider.event.UpdateDissmissEvent
import com.daqsoft.provider.event.UpdateWebViewDownLoadEvent
import com.daqsoft.provider.event.UpdateWebViewEvent
import com.daqsoft.provider.event.UpdateWebViewPlaceEvent
import com.daqsoft.provider.service.GaoDeLocation
import com.daqsoft.provider.utils.SelectImageUtils
import com.daqsoft.provider.utils.StopClickFast
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_web.*
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.MultiVideoSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-8-14
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.Provider.WEB_ACTIVITY)
class WebActivity : TitleBarActivity<ActivityWebBinding, WebViewModel>() {

    @JvmField
    @Autowired
    var mTitle: String? = ""

    @JvmField
    @Autowired
    var html: String? = ""

    @JvmField
    @Autowired
    var isscar: String? = ""

//    /**
//     * 是否可以分享
//     */
//    @JvmField
//    @Autowired
//    var needShare: Boolean? = false
//
//    /**
//     * 分享内容
//     */
//    @JvmField
//    @Autowired
//    var shareDes: String? = ""
//    /**
//     * 分享封面
//     */
//    @JvmField
//    @Autowired
//    var shareImage: String? = ""

    private  var locationBean :LocationBean ?= null

    override fun getLayout(): Int = R.layout.activity_web

    override fun setTitle(): String = ""

    override fun injectVm(): Class<WebViewModel> = WebViewModel::class.java

    private val REQUEST_CAMERA_CODE = 0

    private val REQUEST_ALBUM_CODE = 1

    private val REQUEST_VIDEO_CODE = 2
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null
    // 文件选择
    private var mValueListCallback: ValueCallback<Array<Uri?>?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun initView() {
        title = mTitle ?: ""
        EventBus.getDefault().register(this)
        KeyBoardListener.getInstance(this).init();
        setBottonView()
        getLocationData()
        initWebView()
        initChooseFile()
    }

//    override fun initCustomTitleBar(mTitleBar: TitleBar) {
//        if(this!!.needShare!!) {
//            showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
//            setShareClick(object : View.OnClickListener {
//                override fun onClick(v: View?) {
//                    if (sharePopWindow == null) {
//                        sharePopWindow = SharePopWindow(this@WebActivity)
//                    }
//                    sharePopWindow?.setShareContent(
//                        mTitle, shareDes, shareImage, html
//                    )
//                    if (!sharePopWindow!!.isShowing) {
//                        sharePopWindow?.showAsDropDown(mTitleBar)
//                    }
//                }
//            })
//        }
//    }
    private fun initChooseFile() {
        mWebView.webChromeClient = mWebChromeClient
    }

    override fun onBackPressed() {
        if(StopClickFast.isSendFastClick()){
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack()
            } else {
                super.onBackPressed()
            }
        }
    }

    /**
     * 初始化WebView
     */
    @SuppressLint("LogNotTimber")
    private fun initWebView() {
        if (AppUtils.isLogin()) {
            html = StringUtil.getDqUrl(html!!, SPUtils.getInstance().getString(SPKey.UUID))
        }
        Log.e("-----------url=", html)
        if (html != null && html!!.isNotEmpty()) {
            if (html!!.startsWith("http://") || html!!.startsWith("https://") || html!!.startsWith("file:///")) {
                mBinding.mWebView.addJavascriptInterface(AppJsUtil(this), "AppJs");
                WebUtils.setWebInfo2(
                    mBinding.mWebView,
                    html,
                    this,true,
                    object : WebUtils.OnfinishListener {
                        override fun onReceivedTitle(name: String?) {
                            if (name != null && name!!.isNotEmpty()
                                && !name.equals("undefine")
                                && !name.contains("http")
                                && !name.contains("file:///")
                                && !name.contains("html")
                            ) {
                                title = name
                            }
                        }


                        override fun finish() {
                        }

                    })
            } else {
                mBinding.mWebView.settings.javaScriptEnabled = true
                var mLayoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                mLayoutParams.setMargins(
                    resources.getDimension(R.dimen.dp_20).toInt(),
                    0,
                    resources.getDimension(R.dimen.dp_20).toInt(),
                    0
                )
                mBinding.mWebView.layoutParams = mLayoutParams
                mBinding.mWebView.loadDataWithBaseURL(
                    null,
                    getHtml(html!!),
                    "text/html",
                    "utf-8",
                    null
                )
            }
        }
        // 隐私策略支持缩放
        if (!isscar.isNullOrEmpty()) {
            mBinding.mWebView.settings.setSupportZoom(true)
            mBinding.mWebView.settings.builtInZoomControls = true
            mBinding.mWebView.settings.displayZoomControls = false
        }
        mBinding.mWebView.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack()
                    return@setOnKeyListener true
                }
            }
            false
        }
    }

    /**
     * 获取定位
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

    override fun initData() {

    }

    override fun onPause() {
        super.onPause()
        mBinding.mWebView?.pauseTimers()
        mBinding?.mWebView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mBinding.mWebView?.resumeTimers()
        mBinding?.mWebView?.onResume()
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        cleanWebView()

    }

    /**
     * 清除token缓存
     */
    private fun cleanWebView() {
        if (mValueListCallback != null) {
            mValueListCallback!!.onReceiveValue(null)
            mValueListCallback = null
        }
        var js = "window.localStorage.setItem('cloudNewToken','')"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mBinding?.mWebView.evaluateJavascript(js, null);
        }
        mBinding?.mWebView?.destroy()

    }

    /**
     * 底部头像选择弹框
     */
    private val popupWindow by lazy {
        SelectImageUtils.initPictureSelectPop(this, REQUEST_CAMERA_CODE, REQUEST_ALBUM_CODE)
    }

    var mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {

        }
        override fun onReceivedTitle(view: WebView?, name: String?) {
            if (name != null) {
                title = name
            }
            super.onReceivedTitle(view, title)
        }

        // For Android >= 5.0
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri?>?>,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            mValueListCallback = filePathCallback

            if(fileChooserParams?.acceptTypes?.get(0)=="image/*"){
                popupWindow.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
            }
            if(fileChooserParams?.acceptTypes?.get(0)=="video/*"){
                chooseVideo()
            }
            return true
        }
    }

    /**
     * 选择视频
     */
    private fun chooseVideo() {
        var intent1:Intent= Intent(this, MultiVideoSelectorActivity::class.java)
        // 是否显示调用相机拍照
        intent1.putExtra(MultiFileSelectorActivity.EXTRA_SHOW_CAMERA, true)
        // 最大视频选择数量
        intent1.putExtra(MultiFileSelectorActivity.EXTRA_SELECT_COUNT, 1)
        intent1.putExtra(MultiFileSelectorActivity.EXTRA_SELECT_MODE, MultiFileSelectorActivity.MODE_SINGLE)
        intent1.putExtra("TYPE", Constrant.ADD_VIDEO)
        // 默认选择
        startActivityForResult(intent1, REQUEST_VIDEO_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_CODE) {
            val file = SelectImageUtils.getFileFromCamera()
            if (file?.exists() == false) {
                mValueListCallback?.onReceiveValue(null)
                mValueListCallback = null
                return
            }
            val urix = arrayOf(file?.toUri())
            mValueListCallback?.onReceiveValue(urix)
            mValueListCallback = null
            return
        }
        // 本地选择相册或是视频
        if (data == null) {
            mValueListCallback?.onReceiveValue(null)
            mValueListCallback = null
            return
        }
        if (requestCode == REQUEST_ALBUM_CODE ) {
            val uri = data?.data
            val urix = arrayOf(uri)
            mValueListCallback?.onReceiveValue(urix)
            mValueListCallback = null
            return
        }
        if (requestCode == REQUEST_VIDEO_CODE) {
            val hasExtra = data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)
            if(hasExtra){
                var list: ArrayList<Image> = data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                if(list.isNotEmpty()){
                    val toUri:Uri? = File(list[0]?.path).toUri()
                    val urix = arrayOf(toUri)
                    mValueListCallback?.onReceiveValue(urix)
                    mValueListCallback = null
                }
            }else{
                mValueListCallback?.onReceiveValue(null)
                mValueListCallback = null
            }
        }
    }


    /**
     * 选图片这些用的
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setmValueListCallbackNull(event: UpdateDissmissEvent) {
        if(mValueListCallback==null){
            return
        }
        mValueListCallback?.onReceiveValue(null)
        mValueListCallback = null
    }

    /**
     * 网页（未登录）-登录成功-返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setmWebViewToken(event: UpdateWebViewEvent) {
        if(mWebView!=null && !event.token.isNullOrEmpty()) {
            var js = "window.localStorage.setItem('cloudNewToken','" + event.token + "')"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.evaluateJavascript(js, null);
                mWebView.reload()
            }
        }
    }
    /**
     * 位置(第一次注入地址)
     */
    private var  needLoaction:Boolean=true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setWebViewPlace(event: UpdateWebViewPlaceEvent) {
        if(mWebView!=null && locationBean!=null && needLoaction){
            needLoaction=false
            var place: String = Gson().toJson(locationBean)
            var js = "window.localStorage.setItem('cloudLocation','$place')"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                mWebView.evaluateJavascript(js, null)
            }
        }else{
            val js = "window.localStorage.getItem('cloudLocation');"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                mWebView.evaluateJavascript(js, ValueCallback {
                })
            }
        }
    }

    /**
     * 下载二维码
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setWebViewDown(event: UpdateWebViewDownLoadEvent) {
        try {
            showLoadingDialog()
            DownLoadFileUtil.downNetworkImage(
                event.url,
                object : DownLoadFileUtil.DownImageListener {
                    override fun onDownLoadImageSuccess() {
                        dissMissLoadingDialog()
                        ToastUtils.showMessage("保存二维码成功~")
                    }
                })
        } catch (e: Exception) {
            dissMissLoadingDialog()
            ToastUtils.showMessage("保存二维码失败~")
        }
    }

    /**
     * 适配底部虚拟按键
     */
    private fun setBottonView(){
        if(IMDensityUtil.isNavigationBarShow(windowManager)){
            mBinding.viewBottom.visibility==View.VISIBLE
            val layoutParams = mBinding.viewBottom.layoutParams
            layoutParams.height=IMDensityUtil.getBottomBarHeight(this)
            mBinding.viewBottom.layoutParams=layoutParams
        }else{
            mBinding.viewBottom.visibility==View.GONE
        }
    }

}
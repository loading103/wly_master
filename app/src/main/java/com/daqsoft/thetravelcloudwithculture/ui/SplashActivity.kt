package com.daqsoft.thetravelcloudwithculture.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import cn.jpush.android.api.JPushInterface
import com.daqsoft.baselib.base.BaseActivity
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.utils.MD5Util
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.utils.file.DownLoadFile
import com.daqsoft.baselib.utils.file.DownLoadFileUtil
import com.daqsoft.baselib.widgets.MyVideoView
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.bean.HomeAd
import com.daqsoft.provider.utils.JpushAliasUtils
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ActivitySplashNewBinding
import com.daqsoft.thetravelcloudwithculture.ui.viewholder.SplashAdsHolder
import com.daqsoft.thetravelcloudwithculture.ui.vm.SplashVm
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *启动页
 **/
class SplashActivity : BaseActivity<ActivitySplashNewBinding,SplashVm>() {

    //rx权限对象
    private var permissions: RxPermissions? = null

    // 倒计时
    private var cutdownDisable: Disposable? = null

    private  var mediaController : MediaController? = null

    private  var isVideo : Boolean = false

    private  var mVideoView : MyVideoView? = null

    // 五秒超时
    private  var isTimeOut : Boolean = false

    // 是否获取数据
    private  var getImageData : Boolean = false
    private  var getVideoVData : Boolean = false
    private  var getVideoHData : Boolean = false

    // 获取的数据图片和两种视频
    private  var homeImageAd : HomeAd? = null
    private  var homeVideoVAd : HomeAd? = null
    private  var homeVideoHAd : HomeAd? = null

    private val mhandle=Handler()

    override fun getLayout(): Int {
        return R.layout.activity_splash_new
    }
    override fun injectVm(): SplashVm = ViewModelProvider.NewInstanceFactory().create(SplashVm::class.java)

    override fun setViewModel() {}

    override fun initView() {
        // 设置推送标签，不用放在applicattion,有时候没初始化完，会失败很多次
        JpushAliasUtils.setTag(this)
        initViewModel()
        mBinding.vSkipSplsh?.onNoDoubleClick {
            toMainPage()
            cutdownDisable?.dispose()
        }

        mBinding.tvAllow.setOnClickListener {
            permissions?.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            permissions?.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )!!.subscribe {
                mBinding.llRoot.visibility=View.GONE
                if (it) {
                    getAdData()
                } else {
                    getAdData()
//                    postToMainPage()
                }
            }
        }

        mBinding.tvNo.setOnClickListener {
            mBinding.llRoot.visibility=View.GONE
//            postToMainPage()
            getAdData()
        }
    }

    /**
     * 获取权限执行
     */
    @SuppressLint("CheckResult")
    override fun initData() {
        permissions = RxPermissions(this)
        val showBoolean = SPUtils.getInstance().getBoolean(SPKey.ALLOW_KEY, true)
        if(showBoolean && !permissions?.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)!! && SPUtils.getInstance().getInt(SPUtils.Config.APP_IS_FIRST_LOAD, 0)==0){
            mBinding.llRoot.visibility=View.VISIBLE
            SPUtils.getInstance().put(SPKey.ALLOW_KEY,false)
            return
        }
        if(permissions?.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)!! && permissions?.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)!!){
            getAdData()
        }else{
//            postToMainPage()
            getAdData()
        }

    }


    /**
     *  延迟五秒没数据就跳转首页
     */
    private fun getAdData() {
        mModel.getSplashImageAds()
        mModel.getSplashVideoV()
        mModel.getSplashVideoH()
        mhandle.postDelayed({
            if(!isTimeOut) {
                isTimeOut=true
                toMainPage()
            }
        },5000)
    }

    private fun initViewModel() {
        mModel.mImageAds.observe(this, Observer {
            homeImageAd=it
            getImageData=true
            handleData()
        })
        mModel.mVideoVAds.observe(this, Observer {
            homeVideoVAd=it
            getVideoVData=true
            handleData()
        })
        mModel.mVideoHAds.observe(this, Observer {
            homeVideoHAd=it
            getVideoHData=true
            handleData()
        })
    }

    /**
     * 处理资源顺序，有图片显示图片，没有显示竖屏视频，还没有显示横屏视频
     */
    @SuppressLint("CheckResult")
    private fun handleData() {
        if(getImageData && getVideoHData && getVideoVData && !isTimeOut){
            if(homeImageAd!=null && homeImageAd?.adInfo!!.isNotEmpty()){
                isTimeOut=true
                setImageData(homeImageAd!!)
                return
            }
            if(homeVideoVAd!=null && homeVideoVAd?.adInfo!!.isNotEmpty()){
                isTimeOut=true
                permissions?.request( Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )!!.subscribe {
                    if (it) {
                        setVideoVData(homeVideoVAd!!)
                    } else {
                        postToMainPage()
                    }
                }
                return
            }
            if(homeVideoHAd!=null && homeVideoHAd?.adInfo!!.isNotEmpty()){
                isTimeOut=true
                permissions?.request( Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )!!.subscribe {
                    if (it) {
                        setVideoHData(homeVideoHAd!!)
                    } else {
                        postToMainPage()
                    }
                }
            }
        }
    }

    /**
     * 处理图片逻辑
     */
    private fun setImageData(it: HomeAd) {
        if (it!= null) {
            if (!it.adInfo.isNullOrEmpty()) {
                // 更新缓存
                try {
                    showAdsView(it.adInfo)
                    SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).clear()
                    // 将adinfo列表，转换成gson缓存
                    var adInfoJson: String = GsonBuilder().create().toJson(it.adInfo)
                    SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).put(SPUtils.Config.APP_SPLASH_IMAGES, adInfoJson)
                    // 缓存图片
                    var screenHeight = Utils.getHeightInPx(BaseApplication.context!!)
                    var screenWidth = Utils.getWidthInPx(BaseApplication.context!!)
                    for (item in it.adInfo) {
                        DownLoadFileUtil.downNetworkImage(item.imgUrl, screenWidth, screenHeight)
                    }
                } catch (e: java.lang.Exception) {
                    // 清除缓存
                    toMainPage()
                    SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).clear()
                }
            } else {
                // 清除缓存
                toMainPage()
                SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).clear()
            }
        }else{
            toMainPage()
            SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).clear()
        }
    }

    /**
     * 处理竖屏视频
     */
    private  val downloadAdFile= DownLoadFile()
    private fun setVideoVData(it: HomeAd) {
        if (it != null) {
            if (!it.adInfo.isNullOrEmpty()) {
                // 更新缓存
                try {
                    if(!it.adInfo[0].imgUrl.isNullOrEmpty()){
                        mBinding.videoViewH.visibility=View.GONE
                        mBinding.rlRoot.visibility=View.GONE
                        mBinding.videoView.visibility=View.VISIBLE
                        mVideoView=mBinding.videoView
                        it.adInfo[0].imgUrl?.let { it1 -> PlayAndSaveVideo(it1,1) }
                    }else{
                        toMainPage()
                    }
                } catch (e: java.lang.Exception) {
                    toMainPage()
                }
            } else {
                toMainPage()
            }
        }else{
            toMainPage()
        }
    }
    /**
     * 处理横屏视频
     */
    private fun setVideoHData(it: HomeAd?) {
        if (it != null) {
            if (!it.adInfo.isNullOrEmpty()) {
                // 更新缓存
                try {
                    if(!it.adInfo[0].imgUrl.isNullOrEmpty()){
                        mBinding.videoViewH.visibility=View.VISIBLE
                        mBinding.rlRoot.visibility=View.VISIBLE
                        mBinding.videoView.visibility=View.GONE
                        mVideoView=mBinding.videoViewH
                        it.adInfo[0].imgUrl?.let { it1 -> PlayAndSaveVideo(it1,2) }
                    }else{
                        toMainPage()
                    }
                } catch (e: java.lang.Exception) {
                    showAdsView(null)
                }
            } else {
                showAdsView(null)
            }
        }else{
            showAdsView(null)
        }
    }

    /**
     * 播放视频和下载时评
     */
    private fun PlayAndSaveVideo(imgUrl: String, type: Int) {
        val videoFileName: String = MD5Util.md5(imgUrl)
        val file = File(DownLoadFile.Video_PATH, videoFileName)
        Log.e("file.exists()-----",file.exists().toString())
        if (file.exists()) {
            val a = SPUtils.getInstance().getBoolean(SPUtils.SpNameConfig.SPLASH_VIDEO_SUCCESS_V) && type == 1;
            val b = SPUtils.getInstance().getBoolean(SPUtils.SpNameConfig.SPLASH_VIDEO_SUCCESS_H) && type == 2;
            if(a || b){
                playAd(file.path)
            }else{
                file.delete();
                playAd(imgUrl)
                downloadAdFile.downloadAdFile(imgUrl, videoFileName,type)
            }
        } else {
            playAd(imgUrl)
            downloadAdFile.downloadAdFile(imgUrl, videoFileName,type)
        }
    }

    /**
     * 播放资源（有链接播放链接，没有播放本地）
     */
    private fun showAdsView(adInfoJson: List<AdInfo>?) {
        if(adInfoJson==null){
            showLocalAdsView()
        }else{
            showNetAdsView(adInfoJson)
        }
    }
    /**
     * 播放在线资源
     */
    private fun showNetAdsView(adInfos: List<AdInfo>) {
        try {
            if (!adInfos.isNullOrEmpty()) {
                setSplashAdsView(adInfos as ArrayList<AdInfo>)
            } else {
                toMainPage()
            }
        } catch (e: Exception) {
            toMainPage()
        }
    }
    /**
     * 播放本地的图片
     */
    private fun showLocalAdsView() {
        var images: String = SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).getString(SPUtils.Config.APP_SPLASH_IMAGES, "")
        if (!images.isNullOrEmpty()) {
            try {
                var adInfos: java.util.ArrayList<AdInfo>? = GsonBuilder().create().fromJson(images, object : TypeToken<ArrayList<AdInfo>>() {}.type)
                if (!adInfos.isNullOrEmpty()) {
                    setSplashAdsView(adInfos)
                } else {
                    postToMainPage()
                }
            } catch (e: Exception) {
                postToMainPage()
            }
        } else {
            postToMainPage()
        }
    }
    /**
     * 广告5s倒计时
     */
    private fun showCutDownTime(size: Int) {
        // 倒计时 1屏广告 5s 跳转  多张广告3s轮播跳转
        var count = if (size == 1) {
            5L
        } else {
            3L
        }
        cutdownDisable = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .take(count)
            .subscribe {
                var curr = count - it
                if (size == 1) {
                    v_skip_splsh?.visibility = View.VISIBLE
                    tv_skip_splash_time.text = "${curr}s"
                    tv_skip_splash_time.visibility = View.VISIBLE
                }
                if (curr == 1L) {
                    toMainPage()
                }
            }

    }


    /**
     * 延迟跳转首页
     */
    private fun postToMainPage() {
        // 延迟3s跳转首页
        Handler().postDelayed({
            toMainPage()
        }, 3000)
    }

    /**
     * 直接跳转首页
     */
    private fun toMainPage() {
        isTimeOut=true
        var intent = Intent(this, MainActivity::class.java)
        try {
            var action = getIntent().data.getQueryParameter("action")
            var inviteCode: String? = getIntent().data.getQueryParameter("code")
            if (!action.isNullOrEmpty()) {
                intent.putExtra("action", action.toInt())
                if (!inviteCode.isNullOrEmpty()) {
                    intent.putExtra("code", inviteCode)
                }
            }

        } catch (e: java.lang.Exception) {

        }
        val bundleExtra = getIntent().getBundleExtra("notifyBundle")
        intent.putExtra("notifyBundle",bundleExtra)
        startActivity(intent)
        finish()
    }

    /**
     * 跳转首页，并传递广告相关数据
     */
    private fun toMainPage(adInfo: AdInfo) {
        var intent = Intent()
        intent.setClass(this@SplashActivity, MainActivity::class.java)
        var bundle = Bundle()
        bundle.putParcelable(MainActivity.ACTION_ADS_OBJ, adInfo)
        bundle.putString(MainActivity.ACTION_TYPE, MainActivity.TYPE_ADS)
        intent.putExtra("bundle", bundle)
        val bundleExtra = getIntent().getBundleExtra("notifyBundle")
        intent.putExtra("notifyBundle",bundleExtra)
        startActivity(intent)
    }

    /**
     * 设置闪屏广告
     */
    private fun setSplashAdsView(adInfos: ArrayList<AdInfo>) {
        mBinding.ivBg.visibility=View.GONE
        mBinding.cbrSplashAds?.setPages(object : CBViewHolderCreator {
            override fun createHolder(itemView: View?): Holder<*> {
                return SplashAdsHolder(itemView, this@SplashActivity)
            }

            override fun getLayoutId(): Int {
                return R.layout.item_splash_ads
            }
        }, adInfos as MutableList<AdInfo>)?.setCanLoop(false)
            ?.setOnItemClickListener { pos ->
                try {
                    if (pos < adInfos.size) {
                        var adInfo = adInfos[pos]
                        if (adInfo != null) {
                            if (!adInfo.jumpUrl.isNullOrEmpty() || (!adInfo.resourceId.isNullOrEmpty()
                                        && !adInfo.resourceType.isNullOrEmpty())
                            ) {
                                toMainPage(adInfo)
                                cutdownDisable?.dispose()
                                finish()
                            }
                        }
                    }
                } catch (e: Exception) {

                }
            }?.setPageIndicatorPadding(0, 0, 0, resources.getDimension(R.dimen.dp_20).toInt())
            ?.setPointViewVisible(adInfos?.size>1)?.setOnPageChangeListener(object : OnPageChangeListener {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                }

                override fun onPageSelected(index: Int) {
                    var size = adInfos.size - 1
                    cutdownDisable?.dispose()
                    if (index == size) {
                        mBinding.cbrSplashAds?.stopTurning()
                        showCutDownTime(adInfos.size)
                    } else {
                        if (! mBinding.cbrSplashAds!!.isTurning) {
                            mBinding.cbrSplashAds?.startTurning(3000)
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

                }

            })
        // 显示广告
        if ( mBinding.cbrSplashAds!!.visibility == View.GONE) {
            mBinding.cbrSplashAds?.visibility = View.VISIBLE
            mBinding.vSkipSplsh?.visibility = View.VISIBLE
            if (adInfos.size > 1) {
                mBinding.cbrSplashAds?.startTurning(3000)
            } else {
                showCutDownTime(adInfos.size)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        permissions = null
        mBinding.cbrSplashAds?.stopTurning()
        cutdownDisable?.dispose()
        cutdownDisable = null
        if(mVideoView!=null && mediaController!=null && isVideo){
            mVideoView?.stopPlayback();
            mVideoView?.setOnCompletionListener(null);
            mVideoView?.setOnPreparedListener(null);
            mediaController?.removeAllViews();
        }
    }

    var stopPosition  :Int=0;
    override fun onPause() {
        super.onPause()
        if(mVideoView!=null&& isVideo ){
            stopPosition  = mVideoView?.currentPosition!!
            mVideoView?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if(mVideoView!=null && isVideo ){
            mVideoView?.seekTo(stopPosition );
            mVideoView?.start();
        }
    }
    /**
     *乌鲁木齐启动页为视频
     */
    @SuppressLint("ClickableViewAccessibility")
    fun playAd(imgUrl: String?) {
        isVideo=true
        mBinding.ivBg.visibility=View.VISIBLE
        mediaController = MediaController(this)
        Log.e("imgUrl-------",imgUrl);
        // 获取raw.mp4的uri地址
        // val uri = "android.resource://" + packageName + "/" + R.raw.splash_ad
        // mBinding.videoView?.setVideoURI(Uri.parse(uri))
        mVideoView?.setVideoPath(imgUrl)
        // 让video和mediaController建立关联
        mediaController?.visibility = View.GONE;
        mVideoView?.setMediaController(mediaController)
        mediaController?.setMediaPlayer(mBinding.videoView)
        // 监听播放完成，
        mVideoView?.setOnCompletionListener {
            toMainPage()
        }
        // 开始播放
        mVideoView?.setOnPreparedListener() {
            isTimeOut=true
            mBinding.vSkipSplsh?.visibility = View.VISIBLE
            mBinding.ivBg.visibility=View.GONE
        }
        mVideoView!!.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            return@OnTouchListener true
        })
        mVideoView?.start()
    }
}
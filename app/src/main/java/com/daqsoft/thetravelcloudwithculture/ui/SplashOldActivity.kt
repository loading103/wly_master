package com.daqsoft.thetravelcloudwithculture.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.android.CommonURL
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.MyVideoView
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.AdInfo
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.ui.viewholder.SplashAdsHolder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.thetravelcloudwithculture.ui
 *@date:2020/4/14 16:49
 *@author: caihj
 *@des:启动页
 **/
class SplashOldActivity : AppCompatActivity() {

    /**
     * rx权限对象
     */
    private var permissions: RxPermissions? = null
    /**
     * 广告控件
     */
    private var mCbrSplashAds: ConvenientBanner<AdInfo>? = null
    /**
     * 倒计时
     */
    private var cutdownDisable: Disposable? = null

    private var mVideoView: MyVideoView? = null

    private  var mediaController : MediaController? = null

    private  var isVideo : Boolean = false
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(CommonURL.APP_AREA == "sc")
            setContentView(R.layout.activity_splash_sc)
        else
            setContentView(R.layout.activity_splash)

        mCbrSplashAds = findViewById(R.id.cbr_splash_ads)
        mVideoView = findViewById(R.id.video_view)
        permissions = RxPermissions(this)

        permissions?.request(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE
        )!!.subscribe {
            if (it) {
                Handler().postDelayed({
                    if( CommonURL.APP_AREA  ==  "sc" || CommonURL.APP_AREA   == "ws")
                        playAd()
                    else
                        showAdsView()

                }, 1000)

            } else {
                ToastUtils.showMessage("非常抱歉，未完整授权，将影响您的体验~")
                postToMainPage()
            }
        }
        initView()
    }

    private fun initView() {
        // 跳过
        v_skip_splsh?.onNoDoubleClick {
            toMainPage()
            cutdownDisable?.dispose()
        }
    }

    private fun showAdsView() {
        var images: String = SPUtils.getInstance(SPUtils.SpNameConfig.SPLASH_ADS).getString(SPUtils.Config.APP_SPLASH_IMAGES, "")
        if (!images.isNullOrEmpty()) {
            try {
                var adInfos: java.util.ArrayList<AdInfo>? = GsonBuilder().create().fromJson(
                    images,
                    object : TypeToken<ArrayList<AdInfo>>() {}.type
                )
                if (!adInfos.isNullOrEmpty()) {
                    setSplashAdsView(adInfos)
                } else if(true){
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
        startActivity(intent)
        finish()
    }

    /**
     * 跳转首页，并传递广告相关数据
     */
    private fun toMainPage(adInfo: AdInfo) {
        var intent = Intent()
        intent.setClass(this@SplashOldActivity, MainActivity::class.java)
        var bundle = Bundle()
        bundle.putParcelable(MainActivity.ACTION_ADS_OBJ, adInfo)
        bundle.putString(MainActivity.ACTION_TYPE, MainActivity.TYPE_ADS)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }


    /**
     * 设置闪屏广告
     * @param  adInfos 广告列表
     */
    private fun setSplashAdsView(adInfos: ArrayList<AdInfo>) {
        mCbrSplashAds?.setPages(object : CBViewHolderCreator {
            override fun createHolder(itemView: View?): Holder<*> {
                return SplashAdsHolder(itemView, this@SplashOldActivity)
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
                        mCbrSplashAds?.stopTurning()
                        showCutDownTime(adInfos.size)
                    } else {
                        if (!mCbrSplashAds!!.isTurning) {
                            mCbrSplashAds?.startTurning(3000)
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

                }

            })
        // 显示广告
        if (mCbrSplashAds!!.visibility == View.GONE) {
            mCbrSplashAds?.visibility = View.VISIBLE
            if (adInfos.size > 1) {
                v_skip_splsh?.visibility = View.VISIBLE
                mCbrSplashAds?.startTurning(3000)
            } else {
                showCutDownTime(adInfos.size)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        permissions = null
        mCbrSplashAds?.stopTurning()
        cutdownDisable?.dispose()
        cutdownDisable = null

        if(mVideoView!=null && mediaController!=null && isVideo){
            mVideoView?.stopPlayback();
            mVideoView?.setOnCompletionListener(null);
            mVideoView?.setOnPreparedListener(null);
            mediaController?.removeAllViews();
            mVideoView = null;
        }
    }


    var stopPosition  :Int=0;
    override fun onPause() {
        super.onPause()
        if(mVideoView!=null&& isVideo){
            stopPosition  = mVideoView?.currentPosition!!
            mVideoView?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if(mVideoView!=null && isVideo){
            mVideoView?.seekTo(stopPosition );
            mVideoView?.start();
        }
    }



    /**
     *乌鲁木齐启动页为视频
     */
    /**
     *乌鲁木齐启动页为视频
     */
    @SuppressLint("ClickableViewAccessibility")
    fun playAd() {
        isVideo=true
        v_skip_splsh?.visibility = View.VISIBLE
        iv_bg.visibility=View.VISIBLE
        video_view.visibility=View.VISIBLE
        mediaController = MediaController(this)
        // 获取raw.mp4的uri地址
        val uri = "android.resource://" + packageName + "/" + R.raw.splash_ad
        mVideoView?.setVideoURI(Uri.parse(uri))
        // 让video和mediaController建立关联
        mediaController?.visibility = View.GONE;        //隐藏进度条
        mVideoView?.setMediaController(mediaController)
        mediaController?.setMediaPlayer(mVideoView)
        // 监听播放完成，
        mVideoView?.setOnCompletionListener { //重新开始播放
            toMainPage()
        }
        mVideoView?.setOnPreparedListener() { //重新开始播放
            iv_bg.visibility=View.GONE
        }
        mVideoView!!.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            return@OnTouchListener true
        })
        mVideoView?.start()
    }

}
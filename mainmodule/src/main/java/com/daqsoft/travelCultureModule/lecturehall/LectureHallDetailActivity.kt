package com.daqsoft.travelCultureModule.lecturehall

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.ViewPagerFragAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityLectureDetailBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.LectureHallDetailBean
import com.daqsoft.provider.businessview.fragment.ProviderAddCommentFragment
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.lecturehall.adapter.LectureHallTabAdapter
import com.daqsoft.travelCultureModule.lecturehall.event.UpdateLectureInfoEvent
import com.daqsoft.travelCultureModule.lecturehall.fragment.LectureChapterFragment
import com.daqsoft.travelCultureModule.lecturehall.fragment.LectureCommentFragment
import com.daqsoft.travelCultureModule.lecturehall.fragment.LectureDetailFragment
import com.daqsoft.travelCultureModule.lecturehall.fragment.LectureRequestFragment
import com.daqsoft.travelCultureModule.lecturehall.model.LectureHallModel
import com.daqsoft.travelCultureModule.lecturehall.viewmodel.LectureHallDetailViewModel
import com.dueeeke.videoplayer.player.VideoView
import com.umeng.socialize.UMShareAPI
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   LectureHallDetailActivity
 * @Author      luoyi
 * @Time        2020/6/15 11:46
 */
@Route(path = MainARouterPath.LECTURE_HALL_DETIAL)
class LectureHallDetailActivity :
    TitleBarActivity<ActivityLectureDetailBinding, LectureHallDetailViewModel>() {

    @JvmField
    @Autowired
    var id: String = "0"

    var lectureTabAdapter: LectureHallTabAdapter? = null

    //    var lectureCommentPop: LecturePosReqPopWindow? = null
    private var addCommentPopFragment: ProviderAddCommentFragment? = null

    /**
     * 课程详情实体
     */
    var lectureDetailBean: LectureHallDetailBean? = null

    /**
     *   每隔 10s
     */
    var cutdownDisable: Disposable? = null

    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null
    var chapterId: String? = null
    var userStudyChapterDuration: Int = 0
    var chapterDuration: Int = 0
    override fun getLayout(): Int {
        return R.layout.activity_lecture_detail
    }

    override fun setTitle(): String {
        return "课程详情"
    }

    override fun injectVm(): Class<LectureHallDetailViewModel> {
        return LectureHallDetailViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        mBinding.rvLectureTabs.layoutManager = GridLayoutManager(this@LectureHallDetailActivity, 4)
        lectureTabAdapter = LectureHallTabAdapter(this@LectureHallDetailActivity)
        lectureTabAdapter?.emptyViewShow = false
        mBinding.rvLectureTabs.adapter = lectureTabAdapter
        lectureTabAdapter?.clear()
        lectureTabAdapter?.add(LectureHallModel.lectureTabs)
        lectureTabAdapter?.onTabClickListener = object : LectureHallTabAdapter.OnTabClickListener {
            override fun onTabClick(position: Int) {
                mBinding.vpScrollLecture.currentItem = position
            }

        }
        var minHeight =
            mBinding.rlvLectureDetail.height - Utils.dip2px(this@LectureHallDetailActivity, 316f)
        var datas: MutableList<Fragment> = mutableListOf()
        datas.add(LectureDetailFragment.newInstance(id))
        datas.add(LectureChapterFragment.newInstance(id))
        datas.add(LectureCommentFragment.newInstance(id, minHeight))
        datas.add(LectureRequestFragment.newInstance(id))
        var vpAdapter = ViewPagerFragAdapter(supportFragmentManager, datas)
        mBinding.vpScrollLecture.adapter = vpAdapter
        mBinding.vpScrollLecture.offscreenPageLimit = 4
        mBinding.vpScrollLecture.currentItem = 0
        initViewEvent()
        initViewModel()
    }

    private fun initViewModel() {
        mModel.lectureCommentLd.observe(this, Observer {
            dissMissLoadingDialog()
//            if (it) {
//                ToastUtils.showMessage("评论已提交，等待管理员审核！")
//                lectureCommentPop?.dismiss()
//                lectureCommentPop = null
//            } else {
//                ToastUtils.showMessage("评论提交失败，请稍后再试！")
//            }
        })
        mModel.collectLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                lectureDetailBean?.collectionStatus = true
                lectureDetailBean?.collections = lectureDetailBean!!.collections + 1
                setCollectStauts()
            } else {
                ToastUtils.showMessage("收藏失败，请稍后再试~")
            }
        })
        mModel.canceCollectLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                lectureDetailBean?.collectionStatus = false
                var collections = lectureDetailBean!!.collections
                if (collections >= 1) {
                    lectureDetailBean!!.collections = collections - 1
                }
                setCollectStauts()
            } else {
                ToastUtils.showMessage("取消收藏失败，请稍后再试~")
            }
        })
    }

    /**
     * 显示弹窗
     */
    private fun showCommentPopup() {
//        commentPopup?.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
        if (addCommentPopFragment == null) {
            addCommentPopFragment =
                ProviderAddCommentFragment.newInstance(id, ResourceType.CONTENT_TYPE_COURSE)
        }
        if (!addCommentPopFragment!!.isAdded) {
            addCommentPopFragment?.show(supportFragmentManager, "course_add_comment")
        }
    }

    private fun initViewEvent() {
        mBinding.vpScrollLecture.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                lectureTabAdapter?.selectPos = position
                lectureTabAdapter?.notifyDataSetChanged()
                if (position == 3) {
                    mBinding.tvLectureToRequestion.visibility = View.VISIBLE
                } else {
                    mBinding.tvLectureToRequestion.visibility = View.GONE
                }
            }
        })
        mBinding.tvLectureToRequestion.onNoDoubleClick {
            if(AppUtils.isLogin()){
                if (!id.isNullOrEmpty())
                    MainARouterPath.toLectureHallReq(id)
            }else{
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            }
        }
        mBinding.tvToComment.onNoDoubleClick {
            if(AppUtils.isLogin()){
                showCommentPopup()
            }else{
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            }
        }
        mBinding.vBottomShare?.onNoDoubleClick {

        }
        mBinding.vBottomCollect?.onNoDoubleClick {
            if (lectureDetailBean != null) {
                if (AppUtils.isLogin()) {
                    showLoadingDialog()
                    if (lectureDetailBean!!.collectionStatus) {
                        // 取消收藏
                        mModel.canceCollect(id)
                    } else {
                        // 收藏
                        mModel.collect(id)
                    }
                } else {
                    ToastUtils.showUnLoginMsg()
                    ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }
        }
    }

    override fun initData() {

    }

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                lectureDetailBean?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@LectureHallDetailActivity)
                    }
                    sharePopWindow?.setShareContent(
                        it.name, if (it.content.isNullOrEmpty()) {
                            "点击查看详情"
                        } else {
                            it.content
                        }, it.image.getRealImages(),
                        ShareModel.getCourseDescUrl(id)
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }
                }
            }

        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateVideoInfo(event: UpdateLectureInfoEvent) {
        event?.let {
            when (it.type) {
                0 -> {
                    chapterId = it.chapterId
                    userStudyChapterDuration = it.userStudyDuration
                    chapterDuration = it.duration
                    setVideoPlayer(it, it.isAutoPlay)
                }
                1 -> {
                    lectureDetailBean = it.bean
                    setCollectStauts()
                }
            }

        }
    }

    private fun setCollectStauts() {
        lectureDetailBean?.let {
            // 收藏数目
            if (it.collections > 0) {
                mBinding.tvLectureCollect.text = "" + it.collections
            } else {
                mBinding.tvLectureCollect.text = "收藏"
            }
            // 收藏状态
            if (it.collectionStatus) {
                mBinding.imgLectureCollect.setImageResource(R.mipmap.bottom_icon_collect_selected)
            } else {
                mBinding.imgLectureCollect.setImageResource(R.mipmap.bottom_icon_collect_normal)
            }
        }
    }


    fun postStudyRecoder() {
        cutdownDisable = null
        cutdownDisable = Observable.interval(8, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
//            .takeWhile { isPageVisible }
            .subscribe {
                uploadPlayerStatus(false)
            }

    }

    private fun uploadPlayerStatus(comple: Boolean) {
        var duration = mBinding?.lectureVideoPlayer?.duration
        if (comple && chapterDuration > 0) {
            duration = (chapterDuration * 1000L).toInt()
        }
        if (duration != null && !chapterId.isNullOrEmpty()) {
            if (duration > userStudyChapterDuration) {
                mModel?.postStudyRecorder(chapterId!!, duration / 1000)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
        cutdownDisable?.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    override fun onDestroy() {
        super.onDestroy()
        JCVideoPlayer.releaseAllVideos()
        try {
            mBinding.lectureVideoPlayer.release()
            cutdownDisable?.dispose()
            sharePopWindow = null
            UMShareAPI.get(this).release()
            addCommentPopFragment = null
        } catch (e: Exception) {
        }

        EventBus.getDefault().unregister(this)
    }

    private fun setVideoPlayer(it: UpdateLectureInfoEvent, isAutoPlay: Boolean) {
        try {
            JCVideoPlayer.releaseAllVideos()
        } catch (e: Exception) {

        }
        var mContext = this@LectureHallDetailActivity
        var coverUrl =
            StringUtil.getVideoCoverUrl(StringUtil.enCodeVideoUrl(it.videoUrl!!))
        mBinding?.lectureVideoPlayer?.setUp(it.videoUrl!!, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "")
        mBinding?.lectureVideoPlayer?.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
        mBinding?.lectureVideoPlayer?.thumbImageView?.let {
            Glide.with(mContext!!).load(coverUrl).into(mBinding?.lectureVideoPlayer?.thumbImageView)
        }
        mBinding?.lectureVideoPlayer?.setOnJcVideoPlayerListener(object : JCVideoPlayer.OnJcVideoPlayerListener {
            override fun onComplete() {
                cutdownDisable?.dispose()
                uploadPlayerStatus(true)
            }

            override fun onPause() {
                cutdownDisable?.dispose()
            }

            override fun onError() {

            }

            override fun onStartPlayer() {
                postStudyRecoder()
            }

            override fun onStop() {
            }

        })
        if (isAutoPlay) {
            try {
                mBinding?.lectureVideoPlayer?.startPlayLogic()
            } catch (e: Exception) {

            }
        }
    }
}
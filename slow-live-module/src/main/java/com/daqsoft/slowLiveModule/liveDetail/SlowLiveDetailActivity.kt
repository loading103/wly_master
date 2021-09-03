package com.daqsoft.slowLiveModule.liveDetail

import android.graphics.drawable.AnimationDrawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseActivity
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.businessview.adapter.ProviderCommentAdapter
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.slowLiveModule.bean.VideoZbImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.slowLiveModule.R
import com.daqsoft.slowLiveModule.bean.LiveDetailBean
import com.daqsoft.slowLiveModule.bean.VideoTxZbImageHolder
import com.daqsoft.slowLiveModule.bean.getpositionImages
import com.daqsoft.slowLiveModule.databinding.SlowLiveAtyLiveDetailBinding
import com.daqsoft.slowLiveModule.databinding.SlowLiveItemAroundBinding
import com.daqsoft.slowLiveModule.databinding.SlowLiveItemRvTagBinding
import com.daqsoft.slowLiveModule.rv.dsl.linear
import com.daqsoft.slowLiveModule.video.VideoActivity
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerDqUI
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast


/**
 * @des     SlowLiveDetailActivity
 * @class   SlowLiveDetailActivity
 * @author  Wongxd
 * @date    2020-4-16  20:19
 *
 */
@Route(path = ARouterPath.SlowLiveModule.SLOW_LIVE_DETAIL_ACTIVITY)
internal class SlowLiveDetailActivity : TitleBarActivity<SlowLiveAtyLiveDetailBinding, LiveDetailViewModel>() {


    @JvmField
    @Autowired
    var scenicSpotsId: Int = 0


    @JvmField
    @Autowired
    var scenicSpotsName: String = ""
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    private var mDetailBean: LiveDetailBean? = null

    override fun getLayout(): Int = R.layout.slow_live_aty_live_detail

    override fun injectVm(): Class<LiveDetailViewModel> = LiveDetailViewModel::class.java

    override fun setTitle(): String = getString(R.string.slow_live_live_detail)


    override fun initData() {

    }
    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            mDetailBean?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@SlowLiveDetailActivity)
                }
                var content= Constant.SHARE_DEC
                if(!TextUtils.isEmpty(it.summary)){
                    content=it.summary
                }
                sharePopWindow?.setShareContent(
                    it.scenicSpotsName, content, it.images.getRealImages(),
                    ShareModel.getSlowDesc(scenicSpotsId.toString())
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }
    override fun onStop() {
        super.onStop()
        try {
            imageHolder?.release()
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
            imageHolder?.release()
        } catch (e: Exception) {
        }
    }


    override fun initView() {
        EventBus.getDefault().register(this)
        initViewModel()
        mBinding.srl.setOnRefreshListener {
            mModel.getLiveDetail(scenicSpotsId)
        }
        mBinding.rvComment.apply {
            layoutManager = LinearLayoutManager(this@SlowLiveDetailActivity)
            adapter = commentAdapter
        }
        mModel.getLiveDetail(scenicSpotsId)

    }

    private  var imageHolder : VideoTxZbImageHolder?=null
    private fun initViewModel() {
        mModel.liveDetail.observe(this, Observer {
            mBinding.srl.finishRefresh()
            mDetailBean = it
            if(it==null || it.liveList.isEmpty()){
                mBinding.cbrFoodDetail.visibility=View.GONE
                return@Observer
            }
            if(it.liveList.size>1){
                mBinding.txtFoodDetailImages.visibility=View.VISIBLE
            }else{
                mBinding.txtFoodDetailImages.visibility=View.GONE
            }
            if (!it.liveList[0].liveUrl.isNullOrEmpty()) {

                if(it.liveList[0].getType()=="1"){
                    mBinding.ivLive.visibility=View.VISIBLE
                    mBinding.tvLive.visibility=View.VISIBLE
                    mBinding.tvLive.text="Live"
                }else  if(it.liveList[0].getType()=="2"){
                    mBinding.ivLive.visibility=View.GONE
                    mBinding.tvLive.visibility=View.VISIBLE
                    mBinding.tvLive.text="直播回放"
                }else{
                    mBinding.ivLive.visibility=View.GONE
                    mBinding.tvLive.visibility=View.GONE
                }

                var dataVideoImages: MutableList<VideoImageBean> = mutableListOf()

                if(it.liveList.isNotEmpty()){
                    for (i in   it.liveList.indices) {
                        dataVideoImages.add(VideoImageBean().apply {
                            type = 1
                            if( it.liveList[i].getType()=="1"){
                                videoUrl = it.liveList[i].liveUrl
                                imageUrl=it.images.getpositionImages(i)
                            }else{
                                videoUrl = it.liveList[i].replayUrl
                                imageUrl=it.liveList[i].replayCover
                            }
                            videoType=it.liveList[i].getType()
                        })
                    }
                    mBinding.txtFoodDetailImages.text = "1/${dataVideoImages?.size}"
                    mBinding.cbrFoodDetail.setPages(object : CBViewHolderCreator {
                        override fun createHolder(itemView: View?): Holder<*> {
                            imageHolder=
                                VideoTxZbImageHolder(itemView!!, this@SlowLiveDetailActivity)
                            return imageHolder as VideoTxZbImageHolder
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.layout_videos_tx
                        }
                    }, dataVideoImages).setCanLoop(dataVideoImages.size > 1).startTurning(3000).setPointViewVisible(false).setOnItemClickListener {
                        when (dataVideoImages[it].type) {
                            0 -> {
                            }
                            1 -> {
                            }
                            2 -> {

                            }
                        }
                    }
                    mBinding.cbrFoodDetail.onPageChangeListener = object : OnPageChangeListener {
                        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        }

                        override fun onPageSelected(index: Int) {
                            var pos = index
                            mBinding.txtFoodDetailImages.text = "${pos + 1}/${dataVideoImages?.size}"
                        }

                        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                        }

                    }

                }
            }



            setThumbUi(it.likeStatus != 0)
            setCollectUi(it.collectionStatus != 0)

            mBinding.tvCollect.onNoDoubleClick {
                if (it.collectionStatus != 0) {
                    mModel.collectionCancel(it.scenicId.toString())
                } else {
                    mModel.collection(it.scenicId.toString())
                }
            }

            mBinding.tvLike.onNoDoubleClick {
                if (it.likeStatus != 0) {
                    mModel.thumbCancel(it.scenicId.toString())
                } else {
                    mModel.thumbUp(it.scenicId.toString())
                }
            }

            mBinding.tvComment.onNoDoubleClick {
                mDetailBean?.let {
                    ARouter.getInstance().build(ARouterPath.Provider.PROVIDER_POST_COMMENT)
                        .withString("id", it.scenicId.toString() ?: "")
                        .withString("type", ResourceType.CONTENT_TYPE_SCENERY)
                        .withString("contentTitle", it.scenicName)
                        .navigation()
                }

            }

            mBinding.tvShare.onNoDoubleClick {
                toast(getString(R.string.slow_live_function_is_not_ready))
            }


            mBinding.detail = it

            if (!it.summary?.isNullOrEmpty()) {
                mBinding.tvSummary.apply {
                    maxLines = 5
                    setOriginalText(it.summary)
                }
            }


            mBinding.ivDetail.onNoDoubleClick {
                if (!it.liveUrl.isNullOrEmpty())
                    VideoActivity.playUrl(this, it.liveUrl, it.getImagess())
            }

            mBinding.ivLive.apply {
                val drawable = background as AnimationDrawable
                if (!drawable.isRunning) {
                    drawable.start()
                }
            }

            mBinding.rvTag.let { rv ->


                val tags = mutableListOf<String>()


                if (!it.scenicTheme.isNullOrEmpty()) {
                    val split = it.scenicTheme.split(",")
                    split.forEach {its->
                        tags.add(its)
                    }
                }
                if (!it.scenicLevel.isNullOrEmpty()) {
                    tags.add(it.scenicLevel)
                }
                rv.linear {
                    orientation = LinearLayoutManager.HORIZONTAL

                    tags.forEach { tag ->

                        itemDsl {
                            xml(R.layout.slow_live_item_rv_tag)
                            render {
                                val binding = DataBindingUtil.bind<SlowLiveItemRvTagBinding>(it)
                                binding?.tag = tag
                            }
                        }
                    }

                }
            }


            mBinding.tvScenicIntroduce.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withString("id", it.scenicId.toString())
                    .navigation()
            }

        })


        mModel.liveAroundList.observe(this, Observer { list ->

            mBinding.llAround.visibility = if (list.isNullOrEmpty()) View.GONE else View.VISIBLE
            mBinding.rvAround.visibility = if (list.isNullOrEmpty()) View.GONE else View.VISIBLE

            mBinding.rvAround.let { rv ->

                rv.linear {
                    orientation = LinearLayoutManager.HORIZONTAL

                    list.forEach { item ->

                        itemDsl {
                            xml(R.layout.slow_live_item_around)
                            render {
                                val binding = DataBindingUtil.bind<SlowLiveItemAroundBinding>(it)
                                binding?.item = item

                                it.onNoDoubleClick {
                                    ARouter.getInstance()
                                        .build(ARouterPath.SlowLiveModule.SLOW_LIVE_DETAIL_ACTIVITY)
                                        .withInt("scenicSpotsId", item.scenicSpotsId)
                                        .withString("scenicSpotsName", item.scenicSpotsName)
                                        .navigation()
                                }
                            }
                        }

                    }

                }

            }

        })


        // 点赞
        mModel.thumbLiveData.observe(this, Observer {
            setThumbUi(it)
            mDetailBean?.let { detail ->
                val tempLikeNum: Int = detail.likeNum
                var result = tempLikeNum
                if (it) {
                    // 点赞成功
                    result = tempLikeNum + 1
                } else {
                    // 取消点赞
                    if (tempLikeNum > 0) {
                        result = tempLikeNum - 1
                    }
                }
                detail.likeStatus = if (it) 1 else 0
                detail.likeNum = result

                mBinding.tvLike.text = result.toString()
                mBinding.tvLikeNum.text = result.toString()
            }

        })

        // 收藏
        mModel.collectLiveData.observe(this, Observer {
            setCollectUi(it)
            mDetailBean?.let { detail ->

                val tempCollectionNum: Int = detail.collectionNum
                var result = tempCollectionNum
                if (it) {
                    // 点赞成功
                    result = tempCollectionNum + 1
                } else {
                    // 取消点赞
                    if (tempCollectionNum > 0) {
                        result = tempCollectionNum - 1
                    }
                }

                detail.collectionStatus = if (it) 1 else 0
                detail.likeNum = result

                mBinding.tvCollect.text = result.toString()

            }
        })

        mModel.commentList.observe(this, Observer {
            mBinding.llComment.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
            mBinding.rvComment.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE

            commentAdapter.clearNotify()
            commentAdapter.add(it)
            commentAdapter.notifyDataSetChanged()
            renderCommentNum(
                it.size,
                scenicSpotsId.toString(),
                ResourceType.CONTENT_TYPE_SCENERY,
                scenicSpotsName
            )
        })


    }

    /**
     * 点赞
     */
    private fun setThumbUi(it: Boolean) {
        val drawable = if (it) {
            // 点赞成功
            resources.getDrawable(R.drawable.slow_live_bottom_icon_like_selected)

        } else {
            // 取消点赞
            resources.getDrawable(R.drawable.slow_live_bottom_icon_like_normal)
        }

        mBinding.tvLike.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
    }

    /**
     * 收藏
     */
    private fun setCollectUi(it: Boolean) {
        val drawable = if (it) {
            // 收藏成功
            resources.getDrawable(R.drawable.slow_live_bottom_icon_collect_selected)

        } else {
            // 收藏点赞
            resources.getDrawable(R.drawable.slow_live_bottom_icon_collect_normal)
        }

        mBinding.tvCollect.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
    }


    private val commentAdapter by lazy { ProviderCommentAdapter(this,false) }

    /**
     * 渲染 评论
     */
    private fun renderCommentNum(
        num: Int,
        resourceId: String,
        type: String,
        contentTitle: String = ""
    ) {
        mBinding.tvCommentNum.visibility = View.GONE
        if (num >= 2) {
            mBinding.tvCommentNum.visibility = View.VISIBLE
            mBinding.tvCommentNum.text =
                "查看更多"
            mBinding.tvCommentNum.onNoDoubleClick {
                mDetailBean?.let {
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.PROVIDER_COMMENT_LS)
                        .withString("id", it.scenicId.toString() ?: "")
                        .withString("type", ResourceType.CONTENT_TYPE_SCENERY)
                        .withString("contentTitle", it.scenicName)
                        .navigation()
                }

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAudioPlayer(event: UpdateAudioStateEvent) {
        // 1 准备播放 2 播放 3暂停/完成
        try {
            when (event.type) {
                1 -> {
                    mBinding?.cbrFoodDetail.stopTurning()
                }
                2 -> {
                    mBinding?.cbrFoodDetail.stopTurning()
                }
                3 -> {
                    mBinding?.cbrFoodDetail.startTurning(3000)
                }
            }
        } catch (e: java.lang.Exception) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel. getCommentList(mDetailBean?.scenicId.toString(), ResourceType.CONTENT_TYPE_SCENERY)
    }
}
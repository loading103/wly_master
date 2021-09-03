package com.dqsoft.votemodule.activity

import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.vote.VoteConstant
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.BaseImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.adapter.GridVoteLsAdapter
import com.dqsoft.votemodule.adapter.VoteChildTypeAdapter
import com.dqsoft.votemodule.adapter.VoteRankLsAdapter
import com.dqsoft.votemodule.adapter.VoteTypeAdapter
import com.dqsoft.votemodule.databinding.ActivityVoteDetailBinding
import com.dqsoft.votemodule.event.UpdateVoteStatusEvent
import com.dqsoft.votemodule.view.PopVoteRuleWindow
import com.dqsoft.votemodule.vm.VoteDetailViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.append
import java.util.concurrent.TimeUnit

/**
 * @Description 投票详情
 * @ClassName   VoteDetailActivity
 * @Author      luoyi
 * @Time        2020/11/9 9:50
 */
@Route(path = ARouterPath.VoteModule.VOTE_DETAIL)
class VoteDetailActivity : TitleBarActivity<ActivityVoteDetailBinding, VoteDetailViewModel>() {

    @Autowired
    @JvmField
    var voteId: String = ""

    var voteDetail: VoteDetailBean? = null

    // 0 默认或者人气 1 排行榜
    var SHOW_MODE: Int = 0

    var orderMode: String = "0"

    private var popVoteRuleWindow: PopVoteRuleWindow? = null

    private var isCutDownTime: Boolean = false

    /**
     * 投票时间倒计时
     */
    private var cutdownDisable: Disposable? = null
    /**
     * 上传时间倒计时
     */
    private var cutdownDisable1: Disposable? = null
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null


    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            voteDetail?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this)
                }
                // 设置分享数据
                var content="你的好友邀请你参加【${ it.title}】，快来看看吧！"
                sharePopWindow?.setShareContent(
                    it.title, content, it.mainImages.getRealImages(),
                    ShareModel.getVoteDesc(voteId)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }
    private val voteTypeAdapter: VoteTypeAdapter by lazy {
        VoteTypeAdapter(this@VoteDetailActivity)
            .apply {
                emptyViewShow = false
                onItemClickListener = object : VoteTypeAdapter.OnItemClickListener {
                    override fun onItemClick(pos: Int, item: VoteTypeBean) {
                        if (!item.child.isNullOrEmpty()) {
                            mBinding.rvVoteChildTypes.visibility = View.VISIBLE
                            voteChildTypeAdapter.clear()
                            voteChildTypeAdapter.selectPos = 0
                            voteChildTypeAdapter.getData().add(VoteSubTypeBean().apply {
                                name = "全部"
                                id = -1
                            })
                            voteChildTypeAdapter.add(item.child!!)
                        } else {
                            mBinding.rvVoteChildTypes.visibility = View.GONE
                        }
                        if (pos == 0) {
                            mModel.type = ""
                        } else {
                            mModel.type = item?.id.toString()
                        }
                        mModel.currPage = 1
                        mModel.typeChild = ""
                        mModel.getVoteWorkList(voteId ?: "", orderMode)
                    }

                }
            }
    }
    private val voteChildTypeAdapter: VoteChildTypeAdapter by lazy {
        VoteChildTypeAdapter(this@VoteDetailActivity).apply {
            onItemClickListener = object : VoteChildTypeAdapter.OnItemClickListener {
                override fun onItemClick(pos: Int, typeId: String) {
                    mModel.currPage = 1
                    if (pos == 0) {
                        mModel.typeChild = ""
                    } else {
                        mModel.typeChild = typeId
                    }
                    mModel.getVoteWorkList(voteId ?: "", orderMode)
                }

            }
        }
    }

    private val defaultWorkAdapter: GridVoteLsAdapter by lazy {
        GridVoteLsAdapter().apply {
            emptyViewShow = false
            setItemFooterTypeIsShow(false)
            setOnLoadMoreListener {
                mModel.currPage = mModel.currPage + 1
                mModel.getVoteWorkList(voteId ?: "", orderMode)
            }
            onVotoLsItemClickListener = object : GridVoteLsAdapter.OnVoteLsItemClickListener {
                override fun onVoteItem(position: Int, item: VoteWorkBean) {
                    mModel.voteWork(item.id.toString(), position)
                }

            }
        }
    }

    private val rankWorkAdapter: VoteRankLsAdapter by lazy {
        VoteRankLsAdapter().apply {
            emptyViewShow = false
            setItemFooterTypeIsShow(false)
            setOnLoadMoreListener {
                mModel.currPage = mModel.currPage + 1
                mModel.getVoteWorkList(voteId ?: "", orderMode)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_vote_detail
    }

    override fun setTitle(): String {
        return resources.getString(R.string.vote_detail)
    }

    override fun injectVm(): Class<VoteDetailViewModel> {
        return VoteDetailViewModel::class.java
    }

    override fun initView() {
        mBinding.vVoteRules.onNoDoubleClick {
            showPopVoteRuleWindow()
        }
        mBinding.rvVoteSubTypes.run {
            adapter = voteTypeAdapter
            layoutManager = LinearLayoutManager(this@VoteDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        }
        mBinding.rvVoteChildTypes.run {
            adapter = voteChildTypeAdapter
            layoutManager = LinearLayoutManager(this@VoteDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        }
        mBinding.rvVoteContents.run {
            adapter = defaultWorkAdapter
            layoutManager = GridLayoutManager(this@VoteDetailActivity, 2, GridLayoutManager.VERTICAL, false)
        }
        mBinding.rvVoteRanks.run {
            adapter = rankWorkAdapter
            layoutManager = LinearLayoutManager(this@VoteDetailActivity, LinearLayoutManager.VERTICAL, false)
        }
        initViewModel()
        mBinding.vVoteDetailSearch.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_SEARCH)
                .withString("voteId", voteId)
                .navigation()
        }
        mBinding.tvVoteBottomTitle.onNoDoubleClick {
            if (voteDetail != null) {
                if (voteDetail!!.userJoinCount > 0) {
                    // 我的参与
                    ARouter.getInstance().build(ARouterPath.VoteModule.MINE_VOTE_WORK_LIST)
                        .withString("voteId", voteId)
                        .navigation()
                } else {
                    if (voteDetail!!.voteStatus != VoteConstant.STATUS.END) {
                        // 我要参与
                        if (!AppUtils.isLogin()) {
                            ToastUtils.showUnLoginMsg()
                            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                            return@onNoDoubleClick
                        } else {
                            ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_INPART)
                                .withString("voteId", voteId)
                                .withInt("mode", 1)
                                .navigation()
                        }
                    }
                }
            }
        }

        mBinding.vVoteTabDefault.onNoDoubleClick {
            initTabSeting()
            mBinding.vVoteSlideDefault.visibility = View.VISIBLE
            mBinding.tvVoteTabDefault.setTextColor(resources.getColor(R.color.color_333))
            mBinding.tvVoteTabDefault.textSize = 16f
            mBinding.tvVoteTabDefault.paint.isFakeBoldText = true
            SHOW_MODE = 0
            mBinding.rvVoteRanks.visibility = View.GONE
            mBinding.rvVoteContents.visibility = View.VISIBLE
            mBinding.rvVoteSubTypes.visibility = View.VISIBLE
            mBinding.rvVoteChildTypes.visibility = View.GONE
            mBinding.vVoteEmpty.visibility = View.GONE
            mBinding.tvVoteNoMore.visibility = View.GONE
            voteTypeAdapter.selectPos = 0
            voteTypeAdapter.notifyDataSetChanged()
            orderMode = getOrderModel(1)
            mModel.getVoteWorkList(voteId ?: "", orderMode)
        }
        mBinding.vVoteTabPopularty.onNoDoubleClick {
            initTabSeting()
            mBinding.vVoteSlidePopularity.visibility = View.VISIBLE
            mBinding.tvVoteTabPopularity.setTextColor(resources.getColor(R.color.color_333))
            mBinding.tvVoteTabPopularity.textSize = 16f
            mBinding.tvVoteTabPopularity.paint.isFakeBoldText = true
            SHOW_MODE = 0
            mModel.currPage = 1
            mBinding.rvVoteRanks.visibility = View.GONE
            mBinding.rvVoteContents.visibility = View.VISIBLE
            mBinding.rvVoteSubTypes.visibility = View.VISIBLE
            mBinding.vVoteEmpty.visibility = View.GONE
            mBinding.tvVoteNoMore.visibility = View.GONE
            mBinding.rvVoteChildTypes.visibility = View.GONE
            voteTypeAdapter.selectPos = 0
            voteTypeAdapter.notifyDataSetChanged()
            orderMode = getOrderModel(2)
            mModel.getVoteWorkList(voteId ?: "", orderMode)
        }
        mBinding.vVoteTabRankList.onNoDoubleClick {
            initTabSeting()
            mBinding.vVoteSlideRankList.visibility = View.VISIBLE
            mBinding.tvVoteTabRankList.setTextColor(resources.getColor(R.color.color_333))
            mBinding.tvVoteTabRankList.textSize = 16f
            mBinding.tvVoteTabRankList.paint.isFakeBoldText = true
            SHOW_MODE = 1
            mBinding.rvVoteRanks.visibility = View.VISIBLE
            mBinding.rvVoteContents.visibility = View.GONE
            mBinding.rvVoteSubTypes.visibility = View.GONE
            mBinding.rvVoteChildTypes.visibility = View.GONE
            mModel.currPage = 1
            orderMode = "2"
            mModel.getVoteWorkList(voteId ?: "", orderMode)
        }
        mBinding.srlMineWorks.setOnRefreshListener {
            orderMode = "2"
            mModel.getVoteWorkList(voteId ?: "", orderMode)
        }
    }

    private fun getOrderModel(index: Int): String {
        if (voteDetail?.voteType == null) {
            return "0"
        }
        when (index) {
            1 -> {
                return if (voteDetail!!.voteType == VoteConstant.TYPE.MANAGE) {
                    "0"
                } else {
                    "1"
                }
            }
            2 -> {
                return if (voteDetail!!.voteType == VoteConstant.TYPE.MANAGE) {
                    "1"
                } else {
                    "3"
                }
            }
        }
        return "0"
    }

    private fun initTabSeting() {
        mBinding.vVoteSlideDefault.visibility = View.GONE
        mBinding.vVoteSlidePopularity.visibility = View.GONE
        mBinding.vVoteSlideRankList.visibility = View.GONE
        mBinding.tvVoteTabDefault.setTextColor(resources.getColor(R.color.color_666))
        mBinding.tvVoteTabDefault.textSize = 14f
        mBinding.tvVoteTabDefault.paint.isFakeBoldText = false
        mBinding.tvVoteTabPopularity.setTextColor(resources.getColor(R.color.color_666))
        mBinding.tvVoteTabPopularity.textSize = 14f
        mBinding.tvVoteTabPopularity.paint.isFakeBoldText = false
        mBinding.tvVoteTabRankList.setTextColor(resources.getColor(R.color.color_666))
        mBinding.tvVoteTabRankList.textSize = 14f
        mBinding.tvVoteTabRankList.paint.isFakeBoldText = false
    }

    private fun initViewModel() {
        mModel.voteDetailLd.observe(this, Observer {
            if (it != null) {
                // 时间
                voteDetail = it
                mBinding.data = it

                //预告时候展示网页
                if( !it.voteRule.isNullOrBlank() && it.voteStatus==0){
                    mBinding.tvContent.settings.defaultTextEncodingName = "utf-8"
                    mBinding.tvContent.settings.javaScriptEnabled = true
                    mBinding.tvContent.isScrollContainer = false
                    mBinding.tvContent.isVerticalScrollBarEnabled = false
                    mBinding.tvContent.isHorizontalScrollBarEnabled = false
                    mBinding.tvContent.loadDataWithBaseURL(null, StringUtil.getHtml(it?.voteRule!!), "text/html", "utf-8", null)
                }
                initVoteTypeSeting(it.voteType)
                initVoteCover(it.mainImages)
                initVoteCoverStatus(it!!)
                setBottomBtn(it)

            }
        })
        mModel.voteTypeLd.observe(this, Observer {
            voteTypeAdapter.clear()
            if (!it.isNullOrEmpty()) {
                it.add(0, VoteTypeBean().apply {
                    id = -1
                    name = "全部"
                })
                voteTypeAdapter.add(it)
                mBinding.rvVoteSubTypes.visibility = View.VISIBLE
            } else {
                mBinding.rvVoteSubTypes.visibility = View.GONE
            }
        })
        mModel.voteWorksLd.observe(this, Observer {
            when (SHOW_MODE) {
                0 -> {
                    // 默认&人气
                    PageDealUtils().apply {
                        onPageListener = object : PageDealUtils.OnPageListener {
                            override fun onEmpty() {
                                mBinding.vVoteEmpty.visibility = View.VISIBLE
                                mBinding.rvVoteContents.visibility = View.GONE
                            }

                            override fun onNoMoreData() {
                                mBinding.tvVoteNoMore.visibility = View.VISIBLE
                            }

                        }
                    }.pageDeal(mModel.currPage, it, defaultWorkAdapter)
                    if (it != null && !it.datas.isNullOrEmpty()) {
                        defaultWorkAdapter.add(it.datas!!)
                    }
                }
                1 -> {
                    // 排行榜
                    PageDealUtils().apply {
                        onPageListener = object : PageDealUtils.OnPageListener {
                            override fun onEmpty() {
                                mBinding.vVoteEmpty.visibility = View.VISIBLE
                                mBinding.rvVoteRanks.visibility = View.GONE
                            }

                            override fun onNoMoreData() {
                                mBinding.tvVoteNoMore.visibility = View.VISIBLE
                            }
                        }

                    }.pageDeal(mModel.currPage, it, rankWorkAdapter)
                    if (it != null && !it.datas.isNullOrEmpty()) {
                        rankWorkAdapter.add(it.datas!!)
                    }
                }
            }
            mBinding.srlMineWorks.finishRefresh()
        })
        mModel.voteWorkLd.observe(this, Observer {
            if (it != null) {
                mModel.currPage = 1
                mModel.getVoteWorkList(voteId ?: "", orderMode)
                if (!it.continueFlag) {
                    if (it.voteLimitStatus == 2) {
                        ToastUtils.showMessage("您今天的投票次数已用完")
                    }
                    if (it.voteLimitStatus == 3) {
                        ToastUtils.showMessage("该作品的投票次数已用完")
                    } else {
                        ToastUtils.showMessage("您的投票次数已用完")
                    }
                } else {
                    if (it.voteLimitStatus == 0 || it.surplusCount == -1) {
                        ToastUtils.showMessage("投票成功")
                    } else {
                        ToastUtils.showMessage("投票成功，还可投${it.surplusCount}次")
                    }
                }
            }
        })
        mModel.cutDownTimeLd.observe(this, Observer {
            if (it != null) {
                initVoteCoverStatus(it!!)
                setBottomBtn(it)
            }
        })
    }

    private fun initVoteTypeSeting(voteType: String?) {
        voteType?.let {
            if (voteType == VoteConstant.TYPE.MANAGE) {
                // 平台发布
//                mBinding.vBottomStatus.visibility = View.GONE
                mModel.currPage = 1
                mModel.getVoteWorkList(voteId ?: "")
                //
            } else {
                // 用户发布
//                mBinding.vBottomStatus.visibility = View.VISIBLE
                mBinding.tvVoteTabDefault.text = "人气"
                mBinding.tvVoteTabPopularity.text = "最新"
                mModel.currPage = 1
                orderMode = "1"
                mModel.getVoteWorkList(voteId ?: "", "1")
            }
        }
    }


    private fun setBottomBtn(data: VoteDetailBean) {
        voteDetail = data
        if (data.userJoinCount > 0) {
            mBinding.bottomTitle = getString(R.string.vote_bottom_mine_inpart)
            if( data?.uploadStatus=="1") {
                mBinding.vBottomStatus.visibility = View.VISIBLE
            }else{
                mBinding.vBottomStatus.visibility = View.GONE
            }
            mBinding.tvVoteBottomTitle.setBackgroundColor(ContextCompat.getColor(this@VoteDetailActivity, R.color.app_main_color))
            mBinding.tvVoteBottomTitle.setTextColor(ContextCompat.getColor(this@VoteDetailActivity, R.color.white))
        } else {
            if (data.voteStatus != VoteConstant.STATUS.END) {
                mBinding.bottomTitle = getString(R.string.vote_bottom_inpart)
                mBinding.tvVoteBottomTitle.setBackgroundColor(ContextCompat.getColor(this@VoteDetailActivity, R.color.app_main_color))
                mBinding.tvVoteBottomTitle.setTextColor(ContextCompat.getColor(this@VoteDetailActivity, R.color.white))
            } else {
                mBinding.bottomTitle = getString(R.string.vote_bottom_mine_end)
                mBinding.tvVoteBottomTitle.setBackgroundColor(ContextCompat.getColor(this@VoteDetailActivity, R.color.white))
                mBinding.tvVoteBottomTitle.setTextColor(ContextCompat.getColor(this@VoteDetailActivity, R.color.color_666))
            }
        }
    }

    private fun initVoteCoverStatus(data: VoteDetailBean) {
        when (data.voteStatus) {
            VoteConstant.STATUS.END -> {
                mBinding.llNoStart.visibility = View.GONE
                mBinding.llStarted.visibility=  View.VISIBLE
                if (data.voteType!= VoteConstant.TYPE.MANAGE) {
                    if (data.uploadStatus == "2" || data.voteStatus == 2) {
                        mBinding.tvVoteStatus.run {
                            text = resources.getString(R.string.vote_end)
                            background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_yugao)
                        }
                        mBinding.tvDownTime1.text = setDownTimeTip(0, SpannableStringBuilder(""), 0, 0, 0, true)
                        mBinding.tvDownTime2.text = setDownTimeTip(0, SpannableStringBuilder(""), 0, 0, 0, true)
                        mBinding.vBottomStatus.visibility = View.GONE
                        cutdownDisable?.dispose()
                        cutdownDisable1?.dispose()
                        return
                    }
                }else{
                    // 结束
                    mBinding.tvVoteStatus.run {
                        text = resources.getString(R.string.vote_end)
                        background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_yugao)
                    }
                    mBinding.lvTopVoteCount.visibility = View.VISIBLE
                    mBinding.rvVoteDownTime2.visibility = View.GONE
                    mBinding.tvVoteDownTimeTip1.text = getString(R.string.vote_down_time_end)
                    mBinding.tvDownTime1.text = setDownTimeTip(0, SpannableStringBuilder(""), 0, 0, 0, true)
                    cutdownDisable?.dispose()
                    cutdownDisable1?.dispose()
                }

            }
            VoteConstant.STATUS.PROGRESS_ING -> {

                if (data.voteType!= VoteConstant.TYPE.MANAGE) {
                    //上传未结束但投票已开始
                    if(data.uploadStatus=="1" ){
                        handCommDetailData()
                        mBinding.tvVoteDownTimeTip1.text = getString(R.string.vote_down_time_end)
                        data?.endSurplusTimestamp?.toInt()?.let { showCutDownView(it, mBinding.rvVoteDownTime1, mBinding.tvDownTime1) }

                        handlisManageData(data,getString(R.string.updata_down_time_end))
                        return
                    }
                    //  上传已结束但投票已开始
                    if(data.uploadStatus=="2"){
                        handCommDetailData()
                        mBinding.tvVoteDownTimeTip1.text = getString(R.string.vote_down_time_start)
                        data?.endSurplusTimestamp?.toInt()?.let { showCutDownView(it, mBinding.rvVoteDownTime1, mBinding.tvDownTime1) }

                        if (data.voteType!= VoteConstant.TYPE.MANAGE) {
                            mBinding.tvVoteDownTimeTip2.text =getString(R.string.updata_down_time_end)
                            data?.endUploadTimestamp?.toInt()?.let { showUpdateCutDownView(it, mBinding.rvVoteDownTime2, mBinding.tvDownTime2) }
                            if(data!!.userJoinCount > 0){
                                mBinding.bottomTitle = getString(R.string.vote_bottom_mine_inpart)
                                if( data?.uploadStatus=="1") {
                                    mBinding.vBottomStatus.visibility = View.VISIBLE
                                }else{
                                    mBinding.vBottomStatus.visibility = View.GONE
                                }

                            }else{
                                mBinding.vBottomStatus.visibility = View.GONE
                            }
                        }else{
                            mBinding.vBottomStatus.visibility = View.GONE
                            mBinding.rvVoteDownTime2.visibility = View.GONE
                        }
                        return
                    }
                }else{
                    // 源代码
                    mBinding.llNoStart.visibility = View.GONE
                    mBinding.llStarted.visibility=  View.VISIBLE
                    mBinding.tvVoteStatus.run {
                        text = resources.getString(R.string.vote_proceing)
                        background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_toupiaozhong)
                    }
                    mBinding.lvTopVoteCount.visibility = View.VISIBLE
                    mBinding.rvVoteDownTime2.visibility = View.GONE
                    mBinding.tvVoteDownTimeTip1.text = getString(R.string.vote_down_time_end)
                    showCutDownView(data.endSurplusTimestamp,mBinding.rvVoteDownTime1,mBinding.tvDownTime1)
                    if (data.voteType != VoteConstant.TYPE.MANAGE) {
                        mBinding.vBottomStatus.visibility = View.VISIBLE
                    }
                }

            }
            //投票未开始
            else -> {

                if (data.voteType== VoteConstant.TYPE.MANAGE) {
                    // 预告&未开始
                    mBinding.tvVoteStatus.run {
                        text = resources.getString(R.string.vote_un_start)
                        background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_yugao)
                    }
                    mBinding.lvTopVoteCount.visibility = View.GONE
                    mBinding.tvVoteDownTimeTip1.text = getString(R.string.vote_down_time_start)
                    showCutDownView(data.startSurplusTimestamp, mBinding.rvVoteDownTime1, mBinding.tvDownTime1)
                    mBinding.vBottomStatus.visibility = View.GONE
                }else{
                    //投票未开始 上传时间未开始
                    if(data.uploadStatus=="0"){
                        mBinding.llNoStart.visibility = View.VISIBLE
                        mBinding.llStarted.visibility=  View.GONE
                        mBinding.tvVoteStatus1.run {
                            text = resources.getString(R.string.vote_un_start)
                            background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_yugao)
                        }
                        if (data.voteType!= VoteConstant.TYPE.MANAGE) {
                            mBinding.tvVoteDownTimeTip.text = getString(R.string.updata_down_time_start)
                            data?.startUploadTimestamp?.toInt()?.let { showCutDownView(it, mBinding.rvVoteDownTime, mBinding.tvDownTime) }
                        }else{
                            mBinding.tvVoteDownTimeTip.text = getString(R.string.vote_down_time_start)
                            data?.startSurplusTimestamp?.toInt()?.let { showCutDownView(it, mBinding.rvVoteDownTime, mBinding.tvDownTime) }
                        }

                    }
                    // 投票未开始 上传已开始
                    else if(data.uploadStatus=="1"){
                        handCommDetailData()
                        mBinding.tvVoteDownTimeTip1.text = getString(R.string.vote_down_time_start)
                        data?.startSurplusTimestamp?.toInt()?.let { showCutDownView(it, mBinding.rvVoteDownTime1, mBinding.tvDownTime1) }
                        handlisManageData(data,getString(R.string.updata_down_time_end))
                    }
                    //投票未开始 上传已开始上传已结束
                    else if(data.uploadStatus=="2"){

                        handCommDetailData()
                        mBinding.tvVoteDownTimeTip1.text = getString(R.string.vote_down_time_start)
                        data?.startSurplusTimestamp?.toInt()?.let { showCutDownView(it, mBinding.rvVoteDownTime1, mBinding.tvDownTime1) }
                        // 区分平台发布和用户发布   用户发布才有我的上传
                        handlisManageData(data,getString(R.string.updata_down_time_end))
                    }
                }
            }
        }
    }

    /**
     * 公共状态
     */
    private fun handCommDetailData() {
        mBinding.llNoStart.visibility = View.GONE
        mBinding.llStarted.visibility=  View.VISIBLE
        mBinding.tvVoteStatus.run {
            text = resources.getString(R.string.vote_proceing)
            background = mBinding.root.context.resources.getDrawable(R.mipmap.vote_list_tag_toupiaozhong)
        }
    }
    /**
     * 区分平台发布和用户发布   用户发布才有我的上传
     */
    private fun handlisManageData(data: VoteDetailBean, content: String) {
        if (data.voteType!= VoteConstant.TYPE.MANAGE) {
            mBinding.vBottomStatus.visibility = View.VISIBLE
            mBinding.tvVoteDownTimeTip2.text =content
            data?.endUploadTimestamp?.toInt()?.let { showUpdateCutDownView(it, mBinding.rvVoteDownTime2, mBinding.tvDownTime2) }
            if(data!!.userJoinCount > 0){
                mBinding.bottomTitle = getString(R.string.vote_bottom_mine_inpart)
                if( data?.endUploadTimestamp=="1") {
                    mBinding.vBottomStatus.visibility = View.VISIBLE
                }else{
                    mBinding.vBottomStatus.visibility = View.GONE
                }

            }else{
                if( data?.endUploadTimestamp=="0"){
                    mBinding.vBottomStatus.visibility = View.GONE
                }else{
                    mBinding.vBottomStatus.visibility = View.VISIBLE
                }
                mBinding.bottomTitle = getString(R.string.vote_bottom_inpart)
            }
        }else{
            mBinding.vBottomStatus.visibility = View.GONE
            mBinding.rvVoteDownTime2.visibility = View.GONE
        }
    }

    private fun initVoteCover(coverImage: String?) {
        var images: MutableList<String?> = mutableListOf()
        if (!coverImage.isNullOrEmpty()) {
            images.addAll(coverImage.split(","))
        } else {
            images.add(coverImage)
        }
        mBinding.cbrVoteDetail.setPages(
            object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return BaseImageHolder(itemView!!)
                }

                override fun getLayoutId(): Int {
                    return R.layout.item_provider_holder_banner_img
                }
            }, images
        )
            .setCanLoop(images.size > 1)
            .setPointViewVisible(images.size > 1)
            .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
            .setOnItemClickListener {
                // 跳转事件
                val intent =
                    Intent(this@VoteDetailActivity, BigImgActivity::class.java)
                intent.putExtra("position", 0)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(images)
                )
                startActivity(intent)
            }
            .setPageIndicator(null)

        mBinding.cbrVoteDetail1.setPages(
            object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return BaseImageHolder(itemView!!)
                }

                override fun getLayoutId(): Int {
                    return R.layout.item_provider_holder_banner_img
                }
            }, images
        )
            .setCanLoop(images.size > 1)
            .setPointViewVisible(images.size > 1)
            .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
            .setOnItemClickListener {
                // 跳转事件
                val intent =
                    Intent(this@VoteDetailActivity, BigImgActivity::class.java)
                intent.putExtra("position", 0)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(images)
                )
                startActivity(intent)
            }
            .setPageIndicator(null)
    }

    override fun initData() {
//        mModel.getVoteDetail(voteId)
        mModel.getVoteDetail(voteId ?: "0")
        mModel.getVoteTypes(voteId ?: "")
    }

    private fun showPopVoteRuleWindow() {
        if (voteDetail != null) {
            if (popVoteRuleWindow == null) {
                popVoteRuleWindow = PopVoteRuleWindow(this@VoteDetailActivity)
            }
            if (!popVoteRuleWindow!!.isShowing) {
                popVoteRuleWindow?.updateData(voteDetail!!)
                popVoteRuleWindow?.showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
            }
        }
    }

    /**
     * 投票时间倒计时(预告倒计时)
     */
    private fun showCutDownView(count: Int, rvVoteDownTime: RelativeLayout, tvDownTime: TextView) {
        cutdownDisable?.dispose()
        var builder = SpannableStringBuilder("")

        val mi = 60
        val hh = mi * 60
        val dd = hh * 24

        if(count==0){
            if (!rvVoteDownTime.isVisible) {
                rvVoteDownTime.visibility = View.GONE
            }
            tvDownTime.text = setDownTimeTip(0, builder, 0, 0, 0)
        }

        cutdownDisable = Observable.interval(1, TimeUnit.SECONDS)
            .take(count.toLong())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                var curr = count - it
                val day = curr / dd
                val hour = (curr - day * dd) / hh
                val minute = (curr - day * dd - hour * hh) / mi
                var second = curr - day * dd - hour * hh - minute * 60
                builder.clear()

                if (!rvVoteDownTime.isVisible) {
                    rvVoteDownTime.visibility = View.VISIBLE
                }
                tvDownTime.text = setDownTimeTip(day, builder, hour, minute, second)
            }
    }
    /**
     * 上传时间倒计时
     */
    private fun showUpdateCutDownView(count: Int, rvVoteDownTime: RelativeLayout, tvDownTime: TextView) {
        cutdownDisable1?.dispose()
        var builder = SpannableStringBuilder("")

        val mi = 60
        val hh = mi * 60
        val dd = hh * 24

        if(count==0){
            if (!rvVoteDownTime.isVisible) {
                rvVoteDownTime.visibility = View.GONE
            }
            tvDownTime.text = setDownTimeTip(0, builder, 0, 0, 0)
        }
        cutdownDisable1 = Observable.interval(1, TimeUnit.SECONDS)
            .take(count.toLong())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                var curr = count - it
                val day = curr / dd
                val hour = (curr - day * dd) / hh
                val minute = (curr - day * dd - hour * hh) / mi
                var second = curr - day * dd - hour * hh - minute * 60
                builder.clear()

                if (!rvVoteDownTime.isVisible) {
                    rvVoteDownTime.visibility = View.VISIBLE
                }
                tvDownTime.text = setDownTimeTip(day, builder, hour, minute, second)
            }
    }
    private fun setDownTimeTip(
        day: Long,
        builder: SpannableStringBuilder,
        hour: Long,
        minute: Long,
        second: Long,
        isShowZero: Boolean = true
    ): SpannableStringBuilder {
        if (day > 0 || (day == 0L && isShowZero)) {
            var dayStr = "$day"
            val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.app_main_color))
            var absoluteSizeSpan = RelativeSizeSpan(1.4f)
            var styleSpan = StyleSpan(Typeface.BOLD)
            builder.append(dayStr, foregroundColorSpan, absoluteSizeSpan, styleSpan)
            builder.append("天 ")
        }
        if (hour > 0 || (day == 0L && isShowZero)) {
            var hourStr = "$hour"
            val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.app_main_color))
            var absoluteSizeSpan = RelativeSizeSpan(1.4f)
            var styleSpan = StyleSpan(Typeface.BOLD)
            builder.append(hourStr, foregroundColorSpan, absoluteSizeSpan, styleSpan)
            builder.append("时 ")
        }
        if (minute > 0 || (day == 0L && isShowZero)) {
            var minuteStr = "$minute"
            val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.app_main_color))
            var absoluteSizeSpan = RelativeSizeSpan(1.4f)
            var styleSpan = StyleSpan(Typeface.BOLD)
            builder.append(minuteStr, foregroundColorSpan, absoluteSizeSpan, styleSpan)
            builder.append("分 ")
        }
        if (second >= 0 || (day == 0L && isShowZero)) {
            var secondStr = "$second"
            val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.app_main_color))
            var absoluteSizeSpan = RelativeSizeSpan(1.4f)
            var styleSpan = StyleSpan(Typeface.BOLD)
            builder.append(secondStr, foregroundColorSpan, absoluteSizeSpan, styleSpan)
            builder.append("秒 ")
        }
        return builder
    }

    override fun onResume() {
        super.onResume()
//        mModel.getVoteDetail(voteId ?: "0")
        if (isCutDownTime) {
            isCutDownTime = false
            mModel.refreshVoteDetailTime(voteId ?: "")
        }
    }

    override fun onPause() {
        super.onPause()
        isCutDownTime = true
        cutdownDisable?.dispose()
        cutdownDisable1?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        cutdownDisable?.dispose()
        cutdownDisable1?.dispose()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateStatus(event: UpdateVoteStatusEvent) {
        // 刷新作品数据
        mModel.currPage = 1
        mModel.getVoteWorkList(voteId ?: "", orderMode)
    }
}
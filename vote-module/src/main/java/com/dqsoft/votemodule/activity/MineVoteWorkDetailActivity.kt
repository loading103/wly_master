package com.dqsoft.votemodule.activity

import android.content.Intent
import android.content.res.Configuration
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.VoteWorkDetailBean
import com.daqsoft.provider.bean.VoteWrokMinInfo
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.vote.VoteConstant
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.dialog.ProviderOperationTipDialog
import com.daqsoft.provider.view.dialog.ProviderTipDialog
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.databinding.ActivityMineVoteDetailBinding
import com.dqsoft.votemodule.event.UpdateWorkStatusEvent
import com.dqsoft.votemodule.view.PopVoteRuleWindow
import com.dqsoft.votemodule.vm.MineVoteDetailViewModel
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.backgroundColor
import java.lang.Exception
import java.lang.StringBuilder

/**
 * @Description ??????????????????
 * @ClassName   MineVoteDetailActivity
 * @Author      luoyi
 * @Time        2020/11/9 9:55
 */
@Route(path = ARouterPath.VoteModule.VOTE_MINE_WORK_DETAIL)
class MineVoteWorkDetailActivity : TitleBarActivity<ActivityMineVoteDetailBinding, MineVoteDetailViewModel>() {


    /**
     * 0 ???????????????????????????????????????????????????????????????????????????????????? 1 ??????????????????
     */
    @Autowired
    @JvmField
    var mode: Int = 0

    @Autowired
    @JvmField
    var proId: String = ""

    private var popVoteRuleWindow: PopVoteRuleWindow? = null

    private var currentOperationMode: String = ""

    private var voteInfoBean: VoteWrokMinInfo? = null

    private var voteWorkDetailBean: VoteWorkDetailBean? = null

    private var isStopBanner: Boolean = false

    /**
     * ????????????
     */
    var sharePopWindow: SharePopWindow? = null

     fun showShareIcon() {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            voteWorkDetailBean?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this)
                }
                // ??????????????????
                var content="???????????????????????????${ it.name}????????????????????????"
                sharePopWindow?.setShareContent(
                    it.name, content, it.images.getRealImages(),
                    ShareModel.getMineVoteDesc(proId)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mtitleBar)
                }
            }
        })
    }
    /**
     * ??????????????????
     */
    private val deleteTipDialog: ProviderOperationTipDialog by lazy {
        ProviderOperationTipDialog.Builder().setContent(resources.getString(R.string.vote_tip_delete))
            .setTitle(resources.getString(R.string.vote_tip_delete_title))
            .setOnTipConfirmListener(object : ProviderOperationTipDialog.OnTipConfirmListener {
                override fun onConfirm() {
                    showLoadingDialog()
                    currentOperationMode = VoteConstant.OPERATION_STATUS.DELETE
                    mModel.operationWorkDetail(proId ?: "", VoteConstant.OPERATION_STATUS.DELETE)
                }

            })
            .create(this@MineVoteWorkDetailActivity)
    }

    /**
     * ??????????????????
     */
    private val backTipDialog: ProviderOperationTipDialog by lazy {
        ProviderOperationTipDialog.Builder().setContent(resources.getString(R.string.vote_tip_back))
            .setTitle(resources.getString(R.string.vote_tip_back_title))
            .setOnTipConfirmListener(object : ProviderOperationTipDialog.OnTipConfirmListener {
                override fun onConfirm() {
                    showLoadingDialog()
                    currentOperationMode = VoteConstant.OPERATION_STATUS.BACK
                    mModel.operationWorkDetail(proId ?: "", VoteConstant.OPERATION_STATUS.BACK)
                }

            })
            .create(this@MineVoteWorkDetailActivity)
    }

    override fun getLayout(): Int {
        return R.layout.activity_mine_vote_detail
    }

    override fun setTitle(): String {
        return ""
    }

    override fun injectVm(): Class<MineVoteDetailViewModel> {
        return MineVoteDetailViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        initViewModel()
        initViewEvent()
    }

    private fun initViewEvent() {
        mBinding.vBottomVoteEdit.onNoDoubleClick {
            // ??????
            voteInfoBean?.let {
                ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_INPART)
                    .withString("voteId", it.id.toString())
                    .withInt("mode", 2)
                    .withString("proId", proId)
                    .navigation()
            }

        }
        mBinding.vVoteInfo.onNoDoubleClick {
            voteInfoBean?.let {
                ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_DETAIL)
                    .withString("voteId", it.id.toString())
                    .navigation()
            }
        }
        mBinding.vBottomVoteDel.onNoDoubleClick {
            // ??????
            deleteTipDialog.show()
        }
        mBinding.vBottomVoteBack.onNoDoubleClick {
            // ??????
            backTipDialog.show()
        }
        mBinding.imgDeleteWork.onNoDoubleClick {
            // ??????
            deleteTipDialog.show()
        }
        mBinding.tvMineVoteTipInfo.onNoDoubleClick {
            showPopVoteRuleWindow()
        }

        mBinding.tvToShareVoteWork.onNoDoubleClick {
            voteWorkDetailBean?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this)
                }
                // ??????????????????
                var content="???????????????????????????${ it.name}????????????????????????"
                sharePopWindow?.setShareContent(
                    it.name, content, it.images.getRealImages(),
                    ShareModel.getMineVoteDesc(proId)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mtitleBar)
                }
            }
        }
        mBinding.tvGotoVote.onNoDoubleClick {
            // ??????
            mModel.voteWork(proId ?: "")
        }
        mBinding.tvGoVote.onNoDoubleClick {

            voteInfoBean?.let {
                ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_DETAIL)
                    .withString("voteId", it.id.toString())
                    .navigation()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun initViewModel() {
        mModel.voteDetailLd.observe(this, Observer {
            mBinding.scVoteWorkDetail.visibility = View.VISIBLE
            it?.let { data ->
                mBinding.data = data
                voteWorkDetailBean = data
                title = "${data.name ?: ""}"
                initCover(data)
                if (data.myselfFlag) {
                    // ??????????????????????????????
                    mode = 1
                    setMySelfWorkInfo(data)
                } else {
                    mBinding.lvBottomOperation.visibility = View.GONE
                    setOtherWorkInfo(data)
                }
                setIdCardAndPhoe(data)
                setVoteInfo(data.voteInfo)
                setCommonInfo(data)
                if (!data.intro.isNullOrEmpty()) {
                    mBinding?.wvVoteInfo?.settings?.defaultTextEncodingName = "utf-8"
                    mBinding?.wvVoteInfo?.settings?.javaScriptEnabled = true
                    mBinding?.wvVoteInfo?.loadDataWithBaseURL(
                        null, StringUtil.getHtml((data.intro!!)),
                        "text/html", "utf-8", null
                    )
                }
            }
            dissMissLoadingDialog()
        })
        mModel.voteWorkOpeartionLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                EventBus.getDefault().post(UpdateWorkStatusEvent(proId ?: ""))
                when (currentOperationMode) {
                    VoteConstant.OPERATION_STATUS.DELETE -> {
                        ToastUtils.showMessage("????????????~")
                        finish()
                    }
                    VoteConstant.OPERATION_STATUS.BACK -> {
                        ToastUtils.showMessage("????????????~")
                    }
                }
            }

        })
        mModel.voteWorkLd.observe(this, Observer {
            if (it != null) {
                mModel.getVoteWorkDetail(proId ?: "")
                EventBus.getDefault().post(UpdateWorkStatusEvent(proId!!))
                if (!it.continueFlag) {
                    if (it.voteLimitStatus == 2) {
                        ToastUtils.showMessage("?????????????????????????????????")
                    }
                    if (it.voteLimitStatus == 3) {
                        ToastUtils.showMessage("?????????????????????????????????")
                    } else {
                        ToastUtils.showMessage("???????????????????????????")
                    }
                } else {
                    if (it.voteLimitStatus == 0 || it.surplusCount == -1) {
                        ToastUtils.showMessage("????????????")
                    } else {
                        ToastUtils.showMessage("????????????????????????${it.surplusCount}???")
                    }
                }
            }
        })
    }

    /**
     * ????????????????????????
     */
    private fun setVoteButton(voteButton: Int) {
        mBinding.tvGotoVote.visibility = View.VISIBLE
        mBinding.tvToShareVoteWork.visibility = View.GONE
        when (voteButton) {
            //0 ???????????? 1 ???????????? 2 ????????? 3 ?????????
            0 -> {
                mBinding.tvGotoVote.backgroundColor = mBinding.root.context.resources.getColor(R.color.app_main_forbidden)
                mBinding.tvToShareVoteWork.visibility = View.VISIBLE
                mBinding.tvGotoVote.visibility = View.GONE
            }
            1 -> {
                mBinding.tvGotoVote.backgroundColor = mBinding.root.context.resources.getColor(R.color.app_main_color)
                mBinding.tvToShareVoteWork.visibility = View.VISIBLE
            }
            2, 4 -> {
                mBinding.tvGotoVote.backgroundColor = mBinding.root.context.resources.getColor(R.color.app_main_forbidden)
                mBinding.tvToShareVoteWork.visibility = View.VISIBLE
                mBinding.tvGotoVote.text = "??????"
            }
            3 -> {
                mBinding.tvGotoVote.backgroundColor = mBinding.root.context.resources.getColor(R.color.app_main_forbidden)
                mBinding.tvToShareVoteWork.visibility = View.VISIBLE
                mBinding.tvGotoVote.visibility = View.GONE
            }

        }
    }

    private fun setIdCardAndPhoe(data: VoteWorkDetailBean) {
        if (!data.idCard.isNullOrEmpty()) {
            try {
                mBinding.idCard = Utils.IDNumberInvisible(SM4Util.decryptByEcb(data.idCard))
            } catch (e: Exception) {

            }
        } else {
            mBinding.idCard = ""
        }
        if (!data.phone.isNullOrEmpty()) {
            try {
                mBinding.phone = Utils.phoneInvisible(SM4Util.decryptByEcb(data.phone))
            } catch (e: Exception) {

            }
        } else {
            mBinding.phone = ""
        }

    }

    /**
     * ???????????????
     */
    private fun initCover(data: VoteWorkDetailBean) {
        val dataVideoImages: MutableList<VideoImageBean> = mutableListOf()
        //??????
        if (!data.video.isNullOrEmpty()) {
            dataVideoImages.add(0, VideoImageBean().apply {
                type = 1
                videoUrl = data.video
            })
        }
        var images: List<String> = ArrayList<String>()
        if (!data.images.isNullOrEmpty()) {
            images = data.images!!.split(",")
            if (!images.isNullOrEmpty()) {
                for (item in images) {
                    dataVideoImages.add(VideoImageBean().apply {
                        type = 0
                        imageUrl = item
                    })
                }
            }
        }
        mBinding.tvTotalIndex.text = "/${dataVideoImages.size}"
        mBinding.tvCurrentIndex.text = "1"

        mBinding.bannerTopAdv.setPages(object : CBViewHolderCreator {
            override fun createHolder(itemView: View?): Holder<*> {
                return VideoImageHolder(itemView!!, this@MineVoteWorkDetailActivity)
            }

            override fun getLayoutId(): Int {
                return R.layout.layout_video_image
            }
        }, dataVideoImages).setCanLoop(dataVideoImages.size > 1).startTurning(3000).setPointViewVisible(false).setOnItemClickListener {
            when (dataVideoImages[it].type) {
                0 -> {
                    // ????????????
                    val intent =
                        Intent(this@MineVoteWorkDetailActivity, BigImgActivity::class.java)
                    intent.putExtra("position", it)
                    intent.putStringArrayListExtra(
                        "imgList",
                        ArrayList(images)
                    )
                    startActivity(intent)
                }
                1 -> {
                }
                2 -> {

                }
            }
        }.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            }

            override fun onPageSelected(index: Int) {
                var pos = index
                mBinding.tvCurrentIndex.text = "${pos + 1}"
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

            }
        }
        )

    }

    private fun setCommonInfo(data: VoteWorkDetailBean) {
        // ????????????
        if (data.typeChildName.isNullOrEmpty() || data.typeName.isNullOrEmpty()) {
            mBinding.vVoteType.visibility = View.GONE
        } else {
            mBinding.vVoteType.visibility = View.VISIBLE
            mBinding.tvVoteTypeValue.text = DividerTextUtils.converttDivideString(
                StringBuilder(), "-", data.typeName ?: "",
                data.typeChildName ?: ""
            )
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAudioPlayer(event: UpdateAudioStateEvent) {
        // 1 ???????????? 2 ?????? 3??????/??????
        try {
            when (event.type) {
                1 -> {
                    mBinding?.bannerTopAdv?.stopTurning()
                }
                2 -> {
                    mBinding?.bannerTopAdv?.stopTurning()
                }
                3 -> {
                    mBinding?.bannerTopAdv?.startTurning(3000)
                }
            }
        } catch (e: java.lang.Exception) {

        }


    }

    /**
     * ??????????????????
     */
    private fun setVoteInfo(voteInfo: VoteWrokMinInfo?) {

        // ??????????????????
        voteInfo?.let {
            when (it.voteStatus) {
                VoteConstant.STATUS.END -> {
                    mBinding.tvVoteStatusTag.text = "?????????"
                    mBinding.tvVoteStatusTag.background =
                        ContextCompat.getDrawable(this@MineVoteWorkDetailActivity, R.drawable.shape_vote_status__un_r2)
                    mBinding.tvVoteStatusTag.setTextColor(ContextCompat.getColor(this@MineVoteWorkDetailActivity, R.color.color_666))
                }
                VoteConstant.STATUS.UN_START -> {

                    if(it.uploadStatus==1 || it.uploadStatus==2 ){
                        mBinding.tvVoteStatusTag.text = "?????????"

                        mBinding.tvVoteStatusTag.background = ContextCompat.getDrawable(this@MineVoteWorkDetailActivity, R.drawable.shape_vote_status_r2)
                        mBinding.tvVoteStatusTag.setTextColor(ContextCompat.getColor(this@MineVoteWorkDetailActivity, R.color.app_main_color))
                    }else{
                        mBinding.tvVoteStatusTag.text = "??????"
                        mBinding.tvVoteStatusTag.background = ContextCompat.getDrawable(this@MineVoteWorkDetailActivity, R.drawable.shape_vote_status__un_r2)
                        mBinding.tvVoteStatusTag.setTextColor(ContextCompat.getColor(this@MineVoteWorkDetailActivity, R.color.color_666))
                    }

                }
                VoteConstant.STATUS.PROGRESS_ING -> {
                    mBinding.tvVoteStatusTag.text = "?????????"

                    mBinding.tvVoteStatusTag.background = ContextCompat.getDrawable(this@MineVoteWorkDetailActivity, R.drawable.shape_vote_status_r2)
                    mBinding.tvVoteStatusTag.setTextColor(ContextCompat.getColor(this@MineVoteWorkDetailActivity, R.color.app_main_color))
                }
            }
        }
        // ??????????????????
        mBinding.tvVoteStatusTime.text = "${DateUtil.formatDateByString("yyyy.MM.dd HH:mm", "yyyy-MM-dd HH:mm:ss", voteInfo?.startTime)}-" +
                "${DateUtil.formatDateByString("yyyy.MM.dd HH:mm", "yyyy-MM-dd HH:mm:ss", voteInfo?.endTime)}"
        voteInfoBean = voteInfo
    }

    /**
     * ???????????????????????????
     */
    private fun setOtherWorkInfo(data: VoteWorkDetailBean) {
        mBinding.lvMineVoteDetail.visibility = View.GONE
        mBinding.lvWorkOtherInfo.visibility = View.VISIBLE
        mBinding.lvBottomOperation.visibility = View.GONE
        setVoteButton(data.voteButton)
        mBinding.tvWorkOtherInfo.text = DividerTextUtils.convertString(
            StringBuilder(),
            if (!data.author.isNullOrEmpty()) {
                "?????????${data.author}"
            } else {
                ""
            }, if (data.typeName.isNullOrEmpty() && data.typeChildName.isNullOrEmpty()) {
                ""
            } else {
                if (data.typeChildName.isNullOrEmpty()) {
                    "?????????${data.typeName}"
                } else {
                    "?????????${data.typeName}-${data.typeChildName}"
                }
            }, "${DateUtil.formatDateByString("yyyy.MM.dd HH:mm", "yyyy-MM-dd HH:mm:ss", data.createTime)}"
        )
    }

    /**
     * ???????????????????????????
     */
    private fun setMySelfWorkInfo(data: VoteWorkDetailBean) {
        mBinding.lvMineVoteDetail.visibility = View.VISIBLE
        mBinding.lvWorkOtherInfo.visibility = View.GONE
        mBinding.tvMineVoteTipReason.visibility = View.GONE
        mBinding.lvBottomOperation.visibility = View.VISIBLE
        mBinding.tvGotoVote.visibility = View.GONE
        if (data.auditInfo != null) {
            //???????????? ?????????(4) ????????????(6) ??????/??????(7) ??????(8) ??????(9) ???????????????(79)
            when (data.auditInfo!!.auditStatus) {
                4 -> {
                    showShareButton(0)
                    mBinding.rvWorkStatus.visibility = View.VISIBLE
                    mBinding.tvMineVoteStatus.text = resources.getString(
                        R.string.vote_status_str,
                        resources.getString(R.string.vote_status_wait_pass)
                    )
                    mBinding.vBottomVoteBack.visibility = View.VISIBLE
                    mBinding.vBottomVoteDel.visibility = View.GONE
                    mBinding.vBottomVoteEdit.visibility = View.GONE
                    mBinding.tvGoVote.visibility = View.VISIBLE
                    mBinding.vDivdleBack.visibility = View.GONE
                }
                6 -> {
                    mBinding.rvWorkStatus.visibility = View.GONE
                    mBinding.lvBottomOperation.visibility = View.GONE
                    // ???????????????????????? ????????????????????????????????????
                    if (data.voteInfo != null && data.voteInfo!!.voteStatus != VoteConstant.STATUS.PROGRESS_ING) {
                        setVoteButton(data.voteButton)
                    }

                    showShareIcon()
                    mBinding.imgDeleteWork.visibility = View.VISIBLE
                    mBinding.tvGoVote.visibility = View.GONE
                    setOtherWorkInfo(data)
                }
                7, 9, 79 -> {
                    showShareButton(0)
                    mBinding.rvWorkStatus.visibility = View.VISIBLE
                    mBinding.tvMineVoteStatus.text = resources.getString(
                        R.string.vote_status_str,
                        resources.getString(R.string.vote_status_un_pass)
                    )
                    mBinding.vBottomVoteBack.visibility = View.GONE
                    mBinding.vBottomVoteDel.visibility = View.VISIBLE
                    mBinding.tvGoVote.visibility = View.VISIBLE
                    mBinding.vDivdleBack.visibility = View.GONE
                    if (!data.auditInfo!!.auditResult.isNullOrEmpty()) {
                        mBinding.tvMineVoteTipReason.visibility = View.VISIBLE
                        mBinding.tvMineVoteTipReason.text = "${data.auditInfo!!.auditResult}"
                    }
                    if (data.auditInfo!!.auditStatus == 9) {
                        // ???????????? ??????????????????
                        mBinding.vBottomVoteEdit.visibility = View.GONE
                    } else {
                        mBinding.vBottomVoteEdit.visibility = View.VISIBLE
                    }
                }
                8 -> {
                    mBinding.tvGoVote.visibility = View.VISIBLE
                    mBinding.rvWorkStatus.visibility = View.VISIBLE
                    mBinding.vBottomVoteBack.visibility = View.GONE
                    mBinding.vBottomVoteDel.visibility = View.VISIBLE
                    mBinding.vBottomVoteEdit.visibility = View.VISIBLE
                    mBinding.vDivdleBack.visibility = View.GONE
                    mBinding.tvMineVoteStatus.text = resources.getString(
                        R.string.vote_status_str,
                        resources.getString(R.string.vote_status_back)
                    )
                }
            }
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getVoteWorkDetail(proId ?: "")
    }

    private fun showPopVoteRuleWindow() {
        if (voteInfoBean != null) {
            if (popVoteRuleWindow == null) {
                popVoteRuleWindow = PopVoteRuleWindow(this@MineVoteWorkDetailActivity)
            }
            if (!popVoteRuleWindow!!.isShowing) {
                popVoteRuleWindow?.updateData(voteInfoBean!!)
                popVoteRuleWindow?.showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        isStopBanner = true
        mBinding.bannerTopAdv.pauseVideoPlayer()
        mBinding.bannerTopAdv.stopTurning()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding?.bannerTopAdv?.stopVideoPlayer()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateWorkStatus(event: UpdateWorkStatusEvent) {
        // ??????????????????
        if (!proId.isNullOrEmpty() && event.id == proId) {
            showLoadingDialog()
            mModel.getVoteWorkDetail(proId ?: "")
        }
    }

    override fun onResume() {
        super.onResume()
        if (isStopBanner) {
            mBinding?.bannerTopAdv?.startTurning(3000)
            isStopBanner = false
        }
    }
}
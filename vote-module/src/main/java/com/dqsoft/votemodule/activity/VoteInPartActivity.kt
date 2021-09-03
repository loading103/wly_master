package com.dqsoft.votemodule.activity

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.*
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.VoteDetailBean
import com.daqsoft.provider.bean.VoteSubTypeBean
import com.daqsoft.provider.bean.VoteTypeBean
import com.daqsoft.provider.bean.VoteWorkDetailBean
import com.daqsoft.provider.network.vote.VoteConstant
import com.daqsoft.provider.view.LabelsView
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.databinding.ActivityVoteInPartBinding
import com.dqsoft.votemodule.event.UpdateWorkStatusEvent
import com.dqsoft.votemodule.view.PopVoteRuleWindow
import com.dqsoft.votemodule.vm.VoteInPartViewModel
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import org.greenrobot.eventbus.EventBus
import kotlin.reflect.typeOf

/**
 * @Description 参与页面
 * @ClassName   VoteInPartActivity
 * @Author      luoyi
 * @Time        2020/11/9 14:19
 */
@Route(path = ARouterPath.VoteModule.VOTE_INPART)
class VoteInPartActivity : TitleBarActivity<ActivityVoteInPartBinding, VoteInPartViewModel>() {

    /**
     * 模式 1 新增模式 2 编辑模式
     */
    @Autowired
    @JvmField
    var mode: Int = 0


    @Autowired
    @JvmField
    var voteId: String? = ""

    // 编辑模式使用
    @Autowired
    @JvmField
    var proId: String? = ""

    /**
     * 投票详情信息
     */
    var voteDetailBean: VoteDetailBean? = null

    /**
     * 作品详情信息
     */
    var voteWorkDetailBean: VoteWorkDetailBean? = null

    var voteTypes: MutableList<VoteTypeBean> = mutableListOf()

    var childVoteTypes: MutableList<VoteSubTypeBean> = mutableListOf()

    var typeId: String? = ""

    var childTypeId: String? = ""

    private var popVoteRuleWindow: PopVoteRuleWindow? = null

    private var isInitData: Boolean = true

    override fun getLayout(): Int {
        return R.layout.activity_vote_in_part
    }

    override fun setTitle(): String {
        return "我要参与"
    }

    override fun injectVm(): Class<VoteInPartViewModel> {
        return VoteInPartViewModel::class.java
    }

    override fun initView() {

        initViewModel()
        mBinding.tvSaveVoteInPart.onNoDoubleClick {
            if (!AppUtils.isLogin()) {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
                return@onNoDoubleClick
            }
            var workName: String? = mBinding.edtInputVoteWorkName.text.toString()
            var author: String? = mBinding.edtVoteWorkAuthor.text.toString()
            var infos: String? = mBinding.edtVoteIntrod.text.toString()

            var idCard: String? = ""
            if (mBinding.rlIdcardInput.visibility == View.VISIBLE) {
                idCard = mBinding.edtVoteIdCard.text.toString()
            }
            var phone: String? = ""
            if (mBinding.edtVotePhone.visibility == View.VISIBLE) {
                phone = mBinding.edtVotePhone.text.toString()
            }

            voteDetailBean?.let { it ->
                var haveMustInput: Boolean = false
                var videUrl: String? = mBinding.uvVoteInPartVideo.path
                var imageUrl: String? = mBinding.uvVoteInPartImages.path
                if (it.proSetting != null && !it.proSetting!!.requiredItem.isNullOrEmpty()) {
                    var requiredItems: List<String> = it.proSetting!!.requiredItem!!.split(",")
                    flag1@ for (item in requiredItems) {
                        item?.let { type ->
                            when (type) {
                                VoteConstant.RequiedItem.PRO_NAME -> {
                                    if (workName.isNullOrEmpty()) {
                                        haveMustInput = true
                                        ToastUtils.showMessage("非常抱歉，作品名称不能为空")
                                    }
                                }
                                VoteConstant.RequiedItem.PRO_AUTHOR -> {
                                    if (author.isNullOrEmpty()) {
                                        haveMustInput = true
                                        ToastUtils.showMessage("非常抱歉，参与者姓名不能为空")
                                    }
                                }
                                VoteConstant.RequiedItem.PRO_ID_CARD -> {
                                    if (idCard.isNullOrEmpty()) {
                                        haveMustInput = true
                                        ToastUtils.showMessage("非常抱歉，身份证号码不能为空")
                                    }
                                }
                                VoteConstant.RequiedItem.PRO_INTRO -> {
                                    if (infos.isNullOrEmpty()) {
                                        haveMustInput = true
                                        ToastUtils.showMessage("非常抱歉，作品简介不能为空")
                                    }
                                }
                                VoteConstant.RequiedItem.PRO_PHONE -> {
                                    if (phone.isNullOrEmpty()) {
                                        haveMustInput = true
                                        ToastUtils.showMessage("非常抱歉，手机号介不能为空")
                                    } else if (!RegexUtil.isMobilePhone(phone)) {
                                        haveMustInput = true
                                        ToastUtils.showMessage("非常抱歉，手机号格式不对，请重新输入~")
                                    }
                                }
                            }
                        }
                        if (!haveMustInput) {
                            if (it.proSetting!!.imageStatus == 1 && it.proSetting!!.videoStatus == 1) {
                                if (videUrl.isNullOrEmpty() && imageUrl.isNullOrEmpty()) {
                                    haveMustInput = true
                                    ToastUtils.showMessage("图片或者视频，至少传一种资源~")
                                }
                            } else {
                                if (it.proSetting!!.imageStatus == 1) {
                                    if (imageUrl.isNullOrEmpty()) {
                                        haveMustInput = true
                                        ToastUtils.showMessage("至少传一张图片哦~")
                                    }
                                }
                                if (it.proSetting!!.videoStatus == 1) {
                                    if (videUrl.isNullOrEmpty()) {
                                        haveMustInput = true
                                        ToastUtils.showMessage("至少传一个视频哦~")
                                    }
                                }
                            }
                        }
                        // 判断视频是否未传完成的情况
                        if (!videUrl.isNullOrEmpty() && !videUrl.startsWith("http")) {
                            haveMustInput = true
                            ToastUtils.showMessage("您还有未上传完成的视频，请等待上传完成~")
                        }
                        // 判断图片是否有未上传完成的情况
                        if (!imageUrl.isNullOrEmpty()) {
                            var images: List<String>? = imageUrl.split(",")
                            if (!images.isNullOrEmpty()) {
                                for (image in images) {
                                    if (!image.isNullOrEmpty() && !image.startsWith("http")) {
                                        haveMustInput = true
                                        ToastUtils.showMessage("您还有未上传完成的图片，请等待上传完成~")
                                        break
                                    }
                                }
                            }
                        }
                        if (haveMustInput) {
                            break@flag1
                        }
                    }
                }

                if (!haveMustInput) {
                    showLoadingDialog()
                    if (mode == 1) {
                        mModel.saveVoteInPart(
                            idCard, typeId, videUrl,
                            voteId ?: "", workName, infos, childTypeId, author, phone, imageUrl
                        )
                    } else {
                        mModel.saveVoteInPart(
                            idCard, typeId, videUrl,
                            voteId ?: "", workName, infos, childTypeId, author, phone, imageUrl, proId
                        )
                    }
                }
            }
        }
        mBinding.uvVoteInPartImages.run {
            setPicNumber(9)
            setSelectMode(0)
            setIsNeedShowSelect(false)
        }
        mBinding.uvVoteInPartImages.init(this@VoteInPartActivity, true, true)

        mBinding.uvVoteInPartVideo.run {
            setMaxVideoNumer(1)
            setSelectMode(1)
            setIsNeedShowSelect(false)
        }
        mBinding.uvVoteInPartVideo.init(this@VoteInPartActivity, true, true)
        mBinding.tvGotoVote.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.VoteModule.VOTE_DETAIL)
                .withString("voteId", voteId ?: "")
                .navigation()
        }
        mBinding.tvVoteNote.onNoDoubleClick {
            showPopVoteRuleWindow()
        }
        mBinding.llvWorkTypes.setOnLabelSelectChangeListener { label, data, isSelect, position ->
            if (isSelect) {
                if (position in voteTypes.indices) {
                    var voteType = voteTypes[position]
                    typeId = voteType.id.toString()
                    if (voteType?.child != null && !voteType!!.child!!.isNullOrEmpty()) {
                        childVoteTypes.clear()
                        childVoteTypes.addAll(voteType!!.child!!)
                        var temps: MutableList<String> = mutableListOf()
                        for (item in voteType!!.child!!) {
                            temps.add("${item.name}")
                        }
                        mBinding.llvWorkChildTypes.setLabels(temps)
                        if (voteWorkDetailBean == null) {
                            mBinding.llvWorkChildTypes.setSelect(0)
                        } else {
                            if (voteWorkDetailBean!!.typeChild > 0 && isInitData) {
                                isInitData = false
                                for (i in childVoteTypes.indices) {
                                    var chidType: VoteSubTypeBean? = childVoteTypes[i]
                                    if (chidType != null && chidType!!.id == voteWorkDetailBean!!.typeChild) {
                                        mBinding.llvWorkChildTypes.setSelects(i)
                                        break
                                    }
                                }
                            } else {
                                mBinding.llvWorkChildTypes.setSelect(0)
                            }
                        }
                        mBinding.lvVoteTypeChild.visibility = View.VISIBLE
                    } else {
                        childVoteTypes.clear()
                        childTypeId = ""
                        mBinding.lvVoteTypeChild.visibility = View.GONE
                    }
                }
                label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
                label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_selected_r3)
            } else {
                label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.color_333))
                label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_default_r3)
            }
        }
        mBinding.llvWorkChildTypes.setOnLabelSelectChangeListener { label, data, isSelect, position ->
            if (isSelect) {
                label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
                label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_selected_r3)
                if (position in childVoteTypes.indices) {
                    childTypeId = childVoteTypes[position]?.id?.toString()
                }
            } else {
                label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.color_333))
                label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_default_r3)
            }
        }
    }


    private fun initViewModel() {
        mModel.voteTypeLd.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.rlVoteTypeTitle.visibility = View.VISIBLE
                mBinding.llvWorkTypes.visibility = View.VISIBLE

                voteTypes.clear()
                voteTypes.addAll(it)
                var tempType: MutableList<String> = mutableListOf()
                for (item in voteTypes) {
                    tempType.add("" + item?.name)
                }
                mBinding.llvWorkTypes.setLabels(tempType)
                if (voteWorkDetailBean == null) {
                    mBinding.llvWorkTypes.setSelect(0)
                } else {
                    voteWorkDetailBean?.let {
                        if (it.type > 0) {
                            for (i in voteTypes.indices) {
                                var type: VoteTypeBean? = voteTypes[i]
                                if (type != null && type.id == voteWorkDetailBean!!.type) {
                                    mBinding.llvWorkTypes.setSelect(i)
                                    break
                                }
                            }
                        } else {
                            mBinding.llvWorkTypes.setSelect(0)
                        }
                    }
                }
            } else {
                mBinding.llvWorkTypes.visibility = View.GONE
                mBinding.lvVoteTypeChild.visibility = View.GONE
                mBinding.rlVoteTypeTitle.visibility = View.GONE
            }

        })
        mModel.saveVoteLd.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
                EventBus.getDefault().post(UpdateWorkStatusEvent(proId ?: ""))
                ToastUtils.showMessage("恭喜您，提交成功，等待审核中~")
                finish()
            }
        })
        // 投票信息
        mModel.voteDetailLd.observe(this, Observer {
            if (it != null) {
                voteDetailBean = it
                it.proSetting?.run {
                    if (imageStatus == 1) {
                        mBinding.lvUploadImages.visibility = View.VISIBLE
                        mBinding.uvVoteInPartVideo.setPicNumber(imageCount)
                        mBinding.tvVoteWorkImageVideos.text = "图片仅限${imageCount}个"
                        mBinding.uvVoteInPartImages.setPicNumber(imageCount)
                    } else {
                        mBinding.lvUploadImages.visibility = View.GONE

                    }
                    if (videoStatus == 1) {
                        mBinding.uvVoteInPartVideo.setMaxVideoNumer(videoCount)
                        mBinding.tvVoteWorkImageVideos.text = "视频仅限${videoCount}个"
                        mBinding.uvVoteInPartVideo.setMaxVideoNumer(videoCount)
                        mBinding.lvUploadVideo.visibility = View.VISIBLE

                    } else {
                        mBinding.lvUploadVideo.visibility = View.GONE
                    }
                }
                if (it.userJoinCount <= 0) {
                    // 未参与
                    mBinding.tvVoteStatus.setText(
                        resources.getString(
                            R.string.vote_status_str,
                            resources.getString(R.string.vote_status_un_inpart)
                        )
                    )
                    mBinding.vVoteStatus.visibility = View.VISIBLE
                } else {
                    mBinding.vVoteStatus.visibility = View.GONE
                }
                setUiStatus(it)
            }
        })
        // 实名认证信息
        mModel.vipInfold.observe(this, Observer {
            if (it != null) {
                // 回填实名认证信息
                mBinding.edtVoteWorkAuthor.setText(it.name ?: "")
                mBinding.edtVoteIdCard.setText(it.idCard ?: "")
                var phone: String? = SPUtils.getInstance().getString(SPKey.PHONE)
                if (!phone.isNullOrEmpty()) {
                    mBinding.edtVotePhone.setText(
                        "" + phone
                    )
                } else {
                    mBinding.edtVotePhone.setText(it.phone ?: "")
                }
            }
        })
        // 编辑模式，作品信息回填
        mModel.voteWorkDetailLd.observe(this, Observer {
            if (it != null) {
                voteWorkDetailBean = it
                mBinding.edtVoteWorkAuthor.setText(it.author ?: "")
                mBinding.edtInputVoteWorkName.setText(it.name ?: "")
                mBinding.edtVoteIntrod.setText(it.intro ?: "")
                // 身份证
                if (!it.phone.isNullOrEmpty()) {
                    var phoeStr: String? = SM4Util.decryptByEcb(it.phone)
                    mBinding.edtVotePhone.setText(phoeStr ?: "")
                }
                if (!it.idCard.isNullOrEmpty()) {
                    var idCardStr: String? = SM4Util.decryptByEcb(it.idCard)
                    mBinding.edtVoteIdCard.setText(idCardStr ?: "")
                }
                // 手机号
                // 图片
                if (!it.images.isNullOrEmpty()) {
                    var list: List<String> = it.images!!.split(",")
                    var temps: MutableList<Image> = mutableListOf()
                    if (!list.isNullOrEmpty()) {
                        for (item in list) {
                            if (!item.isNullOrEmpty()) {
                                var image: Image = Image(item, "", 0L)
                                temps.add(image)
                            }
                        }
                        mBinding.uvVoteInPartImages.onActivityResult(ArrayList(temps))
                    }
                }
                // 视频 目前只有一个视频
                if (!it.video.isNullOrEmpty()) {
                    var video: Image = Image(it.video, "", 0L).apply {
                        type = 1
                    }
                    mBinding.uvVoteInPartVideo.addVideo(video)
                }
                // 回填分类信息 重新获取信息
                mModel.getVoteTypes(voteId ?: "")
                // 回填当前状态
                initStatus(it)
            }
        })
    }

    private fun setUiStatus(data: VoteDetailBean) {
        if (data.proSetting != null && !data.proSetting!!.requiredItem.isNullOrEmpty()) {
            if (data.proSetting!!.imageStatus==1 && data.proSetting!!.imageCount > 0) {
                mBinding.imageVisable = true
                mBinding.imageCount = data.proSetting!!.imageCount.toString()
            }
            if (data.proSetting!!.videoStatus==1 &&data.proSetting!!.videoCount > 0) {
                mBinding.videoVisable = true
            }
            var requiredItems: List<String> = data.proSetting!!.requiredItem!!.split(",")
            flag1@ for (item in requiredItems) {
                item?.let { type ->
                    when (type) {
                        VoteConstant.RequiedItem.PRO_NAME -> {
                            mBinding.nameVisable = true
                        }
                        VoteConstant.RequiedItem.PRO_AUTHOR -> {
                            mBinding.authorVisable = true
                        }
                        VoteConstant.RequiedItem.PRO_ID_CARD -> {
                            mBinding.idCardVisable = true
                        }
                        VoteConstant.RequiedItem.PRO_INTRO -> {
                            mBinding.infoVisable = true
                        }
                        VoteConstant.RequiedItem.PRO_PHONE -> {
                            mBinding.contactVisable = true
                        }
                    }
                }
            }
        }
    }

    private fun initStatus(data: VoteWorkDetailBean) {
        if (data.auditInfo != null) {
            //审核状态 待审核(4) 审核通过(6) 回退/驳回(7) 撤回(8) 终止(9) 审核不通过(79)
            when (data.auditInfo!!.auditStatus) {
                4 -> {
                    mBinding.tvVoteStatus.text = resources.getString(
                        R.string.vote_status_str,
                        resources.getString(R.string.vote_status_wait_pass)
                    )
                }
                7, 9, 79 -> {
                    mBinding.tvVoteStatus.text = resources.getString(
                        R.string.vote_status_str,
                        resources.getString(R.string.vote_status_un_pass)
                    )
                }
                8 -> {
                    mBinding.tvVoteStatus.text = resources.getString(
                        R.string.vote_status_str,
                        resources.getString(R.string.vote_status_back)
                    )
                }
            }
        }
    }

    override fun initData() {
        mModel.getVoteDetail(voteId ?: "")
        if (mode == 1) {
            mModel.getVoteTypes(voteId ?: "")
            if (AppUtils.isLogin()) {
                mModel.getVipInfo()
            }
            mBinding.tvVoteStatus.text = "当前状态:未参与"
            mBinding.vVoteStatus.visibility = View.VISIBLE
        } else {
            // 编辑模式 必须回传作品id
            mBinding.vVoteStatus.visibility = View.VISIBLE
            mModel.getVoteWorkDetail(proId ?: "")
        }

    }

    private fun showPopVoteRuleWindow() {
        if (voteDetailBean != null) {
            if (popVoteRuleWindow == null) {
                popVoteRuleWindow = PopVoteRuleWindow(this@VoteInPartActivity)
            }
            if (!popVoteRuleWindow!!.isShowing) {
                popVoteRuleWindow?.updateData(voteDetailBean!!)
                popVoteRuleWindow?.showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constrant.ADD_IMAGE) {
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                mBinding.uvVoteInPartImages.onActivityResult(list)
            }
        } else if (requestCode == Constrant.ADD_VIDEO) {
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                if (list.size > 0) {
                    mBinding.uvVoteInPartVideo.addVideo(list[0].apply {
                        type = 1
                    })
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}
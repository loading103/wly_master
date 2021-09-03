package com.daqsoft.provider.businessview.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.businessview.adapter.EmoticonsEditBigAdapter
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.event.UpdateOrderCommentStatus
import com.daqsoft.provider.businessview.fragment.ProviderAddEmoticonsFragment
import com.daqsoft.provider.businessview.viewmodel.ProviderPosCommentViewModel
import com.daqsoft.provider.databinding.ProviderActivityPostCommentBinding
import com.daqsoft.provider.network.comment.beans.CommtentTagBean
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.SoftHideKeyBoardUtil
import com.daqsoft.provider.view.LabelsView
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

/**
 * @Description 发表评论
 * @ClassName   ProviderPosCommentActivity
 * @Author      luoyi
 * @Time        2020/4/13 11:00
 */
@Route(path = ARouterPath.Provider.PROVIDER_POST_COMMENT)
class ProviderPosCommentActivity :
    TitleBarActivity<ProviderActivityPostCommentBinding, ProviderPosCommentViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var contentTitle: String = "" // 内容标题

    @JvmField
    @Autowired
    var type: String = "" // 类型

    @JvmField
    @Autowired
    var orderId: String = "" // 订单id

    // 选中的好评id
    var tagGoodIds: MutableList<Int> = mutableListOf()

    // 好评tags
    var goodTags: MutableList<CommtentTagBean> = mutableListOf()

    // 差评tags
    var badTags: MutableList<CommtentTagBean> = mutableListOf()

    // 选中的差评id
    var tagBadIds: MutableList<Int> = mutableListOf()

    // 当前选择的标签 0 好评 1 差评 2 一般
    var current: Int = 0

    /**
     * 好评标签展示更多标识
     */
    var goodLabelMoreFlag = false

    /**
     * 差评标签展示更多标签
     */
    var badLabelMoreFlag = false


    var haveGoods = false

    var haveBads = false
    // 已选择的表情数目
    var selectEmoticonsDatas: MutableList<EmoticonsBean> = mutableListOf()

    /**
     * 表情包大图显示控件
     */
    val emoticonsEditBigAdapter: EmoticonsEditBigAdapter by lazy {
        EmoticonsEditBigAdapter().apply {
            onEmoticonsEditListener = object : EmoticonsEditBigAdapter.OnEmoticonsEditListener {
                override fun onDeleteAllEmoticons() {
                    mBinding.rvEmoticons.visibility = View.GONE
                }

                override fun onDeleteEmoticons(position: Int) {
                    if (position in selectEmoticonsDatas.indices) {
                        selectEmoticonsDatas.removeAt(position)
                    }
                }

            }
            emptyViewShow = false
        }
    }

    private val addEmoticonsFrag: ProviderAddEmoticonsFragment by lazy {
        ProviderAddEmoticonsFragment().apply {
            onSelectResultListener = object : ProviderAddEmoticonsFragment.OnSelectResultListener {
                override fun onResultEmoticons(datas: MutableList<EmoticonsBean>) {
                    selectEmoticonsDatas.clear()
                    emoticonsEditBigAdapter.clear()
                    if (datas.isNotEmpty()) {
                        selectEmoticonsDatas.addAll(datas)
                        emoticonsEditBigAdapter.add(selectEmoticonsDatas)
                        mBinding.rvEmoticons.visibility = View.VISIBLE
                    } else {
                        mBinding.rvEmoticons.visibility = View.GONE
                    }

                }

            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.provider_activity_post_comment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        SoftHideKeyBoardUtil.assistActivity(this)
    }

    override fun setTitle(): String = getTitleByType()

    override fun injectVm(): Class<ProviderPosCommentViewModel> {
        return ProviderPosCommentViewModel::class.java
    }

    override fun initView() {

        if (type == ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE) {
            mBinding.llCommentBtn.visibility = View.GONE
        }

        mBinding.countWords = "0/200"
        mBinding.rvComments.setPicNumber(9)
        mBinding.rvComments.setMaxVideoNumer(1)
        mBinding.rvComments.init(this, true, false)
        mBinding.tvTitle.text = contentTitle
//        mBinding.tvGood.isSelected = true
        mBinding.lvBadLabels.isNeedToastMaxTip = true
        mBinding.lvGoodLabels.isNeedToastMaxTip = true
        mBinding.rvEmoticons.run {
            layoutManager = GridLayoutManager(
                this@ProviderPosCommentActivity,
                5,
                GridLayoutManager.VERTICAL,
                false
            )
            adapter = emoticonsEditBigAdapter
        }
        mModel.countWord.observe(this, Observer {
            mBinding.countWords = it
        })
        mModel.emoticonsLd.observe(this, Observer {
            if(it==null||it?.datas.isNullOrEmpty()){
                mBinding.imgAddEmoticons.visibility=View.GONE
            }else{
                mBinding.imgAddEmoticons.visibility=View.VISIBLE
            }
        })
        mModel.commentTagsBeans.observe(this, Observer {
            if (it["0"] != null) {
                var item = it["0"]
//                mBinding.vProviderCommentLabesMore.visibility = View.GONE
                var tags: MutableList<String> = mutableListOf()
                if (item != null && !item.tagList.isNullOrEmpty()) {
                    goodTags = item.tagList
                    for (tag in item.tagList) {
                        tags.add("${tag.name}")
                    }
//                    mBinding.llGood.visibility = View.VISIBLE
                    haveGoods=true
                } else {
                    mBinding.tvLabelTitle.visibility = View.GONE
                    goodTags.clear()
//                    mBinding.llGood.visibility = View.GONE
                    haveGoods=false
                }
                mBinding.lvGoodLabels.setLabels(tags)
                tags.clear()
                item = it["1"]
                if (item != null && !item.tagList.isNullOrEmpty()) {
                    badTags = item.tagList
                    for (tag in item.tagList) {
                        tags.add("${tag.name}")
                    }
//                    mBinding.llBad.visibility = View.VISIBLE
                    haveBads=true
                } else {
//                    mBinding.llBad.visibility = View.GONE
                    haveBads=false
                }


                mBinding.lvBadLabels.setLabels(tags)
                mBinding.lvGoodLabels.maxLines = 2
                mBinding.lvBadLabels.maxLines = 2
                mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_down)
            }
        })

        mModel.addFinish.observe(this, Observer {
            if (it) {

                EventBus.getDefault().post(UpdateCommentEvent())
                if (current == 1) {//跳转到不满意界面
                    ARouter.getInstance()
                        .build(ARouterPath.Provider.PROVIDER_COMMENT_RESULT)
                        .withString("contentTitle", getTitleByType())
                        .navigation()
                }
                if (!orderId.isNullOrEmpty()) {
                    EventBus.getDefault().post(UpdateOrderCommentStatus().apply {
                        updateOrderId = orderId
                    })
                }

                finish()
            }
        })
    }

    override fun initData() {
        initEvent()
        if (!orderId.isNullOrEmpty()) {
            mBinding.tvLabel.text = "评价订单"
        }
        if (current == 0) {
            mBinding.tvLabelTitle.visibility = View.VISIBLE
        }
        mModel.getCommentTagCountInfo(id, type)
        mModel.getEmoticons()
    }

    private fun initEvent() {
        mBinding.etContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mModel.getWordCount(mBinding.etContent.text.toString())
            }
        })

        mBinding.imgAddEmoticons.onNoDoubleClick {

            showAddEmoticons()
        }
        mBinding.imgAddImages.onNoDoubleClick {
            mBinding.rvComments.showSelectImages()
        }
        mBinding.rlGood.isSelected = true
        mBinding.ivGood.isSelected = true
        mBinding.rlGood.onNoDoubleClick {
            mBinding.rlGood.isSelected = true
            mBinding.ivGood.isSelected = true
            mBinding.rlBad.isSelected = false
            mBinding.ivBad.isSelected = false
            mBinding.rlGeneral.isSelected = false
            mBinding.ivGeneral.isSelected = false

            mBinding.llLabels.visibility = View.VISIBLE
            mBinding.lvBadLabels.visibility = View.GONE
            if(haveGoods){
                mBinding.tvLabelTitle.visibility = View.VISIBLE
                mBinding.lvGoodLabels.visibility = View.VISIBLE
            } else{
                mBinding.tvLabelTitle.visibility = View.GONE
                mBinding.lvGoodLabels.visibility = View.GONE
            }
            mBinding.libraryTintedWideRatingbar.rating=5f
            mBinding.tvScore.text="5.0"
//            if (goodTags.size > 0) {
//                mBinding.vProviderCommentLabesMore.visibility = View.VISIBLE
//            } else {
//                mBinding.vProviderCommentLabesMore.visibility = View.GONE
//            }
            mBinding.vProviderCommentLabesMore.visibility = if (goodLabelMoreFlag) View.VISIBLE else View.GONE
            mBinding.tvLabelTitle.text = "好评理由"
            current = 0
        }

        mBinding.rlBad.onNoDoubleClick {
            mBinding.rlGood.isSelected = false
            mBinding.ivGood.isSelected = false
            mBinding.rlBad.isSelected = true
            mBinding.ivBad.isSelected = true
            mBinding.rlGeneral.isSelected = false
            mBinding.ivGeneral.isSelected = false

            mBinding.llLabels.visibility = View.VISIBLE
            mBinding.lvGoodLabels.visibility = View.GONE
            if(haveBads){
                mBinding.tvLabelTitle.visibility = View.VISIBLE
                mBinding.lvBadLabels.visibility = View.VISIBLE
            }
            else{
                mBinding.tvLabelTitle.visibility = View.GONE
                mBinding.lvBadLabels.visibility = View.GONE
            }
//            if (badTags.size > 0) {
//                mBinding.vProviderCommentLabesMore.visibility = View.VISIBLE
//            } else {
//                mBinding.vProviderCommentLabesMore.visibility = View.GONE
//            }
            mBinding.libraryTintedWideRatingbar.rating=2f
            mBinding.tvScore.text="2.0"
            mBinding.vProviderCommentLabesMore.visibility = if (badLabelMoreFlag) View.VISIBLE else View.GONE
            mBinding.tvLabelTitle.text = "还需改善"
            current = 1

        }

        mBinding.rlGeneral.onNoDoubleClick {
            mBinding.rlGood.isSelected = false
            mBinding.ivGood.isSelected = false
            mBinding.rlBad.isSelected = false
            mBinding.ivBad.isSelected = false
            mBinding.rlGeneral.isSelected = true
            mBinding.ivGeneral.isSelected = true

            mBinding.lvBadLabels.visibility = View.GONE
            mBinding.lvGoodLabels.visibility = View.GONE
            mBinding.tvLabelTitle.visibility = View.GONE
            mBinding.vProviderCommentLabesMore.visibility = View.GONE
            mBinding.llLabels.visibility = View.GONE
            mBinding.libraryTintedWideRatingbar.rating=4f
            mBinding.tvScore.text="4.0"
            current = 2
        }
        mBinding.libraryTintedWideRatingbar.setOnRatingChangeListener { ratingBar, rating ->

            if(rating<=2.0){
                mBinding.rlBad.performClick();
                mBinding.libraryTintedWideRatingbar.rating=rating
                mBinding.tvScore.text=rating.toString()
                return@setOnRatingChangeListener
            }
            if(rating<=4.0){
                mBinding.rlGeneral.performClick();
                mBinding.libraryTintedWideRatingbar.rating=rating
                mBinding.tvScore.text=rating.toString()
                return@setOnRatingChangeListener
            }
            mBinding.rlGood.performClick()
            mBinding.libraryTintedWideRatingbar.rating=rating
            mBinding.tvScore.text=rating.toString()
        }
        mBinding.addCommentBtn.onNoDoubleClick {
            val content = mBinding.etContent.text.toString()
            if (content.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入评论内容!")
            } else {
                mModel.images = mBinding.rvComments.path
                when (current) {
                    0 -> {
                        mModel.tagIds = tagGoodIds.joinToString(",")
                    }
                    1 -> {
                        mModel.tagIds = tagBadIds.joinToString { "," }
                    }
                    else -> {
                        mModel.tagIds = ""
                    }
                }
                if (!mModel.images.isNullOrEmpty()) {
                    var imags = mModel.images.split(",")
                    var isHadUnUploadFile = false
                    for (item in imags) {
                        if (!item.isNullOrEmpty() && !item.startsWith("http")) {
                            isHadUnUploadFile = true
                            break
                        }
                    }
                    if (isHadUnUploadFile) {
                        ToastUtils.showMessage("您还有未上传完成的图片或者视频，请等待上传完成~")
                        return@onNoDoubleClick
                    }
                }
                var emoticonsIds: String = if (selectEmoticonsDatas.isNotEmpty()) {
                    var emoticons: MutableList<String> = mutableListOf()
                    for (i in selectEmoticonsDatas.indices) {
                        emoticons.add(selectEmoticonsDatas[i].id.toString())
                    }
                    DividerTextUtils.convertMuCommaString(emoticons)
                } else {
                    ""
                }
                var  star=mBinding.tvScore.text.substring(0,mBinding.tvScore.text.indexOf("."))
                mModel.addComment(id, type, content, current.toString(), orderId, emoticonsIds,star)
            }
        }
        mBinding.lvGoodLabels.setOnLabelSelectChangeListener { label, data, isSelect, position ->
            if (isSelect) {
                label?.setTextColor(resources.getColor(R.color.color_36cd64))
                label?.setBackgroundResource(R.drawable.shape_provider_comment_tag_selected)
                if (current == 0) {
                    tagGoodIds.add(position)
                }
            } else {
                label?.setTextColor(resources.getColor(R.color.color_333))
                label?.setBackgroundResource(R.drawable.shape_provider_comment_tag_normal)
                if (current == 0) {
                    tagGoodIds.remove(position)
                }
            }
        }

        mBinding.lvGoodLabels.setmOnLabelShowMoreListener {
            mBinding.vProviderCommentLabesMore.visibility = View.VISIBLE
            goodLabelMoreFlag = true
        }

        mBinding.lvBadLabels.setOnLabelSelectChangeListener { label, data, isSelect, position ->
            if (isSelect) {
                label?.setTextColor(resources.getColor(R.color.color_36cd64))
                label?.setBackgroundResource(R.drawable.shape_provider_comment_tag_selected)
                if (current == 1) {
                    tagBadIds.add(position)
                }
            } else {
                label?.setTextColor(resources.getColor(R.color.color_333))
                label?.setBackgroundResource(R.drawable.shape_provider_comment_tag_normal)
                if (current == 1) {
                    tagBadIds.remove(position)
                }
            }
        }

        mBinding.lvBadLabels.setmOnLabelShowMoreListener {
            mBinding.vProviderCommentLabesMore.visibility = View.VISIBLE
            badLabelMoreFlag = true
        }

        mBinding.vProviderCommentLabesMore.onNoDoubleClick {
            // 显示更多标签
            if (current == 0) {
                if (mBinding.lvGoodLabels.maxLines == -1) {
                    mBinding.lvGoodLabels.maxLines = 2
                    mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_down)
                } else {
                    mBinding.lvGoodLabels.maxLines = -1
                    mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_up)
                }
            } else {
                if (mBinding.lvBadLabels.maxLines == -1) {
                    mBinding.lvBadLabels.maxLines = 2
                    mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_down)
                } else {
                    mBinding.lvBadLabels.maxLines = -1
                    mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_up)
                }
            }
        }
    }

    /**
     * 根据上一个页面传来的类型确定标题
     */
    private fun getTitleByType() = when (type) {
//        ResourceType.CONTENT_TYPE_SCENERY -> "景区点评"
//        ResourceType.CONTENT_TYPE_HOTEL -> "酒店点评"
//        ResourceType.CONTENT_TYPE_RESTAURANT -> "美食点评"
//        ResourceType.CONTENT_TYPE_HERITAGE_ITEM -> "非遗点评"
//        ResourceType.CONTENT_TYPE_COUNTRY -> "全部点评"
        else -> "写评论"
    }

    private fun showAddEmoticons() {
        addEmoticonsFrag.selectEmoticons.clear()
        addEmoticonsFrag.selectEmoticons.addAll(selectEmoticonsDatas)
        if (!addEmoticonsFrag.isAdded) {
            addEmoticonsFrag.show(supportFragmentManager, "add_emoticons_frag")
        }
    }

    /**
     * 获取选中tag的id
     */
    private fun getTagIds(): String {
        var temp: MutableList<CommtentTagBean> = mutableListOf()
        var tempIds: MutableList<Int> = mutableListOf()
        if (current == 0) {
            temp = goodTags
            tempIds = tagGoodIds
        } else {
            temp = badTags
            tempIds = tagBadIds
        }
        var ids = ""

        if (tempIds.size == 0) {
            return ids
        }
        for (item in tempIds) {
            ids += "${temp[item].id},"
        }
        return ids.substring(0, ids.length - 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constrant.ADD_IMAGE) {
            // 選擇图片视频上传
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                mBinding.rvComments.onActivityResult(list)
            }
//                }
        } else if (requestCode == Constrant.ADD_VIDEO) {
            // 選擇图片视频上传
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                if (list.size > 0) {
                    mBinding.rvComments.insertAtFirst(list[0].apply {
                        type = 1
                    })
                }
            }
        }
    }
}
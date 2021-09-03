package com.daqsoft.provider.businessview.ui

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import com.daqsoft.provider.businessview.event.UpdateOrderCommentStatus
import com.daqsoft.provider.businessview.fragment.ProviderAddEmoticonsFragment
import com.daqsoft.provider.businessview.viewmodel.OrderCommentViewModel
import com.daqsoft.provider.databinding.ActivityOrderCommentBinding
import com.daqsoft.provider.network.comment.beans.CommtentTagBean
import com.daqsoft.provider.utils.DividerTextUtils
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import org.greenrobot.eventbus.EventBus

/**
 * @Description 评论订单
 * @ClassName   OrderCommentActiivty
 * @Author      luoyi
 * @Time        2020/7/21 9:36
 */
@Route(path = ARouterPath.Provider.PROVIDER_ORDER_COM_ACTIVITY)
class OrderCommentActivity :
    TitleBarActivity<ActivityOrderCommentBinding, OrderCommentViewModel>() {

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

    // 差评tags
    var badTags: MutableList<CommtentTagBean> = mutableListOf()

    // 选中的差评id
    var tagBadIds: MutableList<String> = mutableListOf()

    // 当前选择的标签 0 好评 1 差评 2 一般
    var current: Int = 0

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
        return R.layout.activity_order_comment
    }

    override fun setTitle(): String {
        return "写评论"
    }

    override fun injectVm(): Class<OrderCommentViewModel> {
        return OrderCommentViewModel::class.java
    }

    override fun initView() {
        if (!contentTitle.isNullOrEmpty()) {
            mBinding.tvTitle.text = contentTitle
            mBinding.tvTitle.visibility = View.VISIBLE
        } else {
            mBinding.tvTitle.visibility = View.GONE
        }
        mBinding.countWords = "0/200"
        mBinding.rvComments.setPicNumber(9)
        mBinding.rvComments.setMaxVideoNumer(1)
        mBinding.rvComments.init(this, true, false)
        mBinding.rvEmoticons.run {
            layoutManager = GridLayoutManager(
                this@OrderCommentActivity,
                5,
                GridLayoutManager.VERTICAL,
                false
            )
            adapter = emoticonsEditBigAdapter
        }
        mBinding.imgAddEmoticons.onNoDoubleClick {

            showAddEmoticons()
        }
        mBinding.imgAddImages.onNoDoubleClick {
            mBinding.rvComments.showSelectImages()
        }

        mModel.countWord.observe(this, Observer {
            mBinding.countWords = it
        })
        mModel.commentTagsBeans.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llLabels.visibility = View.VISIBLE
                mBinding.vProviderCommentLabesMore.visibility = View.GONE
                var tags: MutableList<String> = mutableListOf()
                tags.clear()
                for (tag in it) {
                    tags.add("${tag.name}")
                }
                if (tags.size > 5) {
                    mBinding.tvLabelTip.visibility = View.VISIBLE
                }
                badTags.clear()
                badTags.addAll(it)

                mBinding.lvBadLabels.setLabels(tags)
                mBinding.lvBadLabels.maxLines = 2
                mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_down)
            } else {
                mBinding.llLabels.visibility = View.GONE
            }
        })

        mModel.addFinish.observe(this, Observer {
            dissMissLoadingDialog()
            if (it) {
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
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()

        })

        mModel.emoticonsLd.observe(this, Observer {
            if (it == null || it?.datas.isNullOrEmpty()) {
                mBinding.imgAddEmoticons.visibility = View.GONE
            } else {
                mBinding.imgAddEmoticons.visibility = View.VISIBLE
            }
        })
    }

    override fun initData() {
        initEvent()
        if (!orderId.isNullOrEmpty()) {
            mBinding.tvLabel.text = "评价订单"
        } else {
            mBinding.tvLabel.text = "评价内容"
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



        mBinding.addCommentBtn.onNoDoubleClick {
            val content = mBinding.etContent.text.toString()
            if (content.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入评论内容!")
            } else {
                mModel.images = mBinding.rvComments.path
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
                mModel.tagIds = getTagIds()
                showLoadingDialog()
                var emoticonsIds: String = if (selectEmoticonsDatas.isNotEmpty()) {
                    var emoticons: MutableList<String> = mutableListOf()
                    for (i in selectEmoticonsDatas.indices) {
                        emoticons.add(selectEmoticonsDatas[i].id.toString())
                    }
                    DividerTextUtils.convertMuCommaString(emoticons)
                } else {
                    ""
                }
                mModel.addComment(id, type, content, current.toString(), orderId, emoticonsIds)
            }
        }

        mBinding.lvBadLabels.setOnLabelSelectChangeListener { label, data, isSelect, position ->
            if (isSelect) {
                label?.setTextColor(resources.getColor(R.color.color_36cd64))
                label?.setBackgroundResource(R.drawable.shape_provider_comment_tag_selected)
                if (current == 0) {
                    if (position in 0 until badTags.size) {
                        var bad = badTags[position]
                        tagBadIds.add("${bad.id}")
                    }

                }
            } else {
                label?.setTextColor(resources.getColor(R.color.color_333))
                label?.setBackgroundResource(R.drawable.shape_provider_comment_tag_normal)
                if (current == 0) {
                    if (position in 0 until badTags.size) {
                        tagBadIds.remove("${badTags[position].id}")
                    }
                }
            }
        }

        mBinding.lvBadLabels.setmOnLabelShowMoreListener {
            mBinding.vProviderCommentLabesMore.visibility = View.VISIBLE
        }

        mBinding.vProviderCommentLabesMore.onNoDoubleClick {
            // 显示更多标签
            if (mBinding.lvBadLabels.maxLines == -1) {
                mBinding.lvBadLabels.maxLines = 2
                mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_down)
            } else {
                mBinding.lvBadLabels.maxLines = -1
                mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_up)
            }
        }
    }

    /**
     * 根据上一个页面传来的类型确定标题
     */
    private fun getTitleByType() = when (type) {
        ResourceType.CONTENT_TYPE_SCENERY -> "景区点评"
        ResourceType.CONTENT_TYPE_HOTEL -> "酒店点评"
        ResourceType.CONTENT_TYPE_RESTAURANT -> "美食点评"
        ResourceType.CONTENT_TYPE_HERITAGE_ITEM -> "非遗点评"
        else -> "其他点评"
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
        var temp: MutableList<String> = mutableListOf()
        temp.clear()
        temp.addAll(tagBadIds)
        var ids = ""

        if (temp.size == 0) {
            return ids
        }
        return temp.joinToString(",")
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
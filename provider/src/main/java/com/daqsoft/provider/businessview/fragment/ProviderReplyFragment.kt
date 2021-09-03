package com.daqsoft.provider.businessview.fragment

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.businessview.adapter.EmoticonSelectAdapter
import com.daqsoft.provider.businessview.adapter.EmoticonsAllAdapter
import com.daqsoft.provider.businessview.viewmodel.ProviderAddCommentLsViewModel
import com.daqsoft.provider.databinding.FragAddCommentPopBinding
import com.daqsoft.provider.utils.DividerTextUtils

/**
 * @Description
 * @ClassName   ProviderAddEmoticonsFragment
 * @Author      luoyi
 * @Time        2020/11/2 10:35
 */
class ProviderReplyFragment :
    DialogFragment() {

    /**
     * 所有表情适配器
     */
    private var emoticonsAllAdpater: EmoticonsAllAdapter? = null

    /**
     * 最大可选表情包数目
     */
    var maxCanSelectEmoticon: Int = 10

    /**
     * 已选择的表情
     */
    private var emoticonsSelectAdapter: EmoticonSelectAdapter? = null

    private val pageDealUtil: PageDealUtils by lazy {
        PageDealUtils()
    }

    private lateinit var mBinding: FragAddCommentPopBinding

    private lateinit var mModel: ProviderAddCommentLsViewModel

    public var commentId: String? = ""
    public var pid: String? = ""

    /**
     * 已选择的表情
     */
    var selectEmoticons: MutableList<EmoticonsBean> = mutableListOf()


    companion object {
        const val RESOURCE_ID = "resource_id"
        const val RESOURCE_TYPE = "resource_type"
        fun newInstance(commentId: String, pid: String): ProviderReplyFragment {
            var frag = ProviderReplyFragment()
            var bundle: Bundle = Bundle()
            bundle.putString(RESOURCE_ID, commentId)
            bundle.putString(RESOURCE_TYPE, pid)
            frag.arguments = bundle
            return frag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.frag_add_comment_pop, container, false)
        mModel = ProviderAddCommentLsViewModel()
        initView()
        initUiData()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        commentId = arguments?.getString(RESOURCE_ID, "")
        pid = arguments?.getString(RESOURCE_TYPE, "")
    }

    fun initView() {

        mModel.emoticonsLd.observe(this, Observer
        {
            if (it != null) {
                pageDealUtil.pageDeal(mModel.currPage, it, emoticonsAllAdpater!!)
                if (it.datas != null && !it.datas.isNullOrEmpty()) {
                    emoticonsAllAdpater?.add(it.datas!!)
                }
            } else {
                if (mModel.currPage == 1) {
//                    ToastUtils.showMessage("未配置表情包数据，请稍后再试~")
                }
            }
        })
        mModel.dismissPop.observe(this, Observer {
            if (it) {
                ToastUtils.showMessage("回复成功")
                onReplySuccessLisitener?.onReplysuccess()
                dismiss()
            } else {
                ToastUtils.showMessage("评论提交失败，请稍后再试~")
            }
        })
        mBinding.vOutsideContent.onNoDoubleClick {
            dismiss()
        }
    }

    fun initData() {
        mModel.currPage = 1
        mModel.getEmoticons()
    }


    fun initUiData() {
        mBinding?.btnSend.onNoDoubleClick {
            var content: String? = mBinding.etContent.text.toString()
            if (content.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入评论内容!")
            } else {
                var temps: ArrayList<String> = ArrayList()
                if (emoticonsSelectAdapter!!.getData().isNotEmpty()) {
                    for (item in emoticonsSelectAdapter!!.getData()) {
                        temps.add(item.id.toString())
                    }
                }
                var emoticonIds: String? = DividerTextUtils.convertCommaString(temps)

                mModel.publishReplyComment(
                    commentId ?: "",
                    pid ?: "",
                    content!!,
                    temps
                )
            }
        }
        emoticonsSelectAdapter = EmoticonSelectAdapter().apply {
            onEmoticonSelectListener = object : EmoticonSelectAdapter.OnEmoticonSelectListener {
                override fun ondeleteAllData() {
                    mBinding.rvSelectEmoticons.visibility = View.GONE
                    mBinding.vLineEmoticons.visibility = View.GONE
                }
            }
            emptyViewShow = false
        }
        if (selectEmoticons.isNotEmpty()) {
            mBinding.rvSelectEmoticons.visibility = View.VISIBLE
            mBinding.vLineEmoticons.visibility = View.VISIBLE
            emoticonsSelectAdapter?.clear()
            emoticonsSelectAdapter?.add(selectEmoticons)
        }
        mBinding.rvSelectEmoticons.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvSelectEmoticons.adapter = emoticonsSelectAdapter

        mBinding.rvAllEmoticons.layoutManager =
            GridLayoutManager(context, 5, GridLayoutManager.VERTICAL, false)
        emoticonsAllAdpater = EmoticonsAllAdapter().apply {
            onEmotionItemListener = object : EmoticonsAllAdapter.OnEmotionItemListener {
                override fun onItemClick(bean: EmoticonsBean) {
                    if (emoticonsSelectAdapter != null && emoticonsSelectAdapter!!.getData().size < maxCanSelectEmoticon) {
                        if (!mBinding.rvSelectEmoticons.isVisible) {
                            mBinding.rvSelectEmoticons.visibility = View.VISIBLE
                            mBinding.vLineEmoticons.visibility = View.VISIBLE
                        }
                        emoticonsSelectAdapter?.addItem(bean)
                    } else {
                        ToastUtils.showMessage("非常抱歉，最多选择${maxCanSelectEmoticon}表情~")
                    }
                }
            }
            emptyViewShow = false
            setOnLoadMoreListener {
                mModel.currPage = mModel.currPage + 1
                mModel.getEmoticons()
            }
        }
        mBinding.rvAllEmoticons.adapter = emoticonsAllAdpater
    }

    private fun initDialogWindow() {
        val win = dialog!!.window
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val params = win.attributes
        params.gravity = Gravity.BOTTOM
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win.attributes = params
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onStart() {
        super.onStart()
        mBinding?.etContent?.setText("")
        initDialogWindow()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

    interface OnReplySuccessLisitener {
        fun  onReplysuccess()
    }

    var onReplySuccessLisitener: OnReplySuccessLisitener? = null
}
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
import com.daqsoft.baselib.base.BaseDialogFragment
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.businessview.adapter.EmoticonSelectAdapter
import com.daqsoft.provider.businessview.adapter.EmoticonsAllAdapter
import com.daqsoft.provider.businessview.viewmodel.ProviderAddEmoticonsLsViewModel
import com.daqsoft.provider.databinding.FragAddEmoticonsListBinding

/**
 * @Description
 * @ClassName   ProviderAddEmoticonsFragment
 * @Author      luoyi
 * @Time        2020/11/2 10:35
 */
class ProviderAddEmoticonsFragment :
    DialogFragment() {


    var onSelectResultListener: OnSelectResultListener? = null

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

    private lateinit var mBinding: FragAddEmoticonsListBinding

    private lateinit var mModel: ProviderAddEmoticonsLsViewModel

    /**
     * 已选择的表情
     */
    var selectEmoticons: MutableList<EmoticonsBean> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.frag_add_emoticons_list, container, false)
        mModel = ProviderAddEmoticonsLsViewModel()
        initView()
        initUiData()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
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
                    ToastUtils.showMessage("未配置表情包数据，请稍后再试~")
                    emoticonsAllAdpater?.emptyViewShow = true
                }
            }
        })
        mBinding.tvAddEmoticons.onNoDoubleClick {
            if (emoticonsSelectAdapter != null) {
                onSelectResultListener?.onResultEmoticons(emoticonsSelectAdapter!!.getData())
            }
            dismiss()
        }
        mBinding.vOutsideContent.onNoDoubleClick {
            dismiss()
        }
    }

    fun initData() {
        mModel.currPage = 1
        mModel.getEmoticons()
    }


    fun initUiData() {
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
        initDialogWindow()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

    interface OnSelectResultListener {
        fun onResultEmoticons(datas: MutableList<EmoticonsBean>)
    }
}
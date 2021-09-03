package com.dqsoft.votemodule.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.VoteDetailBean
import com.daqsoft.provider.bean.VoteWrokMinInfo
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.BaseImageHolder
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.dqsoft.votemodule.R
import com.dqsoft.votemodule.databinding.PopVoteTipRuleInfoBinding
import me.nereo.multi_image_selector.BigImgActivity

/**
 * @Description 投票规则
 * @ClassName   PopVoteRuleWindow
 * @Author      luoyi
 * @Time        2020/11/12 10:47
 */
class PopVoteRuleWindow : PopupWindow {

    var mBinding: PopVoteTipRuleInfoBinding? = null
    private var context: Context? = null
    private var mViewAppointment: View? = null

    var data: VoteDetailBean? = null

    constructor(context: Context) : super(context) {
        this.context = context
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.pop_vote_tip_rule_info,
            null,
            false
        )
        mViewAppointment = mBinding!!.root
        initView()
        initPopWindow()
    }

    private fun initPopWindow() {
        contentView = mViewAppointment
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体的高
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗体可点击()
        this.isFocusable = true;
        this.isOutsideTouchable = true;
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x00FFFFFF);

        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    private fun initView() {
        mBinding?.imgClosePop?.onNoDoubleClick {
            dismiss()
        }
    }

    public fun updateData(bean: VoteDetailBean) {
        this.data = bean
        initCover()
        mBinding?.data = bean
        if (!bean.voteRule.isNullOrEmpty()) {
            mBinding?.tvVoteRule?.settings?.defaultTextEncodingName = "utf-8"
            mBinding?.tvVoteRule?.settings?.javaScriptEnabled = true
            mBinding?.tvVoteRule?.loadDataWithBaseURL(
                null, StringUtil.getHtml((bean.voteRule!!)),
                "text/html", "utf-8", null
            )
        }
    }

    fun updateData(bean: VoteWrokMinInfo) {
        var voteInfoBean: VoteDetailBean = VoteDetailBean().apply {
            voteRule = bean.voteRule
            voteStatus = bean.voteStatus
            teachUnit = bean.teachUnit
            mainUnit = bean.mainUnit
            jointlyUnit = bean.jointlyUnit
            undertakeUnit = bean.undertakeUnit
            mainImages = bean.mainImages
        }
        this.data = voteInfoBean
        initCover()
        mBinding?.data = voteInfoBean
        if (!voteInfoBean.voteRule.isNullOrEmpty()) {
            mBinding?.tvVoteRule?.settings?.defaultTextEncodingName = "utf-8"
            mBinding?.tvVoteRule?.settings?.javaScriptEnabled = true
            mBinding?.tvVoteRule?.loadDataWithBaseURL(
                null, StringUtil.getHtml((voteInfoBean.voteRule!!)),
                "text/html", "utf-8", null
            )
        }
    }

    private fun initCover() {
        var coverImage = data?.mainImages
        var images: MutableList<String?> = mutableListOf()
        if (!coverImage.isNullOrEmpty()) {
            images.addAll(coverImage.split(","))
        } else {
            images.add(coverImage)
        }
        mBinding?.bannerTopAdv?.setPages(
            object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return BaseImageHolder(itemView!!)
                }

                override fun getLayoutId(): Int {
                    return R.layout.item_provider_holder_banner_img
                }
            }, images
        )?.setCanLoop(images.size > 1)
            ?.setPointViewVisible(images.size > 1)
            ?.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
            ?.setOnItemClickListener {
                // 跳转事件
                val intent =
                    Intent(mBinding?.root?.context, BigImgActivity::class.java)
                intent.putExtra("position", 0)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(images)
                )
                mBinding?.root?.context?.startActivity(intent)
            }
            ?.setPageIndicator(null)
    }
}
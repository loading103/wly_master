package com.daqsoft.guidemodule.fragment

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.guidemodule.GuideResType
import com.daqsoft.guidemodule.R
import com.daqsoft.guidemodule.bean.GuideLineBean
import com.daqsoft.guidemodule.databinding.GuideFragmentGuideVpLineBinding
import com.daqsoft.guidemodule.databinding.GuideItemRvLineLevelOneBinding
import com.daqsoft.guidemodule.databinding.GuideItemRvLineLevelTwoBinding
import com.daqsoft.guidemodule.net.logI
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder
import java.text.DecimalFormat
import kotlin.math.log


/**
 *
 * @Description
 * @ClassName   GuideLineFragment
 * @Author      Wongxd
 * @Time        2020/4/9 16:28
 */
internal class GuideLineFragment(
    private val eventTag: String,
    private val lineList: List<GuideLineBean>,
    private val isAllArea: Boolean = false
) : BaseFragment<GuideFragmentGuideVpLineBinding, GuideLineViewModel>() {

    init {
        pointDes = if (isAllArea) "景区" else "景点"
    }


    /**
     * 切换线路
     */
    internal data class GuideLineChangeWayEvent(val tag: String, val pos: Int)

    /**
     * 点击了线路中的具体景点
     */
    internal data class GuideLineClickSpotEvent(val tag: String, val spotIndex: Int, val data: GuideLineBean.Detail)

    /**
     * 线路预览
     */
    internal data class GuideLinePreviewEvent(val tag: String, val tourId: String, val pos: Int)

    /**
     * 线路-开始导览
     */
    internal data class GuideLineTourEvent(val tag: String, val tourId: String, val pos: Int)

    override fun getLayout(): Int = R.layout.guide_fragment_guide_vp_line

    override fun injectVm(): Class<GuideLineViewModel> = GuideLineViewModel::class.java

    override fun initData() {

    }


    private var mCurrentLinePos = 0

    private val mCurrentData: GuideLineBean
        get() = lineList[mCurrentLinePos].apply { isChecked = true }


    private val levelOneAdapter by lazy {
        GuideLienRvLevelOneAdapter() { position, item ->
            mCurrentLinePos = position
            EventBus.getDefault().post(GuideLineChangeWayEvent(eventTag, mCurrentLinePos))
            renderUI()
        }
    }


    private val levelTwoAdapter by lazy {
        GuideLienRvLevelTwoAdapter { pos, item ->
            EventBus.getDefault().post(GuideLineClickSpotEvent(eventTag, pos, item))
        }
    }


    private fun renderUI() {

        lineList.forEach { it.isChecked = false }

        val netDis = mCurrentData.totalDistance
        val disDistance = if (netDis > 1000) {
            val df = DecimalFormat("0.00")
            df.format(netDis / 1000) + "公里"
        } else {
            netDis.toInt().toString() + "米"
        }


        mBinding.tvSummary.text = "总路程：$disDistance，大约耗时：${getTime(mCurrentData.totalTime)}，共${mCurrentData.totalNums}个${pointDes}"

        levelTwoAdapter.clear()
        var temps = mCurrentData.details.filter {
            it.resourceType != GuideResType.CONTENT_TYPE_GUIDED_TOUR_ROUTE //剔除辅助点
        }.toMutableList()
        levelTwoAdapter.add(temps)
        if (temps.isNullOrEmpty()) {
            mBinding.llGuideVpLineOperation.visibility = View.GONE
        } else {
            mBinding.llGuideVpLineOperation.visibility = View.VISIBLE
        }
    }

    override fun initView() {

        mBinding.rvLineLevelOne.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = levelOneAdapter
        }

        levelOneAdapter.add(lineList.toMutableList())

        mBinding.rvLineLevelTwo.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = levelTwoAdapter
        }


        mBinding.llGuidePreview.setOnClickListener {
            EventBus.getDefault().post(GuideLinePreviewEvent(eventTag, mCurrentData.id.toString(), mCurrentLinePos))
        }


        mBinding.llStartTour.setOnClickListener {
            EventBus.getDefault().post(GuideLineTourEvent(eventTag, mCurrentData.id.toString(), mCurrentLinePos))
        }

        renderUI()
    }


}

private var pointDes = ""

internal class GuideLineViewModel : BaseViewModel()

internal class GuideLienRvLevelOneAdapter(val click: (position: Int, item: GuideLineBean) -> Unit) :
    RecyclerViewAdapter<GuideItemRvLineLevelOneBinding, GuideLineBean>
        (R.layout.guide_item_rv_line_level_one) {

    override fun setVariable(mBinding: GuideItemRvLineLevelOneBinding, position: Int, item: GuideLineBean) {
//        logI("GuideLienRvLevelOneAdapter  setVariable  position-${position},item.isChecked-${item.isChecked}")

        mBinding.data = item

        mBinding.tvName.text = item.name
        mBinding.tvName.setMarquee(item.isChecked)
        mBinding.tvName.isSelected = item.isChecked


        if (item.isChecked) {
            mBinding.ll.setBackgroundResource(R.drawable.guide_bg_line_green)
        } else {
            mBinding.ll.setBackgroundResource(R.drawable.guide_bg_line_f5)
        }

        mBinding.root.setOnClickListener {
            getData().forEach { it.isChecked = false }
            item.isChecked = true
            notifyDataSetChanged()
            click.invoke(position, item)
        }

    }
}


internal class GuideLienRvLevelTwoAdapter(val click: (Int, GuideLineBean.Detail) -> Unit) :
    RecyclerViewAdapter<GuideItemRvLineLevelTwoBinding, GuideLineBean.Detail>(R.layout.guide_item_rv_line_level_two) {

    override fun setVariable(mBinding: GuideItemRvLineLevelTwoBinding, position: Int, item: GuideLineBean.Detail) {

        mBinding.data = item

        mBinding.tvStep.text = "${position + 1}"

        val netDis = item.nextDistance
        val disDistance = if (netDis > 1000) {
            val df = DecimalFormat("0.00")
            df.format(netDis / 1000) + "公里"
        } else {
            netDis.toInt().toString() + "米"
        }
        mBinding.tvDistance.text = if (getData().lastIndex > position) "距下一${pointDes}:${disDistance}" else "线路最后${pointDes}"


        mBinding.tvTime.text = "建议游览时间：${getTime(item.playTime)}"


        mBinding.root.onNoDoubleClick {
            click.invoke(position, item)
        }

    }
}


/**
 * 根据分钟转化时间
 */
internal fun getTime(minuteOri: Long): String {
    var minute = minuteOri

    if (minute < 60) {
        return "${minute}分钟"
    }

    var hour = minute / 60
    minute -= (hour * 60)

    val sb = StringBuilder()
    sb.append("${hour}时")
    if (minute > 0) {
        sb.append("${minute}分钟")
    }

    if (hour < 24) {
        return sb.toString()
    }


    sb.clear()
    val day = (hour / 24).toInt()
    hour -= (day * 24)

    sb.append("${day}天")

    if (hour > 0) {
        sb.append("${hour}时")
    }
    if (minute > 0) {
        sb.append("${minute}分钟")
    }
    return sb.toString()
}




package com.daqsoft.travelCultureModule.hotActivity.orders

import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityOrderSeatSelectBinding
import com.daqsoft.mainmodule.databinding.MainItemSeatSelectBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.Seat
import com.daqsoft.provider.view.SectionView
import com.daqsoft.travelCultureModule.hotActivity.bean.SeatTemplateBean
import com.daqsoft.travelCultureModule.net.MainRepository
import org.jetbrains.anko.backgroundColor

/**
 * @des 志愿者报名活动
 * @author luoyi
 * @Date 2020/4/26 16:21
 * Todo 暂时未做：选择位置，选中后，返回传入默认的选中位置信息
 */

@Route(path = MainARouterPath.MAIN_SEAT_SELECT_ACTIVITY)
class SeatSelectActivity :
    TitleBarActivity<MainActivityOrderSeatSelectBinding, SeatSelectActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var name: String = ""

    @JvmField
    @Autowired
    var time: String = ""

    @JvmField
    @Autowired
    var maxSelect: Int = 1

    /**
     * 选择的位置id
     */
    val selectIds = mutableListOf<String>()
    /**
     * 选择的位置string
     */
    val selectSeatNames = mutableListOf<String>()
    /**
     * 座位适配器
     */
    val seatAdapter = object :
        RecyclerViewAdapter<MainItemSeatSelectBinding, Seat>(R.layout.main_item_seat_select) {
        override fun setVariable(mBinding: MainItemSeatSelectBinding, position: Int, item: Seat) {
            mBinding.name = "第" + item.realRow + "排" + item.realCol + "号"
            mBinding.root.onNoDoubleClick {
                removeItem(item)
                val seatIndex = ((item.row.toInt() - 1) * 10) + item.col.toInt()
                delSeat(seatIndex)
            }
        }

    }

    override fun getLayout(): Int = R.layout.main_activity_order_seat_select

    override fun setTitle(): String = getString(R.string.main_activity_order_title)

    override fun injectVm(): Class<SeatSelectActivityViewModel> =
        SeatSelectActivityViewModel::class.java

    override fun initView() {
//        mBinding.vm = mModel
        mBinding.activityName = name
        mBinding.date = time
        updateTvOder()
        // 需要获取活动室所有座位
        mModel.selectSeats.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.svSeat.selectSeats.clear()
                mBinding.svSeat.selectSeats.addAll(it)

                for (item in it) {
                    mBinding.svSeat.templateSeats[item.row.toInt() - 1][item.col.toInt() - 1].status =
                        "2"
                }
            }
        })
        // 获取座位模板
        mModel.seatTemplate.observe(this, Observer {

            var row = it.seatInfo.size
            var coloum = it.seatInfo[0].size
            mBinding.svSeat.setData(row, coloum, it.seatInfo)
            // 获取已选座位置
            mModel.getSelectedSeat(id)
            mBinding.svSeat.setSeatChecker(object : SectionView.SeatChecker {

                override fun isValidSeat(row: Int, column: Int): Boolean {
                    return mBinding.svSeat.templateSeats[row][column].status != "0"
                }

                override fun isSold(row: Int, column: Int): Boolean {
                    return mBinding.svSeat.templateSeats[row][column].status == "2"
                }

                override fun checked(row: Int, column: Int) {
                    val seat = mBinding.svSeat.templateSeats[row][column]
                    selectIds.add(seat.id)
                    selectSeatNames.add("${seat.realRow}排${seat.realCol}号")
                    seatAdapter.addItem(seat)
                    if (!mBinding.rvSelectSeat.isVisible) {
                        mBinding.rvSelectSeat.visibility = View.VISIBLE
                    }
                    updateTvOder()
                }

                override fun unCheck(row: Int, column: Int) {
                    val seat = mBinding.svSeat.templateSeats[row][column]
                    selectIds.remove(seat.id)
                    selectSeatNames.remove(seat.realRow + "排" + seat.realCol + "号")
                    seatAdapter.removeItem(seat)
                    if (seatAdapter.getData().isNullOrEmpty()) {
                        mBinding.rvSelectSeat.visibility = View.GONE
                    }
                    updateTvOder()
                }

                override fun checkedSeatTxt(row: Int, column: Int): Array<String>? {
                    return null
                }

            })

            if (it.sort == 0) {
                // 从左到右

            } else {
                // 从右到左


            }
        })

        // 设置屏幕名称
        mBinding.svSeat.setScreenName("舞台")
        // 设置最多选中
        mBinding.svSeat.setMaxSelected(maxSelect)

        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvSelectSeat.layoutManager = tagLayoutManager
        seatAdapter.emptyViewShow = false
        mBinding.rvSelectSeat.adapter = seatAdapter

    }

    private fun updateTvOder() {
        if (selectIds.isNullOrEmpty()) {
            mBinding.tvOrder.text = "请选择座位"
            mBinding.tvOrder.backgroundColor = resources.getColor(R.color.colorPrimary_un)
        } else {
            mBinding.tvOrder.text = "确定选座"
            mBinding.tvOrder.backgroundColor = resources.getColor(R.color.colorPrimary)
        }
    }

    override fun initData() {

        mModel.getSeatTemplate(id)
    }

    fun submitSeats(v: View) {
        try {
            var intent = Intent()
            var sb = StringBuilder()
            for (s in selectIds)
                sb.append(s).append(",")

            if (!sb.isNullOrEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            var sitSb = StringBuilder()
            for (s in selectSeatNames) {
                if (!s.isNullOrEmpty()) {
                    sitSb.append(s).append(",")
                }
            }
            if (!sitSb.isNullOrEmpty()) {
                sitSb.deleteCharAt(sitSb.lastIndexOf(","))
            }
            intent.putExtra("ids", sb.toString())
            intent.putExtra("sites", sitSb.toString())
            setResult(0, intent)
        } catch (e: Exception) {
        }

        finish()
    }

    fun delSeat(seatIndex: Int) {
        mBinding.svSeat.refreshed(seatIndex)
        if (seatAdapter.getData().isNullOrEmpty()) {
            mBinding.rvSelectSeat.visibility = View.GONE
        }
    }
}

/**
 * @des 座位选择的viewmodel
 * @author PuHua
 * @Date 2020/1/12 16:03
 */
class SeatSelectActivityViewModel : BaseViewModel() {
    /**
     * 已被选中的座位
     */
    val selectSeats = MutableLiveData<MutableList<Seat>>()
    /**
     * 座位模板
     */
    val seatTemplate = MutableLiveData<SeatTemplateBean>()

    /**
     * 获取已选择的座位
     */
    fun getSelectedSeat(activityId: String) {
        MainRepository.service.getSelectedSeat(activityId)
            .excute(object : BaseObserver<Seat>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Seat>) {
                    if (response.code == 0) {
                        selectSeats.postValue(response.datas)
                    }

                }
            })
    }

    /**
     * 获取座位模板
     */
    fun getSeatTemplate(activityId: String) {
        MainRepository.service.getSeatTemplate(activityId)
            .excute(object : BaseObserver<SeatTemplateBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<SeatTemplateBean>) {
                    if (response.code == 0) {
                        seatTemplate.postValue(response.data)
                    }

                }
            })
    }

}


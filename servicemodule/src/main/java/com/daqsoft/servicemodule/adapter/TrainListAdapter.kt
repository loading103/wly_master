package com.daqsoft.servicemodule.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ItemTrainListBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.bean.TrainListBean
import com.daqsoft.servicemodule.uitils.TextFontUtil.setTextSizeAndColor
import com.jakewharton.rxbinding2.view.RxView
import com.scwang.smart.refresh.layout.util.SmartUtil
import java.util.concurrent.TimeUnit

/**
 * desc :火车列表适配器
 * @author 江云仙
 * @date 2020/4/8 19:15
 */
class TrainListAdapter : RecyclerViewAdapter<ItemTrainListBinding, TrainListBean>(R.layout.item_train_list) {
    @SuppressLint("SetTextI18n", "CheckResult")
    override fun setVariable(mBinding: ItemTrainListBinding, position: Int, item: TrainListBean) {
        mBinding.tvStartTrainTime.text = item.deptTime
        mBinding.tvStartTrainName.text = item.deptStationName
        mBinding.tvEndTrainTime.text = item.arrTime
        mBinding.tvEndTrainName.text = item.arrStationName
        mBinding.tvTrainCode.text = item.trainCode
        mBinding.tvRunTime.text = item.runTime
        val priceStr = "¥" + item.minPrice + "起"
        val priceColor = setTextSizeAndColor(priceStr, SmartUtil.dp2px(20f), Color.parseColor("#ff9e05"), 1, priceStr.length - 1)
        mBinding.tvMinPrice.text = priceColor
        if (item.seatList.size > 0)
            mBinding.firstSeat.text = item.seatList[0].seatName + ":" + item.seatList[0].seatNum + "张"
        if (item.seatList.size > 1)
            mBinding.secondSeat.text = item.seatList[1].seatName + ":" + item.seatList[1].seatNum + "张"
        if (item.seatList.size > 2)
            mBinding.thirdSeat.text = item.seatList[2].seatName + ":" + item.seatList[2].seatNum + "张"
        if (item.seatList.size > 3)
            mBinding.fourSeat.text = item.seatList[3].seatName + ":" + item.seatList[3].seatNum + "张"
        if (item.seatList.size > 4)
            mBinding.fiveSeat.text = item.seatList[4].seatName + ":" + item.seatList[4].seatNum + "张"
        //适配器点击事件
        RxView.clicks(mBinding.root)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS).subscribe {
                //跳转到火车车次详情
                ARouter.getInstance()
                    .build(ARouterPath.ServiceModule.SERVICE_TRAIN_DETAIL_ACTIVITY)
                    .withString("trainCode", item.trainCode)
                    .withString("date", item.deptTime)
                    .withParcelable("trainListBean", item)
//                    .withString("end" ,item.arrStationCode)
                    .navigation()
            }
    }

}
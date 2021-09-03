package com.daqsoft.servicemodule.ui
import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityTrainDetailBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ItemTrainDetailHeaderBinding
import com.daqsoft.android.scenic.servicemodule.databinding.TrainDetailHeaderBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.provider.ARouterPath
import com.daqsoft.servicemodule.adapter.TrainDetailAdapter
import com.daqsoft.servicemodule.bean.SeatList
import com.daqsoft.servicemodule.bean.TrainListBean
import com.daqsoft.servicemodule.model.TrainDetailViewModel
/**
 * desc :火车车次详情
 * @author 江云仙
 * @date 2020/4/8 20:16
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_TRAIN_DETAIL_ACTIVITY)
class TrainDetailActivity : TitleBarActivity<ActivityTrainDetailBinding, TrainDetailViewModel>() {
    @JvmField
    @Autowired
    var trainCode = ""//车次code
    @JvmField
    @Autowired
    var date = ""//日期
    @JvmField
    @Autowired(name = "trainListBean")
    var trainListBean : TrainListBean?=null

    override fun getLayout(): Int {
        return R.layout.activity_train_detail
    }

    override fun setTitle(): String {
        return "车次详情"
    }

    override fun injectVm(): Class<TrainDetailViewModel> =TrainDetailViewModel::class.java

    override fun initView() {
    }

    override fun initData() {
        mBinding.recyTrainDetail.visibility=View.GONE
        mModel.time=date
        mModel.trainCode=trainCode
        mModel.trainListBean=trainListBean
        mModel.getStationLineInfo()
        setAdapter()
    }
    /**
     *设置适配器
     */
    private fun setAdapter() {
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyTrainDetail.layoutManager = tagLayoutManager
        mBinding.recyHeader.attachTo(mBinding.recyTrainDetail)
        val  trainDetailAdapter = TrainDetailAdapter()
        mBinding.recyTrainDetail.adapter=trainDetailAdapter
        mModel.result.observe(this, Observer {
            mBinding.recyTrainDetail.visibility = View.VISIBLE
            val data=it
            trainDetailAdapter.add(data.list)
            trainDetailAdapter.notifyDataSetChanged()
            setHeader(mBinding.trainDetailHeader)
        })


    }

    @SuppressLint("SetTextI18n")
    private fun setHeader(mBinding: TrainDetailHeaderBinding) {
        mBinding.tvStartTrainTime.text= trainListBean?.deptTime ?:""
        mBinding.tvStartTrainName.text= trainListBean?.deptStationName ?:""
        mBinding.tvEndTrainTime.text= trainListBean?.arrTime ?:""
        mBinding.tvEndTrainName.text= trainListBean?.arrStationName ?:""
        mBinding.tvTrainCode.text= trainListBean?.trainCode ?:""
        mBinding.tvRunTime.text= trainListBean?.runTime ?: ""
        mBinding.tvType.text= trainListBean?.trainCode+"时刻表"
        mBinding.recyRestTrain.apply {
            layoutManager = LinearLayoutManager(this@TrainDetailActivity)
            adapter = letterAdapter
        }
        trainListBean?.seatList?.let { letterAdapter.add(it) }
    }
    private val letterAdapter = object : RecyclerViewAdapter<ItemTrainDetailHeaderBinding, SeatList>(R.layout.item_train_detail_header) {
        @SuppressLint("CheckResult", "SetTextI18n")
        override fun setVariable(mBinding: ItemTrainDetailHeaderBinding, position: Int, item: SeatList) {
            mBinding.tvType.text = item.seatName
            mBinding.tvNum.text = item.seatNum
            mBinding.tvPrice.text = "￥"+item.seatPrice
        }
    }
}

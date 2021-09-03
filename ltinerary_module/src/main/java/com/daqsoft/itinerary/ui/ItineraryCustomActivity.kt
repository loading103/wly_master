package com.daqsoft.itinerary.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.col.sln3.it
import com.daqsoft.baselib.base.AppManager
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.ItineraryLabelBean
import com.daqsoft.itinerary.bean.PlayItems
import com.daqsoft.itinerary.databinding.ActivityItineraryCustomBinding
import com.daqsoft.provider.ItineraryRouter
import com.daqsoft.itinerary.ui.fragment.AddAimFragment
import com.daqsoft.itinerary.ui.fragment.ItinerarySettingFragment
import com.daqsoft.itinerary.ui.fragment.ScenicSettingFragment
import com.daqsoft.itinerary.ui.fragment.TrafficSettingFragment
import com.daqsoft.itinerary.util.ItineraryManager
import com.daqsoft.itinerary.vm.ItineraryCustomViewModel
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * @Author：      邓益千
 * @Create by：   2020/4/23 17:37
 * @Description：行程定制界面
 */
@Route(path = ItineraryRouter.ITINERARY_CUSTOM)
class ItineraryCustomActivity: TitleBarActivity<ActivityItineraryCustomBinding, ItineraryCustomViewModel>() {

    private var fragmentTag = ""
    private var nText = "下一步：选择景点"

    val customLabel = MutableLiveData<ItineraryLabelBean>()

    /**小时*/
    private var hours = 0

    /**当前步骤*/
    private var currentStep = 1

    private val fm: FragmentManager by lazy {
        supportFragmentManager
    }

    /**出发日期设置*/
    private val startedFragment: ItinerarySettingFragment by lazy {
        ItinerarySettingFragment()
    }

    /**景点设置*/
    private val scenicFragment: ScenicSettingFragment by lazy {
        ScenicSettingFragment()
    }

    /**交通设置*/
    private val trafficFragment: TrafficSettingFragment by lazy {
        TrafficSettingFragment()
    }

    /**添加景点*/
    private val addAimFragment: AddAimFragment by lazy {
        AddAimFragment()
    }

    override fun getLayout(): Int = R.layout.activity_itinerary_custom

    override fun setTitle(): String = getString(R.string.itinerary_settings)

    override fun injectVm(): Class<ItineraryCustomViewModel> = ItineraryCustomViewModel::class.java

    override fun initView() {
        AppManager.instance.removeActivity(this)
        ItineraryManager.instance.addActivity(this)

        mBinding.step = "$currentStep"
        transactFragment(R.id.view_content, startedFragment)

        mBinding.saveItinerary.setOnClickListener {
            saveItinerary()
        }

        //自定义目的地
        mBinding.customView.setOnClickListener {
            ARouter.getInstance()
                .build(ItineraryRouter.CUSTOM_DESTINATION)
                .withInt("hours",hours)
                .withParcelable("itineraryLabel",customLabel.value)
                .navigation()
        }
    }

    override fun initData() {
        customLabel.value = ItineraryLabelBean()
    }

    /**点击下一步*/
    fun onNextClick(view: View){
        currentStep++
        updateLayout()

        //在点击第三步的时候，拼接选中的标签
        if (currentStep == 3){
            val fragment = fm.findFragmentByTag("scenic") as ScenicSettingFragment
            val names = StringBuilder()
            val scenicIds = StringBuilder()
            val venueIds = StringBuilder()
            for (venue in fragment.venueLabel.values){
                venueIds.append("${venue.id},")
                names.append("${venue.labelName},")
            }
            for (scenery in fragment.sceneryLabel.values){
                scenicIds.append("${scenery.id},")
            }
            customLabel.value?.apply {
                labelName = names.append(crowdName).toString()
                scenicLabelsId = scenicIds.deleteCharAt(scenicIds.length - 1).toString()
                venueLabelsId= venueIds.deleteCharAt(venueIds.length - 1).toString()
            }
        }
    }

    /**检查日期*/
    private fun checkDate(): Boolean{
        customLabel.value?.let {
            if (it.cityName.isNullOrEmpty()){
                ToastUtils.showMessage("请选择出发城市")
                return false
            }

            if (it.travelStartTime.isNullOrEmpty()){
                ToastUtils.showMessage("请选择出发日期")
                return false
            }
            if (it.travelEndTime.isNullOrEmpty()){
                ToastUtils.showMessage("请选择返回日期")
                return false
            }

            //当前时间
            val currentDate = Utils.getDateTime(Utils.dateYMD, Date(System.currentTimeMillis()))
            if (Utils.dateCompare(it.travelStartTime,currentDate) <= 0){
                ToastUtils.showMessage("出发日期不能小于今日")
                return false
            }
            if (Utils.dateCompare(it.travelEndTime,it.travelStartTime) < 0){
                ToastUtils.showMessage("返回日期不能小于出发日期")
                return false
            }
        }
        return true
    }

    /**保存行程*/
    private fun saveItinerary(){
        if (customLabel.value!!.sourceParams.isEmpty()){
            ToastUtils.showMessage("请选择一个目的地景区")
            return
        }
        if(customLabel.value?.cityRegion.isNullOrEmpty()){
            ToastUtils.showMessage("生成行程失败")
            return
        }
        showLoadingDialog()
        mModel.saveItinerary(customLabel.value!!).observe(this, Observer {id->
            dissMissLoadingDialog()
            ToastUtils.showMessage("生成行程成功！")
            //跳转详情
            ARouter.getInstance()
                .build(ItineraryRouter.ITINERARY_DETAIL)
                .withString("itineraryId",id)
                .withString("tagType",ItineraryActivity.CLIENT)
                .navigation()
            finish()
        })
    }

    private fun updateLayout(){
        mBinding.lineView.visibility = View.VISIBLE
        when (currentStep) {
            1 -> {
                nText = "下一步：选择选择景点"
                mBinding.stepsTitle.text = "行程设置"
                transactFragment(R.id.view_content, startedFragment)
            }
            2 -> { //景点设置
                fragmentTag = "scenic"
                nText = "下一步：选择出行方式"
                mBinding.stepsTitle.text = "景点偏好"
                if (!checkDate()) {
                    currentStep -= 1
                    return
                }
                transactFragment(R.id.view_content, scenicFragment, fragmentTag)
            }
            3 -> {//出行方式
                nText = "下一步：选择目的地"
                fragmentTag = "traffic"
                mBinding.stepsTitle.text = "出行方式"
                mBinding.viewNext.visibility = View.VISIBLE
                mBinding.resultLayout.visibility = View.GONE
                transactFragment(R.id.view_content, trafficFragment, fragmentTag)
            }
            4 -> {//添加景点
                fragmentTag = "addaim"
                mBinding.lineView.visibility = View.GONE
                mBinding.stepsTitle.text = "添加想去的目的地景区"
                mBinding.viewNext.visibility = View.GONE
                mBinding.resultLayout.visibility = View.VISIBLE
                transactFragment(R.id.view_content, addAimFragment, fragmentTag)
            }
        }
        mBinding.step = "$currentStep"
        mBinding.viewNext.text = nText
    }

    /**
     * 更新游玩时间CardView
     * @param place 几个目的地？？
     */
    fun updateDate(place: List<PlayItems>){

        mBinding.totalTime.apply {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val startDate = sdf.parse(customLabel.value?.travelStartTime)
            val endDate = sdf.parse(customLabel.value?.travelEndTime)
            //一天按8小时计算
            hours = (DateUtil.getGapCount(startDate,endDate) + 24) / 3
            text = "您的往返日期为：${Utils.getDateTime(Utils.MD, startDate)}-${Utils.getDateTime(Utils.MD, endDate)}，共${hours}小时"
        }


        if (place.isNotEmpty()){
            var sumTime = 0
            for (item in place){
                sumTime += item.hour
            }
            val surplus = hours - sumTime
            if (surplus >= 0){
                mBinding.label1.text = "已选：${place.size}个目的地，游玩${sumTime}小时，剩余${abs(surplus)}小时游玩时间"
            } else {
                mBinding.label1.text = "已选：${place.size}个目的地，游玩${sumTime}小时，超过${abs(surplus)}小时游玩时间"
            }
        } else {
            mBinding.label1.text = "已选：0个目的地，游玩0小时，剩余${hours}小时游玩时间"
        }
    }

    override fun onBackPressed() {
        if (currentStep > 1){
            currentStep--
            updateLayout()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == Activity.RESULT_OK){
            addAimFragment.onActivityResult(requestCode,resultCode,data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.instance.removeActivity(this)
    }

}
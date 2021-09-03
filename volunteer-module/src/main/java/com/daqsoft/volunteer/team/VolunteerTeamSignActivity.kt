package com.daqsoft.volunteer.team

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerTeamDetailBean
import com.daqsoft.volunteer.databinding.*
import com.daqsoft.volunteer.event.SignApplyEvent
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.CalendarUtils
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.utils.SignDaysBean
import com.daqsoft.volunteer.utils.SignTimeBean
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast

/**
 *@package:com.daqsoft.volunteer.team
 *@date:2020/6/9 11:06
 *@author: caihj
 *@des:签到申请
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_SERVICE_SIGN)
class VolunteerTeamSignActivity:TitleBarActivity<ActivityTeamApplySignBinding,VolunteerTeamSignApplyVM>() {

    var volunteerTeamDetail:VolunteerTeamDetailBean? = null

    override fun getLayout(): Int = R.layout.activity_team_apply_sign

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_team_sign_apply_title)

    override fun injectVm(): Class<VolunteerTeamSignApplyVM> = VolunteerTeamSignApplyVM::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this);
    }

    override fun initView() {
        mBinding.rvIntroduce.apply {
            layoutManager = LinearLayoutManager(this@VolunteerTeamSignActivity)
            adapter = introduceAdapter
        }
        mBinding.rvDate.apply {
            layoutManager = GridLayoutManager(this@VolunteerTeamSignActivity,3)
            adapter = signDateAdapter
        }
        mBinding.rvTime.apply {
            layoutManager = GridLayoutManager(this@VolunteerTeamSignActivity,3)
            adapter = signTimeAdapter
        }
        signTimeAdapter.emptyViewShow =false
        mModel.volunteerTeamDetail.observe(this, Observer {

            val introduceData = mutableListOf<String>()
            if(it!=null&&it?.applyServiceAdvance != 0){
                introduceData.add("志愿服务申请需提前${it.applyServiceAdvance}天报名;")
            }
            introduceData.add("志愿服务的具体地点、具体时段以及内容，管理员会进分配，分配好后会短信告知您，您也可个人中心中查看;")
            introduceData.add("每次服务需要进行签到签退，已确认服务时长;")
            introduceData.add("志愿团队有权核查您的签到签退是否属实，并对于不属实的服务时长保留扣除的权利。")

            introduceAdapter.add(introduceData)
           var weeks = CalendarUtils.getWeekDate(it.applyServiceAdvance)
            signDateAdapter.add( parseTime(weeks,it) as MutableList<SignDaysBean>)
        })

    }

    override fun initData() {
        mBinding.tvApply.onNoDoubleClick {
            if(signDateAdapter.currentDate == null){
                toast("请选择服务日期!")
                return@onNoDoubleClick
            }
            if(times.isEmpty()){
                toast("请选择服务时间!")
                return@onNoDoubleClick
            }
            mModel.applySign(mModel.volunteerTeamDetail.value?.id.toString(), signDateAdapter.currentDate!!.date,times.joinToString(","))
        }
    }

    private fun parseTime(weeks:List<SignDaysBean>, volunteerTeamDetail:VolunteerTeamDetailBean):List<SignDaysBean>{
        val morning = "${volunteerTeamDetail.signInMorningStarTime}-${volunteerTeamDetail.signInMorningEndTime}"
        val afternoon = "${volunteerTeamDetail.signInAfternoonStarTime}-${volunteerTeamDetail.signInAfternoonEndTime}"
        val night = "${volunteerTeamDetail.signInNightStarTime}-${volunteerTeamDetail.signInNightEndTime}"
        val times = mutableListOf(
            SignTimeBean("morning",morning),
            SignTimeBean("afternoon",afternoon),
            SignTimeBean("night",night)
        )
        var signWeeks = mutableListOf<SignDaysBean>()
        for(day in weeks){
            when(day.week){
                "周一" ->{
                day.signTime = times
                if(volunteerTeamDetail.signInDateList.monday.size < 3){
                    day.signTime = parseUnableTime(day.signTime as MutableList<SignTimeBean>,volunteerTeamDetail.signInDateList.monday)
                }
                }
                "周二" ->{
                    day.signTime = times
                    if(volunteerTeamDetail.signInDateList.tuesday.size < 3){
                        day.signTime = parseUnableTime(day.signTime as MutableList<SignTimeBean>,volunteerTeamDetail.signInDateList.tuesday)
                    }
                }
                "周三" ->{
                    day.signTime = times
                    if(volunteerTeamDetail.signInDateList.wednesday.size < 3){
                        day.signTime =parseUnableTime(day.signTime as MutableList<SignTimeBean>,volunteerTeamDetail.signInDateList.wednesday)
                    }
                }
                "周四" ->{
                    day.signTime = times
                    if(volunteerTeamDetail.signInDateList.thursday.size < 3){
                        day.signTime = parseUnableTime(day.signTime as MutableList<SignTimeBean>,volunteerTeamDetail.signInDateList.thursday)
                    }
                }
                "周五" ->{
                    day.signTime = times
                    if(volunteerTeamDetail.signInDateList.friday.size < 3){
                        day.signTime =parseUnableTime(day.signTime as MutableList<SignTimeBean>,volunteerTeamDetail.signInDateList.friday)
                    }
                }
                "周六" ->{
                    day.signTime = times
                    if(volunteerTeamDetail.signInDateList.saturday.size < 3){
                        day.signTime = parseUnableTime(day.signTime as MutableList<SignTimeBean>,volunteerTeamDetail.signInDateList.saturday)
                    }
                }
                "周日" ->{
                    day.signTime = times
                    if(volunteerTeamDetail.signInDateList.sunday.size < 3){
                        day.signTime =  parseUnableTime(day.signTime as MutableList<SignTimeBean>,volunteerTeamDetail.signInDateList.sunday)
                    }
                }
            }
            signWeeks.add(day)
        }
        return signWeeks
    }

    /**
     * 解析不可用时间
     */
    private fun parseUnableTime(dateTime:MutableList<SignTimeBean>,times:List<String>):List<SignTimeBean>{
        var dateTimes = dateTime
        if(!times.contains("morning")){
            dateTimes[0].enable = false
        }
        if(!times.contains("afternoon")){
            dateTimes[1].enable =false
        }
        if(!times.contains("night")){
            dateTimes[2].enable =false
        }
        return dateTimes
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    fun getData(event:SignApplyEvent){
        mModel.volunteerTeamDetail.postValue(event.volunteerTeamDetail)
    }

    private var introduceAdapter = object :RecyclerViewAdapter<VolunteerServiceSignIntroduceItemBinding,String>(R.layout.volunteer_service_sign_introduce_item){
        override fun setVariable(mBinding: VolunteerServiceSignIntroduceItemBinding, position: Int, item: String) {
                mBinding.tvTxt.text = "${position}、${item}"
        }

    }

    private var signDateAdapter = object :RecyclerViewAdapter<VolunteerSignDateLabelItemBinding,SignDaysBean>(R.layout.volunteer_sign_date_label_item){

        var currentDate:SignDaysBean ? = null



        override fun setVariable(mBinding: VolunteerSignDateLabelItemBinding, position: Int, item: SignDaysBean) {
            mBinding.tvDate.text = item.date
            mBinding.tvWeek.text = item.week
            mBinding.root.isSelected = item.selected
            mBinding.root.isEnabled = item.signTime != null
            mBinding.root.onNoDoubleClick {
                if(currentDate!=item){
                    item.selected = true
                    if (currentDate != null) {
                        currentDate!!.selected = false
                    }
                    currentDate = item
                    signTimeAdapter.clear()
                    signTimeAdapter.add(item.signTime as MutableList<SignTimeBean>)
                    notifyDataSetChanged()
                }
            }
        }
    }

    var times = mutableListOf<String>()

   private val signTimeAdapter = object:RecyclerViewAdapter<VolunteerSignTimeLabelItemBinding,SignTimeBean>(R.layout.volunteer_sign_time_label_item){
        override fun setVariable(mBinding: VolunteerSignTimeLabelItemBinding, position: Int, item: SignTimeBean) {
            mBinding.tvTime.text = item.time
            mBinding.root.isSelected = item.selected
            mBinding.root.isEnabled = item.enable
            mBinding.root.isSelected = times.contains(item.key)

            mBinding.root.onNoDoubleClick {
                if(times.contains(item.key)){
                    times.remove(item.key)
                }else{
                    times.add(item.key)
                }
                notifyDataSetChanged()
            }
        }

    }

    override fun onDestroy() {
        //移除全部粘性事件
        EventBus.getDefault().removeAllStickyEvents();
        //解绑事件
        EventBus.getDefault().unregister(this);
        super.onDestroy()

    }
}

class VolunteerTeamSignApplyVM:BaseViewModel(){
        var volunteerTeamDetail =MutableLiveData<VolunteerTeamDetailBean>()

    fun applySign(id:String,applyServiceDate:String,time:String){
        val params = HashMap<String,String>()
        params["teamId"] = id
        params["applyServiceDate"] = applyServiceDate
        params["dateOfDay"] = time
        VolunteerRepository.service.postVolunteerSignApply(params).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                if(response.code == 0){
                    finish.postValue(true)
                    JumpUtils.gotoVolunteerApplySuc(2,response.data!!)
                }else{
                    ToastUtils.showMessage(response.message)
                }
            }

        })
    }
}
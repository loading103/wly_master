package com.daqsoft.volunteer.volunteer

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.RankData
import com.daqsoft.volunteer.databinding.ActivityVolunteerRankListBinding
import com.daqsoft.volunteer.databinding.VolunteerRankItemBinding
import com.daqsoft.volunteer.databinding.VolunteerRankListItemBinding
import com.daqsoft.volunteer.utils.EnumGlobal
import com.daqsoft.volunteer.volunteer.vm.VolunteerRankVM
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/4 15:09
 *@author: caihj
 *@des:志愿者排行榜
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_RANK_LIST)
class VolunteerRankListActivity:
    TitleBarActivity<ActivityVolunteerRankListBinding, VolunteerRankVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_rank_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_rank_title)

    override fun injectVm(): Class<VolunteerRankVM> = VolunteerRankVM::class.java

    // 2020/9/2 新增  从志愿者首页榜单跳转的类型
    /**
     * 排名类型
     */
    @JvmField
    @Autowired
    var rankingTypeEnum: String? = EnumGlobal.RankingTypeEnum.SERVICETIME.value
    /**
     * 榜单分类
     */
    @JvmField
    @Autowired
    var listClassEnum: String? = EnumGlobal.ListClassEnum.VOLUNTEER.value

    override fun initData() {
        mModel.rankingTypeEnum = rankingTypeEnum?:EnumGlobal.RankingTypeEnum.SERVICETIME.value
        when(rankingTypeEnum){
            EnumGlobal.RankingTypeEnum.SERVICETIME.value->{
                mBinding.tvServiceRank.isSelected = true
                mBinding.tvScoreRank.isSelected = false
            }
            EnumGlobal.RankingTypeEnum.INTEGRAL.value->{
                mBinding.tvServiceRank.isSelected = false
                mBinding.tvScoreRank.isSelected = true
            }
        }
        mModel.listClassEnum = listClassEnum?:EnumGlobal.ListClassEnum.VOLUNTEER.value
        when(listClassEnum){
            EnumGlobal.ListClassEnum.VOLUNTEERTEAM.value->{
                mBinding.tvRankSwitch.text = "志愿团队榜"
            }
            EnumGlobal.ListClassEnum.VOLUNTEER.value->{
                mBinding.tvRankSwitch.text = "志愿者榜"
            }
        }
    }

    override fun initView() {

//        mBinding.dvTimeRank.start(200000)
//        mBinding.dvTimeRank.setOnCountdownEndListener {
//            mBinding.dvTimeRank.visibility = View.GONE
//        }
        mBinding.tvServiceRank.isSelected = true
        changeRankType(0)
        initEvent()
        mBinding.rvRank.apply {
            layoutManager = LinearLayoutManager(this@VolunteerRankListActivity)
            adapter = rankAdapter
        }
        mModel.volunteerRank.observe(this, Observer {
            mBinding.tvUpper.isEnabled = it.upper != 0
            mBinding.tvNext.isEnabled = it.next != 0
            if(it.data.isNotEmpty()){
                mBinding.rvRank.visibility = View.VISIBLE
                mBinding.dvTimeRank.stop()
                mBinding.llTimer.visibility = View.GONE
                pageDel(it.data as MutableList<RankData>)
            }else{
                if(mModel.currentPage == 1){
                    mBinding.rvRank.visibility = View.GONE
                    mBinding.llTimer.visibility = View.VISIBLE
                    mBinding.dvTimeRank.start(it.nextCycleCountdown)
                    mBinding.dvTimeRank.setOnCountdownEndListener {
                        mBinding.dvTimeRank.visibility = View.GONE
                        mModel.getRankData()
                    }
                }else if(it.data.size < mModel.pageSize.toInt()){
                    rankAdapter.loadEnd()
                }else{
                    rankAdapter.loadComplete()
                }
            }
        })
        mModel.ranks.observe(this, Observer {
            if(!it.isNullOrEmpty()){
               if(it[0].rank > 3){
                   rankAdapter.getData().add(0,it[0])
                   rankAdapter.notifyItemInserted(0)
               }
            }
        })
        rankAdapter.setOnLoadMoreListener {
            mModel.currentPage++
            mModel.getRankData()
        }
    }

    private fun pageDel(datas:MutableList<RankData>){
        if (!datas.isNullOrEmpty()) {
            rankAdapter.add(datas)
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.pageSize.toInt()) {
            rankAdapter.loadEnd()
        } else {
            rankAdapter.loadComplete()
        }
    }


    fun initEvent(){
        mBinding.tvServiceRank.onNoDoubleClick {
            if(!mBinding.tvServiceRank.isSelected){
                mBinding.tvServiceRank.isSelected = true
                mBinding.tvScoreRank.isSelected = false
                mModel.rankingTypeEnum = EnumGlobal.RankingTypeEnum.SERVICETIME.value
            }
            loadData()
        }
        mBinding.tvScoreRank.onNoDoubleClick {
            if(!mBinding.tvScoreRank.isSelected){
                mBinding.tvScoreRank.isSelected = true
                mBinding.tvServiceRank.isSelected = false
                mModel.rankingTypeEnum = EnumGlobal.RankingTypeEnum.INTEGRAL.value
            }
            loadData()
        }
        mBinding.tvRankSwitch.onNoDoubleClick {
            if(mBinding.tvRankSwitch.text == "志愿者榜"){
                mBinding.tvRankSwitch.text = "志愿团队榜"
                mModel.listClassEnum = EnumGlobal.ListClassEnum.VOLUNTEERTEAM.value
            }else{
                mBinding.tvRankSwitch.text = "志愿者榜"
                mModel.listClassEnum = EnumGlobal.ListClassEnum.VOLUNTEER.value
            }
            loadData()
        }
        mBinding.tvWeek.onNoDoubleClick {
            changeRankType(0)
        }
        mBinding.tvMonth.onNoDoubleClick {
            changeRankType(1)
        }
        mBinding.tvQuarter.onNoDoubleClick {
            changeRankType(2)
        }
        mBinding.tvYear.onNoDoubleClick {
            changeRankType(3)
        }
        mBinding.tvUpper.onNoDoubleClick {
            if(mModel.volunteerRank.value?.upper!=0){
                mModel.time = "${mModel.volunteerRank.value?.upper}"
                loadData()
            }
        }
        mBinding.tvNext.onNoDoubleClick {
            if(mModel.volunteerRank.value?.next!=0){
                mModel.time = "${mModel.volunteerRank.value?.next}"
                loadData()
            }
        }
    }


    private fun loadData(){
        rankAdapter.clear()
        mModel.getRankData()
        mModel.getVolunteerRank()
    }

    var selected:View? = null

    @SuppressLint("SetTextI18n")
    private fun changeRankType(type:Int){
        when(type){
            0 ->{
                if(!mBinding.tvWeek.isSelected){
                    if(selected!=null){
                        selected?.isSelected = false
                    }
                    mBinding.tvWeek.isSelected = true
                    mModel.listTypeEnum ="week"
                    mBinding.tvUpper.text = "上一周"
                    mBinding.tvNext.text = "下一周"
                    mBinding.tvUpdateTips.text = "每周五下午6点进行更新"
                    selected = mBinding.tvWeek
                }
            }
            1 ->{
                if(!mBinding.tvMonth.isSelected){
                    if(selected!=null){
                        selected?.isSelected = false
                    }
                    mBinding.tvMonth.isSelected = true
                    mModel.listTypeEnum ="month"
                    mBinding.tvUpper.text = "上一月"
                    mBinding.tvNext.text = "下一月"
                    mBinding.tvUpdateTips.text = "每月最后一天下午6点进行更新"
                    selected = mBinding.tvMonth
                }
            }
            2 ->{
                if(!mBinding.tvQuarter.isSelected){
                    if(selected!=null){
                        selected?.isSelected = false
                    }
                    mBinding.tvQuarter.isSelected = true
                    mModel.listTypeEnum ="quarter"
                    mBinding.tvUpper.text = "上一季"
                    mBinding.tvNext.text = "下一季"
                    mBinding.tvUpdateTips.text = "3/6/9/12月最后一天下午六点更新"
                    selected = mBinding.tvQuarter
                }
            }
            3 ->{
                if(!mBinding.tvYear.isSelected){
                    if(selected!=null){
                        selected?.isSelected = false
                    }
                    mBinding.tvYear.isSelected = true
                    mModel.listTypeEnum ="year"
                    mBinding.tvUpper.text = "上一年"
                    mBinding.tvNext.text = "下一年"
                    mBinding.tvUpdateTips.text = "每月最后一天下午6点进行更新"
                    selected = mBinding.tvYear
                }
            }
        }
        loadData()
    }

    override fun onDestroy() {
        mBinding.dvTimeRank.stop()
        super.onDestroy()
    }

    private val rankAdapter = object :RecyclerViewAdapter<VolunteerRankListItemBinding, RankData>(R.layout.volunteer_rank_list_item){
        @SuppressLint("ResourceAsColor")
        override fun setVariable(mBinding: VolunteerRankListItemBinding, position: Int, item: RankData) {
            if(position == 0){
                mBinding.root.backgroundResource = R.mipmap.volunteer_rank_list_1st
            }
            if(position == 1){
                mBinding.root.backgroundResource = R.mipmap.volunteer_rank_list_2nd
            }
            if(position == 2){
                mBinding.root.backgroundResource = R.mipmap.volunteer_rank_list_3rd
            }
            mBinding.tvRankNum.text = item.rank.toString()
            Glide.with(this@VolunteerRankListActivity).load(item.head).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.riHead)
            mBinding.tvName.text = item.name
            mBinding.tvAddress.text = item.serviceRegionName
            mBinding.tvServiceData.text = item.total
            if(mModel.rankingTypeEnum == "integral"){
                mBinding.tvUnit.text = "分"
            }else{
                mBinding.tvUnit.text = "小时"
            }
            if(position > 2){
                mBinding.tvRankNum.textColor = resources.getColor(R.color.color_666)
                mBinding.tvName.textColor = resources.getColor(R.color.color_333)
                mBinding.tvAddress.textColor = resources.getColor(R.color.color_999)
                mBinding.tvServiceData.textColor = resources.getColor(R.color.color_333)
                mBinding.tvUnit.textColor = resources.getColor(R.color.color_999)
            }else{
                mBinding.tvRankNum.textColor = resources.getColor(R.color.white)
                mBinding.tvName.textColor = resources.getColor(R.color.white)
                mBinding.tvAddress.textColor =resources.getColor(R.color.white)
                mBinding.tvServiceData.textColor =resources.getColor(R.color.white)
                mBinding.tvUnit.setTextColor(resources.getColor(R.color.white))
            }

        }

    }
}
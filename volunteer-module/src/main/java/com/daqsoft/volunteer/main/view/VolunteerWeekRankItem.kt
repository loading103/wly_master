package com.daqsoft.volunteer.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.RankData
import com.daqsoft.volunteer.databinding.MoreTitleBarBinding
import com.daqsoft.volunteer.databinding.VolunteerRankItemBinding
import com.daqsoft.volunteer.utils.Constant

/**
 *@package:com.daqsoft.volunteer.main.view
 *@date:2020/6/2 9:26
 *@author: caihj
 *@des:周排行组件
 **/
class VolunteerWeekRankItem:LinearLayout {

    private var mContext: Context? = null
    private var content:String? = ""
    private var binding:VolunteerRankItemBinding? = null
    private var rankType = ""
    private var volunteerType = ""
    private var volunteerBg = -1
    private var rankData = mutableListOf<RankData>()
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        val typeAttr = context!!.obtainStyledAttributes(attrs!!,R.styleable.VolunteerWeekRankItem)
        rankType = typeAttr.getString(R.styleable.VolunteerWeekRankItem_rank_type)
        volunteerType = typeAttr.getString(R.styleable.VolunteerWeekRankItem_volunteer_type)
        volunteerBg = typeAttr.getResourceId(R.styleable.VolunteerWeekRankItem_volunteer_bg,-1)
         typeAttr.recycle()
        initView()
    }

    private fun initView(){
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.volunteer_rank_item,this,false)
        binding!!.clRoot.background = mContext?.getDrawable(volunteerBg)
        addView(binding!!.root)
    }

    fun setRankData(data:MutableList<RankData>){
        rankData = data
        updateData()
    }

    private fun updateData(){
        if(rankData.size == 3){
            setRank1Data(rankData[0])
            setRank2Data(rankData[1])
            setRank3Data(rankData[2])
        }
        if(rankData.size == 1){
            setRank1Data(rankData[0])
        }
        if(rankData.size == 2){
            setRank1Data(rankData[0])
            setRank2Data(rankData[1])
        }
        postInvalidate()
    }

    private fun setRank1Data(rank:RankData){
        binding?.tvName1?.text = rank.name
        var content = if(rankType == Constant.SERVICE_TIME){
            "${rank.total}小时"
        }else{
           "${rank.total}分"
        }

        binding?.tvContent1?.text = content
    }

    private fun setRank2Data(rank:RankData){
        binding?.tvName2?.text = rank.name
        var content = if(rankType == Constant.SERVICE_TIME){
            "${rank.total}小时"
        }else{
            "${rank.total}分"
        }
        binding?.tvContent2?.text = content
    }
    private fun setRank3Data(rank:RankData){
        binding?.tvName3?.text = rank.name
        var content = if(rankType == Constant.SERVICE_TIME){
            "${rank.total}小时"
        }else{
            "${rank.total}分"
        }
        binding?.tvContent3?.text = content
    }
}
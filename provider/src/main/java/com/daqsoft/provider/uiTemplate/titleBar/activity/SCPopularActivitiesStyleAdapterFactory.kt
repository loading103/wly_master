package com.daqsoft.provider.uiTemplate.titleBar.activity

import androidx.recyclerview.widget.RecyclerView

/**
 * @package name：com.daqsoft.provider.activitymodule
 * @date 2020/10/9 11:57
 * @author zp
 * @describe 四川热门活动样式 适配器工厂模式
 */
class SCPopularActivitiesStyleAdapterFactory {

    companion object{
        fun getAdapter(type:String) : SCPopularActivitiesStyleAdapter {
            return when(type){
                "1" ->{
                    SCPopularActivitiesStyleOne()
                }
                "2" ->{
                    SCPopularActivitiesStyleTwo()
                }
                "3" ->{
                    SCPopularActivitiesStyleThree()
                }
                "4"->{
                    SCPopularActivitiesStyleFour()
                }
                "5"->{
                    SCPopularActivitiesStyleFive()
                }
                else ->{
                    SCPopularActivitiesStyleOne()
                }
            }
        }
    }

}

interface SCPopularActivitiesStyleAdapter{
    fun create(): SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder>
}

class SCPopularActivitiesStyleOne : SCPopularActivitiesStyleAdapter {
    override fun create(): SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder> {
        return SCPopularActivitiesStyleAdapterOne() as SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder>
    }

}

class SCPopularActivitiesStyleTwo : SCPopularActivitiesStyleAdapter {
    override fun create(): SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder> {
        return SCPopularActivitiesStyleAdapterTwo() as SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder>
    }
}

class SCPopularActivitiesStyleThree : SCPopularActivitiesStyleAdapter {
    override fun create(): SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder> {
        return SCPopularActivitiesStyleAdapterThree() as SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder>
    }
}

class SCPopularActivitiesStyleFour : SCPopularActivitiesStyleAdapter {
    override fun create(): SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder> {
        return SCPopularActivitiesStyleAdapterFour() as SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder>
    }
}
class SCPopularActivitiesStyleFive : SCPopularActivitiesStyleAdapter {
    override fun create(): SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder> {
        return SCPopularActivitiesStyleAdapterFive() as SCPopularActivitiesStyleBaseAdapter<RecyclerView.ViewHolder>
    }
}
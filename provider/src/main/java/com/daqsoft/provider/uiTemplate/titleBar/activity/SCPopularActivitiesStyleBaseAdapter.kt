package com.daqsoft.provider.uiTemplate.titleBar.activity

import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.bean.ActivityBean

/**
 * @package name：com.daqsoft.provider.activitymodule
 * @date 2020/10/9 14:14
 * @author zp
 * @describe 四川热门活动样式适配器基类
 */
abstract class SCPopularActivitiesStyleBaseAdapter<T:RecyclerView.ViewHolder> : RecyclerView.Adapter<T>(){

    val menus = mutableListOf<ActivityBean>()

    override fun getItemCount(): Int {
        return menus.size
    }

    var onItemCollectionLisener: ((item: ActivityBean, position: Int) -> Unit)? = null

    fun setOnCollectionLisener(onItemCollectionLisener: (item: ActivityBean, position: Int) -> Unit) {
        this.onItemCollectionLisener = onItemCollectionLisener
    }

}

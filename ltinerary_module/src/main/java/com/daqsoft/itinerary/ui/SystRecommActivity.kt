package com.daqsoft.itinerary.ui

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.col.sln3.id
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.ItineraryListAdapter
import com.daqsoft.itinerary.bean.RecommFilterLabelBean
import com.daqsoft.itinerary.databinding.ActivityRecommBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.provider.ItineraryRouter
import com.daqsoft.itinerary.vm.ItineraryViewModel
import com.daqsoft.itinerary.widget.RecommFilterWidow
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author：      邓益千
 * @Create by：   2020/5/11 10:17
 * @Description： 系统推荐
 */
@Route(path = ItineraryRouter.SYST_RECOMM_LIST)
class SystRecommActivity : TitleBarActivity<ActivityRecommBinding, ItineraryViewModel>() {

    /**人群类型*/
    private val crowdType: MutableList<String> by lazy {
        ArrayList<String>()
    }

    /**景点标签*/
    private val tagType: MutableList<String> by lazy {
        ArrayList<String>()
    }

    /**天数*/
    private val dayList: MutableList<String> by lazy {
        ArrayList<String>()
    }

    private val crowds: StringBuilder by lazy {
        StringBuilder()
    }
    private val tags: StringBuilder by lazy {
        StringBuilder()
    }
    private val days: StringBuilder by lazy {
        StringBuilder()
    }

    private val adapter: ItineraryListAdapter by lazy {
        ItineraryListAdapter(ItineraryActivity.SYSTEM)
    }

    private val filterWidow: RecommFilterWidow by lazy {
        RecommFilterWidow(this)
    }

    override fun setTitle(): String = "系统推荐"

    override fun getLayout(): Int = R.layout.activity_recomm

    override fun injectVm(): Class<ItineraryViewModel> = ItineraryViewModel::class.java

    override fun initView() {
        filterWidow.setOnFilterListener(filterListener)

        adapter.setOnItemListener(itemListener)
        mBinding.systRecommended.adapter = adapter
        mBinding.viewFilter.setOnClickListener {
            filterWidow.showAsDropDown(mBinding.viewFilter)
        }
        adapter.setOnLoadMoreListener {
            mModel.currPage++
            mModel.getItineraryList("SYSTEM")
        }
    }

    override fun initData() {
        mModel.itineraryList.observe(this, Observer {
            PageDealUtils().pageDeal(mModel.currPage, it, adapter!!)

            dissMissLoadingDialog()

            if (it.datas.isNullOrEmpty()){
                adapter?.emptyViewShow = true
            }else{
                adapter?.clear()
                adapter?.add(it.datas!!)
            }
        })

        mModel.filterLabel.observe(this, Observer {
            filterWidow.setLabelData(it)
        })

        showLoadingDialog()
        mModel.getFilterLabel()
        mModel.getItineraryList("SYSTEM")
    }

    private val itemListener = object : ItineraryListAdapter.OnItemClick<Map<String, String>> {
        override fun onSelected(position: Int,map:Map<String,String>) {
            ARouter.getInstance()
                .build(ItineraryRouter.ITINERARY_DETAIL)
                .withString("itineraryId", map["id"])
                .withString("tagType", ItineraryActivity.SYSTEM)
                .navigation()
        }

        override fun onRenamed(position: Int, id: String) {}
    }

    //筛选监听
    private val filterListener = object : RecommFilterWidow.OnFilterListener {
        override fun onClickDay(item: RecommFilterLabelBean) {
            if (item.isSelected && !dayList.contains(item.id)){
                dayList.add(item.id)
            } else {
                if (dayList.contains(item.id)){
                    dayList.remove(item.id)
                }
            }
        }

        override fun onClickCrowd(item: RecommFilterLabelBean) {
            if (item.isSelected && !crowdType.contains(item.name)){
                crowdType.add(item.name)
            } else {
                if (crowdType.contains(item.name)){
                    crowdType.remove(item.name)
                }
            }
        }

        override fun onClickTag(item: RecommFilterLabelBean) {
            if (item.isSelected && !tagType.contains(item.name)){
                tagType.add(item.name)
            } else {
                if (tagType.contains(item.name)){
                    tagType.remove(item.name)
                }
            }
        }

        //0为重置
        override fun onDone(tag: Int) {
            showLoadingDialog()
            //清空上一次选中
            days.clear()
            tags.clear()
            crowds.clear()
            if (tag != 0){
                dayList.forEach { id->
                    days.append("$id,")
                }
                tagType.forEach { id->
                    tags.append("$id,")
                }
                crowdType.forEach { id->
                    crowds.append("$id,")
                }
                mModel.getItineraryList(
                    "SYSTEM",
                    if (days.isNotEmpty()) days.deleteCharAt(days.length - 1).toString() else "",
                    if (crowds.isNotEmpty()) crowds.deleteCharAt(crowds.length - 1).toString() else "",
                    if(tags.isNotEmpty()) tags.deleteCharAt(tags.length - 1).toString() else ""
                )
            } else {
                filterWidow.resetState()
                mModel.getItineraryList("SYSTEM")
            }
        }

    }

}
package com.daqsoft.itinerary.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.AppManager
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.SmartRecommAdapter
import com.daqsoft.itinerary.bean.CityBean
import com.daqsoft.itinerary.bean.ItineraryLabelBean
import com.daqsoft.itinerary.bean.PlayItems
import com.daqsoft.itinerary.bean.RecommScenicItem
import com.daqsoft.itinerary.databinding.ActivitySmartRecommBinding
import com.daqsoft.itinerary.databinding.ItineraryItemCustomScenicBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.itinerary.util.ItineraryManager
import com.daqsoft.provider.ItineraryRouter
import com.daqsoft.itinerary.vm.ItineraryCustomViewModel
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.Constants
import com.daqsoft.provider.MainARouterPath

/**
 * @Author：      邓益千
 * @Create by：   2020/5/20 15:38
 * @Description： 智能推荐
 */
@Route(path = ItineraryRouter.SMART_RECOMM)
class SmartRecommActivity: TitleBarActivity<ActivitySmartRecommBinding, ItineraryCustomViewModel>() {

    /**城市列表*/
    @JvmField
    @Autowired(name = "citys")
    var citys : ArrayList<CityBean>? = null

    /**行程标签*/
    @JvmField
    @Autowired(name = "itineraryLabel")
    var itineraryLabel : ItineraryLabelBean? = null

    /**最大天数*/
    @JvmField
    @Autowired(name = "maxDay")
    var maxDay = 0

    private val scenicList: MutableList<PlayItems> by lazy {
        ArrayList<PlayItems>()
    }

    private val adapter: SmartRecommAdapter by lazy {
        SmartRecommAdapter()
    }

    override fun getLayout(): Int = R.layout.activity_smart_recomm

    override fun setTitle(): String = getString(R.string.itinerary_settings)

    override fun injectVm(): Class<ItineraryCustomViewModel> = ItineraryCustomViewModel::class.java


    private var position = -1
    private var item: PlayItems? = null
    private var recommScenicItem : RecommScenicItem? = null
    private var itineraryItemCustomScenicBinding : ItineraryItemCustomScenicBinding? = null

    override fun initView() {
        AppManager.instance.removeActivity(this)
        ItineraryManager.instance.addActivity(this)

        adapter.setOnItemSelectedListener(itemListener)
        adapter.setRootOnClickListener(object : SmartRecommAdapter.RootOnClickListener<PlayItems, RecommScenicItem>{
            override fun onClick(
                position: Int,
                item: PlayItems,
                r: RecommScenicItem,
                mBinding: ItineraryItemCustomScenicBinding
            ) {

                this@SmartRecommActivity.position = position
                this@SmartRecommActivity.item = item
                this@SmartRecommActivity.recommScenicItem = r
                this@SmartRecommActivity.itineraryItemCustomScenicBinding = mBinding

                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_SCENIC_DETAIL)
                    .withBoolean("isShowButton",item.isSelected)
                    .withString("source","itinerary")
                    .withString("id", item.resourceId.toString())
                    .navigation(this@SmartRecommActivity,14)
            }
        })

        mBinding.recyclerView.adapter = adapter

        mBinding.addView.setOnClickListener {
            if (scenicList.isEmpty()){
                ToastUtils.showMessage("请添加景区")
                return@setOnClickListener
            }
            saveItinerary()
        }
    }

    override fun initData() {
        mModel.customScenic.observe(this, Observer {
            it.forEach { bean->
                for (city in citys!!){
                    if (bean.regionName == city.regionName){
                        bean.playedDay = city.suggestedDay
                    }
                }
            }

            adapter.add(it)
            mBinding.cityNumber.text = "${it.size}"
            var scenicNumber = 0
            for (city in it){
                for (scenic in city.list){
                    scenicNumber += 1
                }
            }
            mBinding.scenicNumber.text = "$scenicNumber"
        })

        citys?.let {
            val sb = StringBuilder()
            for (cy in it){
                sb.append("${cy.region},")
            }
            mModel.getCustomScenic(sb.deleteCharAt(sb.length-1).toString(),maxDay)
        }

    }

    private val itemListener = object : OnItemSelectedListener<PlayItems>{
        override fun onSelected(position: Int,item: PlayItems) {
            if (item.isSelected) {
                scenicList.add(item)
            } else {
                scenicList.remove(item)
            }
        }
    }

    /**保存行程*/
    private fun saveItinerary(){
        showLoadingDialog()

        itineraryLabel!!.sourceParams = scenicList

        if(itineraryLabel?.cityRegion.isNullOrEmpty()){
            ToastUtils.showMessage("生成行程失败")
            return
        }
        mModel.saveItinerary(itineraryLabel!!).observe(this, Observer {id->
            dissMissLoadingDialog()
            ToastUtils.showMessage("生成行程成功！")
            //跳转详情
            ARouter.getInstance()
                .build(ItineraryRouter.ITINERARY_DETAIL)
                .withString("itineraryId",id)
                .withString("tagType",ItineraryActivity.CLIENT)
                .navigation()

            ItineraryManager.instance.finishAllActivity()
        })
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 14 && resultCode == Activity.RESULT_OK){
            scenicList.add(item!!)
            recommScenicItem?.isChecked = true
            adapter.scenicAdapter.updateState(recommScenicItem!!,itineraryItemCustomScenicBinding!!)
        }
    }
}
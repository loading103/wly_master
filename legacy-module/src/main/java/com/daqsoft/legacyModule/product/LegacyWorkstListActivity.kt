package com.daqsoft.legacyModule.product

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.adapter.GridWorksAdapter
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.databinding.ActivityLegacyProductListBinding
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.ARouterPath

/**
 *@package:com.daqsoft.legacyModule.product
 *@date:2020/5/11 15:00
 *@author: caihj
 *@des:全部作品列表,关注列表，PK列表
 **/
@Route(path = ARouterPath.LegacyModule.LEGACY_WORK_LIST_ACTIVITY)
internal class LegacyWorksListActivity : TitleBarActivity<ActivityLegacyProductListBinding, LegacyWorksViewModel>() {

    /**
     * 列表类型 0 ：我的关注,1:pk列表 ,2:全部作品
     */
    @Autowired
    @JvmField
    var type = "0"

    /**
     * pk作品id
     */
    @Autowired
    @JvmField
    var id = ""

    /**
     * 传承人电话
     */
    @Autowired
    @JvmField
    var phone = ""

    override fun getLayout(): Int = R.layout.activity_legacy_product_list

    override fun setTitle(): String = getTitleName()

    override fun injectVm(): Class<LegacyWorksViewModel> = LegacyWorksViewModel::class.java

    /**
     * 根据类型获取标题
     */
    private fun getTitleName():String = when(type){
        "0" ->{
            getString(R.string.legacy_module_mine_focus)
        }
        "1" ->{
            getString(R.string.legacy_module_all_pk)
        }
        else -> {
            getString(R.string.legacy_module_all_works)
        }
    }

    val pageSize:Int = 20
    private var worksAdapter: GridWorksAdapter? = null

    override fun initView() {
        worksAdapter = GridWorksAdapter(context!!)
        val storyLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mBinding.rvProducts.layoutManager = storyLayoutManager
        mBinding.rvProducts.adapter = worksAdapter
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
            mBinding.mSwipeRefreshLayout.finishRefresh()
            worksAdapter!!.clear()
            if(type == "1"){
                mModel.getPkWorks(id)
            }else if(type == "0"){
                mModel.getAttentionStoryList()
            }else{
                mModel.getDetailStoryList(phone,id)
            }
        }
        mModel.worksList.observe(this, Observer {
            dissMissLoadingDialog()
            if(!it.isNullOrEmpty()){
                mBinding.mSwipeRefreshLayout.visibility = View.VISIBLE
                worksAdapter!!.add(it)
            }
        })
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
    }

    override fun initData() {
        showLoadingDialog()
        if(type == "1"){
            mModel.getPkWorks(id)
        }else if(type == "0"){
            mModel.getAttentionStoryList()
        }else{
            mModel.getDetailStoryList(phone,id)
        }
    }

}


 class LegacyWorksViewModel: BaseViewModel() {
        var worksList = MutableLiveData<MutableList<LegacyStoryListBean>>()

     /**
      * 获取我的关注
      */
     fun getAttentionStoryList(){
            LegacyRepository.service.getAttentionStoryList().excute(object :BaseObserver<LegacyStoryListBean>(){
                override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                    worksList.postValue(response.datas)
                }
            })
     }


     fun getPkWorks(id:String){
         LegacyRepository.service.getPKWorksList(id).excute(object :BaseObserver<LegacyStoryListBean>(){
             override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                 worksList.postValue(response.datas)
             }

         })
     }


     val detailStoryList = MutableLiveData<List<LegacyStoryListBean>>()

     /**
      * 获取故事列表
      */
     fun getDetailStoryList(phone:String,id:String) {
         val param = HashMap<String, String>()
         param["ichTermId"] = id
         param["ichHpPhone"] = phone
         LegacyRepository.service.getLegacyWorks(param)
             .excute(object : BaseObserver<LegacyStoryListBean>() {
                 override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                     worksList.postValue(response.datas)
                 }
             })
     }
}
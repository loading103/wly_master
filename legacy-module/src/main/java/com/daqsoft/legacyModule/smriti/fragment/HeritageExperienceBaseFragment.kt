package com.daqsoft.legacyModule.smriti.fragment


import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.adapter.LegacyHeritageAdapter
import com.daqsoft.legacyModule.adapter.LegacyHeritageBaseAdapter
import com.daqsoft.legacyModule.adapter.LegacyHeritagePeopleAdapter
import com.daqsoft.legacyModule.bean.LegacyHeritageExperienceBaseListBean
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.bean.LegacyHeritagePeopleListBean
import com.daqsoft.legacyModule.databinding.*
import com.daqsoft.legacyModule.rv.dsl.grid
import com.daqsoft.legacyModule.rv.dsl.linear
import com.daqsoft.provider.view.convenientbanner.utils.ScreenUtil
import kotlinx.android.parcel.Parcelize
import java.lang.Exception
import com.daqsoft.legacyModule.getRealImages
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.legacyModule.smriti.HERITAGE_LEVEL
import com.daqsoft.legacyModule.smriti.bean.LegacyPeopleBean
import com.daqsoft.legacyModule.smriti.bean.TypeBean
import com.daqsoft.legacyModule.smriti.net.LegacySmritiRepository
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import org.jetbrains.anko.support.v4.onRefresh

/**
 * @des 首页非遗传承人
 * @author caihj
 * @Date 2020/5/11 15:38
 */
internal class HeritageExperienceBaseFragment:
    BaseFragment<FragmentLegacyExperienceBinding, HeritageExperienceBaseVM>() {

    companion object{
        fun newInstance():HeritageExperienceBaseFragment{
            return HeritageExperienceBaseFragment()
        }
    }

    /**
     * 地区选择弹窗窗口
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null

    /**
     * 级别弹窗
     */
    private var levelPopupWindow: ListPopupWindow<Any>? = null


    override fun getLayout(): Int = R.layout.fragment_legacy_experience


    override fun injectVm(): Class<HeritageExperienceBaseVM> =
        HeritageExperienceBaseVM::class.java

    private var rvAdapter:LegacyHeritageBaseAdapter? = null

    override fun initView() {
         rvAdapter = context?.let { it1 -> LegacyHeritageBaseAdapter(it1,ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE) }
         mBinding.rvExperience.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        mModel.heritageExperienceBaseList.observe(this, Observer {
            pageDealed(it.toMutableList())
        })
        rvAdapter!!.onItemClick = object :LegacyHeritageAdapter.OnItemClickListener{
            override fun onItemClick(id: String, position: Int, status: Boolean) {
                if(status){
                    mModel.canceCollect(id,position)
                }else{
                    mModel.collect(id,position)
                }
            }

        }
        // 收藏
        mModel.collectVenueLiveData.observe(this, Observer {
            rvAdapter!!.notifyCollectStatus(it)
        })

        // 取消收藏
        mModel.canceCollectLiveData.observe(this, Observer {
            rvAdapter!!.notifyCollectStatus(it)
        })

        // 地区
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "全部", "", "", emptyList()))
                areaListPopWindow =
                    AreaSelectPopupWindow.getInstance(
                        BaseApplication.context, false
                    ) { item ->
                        mModel.areaSiteSwitch = item.siteId
                        mModel.region = item.region
                        mModel.mCurrPage = 1
                        showLoadingDialog()
                        if ( item .name=="全部"){
                            mBinding.tvArea.text ="地区"
                        }else{
                            mBinding.tvArea.text = item.name
                        }
                        mModel.getHomeHeritageExperienceList()
                    }
                areaListPopWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "不限", "", "", emptyList()))
                areaListPopWindow!!.secondData = ArrayList(temp)
            }
        })

        // 级别
        mModel.levelBeans.observe(this, Observer {
            it?.add(0, TypeBean("","全部","","",""))
            levelPopupWindow = ListPopupWindow.getInstance(mBinding.tvLevel, it) { item ->
                kotlin.run {
                    if ( (item as TypeBean).name=="全部"){
                        mBinding.tvLevel.text ="级别"
                    }else{
                        mBinding.tvLevel.text = item.name
                    }
                    mModel.level = item.id
                    showLoadingDialog()
                    mModel.getHomeHeritageExperienceList()
                }
            }
        })
        initEvent()
    }


    private fun initEvent(){
        mBinding.tvArea.onNoDoubleClick {
            if (areaListPopWindow != null) {
                areaListPopWindow!!.show(mBinding.tvArea)
            }
        }
        mBinding.tvLevel.onNoDoubleClick {
            if(levelPopupWindow!= null){
                levelPopupWindow!!.show()
            }
        }

        rvAdapter?.setOnLoadMoreListener {
            mModel.mCurrPage++
            mModel.getHomeHeritageExperienceList()
        }

        mBinding.sfLegacyExperience.setOnRefreshListener {
//            mBinding.sfLegacyExperience.isRefreshing = true
            mModel.mCurrPage = 1
            mModel.getHomeHeritageExperienceList()
        }
    }


    override fun initData() {
        mModel.getHomeHeritageExperienceList()
        mModel.getChildRegions()
        mModel.getLevel(HERITAGE_LEVEL)
    }


    private fun pageDealed(it: MutableList<LegacyHeritageExperienceBaseListBean>?) {
        dissMissLoadingDialog()
//        mBinding.sfLegacyExperience.isRefreshing = false
        mBinding.sfLegacyExperience.finishRefresh()
        if (!mBinding.rvExperience.isVisible) {
            mBinding.rvExperience.visibility = View.VISIBLE
        }
        if (mModel.mCurrPage == 1) {
            if(!it.isNullOrEmpty())
                mBinding.rvExperience.smoothScrollToPosition(0)
            rvAdapter!!.clear()
        }
        if (!it.isNullOrEmpty()) {
            rvAdapter!!.add(it!!)
        }
        if (it.isNullOrEmpty() || it.size < mModel.pageSize) {
            rvAdapter?.loadEnd()
        } else {
            rvAdapter?.loadComplete()
        }
    }
}

class HeritageExperienceBaseVM:BaseViewModel(){
    val heritageExperienceBaseList = MutableLiveData<List<LegacyHeritageExperienceBaseListBean>>()
    fun getHomeHeritageExperienceList() {
        LegacyRepository.service.getHeritageExperienceList(
            currPage = mCurrPage,
            pageSize = pageSize,
            areaSiteSwitch = areaSiteSwitch,
            region = region,
            level = level
        )
            .excute(object : BaseObserver<LegacyHeritageExperienceBaseListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyHeritageExperienceBaseListBean>) {
                    heritageExperienceBaseList.postValue(response.datas)
                }
            })
    }

    /**
     *级别筛选
     */
    var levelBeans= MutableLiveData<MutableList<TypeBean>>()

    /**
     *级别筛选
     */
    fun getLevel(type:String) {
        val map = HashMap<String, Any>()
        map["type"]=type
        LegacySmritiRepository.service.getSearchType(map).excute(object : BaseObserver<TypeBean>(){
            override fun onSuccess(response: BaseResponse<TypeBean>) {
                levelBeans.postValue(response.datas)
            }

        })
    }

    /**
     * 地区
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    //类型
    var type = ""
    var region = ""
    var areaSiteSwitch = ""
    var mCurrPage = 1
    var pageSize =10
    var level = ""
    /**
     * 站点下级区域(两层)
     */
    fun getChildRegions() {
        LegacySmritiRepository.service.getChildRegions().excute(object : BaseObserver<ChildRegion>() {
            override fun onSuccess(response: BaseResponse<ChildRegion>) {
                areas.postValue(response.datas)
            }
            override fun onFailed(response: BaseResponse<ChildRegion>) {
                mError.postValue(response)
            }
        })
    }

    // 类型
    var typeBeans= MutableLiveData<MutableList<TypeBean>>()

    /**
     *级别筛选
     */
    fun getType(type:String) {
        val map = HashMap<String, Any>()
        map["type"]=type
        LegacySmritiRepository.service.getSearchType(map).excute(object : BaseObserver<TypeBean>(){
            override fun onSuccess(response: BaseResponse<TypeBean>) {
                typeBeans.postValue(response.datas)
            }

        })
    }

    var canceCollectLiveData = MutableLiveData<Int>()

    var collectVenueLiveData = MutableLiveData<Int>()

    /**
     * 收藏
     */
    fun collect(id: String, position: Int) {
        CommentRepository.service.posClloection(id, ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                collectVenueLiveData.postValue(position)
                ToastUtils.showMessage("收藏成功~")
            }

            override fun onFailed(response: BaseResponse<Any>) {
                ToastUtils.showMessage("收藏失败，请稍后再试~")
            }
        })
    }

    /**
     * 取消收藏
     */
    fun canceCollect(id: String, position: Int) {
        CommentRepository.service.posCollectionCancel(id, ResourceType.CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE).excute(object : BaseObserver<Any>() {
            override fun onSuccess(response: BaseResponse<Any>) {
                canceCollectLiveData?.postValue(position)
                ToastUtils.showMessage("取消收藏成功~")
            }

            override fun onFailed(response: BaseResponse<Any>) {
                ToastUtils.showMessage("取消收藏，请稍后再试~")
            }
        })
    }
}
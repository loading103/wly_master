package com.daqsoft.legacyModule.smriti.fragment


import android.os.Bundle
import android.os.Parcelable
import android.util.Log
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
import com.daqsoft.legacyModule.adapter.LegacyHeritagePeopleAdapter
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
import com.daqsoft.legacyModule.smriti.NATIONALITY
import com.daqsoft.legacyModule.smriti.bean.LegacyBehalfBean
import com.daqsoft.legacyModule.smriti.bean.LegacyPeopleBean
import com.daqsoft.legacyModule.smriti.bean.SexBean
import com.daqsoft.legacyModule.smriti.bean.TypeBean
import com.daqsoft.legacyModule.smriti.event.UpdateFocusStatusEvent
import com.daqsoft.legacyModule.smriti.event.UpdateListFocusStatusEvent
import com.daqsoft.legacyModule.smriti.net.LegacySmritiRepository
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.onRefresh

/**
 * @des ?????????????????????
 * @author caihj
 * @Date 2020/5/11 15:38
 */
internal class HeritagePeopleFragment:
    BaseFragment<FragmentLegacyPepoleBinding, HeritagePeopleVM>() {

    companion object{
        fun newInstance():HeritagePeopleFragment{
            return HeritagePeopleFragment()
        }
    }
    /**
     * ????????????????????????
     */
    private var areaListPopWindow: AreaSelectPopupWindow? = null

    // ????????????
    var nationalityPopupWindow: ListPopupWindow<Any>? = null
    // ????????????
    var sexPopupWindow:ListPopupWindow<Any>? =null

    override fun getLayout(): Int = R.layout.fragment_legacy_pepole


    override fun injectVm(): Class<HeritagePeopleVM> =
        HeritagePeopleVM::class.java

    private var rvAdapter:LegacyHeritagePeopleAdapter? = null
    override fun initView() {
        EventBus.getDefault().register(this)

        rvAdapter = context?.let { it1 -> LegacyHeritagePeopleAdapter(it1) }
        mBinding.rvPeople.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }
        rvAdapter?.emptyViewShow = true

        //????????????
        mModel.legacyHeritagePeopleList.observe(this, Observer {
            pageDealed(it as MutableList<LegacyHeritagePeopleListBean>?)
        })
        mModel.focusStatus.observe(this, Observer {
            if(it != -1){
                rvAdapter!!.notifyFocusStatus(postion = it)
            }
        })

        // ??????
        mModel.areas.observe(this, Observer {
            if (areaListPopWindow == null) {
                it.add(0, ChildRegion("", "??????", "", "", emptyList()))
                areaListPopWindow =
                    AreaSelectPopupWindow.getInstance(
                        BaseApplication.context, false
                    ) { item ->
                        mModel.areaSiteSwitch = item.siteId
                        mModel.region = item.region
                        mModel.mCurrPage = 1
                        showLoadingDialog()
                        if ( item .name=="??????"){
                            mBinding.tvArea.text ="??????"
                        }else{
                            mBinding.tvArea.text = item.name
                        }
                        mModel.getHomeDiscoverList()
                    }
                areaListPopWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                temp.add(0, ChildRegion("", "??????", "", "", emptyList()))
                areaListPopWindow!!.secondData = ArrayList(temp)
            }
        })

        // ??????
        mModel.nationalitys.observe(this, Observer {
            it?.add(0, TypeBean("","??????","","",""))
            nationalityPopupWindow = ListPopupWindow.getInstance(mBinding.tvNationality, it) { item ->

                kotlin.run {
                    if ( (item as TypeBean).name=="??????"){
                        mBinding.tvNationality.text ="??????"
                        mModel.nationality = ""
                    }else{
                        mBinding.tvNationality.text = item.name
                        mModel.nationality = item.name
                    }
                    showLoadingDialog()
                    mModel.mCurrPage = 1
                    mModel.getHomeDiscoverList()
                }
            }
        })
        val sex = mutableListOf<Any>(
            SexBean("??????",""),
            SexBean("???","1"),
            SexBean("???","0"))
        sexPopupWindow = ListPopupWindow.getInstance(mBinding.tvSex,sex){item ->
            kotlin.run {
                if ( (item as SexBean).name=="??????"){
                    mBinding.tvSex.text ="??????"
                    mModel.gender = ""
                }else{
                    mBinding.tvSex.text = item.name
                    mModel.gender = item.value
                }
                showLoadingDialog()
                mModel.mCurrPage = 1
                mModel.getHomeDiscoverList()            }
        }
        initEvent()
    }

    private fun initEvent(){
        mBinding.tvArea.onNoDoubleClick {
            if (areaListPopWindow != null) {
                areaListPopWindow!!.show(mBinding.tvArea)
            }
        }
        mBinding.tvNationality.onNoDoubleClick {
            if(nationalityPopupWindow!= null){
                nationalityPopupWindow!!.show()
            }
        }
        mBinding.tvSex.onNoDoubleClick {
            if(sexPopupWindow!=null){
                sexPopupWindow!!.show()
            }
        }

        rvAdapter?.setOnLoadMoreListener {
            mModel.mCurrPage++
            mModel.getHomeDiscoverList()
        }
        rvAdapter?.onItemClick = object :LegacyHeritagePeopleAdapter.OnItemClickListener{
            override fun onItemClick(id: String, position: Int, status: Boolean) {
                if (status){
                    mModel.focusHeritagePeople(id,position)
                }else{
                    mModel.unsubscribe(id,position)
                }
            }

        }
        mBinding.sfLegacyPeople.setOnRefreshListener {
//            mBinding.sfLegacyPeople.isRefreshing = true
            mModel.mCurrPage = 1
            mModel.getHomeDiscoverList()
        }
    }


    override fun initData() {
        mModel.getHomeDiscoverList()
        // ??????????????????
        mModel.getChildRegions()
        // ??????????????????
        mModel.getNationality(NATIONALITY)
    }

    private fun pageDealed(it: MutableList<LegacyHeritagePeopleListBean>?) {
        dissMissLoadingDialog()
//        mBinding.sfLegacyPeople.isRefreshing = false
        mBinding.sfLegacyPeople.finishRefresh()
        if (!mBinding.rvPeople.isVisible) {
            mBinding.rvPeople.visibility = View.VISIBLE
        }
        if (mModel.mCurrPage == 1) {
            if(!it.isNullOrEmpty())
            mBinding.rvPeople.smoothScrollToPosition(0)
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateFocusStatus(event: UpdateFocusStatusEvent) {
        rvAdapter!!.notifyFocusStatus(postion = event.position)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}

class HeritagePeopleVM:BaseViewModel(){
    val legacyHeritagePeopleList = MutableLiveData<List<LegacyHeritagePeopleListBean>>()
    var nationality =""
    var gender = ""
    fun getHomeDiscoverList() {

        LegacyRepository.service.getHeritagePeopleList(
            pageSize = 10,
            currPage = mCurrPage,
            gender = gender,
            nationality = nationality,
            areaSiteSwitch= areaSiteSwitch,
            region = region)
            .excute(object : BaseObserver<LegacyHeritagePeopleListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyHeritagePeopleListBean>) {
                    legacyHeritagePeopleList.postValue(response.datas)
                }
            })
    }

    var focusStatus = MutableLiveData<Int>()

    fun focusHeritagePeople(id:String,position:Int){
        CommentRepository.service.attentionResource(id,ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object :BaseObserver<Any>(mPresenter){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    ToastUtils.showMessage("????????????!")
                    EventBus.getDefault().post(UpdateListFocusStatusEvent(id, true, position))
                    focusStatus.postValue(position)
                }else{
                    ToastUtils.showMessage(response.message)
                    focusStatus.postValue(-1)
                }
            }
        })
    }


    /**
     * ????????????
     * @param id String
     * @param position Int
     */
    fun unsubscribe(id:String,position:Int){
        CommentRepository.service.attentionResourceCancle(id,ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object :BaseObserver<Any>(mPresenter){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    ToastUtils.showMessage("????????????!")
                    EventBus.getDefault().post(UpdateListFocusStatusEvent(id, false, position))
                    focusStatus.postValue(position)
                }else{
                    ToastUtils.showMessage(response.message)
                    focusStatus.postValue(-1)
                }
            }
        })
    }


    /**
     * ??????
     */
    val areas = MutableLiveData<MutableList<ChildRegion>>()
    //??????
    var type = ""
    var region = ""
    var areaSiteSwitch = ""
    var mCurrPage = 1
    var pageSize =10
    /**
     * ??????????????????(??????)
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


    var nationalitys= MutableLiveData<MutableList<TypeBean>>()

    /**
     *????????????
     */
    fun getNationality(type:String) {
        val map = HashMap<String, Any>()
        map["type"]=type
        LegacySmritiRepository.service.getSearchType(map).excute(object : BaseObserver<TypeBean>(){
            override fun onSuccess(response: BaseResponse<TypeBean>) {
                nationalitys.postValue(response.datas)
            }

        })
    }
}
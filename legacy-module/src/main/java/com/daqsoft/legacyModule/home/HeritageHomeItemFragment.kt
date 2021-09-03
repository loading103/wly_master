package com.daqsoft.legacyModule.home


import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
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
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.adapter.LegacyHeritageAdapter
import com.daqsoft.legacyModule.bean.LegacyHeritageItemListBean
import com.daqsoft.legacyModule.databinding.*
import com.daqsoft.legacyModule.rv.dsl.grid
import com.daqsoft.legacyModule.rv.dsl.linear
import com.daqsoft.provider.view.convenientbanner.utils.ScreenUtil
import kotlinx.android.parcel.Parcelize
import java.lang.Exception
import com.daqsoft.legacyModule.getRealImages
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.legacyModule.smriti.event.UpdateFocusStatusEvent
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.comment.CommentRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @des 首页非遗项目
 * @author caihj
 * @Date 2020/5/11 15:38
 */
internal class HeritageHomeItemFragment:
    BaseFragment<LegacyModuleFragmentHomeHeritageBinding, HeritageItemVM>() {

    companion object{
        fun newInstance():HeritageHomeItemFragment{
            return HeritageHomeItemFragment()
        }
    }

    override fun getLayout(): Int = R.layout.legacy_module_fragment_home_heritage


    override fun injectVm(): Class<HeritageItemVM> =
        HeritageItemVM::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private var rvAdapter:LegacyHeritageAdapter? = null

    override fun initView() {
         rvAdapter = context?.let { it1 -> LegacyHeritageAdapter(it1) }
        //发现非遗
        mModel.homeDiscoverList.observe(this, Observer {
            mBinding.rvHeritage.isVisible = it.isNotEmpty()
            mBinding.rvHeritage.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = rvAdapter
            }
            rvAdapter?.add(it.toMutableList())
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
            rvAdapter?.notifyCollectStatus(it)
        })

        // 取消收藏
        mModel.canceCollectLiveData.observe(this, Observer {
            rvAdapter!!.notifyCollectStatus(it)
        })

    }



    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateFocusStatus(event: UpdateFocusStatusEvent) {
        rvAdapter!!.notifyCollectStatus(postion = event.position)
    }

    override fun initData() {
        mModel.getHomeDiscoverList()
    }
}

class HeritageItemVM:BaseViewModel(){
    val homeDiscoverList = MutableLiveData<List<LegacyHeritageItemListBean>>()
    fun getHomeDiscoverList() {
        LegacyRepository.service.getHeritageItemList("1", "3", "", "", "", "", "", "")
            .excute(object : BaseObserver<LegacyHeritageItemListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyHeritageItemListBean>) {
                    homeDiscoverList.postValue(response.datas)
                }
            })
    }
    var canceCollectLiveData = MutableLiveData<Int>()

    var collectVenueLiveData = MutableLiveData<Int>()

    /**
     * 收藏
     */
    fun collect(id: String, position: Int) {
        CommentRepository.service.posClloection(id, ResourceType.CONTENT_TYPE_HERITAGE_PROTECT_BASE).excute(object : BaseObserver<Any>() {
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
        CommentRepository.service.posCollectionCancel(id, ResourceType.CONTENT_TYPE_HERITAGE_PROTECT_BASE).excute(object : BaseObserver<Any>() {
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
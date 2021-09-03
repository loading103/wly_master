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
import com.daqsoft.legacyModule.smriti.bean.LegacyPeopleBean
import com.daqsoft.legacyModule.smriti.event.UpdateFocusStatusEvent
import com.daqsoft.legacyModule.smriti.event.UpdateListFocusStatusEvent
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.network.comment.CommentRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @des 首页非遗传承人
 * @author caihj
 * @Date 2020/5/11 15:38
 */
internal class HeritageHomePeopleFragment :
    BaseFragment<LegacyModuleFragmentHomeHeritageBinding, HeritagePeopleVM>() {

    companion object {
        fun newInstance(): HeritageHomePeopleFragment {
            return HeritageHomePeopleFragment()
        }
    }

    override fun getLayout(): Int = R.layout.legacy_module_fragment_home_heritage


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun injectVm(): Class<HeritagePeopleVM> =
        HeritagePeopleVM::class.java

    private var rvAdapter: LegacyHeritagePeopleAdapter? = null

    override fun initView() {
        rvAdapter = context?.let { it1 -> LegacyHeritagePeopleAdapter(it1) }
        //发现非遗
        mModel.legacyHeritagePeopleList.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.rvHeritage.isVisible = it.isNotEmpty()

            mBinding.rvHeritage.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = rvAdapter
            }
            rvAdapter?.add(it.toMutableList())
            if (rvAdapter != null) {
                rvAdapter?.onItemClick = object : LegacyHeritagePeopleAdapter.OnItemClickListener {
                    override fun onItemClick(id: String, position: Int, status: Boolean) {
                        if (status){
                            mModel.focusHeritagePeople(id,position)
                        }else{
                            mModel.unsubscribe(id,position)
                        }
                    }

                }
            }
        })
        mModel.focusStatus.observe(this, Observer {
            if (it != -1) {
                rvAdapter!!.notifyFocusStatus(postion = it)
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateFocusStatus(event: UpdateFocusStatusEvent) {
        Log.e("update", "==========" + event.position)
        rvAdapter!!.notifyFocusStatus(postion = event.position)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateListFocusStatus(event: UpdateListFocusStatusEvent) {
        rvAdapter!!.notifyFocusStatus(postion = event.position)
    }



    override fun onResume() {
//        showLoadingDialog()
//        rvAdapter?.clear()
//        mModel.getHomeDiscoverList()
        super.onResume()
    }

    override fun initData() {
        mModel.getHomeDiscoverList()

    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}

class HeritagePeopleVM : BaseViewModel() {
    val legacyHeritagePeopleList = MutableLiveData<List<LegacyHeritagePeopleListBean>>()
    fun getHomeDiscoverList() {
        LegacyRepository.service.getHeritagePeopleList(1, 3)
            .excute(object : BaseObserver<LegacyHeritagePeopleListBean>() {
                override fun onSuccess(response: BaseResponse<LegacyHeritagePeopleListBean>) {
                    legacyHeritagePeopleList.postValue(response.datas)
                }
            })
    }

    var focusStatus = MutableLiveData<Int>()

    fun focusHeritagePeople(id: String, position: Int) {
        CommentRepository.service.attentionResource(id, ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        ToastUtils.showMessage("关注成功!")
                        focusStatus.postValue(position)
                    } else {
                        ToastUtils.showMessage(response.message)
                        focusStatus.postValue(-1)
                    }
                }
            })
    }

    /**
     * 取消关注
     * @param id String
     * @param position Int
     */
    fun unsubscribe(id:String,position:Int){
        CommentRepository.service.attentionResourceCancle(id,ResourceType.CONTENT_TYPE_HERITAGE_PEOPLE).excute(object :BaseObserver<Any>(mPresenter){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    ToastUtils.showMessage("取消关注!")
                    focusStatus.postValue(position)
                }else{
                    ToastUtils.showMessage(response.message)
                    focusStatus.postValue(-1)
                }
            }
        })
    }
}
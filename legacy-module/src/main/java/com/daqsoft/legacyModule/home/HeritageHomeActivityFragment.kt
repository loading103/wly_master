package com.daqsoft.legacyModule.home

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.FragHeritageHomeActivitiesBinding
import com.daqsoft.legacyModule.home.event.HeritageActivityQuantityEvent
import com.daqsoft.provider.bean.ActivityBean
import com.daqsoft.provider.businessview.adapter.ProviderActivityAdapter
import com.daqsoft.provider.network.home.HomeRepository
import org.greenrobot.eventbus.EventBus
import java.util.HashMap

/**
 * @Description 非遗活动
 * @ClassName   HeritageHomeActivityFragment
 * @Author      luoyi
 * @Time        2020/10/13 10:13
 */
class HeritageHomeActivityFragment :
    BaseFragment<FragHeritageHomeActivitiesBinding, HeritageHomeActivityVm>() {
    var classId: String? = ""
    val activityAdapter: ProviderActivityAdapter by lazy {
        ProviderActivityAdapter(context!!)
    }

    companion object {
        const val CLASS_ID: String = "class_id"
        fun newIntance(classId: String): HeritageHomeActivityFragment {
            var fragment = HeritageHomeActivityFragment()
            var bundle: Bundle = Bundle()
            bundle.putString(CLASS_ID, classId)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.frag_heritage_home_activities
    }

    override fun injectVm(): Class<HeritageHomeActivityVm> {
        return HeritageHomeActivityVm::class.java
    }

    override fun initView() {
        mBinding.rvHeritageHomeActivities.run {
            layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            this.adapter = activityAdapter
        }
        mModel.activities.observe(this, Observer {
            activityAdapter.clear()
            if (!it.isNullOrEmpty()) {
                activityAdapter.add(it)
                EventBus.getDefault().post(HeritageActivityQuantityEvent(it.size))
            }else{
                EventBus.getDefault().post(HeritageActivityQuantityEvent(-1))
            }

        })
    }

    override fun initData() {
        classId = arguments?.getString(CLASS_ID, "")
        classId?.let {
            mModel.getActivityList(it)
        }
    }
}

class HeritageHomeActivityVm : BaseViewModel() {

    var activities: MutableLiveData<MutableList<ActivityBean>> = MutableLiveData()
    /**
     * 获取活动列表
     */
    fun getActivityList(classId: String) {
        val param = HashMap<String, String>()
        // orderType  为空(默认排序) 1 首页列表 2 距离优先 3 人气优先(活动模板中筛选) 4 最新 5 志愿团队 6 社团活动
        param["orderType"] = "7"
        param["pageSize"] = "3"
        param["classifyId"] = classId
        param["notEndStatus"] = "1"
        param["currPage"] = "1"
        mPresenter.value?.loading = false
        HomeRepository.service.getActivityList(param)
            .excute(object : BaseObserver<ActivityBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ActivityBean>) {
                    activities.postValue(response.datas)
                }

                override fun onFailed(response: BaseResponse<ActivityBean>) {
                    activities.postValue(null)
                }
            })
    }
}
package com.daqsoft.travelCultureModule.clubActivity

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityClubPersonListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.travelCultureModule.clubActivity.adapter.ClubPersonAdapter
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubPersonBean
import com.daqsoft.travelCultureModule.net.MainRepository

/**
 * @des      社团成员列表
 * @author   Wongxd
 * @date     2020-4-27  16:16
 */
@Route(path = MainARouterPath.MAIN_CLUB_PERSON_LIST)
internal class ClubPersonListActivity : TitleBarActivity<ActivityClubPersonListBinding, ClubPersonListViewModel>() {


    override fun getLayout(): Int = R.layout.activity_club_person_list

    override fun setTitle(): String = "成员列表"

    override fun injectVm(): Class<ClubPersonListViewModel> = ClubPersonListViewModel::class.java


    @Autowired
    @JvmField
    var clubId = ""


    private val rvAdapter by lazy {
        ClubPersonAdapter(this).apply {
            setOnLoadMoreListener {
                mModel.pageManager.nexPageIndex
                mModel.getClubPersonList(clubId)
            }
        }
    }

    override fun initView() {

        mBinding.srl.setOnRefreshListener {
//            mBinding.srl.isRefreshing = false
            refreshData()
        }


        mBinding.rv.apply {
            layoutManager = LinearLayoutManager(this@ClubPersonListActivity)
            adapter = rvAdapter
        }


        mModel.clubPersonList.observe(this, Observer {
            mBinding.srl.finishRefresh()

            mBinding.rv.visibility = View.VISIBLE

            rvAdapter.loadComplete()

            if (mModel.pageManager.isFirstIndex) {
                rvAdapter.clearNotify()
            } else {
                if (it.isNullOrEmpty()) rvAdapter.loadEnd()
            }


            rvAdapter.add(it)

        })

    }

    private fun refreshData() {
        mModel.pageManager.initPageIndex()
        mModel.getClubPersonList(clubId)
    }

    override fun initData() {
        refreshData()
    }
}


internal class ClubPersonListViewModel : BaseViewModel() {


    val pageManager = PageManager(10)

    val clubPersonList = MutableLiveData<MutableList<ClubPersonBean>>()

    fun getClubPersonList(id: String) {
        mPresenter.value?.loading = true
        MainRepository.service.getClubAllPersonList(id, pageManager.pageIndex, pageManager.pageSize)
            .excute(object : BaseObserver<ClubPersonBean>() {
                override fun onSuccess(response: BaseResponse<ClubPersonBean>) {
                    clubPersonList.postValue(response.datas)
                }

            })
    }
}
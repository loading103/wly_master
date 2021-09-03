package com.daqsoft.travelCultureModule.clubActivity

import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityClubBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.provider.view.ListPopupWindow
import com.daqsoft.travelCultureModule.clubActivity.ClubAdapter.CallBack
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubBean
import com.daqsoft.travelCultureModule.clubActivity.bean.TypeBean
import com.daqsoft.travelCultureModule.clubActivity.vm.ClubActicityViewModel
import com.daqsoft.travelCultureModule.contentActivity.hideKeyboard

@Route(path = MainARouterPath.MAIN_CLUB)
class ClubActicity : TitleBarActivity<ActivityClubBinding, ClubActicityViewModel>() {
    /**
     * 社团列表
     */
    val clubList = MutableLiveData<MutableList<ClubBean>>()
    var page = 1
    var pageSize = 6
    var region = ""//地区
    var type = ""//类型
    var content = ""//搜索内容
    lateinit var pop_area: ListPopupWindow<Any>
    lateinit var pop_type: ListPopupWindow<Any>
    override fun setTitle(): String = "爱社团"

    override fun getLayout(): Int = R.layout.activity_club

    override fun injectVm(): Class<ClubActicityViewModel> = ClubActicityViewModel::class.java

    override fun initView() {
        //社团搜索
        mBinding.etClubSearch!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo
                    .IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
            ) {
                content = mBinding.etClubSearch.text.toString()
                page = 1
                reloadData()
                hideKeyboard(this@ClubActicity)
            }
            false
        })
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvClub.layoutManager = tagLayoutManager
        val clubAdapter = ClubAdapter(this, object : CallBack {
            override fun postData(id: String, guanzhu: Boolean) {
                Log.e("e", id + ":" + guanzhu)
                if (guanzhu) {
                    mModel.attentionResource(id, ResourceType.CONTENT_TYPE_ASSOCIATION)
                } else {
                    mModel.attentionResourceCancle(id, ResourceType.CONTENT_TYPE_ASSOCIATION)
                }
            }
        })
        mBinding.rvClub.adapter = clubAdapter
        mModel.clubList.observe(this, Observer {
            mBinding.mSwipeRefreshLayout.finishRefresh()
            var data = it
            pageDeal(page, it, clubAdapter)
            clubAdapter.add(data)
            clubAdapter.notifyDataSetChanged()
        })
        clubAdapter.setOnLoadMoreListener {
            page++
            reloadData()
        }
        mBinding.mSwipeRefreshLayout.setOnRefreshListener {
//            mBinding.mSwipeRefreshLayout.isRefreshing = false
            page = 1
            reloadData()
        }
        mModel.areas.observe(this@ClubActicity, Observer {
            it.add(0, ChildRegion("", "全部", "", "", null, 1))
            pop_area = ListPopupWindow.getInstance(mBinding.tvClubArea, it, ListPopupWindow.WindowDataBack<ChildRegion> {
                region = it.siteId
                reloadData()
                if (it.name == "全部") {
                    mBinding.tvClubArea.text = "地区"
                } else {
                    mBinding.tvClubArea.text = it.name
                }
            })
            mBinding.tvClubArea.setOnClickListener {
                pop_area.show()
            }
        })

        mModel.types.observe(this@ClubActicity, Observer {
            it.add(0, TypeBean("", "全部", "", "", ""))
            pop_type = ListPopupWindow.getInstance(mBinding.tvClubType, it, ListPopupWindow.WindowDataBack<TypeBean> {
                type = it.id
                reloadData()
                if (it.name == "全部") {
                    mBinding.tvClubType.text = "分类"
                } else {
                    mBinding.tvClubType.text = it.name
                }
            })
            mBinding.tvClubType.setOnClickListener {
                pop_type.show()
                //  mBinding.tvClubType.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,resources.getDrawable(), null)
            }
        })

        mModel.getChildRegions()
        mModel.getType(ResourceType.TYPE_ASSOCIATION)
    }

    override fun initData() {
        mModel.getClubList(content, region, type, page.toString(), pageSize.toString())
        StatisticsRepository.instance.statisticsPage(title, 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        StatisticsRepository.instance.statisticsPage(title, 2)
    }

    private fun pageDeal(page: Int?, response: MutableList<ClubBean>, adapter: RecyclerViewAdapter<*, *>) {
        if (page == null) {
            return
        }
        if (page == 1) {
            adapter.clear()
        }
        if (response == null) {
            adapter.loadEnd()
            return
        }
        if (response.size >= pageSize) {
            adapter.loadComplete()
        } else {
            adapter.loadEnd()
        }
    }
}
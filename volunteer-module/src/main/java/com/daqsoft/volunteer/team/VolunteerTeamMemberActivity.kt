package com.daqsoft.volunteer.team

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
import com.daqsoft.provider.ARouterPath
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerMemberBean
import com.daqsoft.volunteer.databinding.ActivityTeamMemberListBinding
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.team.adapter.VolunteerTeamMemberAdapter

/**
 *@package:com.daqsoft.volunteer.team
 *@date:2020/6/8 17:13
 *@author: caihj
 *@des:团队成员
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_TEAM_MEMBERS)
class VolunteerTeamMemberActivity:TitleBarActivity<ActivityTeamMemberListBinding,VolunteerTeamMemberVM>() {

    @Autowired
    @JvmField
    var id = ""
    private val memberAdapter = VolunteerTeamMemberAdapter(this)
    private val memberManageAdapter = VolunteerTeamMemberAdapter(this)

    override fun getLayout(): Int = R.layout.activity_team_member_list

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_team_member_title)

    override fun injectVm(): Class<VolunteerTeamMemberVM> = VolunteerTeamMemberVM::class.java
    override fun initView() {
        mBinding.rvHost.apply {
            layoutManager = LinearLayoutManager(this@VolunteerTeamMemberActivity)
            adapter = memberManageAdapter
        }
        mBinding.rvMembers.apply {
            layoutManager = LinearLayoutManager(this@VolunteerTeamMemberActivity)
            adapter = memberAdapter
        }
        mModel.memeberManager.observe(this, Observer {
            if(it.size > 0){
                mBinding.tvNum1.text = "(${it.size})"
                memberManageAdapter.add(it)
            }
        })
        mModel.memebers.observe(this, Observer {
            dissMissLoadingDialog()

            if(it.size > 0){
                mBinding.tvNum2.text = "(${it.size})"
                pageDel(it)
            }else{
                mBinding.llMember.visibility = View.GONE
            }
        })
        memberAdapter.setOnLoadMoreListener {
            mModel.currPage++
            mModel.getMembers(id,"member")
        }
    }

    fun pageDel(datas:MutableList<VolunteerMemberBean>){
        if (mModel.currPage == 1) {
            mBinding.rvMembers.smoothScrollToPosition(0)
            mBinding.rvMembers.visibility = View.VISIBLE
            memberAdapter.clear()
            memberAdapter.emptyViewShow = datas.isNullOrEmpty()
        }
        if(!datas.isNullOrEmpty()){
            memberAdapter.add(datas)
        }

        if (datas.isNullOrEmpty() || datas.size < mModel.pageSize) {
            memberAdapter.loadEnd()
        } else {
            memberAdapter.loadComplete()
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getMembers(id,"manage")
        mModel.getMembers(id,"member")

    }
}

class VolunteerTeamMemberVM:BaseViewModel(){

    val memebers = MutableLiveData<MutableList<VolunteerMemberBean>>()
    val memeberManager = MutableLiveData<MutableList<VolunteerMemberBean>>()
    var currPage = 1
    var pageSize = 10
    fun getMembers(id:String,type:String){
        val params = HashMap<String,String>()
        params["teamId"] = id
        params["pageSize"] = pageSize.toString()
        params["currPage"] = currPage.toString()
        params["founder"] = (type == "manage").toString()

        VolunteerRepository.service.getVolunteerTeamMembers(params).excute(object : BaseObserver<VolunteerMemberBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerMemberBean>) {
                if(type == "manage"){
                    memeberManager.postValue(response.datas)
                }else{
                    memebers.postValue(response.datas)
                }
            }
        })
    }

}
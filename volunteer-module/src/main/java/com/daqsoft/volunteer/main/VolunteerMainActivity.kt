package com.daqsoft.volunteer.main

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.bean.ChildRegion
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.view.popupwindow.AreaSelectPopupWindow
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.RankData
import com.daqsoft.volunteer.bean.VolunteerMenuBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerMainBinding
import com.daqsoft.volunteer.databinding.VolunteerMenuItemBinding
import com.daqsoft.volunteer.databinding.VolunteerNewsListItemBinding
import com.daqsoft.volunteer.main.adapter.VolunteerActivityAdapter
import com.daqsoft.volunteer.main.adapter.VolunteerFocusAdapter
import com.daqsoft.volunteer.main.vm.VolunteerMainVM
import com.daqsoft.volunteer.utils.CalendarUtils
import com.daqsoft.volunteer.utils.Constant
import com.daqsoft.volunteer.utils.EnumGlobal
import com.daqsoft.volunteer.utils.JumpUtils

/**
 *@package:com.daqsoft.volunteer.main
 *@date:2020/6/1 17:31
 *@author: caihj
 *@des:志愿者首页
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_INDEX)
class VolunteerMainActivity:TitleBarActivity<ActivityVolunteerMainBinding,VolunteerMainVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_main

    override fun setTitle(): String = getString(R.string.module_name)

    override fun injectVm(): Class<VolunteerMainVM> = VolunteerMainVM::class.java

    override fun initView() {
        mModel.volunteerCount.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.tvVolunteerNum.text = it.volunteerNum.toString()
            mBinding.tvVolunteerTeamNum.text = it.volunteerTeamNum.toString()
        })
        mModel.siteBean.observe(this, Observer {
            if (!it.regionName.isNullOrEmpty()) {
                mBinding.tvRegion.text = it.regionName
                mModel.getChildRegions()
                mModel.getVolunteerCount(it.region!!)
                getRankData(it.region!!)
            } else {
                mBinding.tvRegion.visibility = View.GONE
            }

        })
        mModel.areas.observe(this, Observer {
            dissMissLoadingDialog()
            if (areaListPopupWindow == null) {
                areaListPopupWindow =
                    AreaSelectPopupWindow.getInstance(
                        BaseApplication.context, false
                    ) { item ->
                        showLoadingDialog()
                        mBinding.tvRegion.text = (item as ChildRegion).name
                        mModel.getVolunteerCount(item.region)

                    }
                areaListPopupWindow!!.firstData = it
                var temp: MutableList<ChildRegion> = mutableListOf()
                areaListPopupWindow!!.secondData = ArrayList(temp)
            }
        })

        mModel.volunteerFocus.observe(this, Observer {
            if (it.size > 0) {
                focusAdapter.add(it)
            } else {
               hideFocus()
            }
        })
        mModel.volunteerIntegralRank.observe(this, Observer {
            if(it.data.isNullOrEmpty()){
                mBinding.vrScoreService.visibility = View.GONE
                return@Observer
            }
            mBinding.vrScoreService.setRankData(it.data as MutableList<RankData>)
        })
        mModel.volunteerServiceRank.observe(this, Observer {
            if(it.data.isNullOrEmpty()){
                mBinding.vrService.visibility =View.GONE
                return@Observer
            }
            mBinding.vrService.setRankData(it.data as MutableList<RankData>)
        })

        mModel.volunteerTeamIntegralRank.observe(this, Observer {
            if(it.data.isNullOrEmpty()){
                mBinding.vrScoreTeamService.visibility = View.GONE
                return@Observer
            }
            mBinding.vrScoreTeamService.setRankData(it.data as MutableList<RankData>)
        })

        mModel.volunteerTeamServiceRank.observe(this, Observer {
            if(it.data.isNullOrEmpty()){
                mBinding.vrTeamService.visibility = View.GONE
                return@Observer
            }
            mBinding.vrTeamService.setRankData(it.data as MutableList<RankData>)
        })

        mModel.activities.observe(this, Observer {
            if(!it.isNullOrEmpty()){
                volunteerActivityAdapter.add(it)
            }else{
                mBinding.tmVolunteer.visibility = View.GONE
                mBinding.rvVolunteerActivity.visibility = View.GONE
            }
        })
        mModel.collectLiveData.observe(this, Observer {
            volunteerActivityAdapter?.notifyUpdateCollectStatus(it, true)
        })
        mModel.canceCollectLiveData.observe(this, Observer {
            volunteerActivityAdapter?.notifyUpdateCollectStatus(it, false)
        })
        mModel.volunteerNews.observe(this, Observer {
            if(!it.isNullOrEmpty()){
                newsAdapter.add(it)
            }else{
                mBinding.tmNews.visibility = View.GONE
                mBinding.rvVolunteerNews.visibility = View.GONE
            }
        })

        mBinding.rvVolunteerFocus.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvVolunteerFocus.adapter = focusAdapter
        mBinding.rvVolunteerActivity.layoutManager =LinearLayoutManager(this)
        mBinding.rvVolunteerActivity.adapter = volunteerActivityAdapter
        mBinding.rvVolunteerNews.layoutManager = LinearLayoutManager(this)
        mBinding.rvVolunteerNews.adapter = newsAdapter
        initEvent()
    }

    private val  volunteerActivityAdapter = VolunteerActivityAdapter(this)

    /**
     * 弹窗
     */
    private var areaListPopupWindow: AreaSelectPopupWindow? = null


    override fun initData() {
        initMenuData()
        val region = SPUtils.getInstance().getString(SPKey.REGION_NAME, "")
        val regionCode = SPUtils.getInstance().getString(SPKey.REGION)
        if (region.isNullOrEmpty()) {
            mBinding.tvRegion.text = region
            mModel.getChildRegions()
            mModel.getVolunteerCount(regionCode)
           getRankData(regionCode)
        } else {
            mModel.siteInfo()
        }
        if(AppUtils.isLogin()){
            mModel.getVolunteerFocus()
        }else{
            hideFocus()
        }
        mModel.getVolunteerActivityList()
        mModel.getVolunteerNews()
    }

    private fun getRankData(regionCode:String){
        mModel.getRankList(Constant.INTEGRAL,Constant.VOLUNTEER,regionCode)
        mModel.getRankList(Constant.INTEGRAL,Constant.VOLUNTEER_TEAM,regionCode)
        mModel.getRankList(Constant.SERVICE_TIME,Constant.VOLUNTEER,regionCode)
        mModel.getRankList(Constant.SERVICE_TIME,Constant.VOLUNTEER_TEAM,regionCode)
    }

    private fun initMenuData() {
        val menus = resources.getStringArray(R.array.volunteer_module_menus)
        val logos = resources.obtainTypedArray(R.array.volunteer_module_menus_icons)
        val keys = resources.getStringArray(R.array.volunteer_module_menus_key)
        val menuDatas = mutableListOf<VolunteerMenuBean>()
        for (index in menus.indices) {
            val menuBean = VolunteerMenuBean(
                title = menus[index],
                icon = logos.getResourceId(index, 0),
                key = keys[index]
            )
            menuDatas.add(menuBean)
        }
        mBinding.rvMenu.apply {
            layoutManager = GridLayoutManager(this@VolunteerMainActivity, 4)
            adapter = menuAdapter
        }
        menuAdapter.add(menuDatas)
    }

    private fun initEvent() {
        mBinding.tvVolunteerRegister.onNoDoubleClick {
            val volunteerStatus = SPUtils.getInstance().getString(SPKey.VOLUNTEER,"0")
            if(volunteerStatus == "0" || volunteerStatus == ""){
                JumpUtils.gotoVolunteerRegister()
            }else{
                JumpUtils.gotoVolunteerApplyDetail()
            }
        }
        mBinding.tvVolunteerTeamReg.onNoDoubleClick {
            val volunteerTeamStatus = SPUtils.getInstance().getString(SPKey.VOLUNTEER_TEAM,"0")
              if(volunteerTeamStatus == "0" || volunteerTeamStatus == ""){
                 JumpUtils.gotoVolunteerTeamRegister()
              }else{
                 JumpUtils.gotoVolunteerTeamApplyDetail()
              }
        }
        mBinding.tvRegion.onNoDoubleClick {
            areaListPopupWindow!!.show(mBinding.tvRegion)
        }
        mBinding.tmNews.onNoDoubleClick {
            JumpUtils.gotoNewsList()
        }
        mBinding.tmVolunteer.onNoDoubleClick {
            JumpUtils.gotoVolunteerActivity()
        }
        mBinding.tvVolunteer.onNoDoubleClick {
            JumpUtils.gotoVolunteerList()
        }
        mBinding.tvVolunteerNum.onNoDoubleClick {
            JumpUtils.gotoVolunteerList()
        }

        mBinding.tvVolunteerTeamNum.onNoDoubleClick {
            JumpUtils.gotoVolunteerTeamList()
        }
        mBinding.tvVolunteerTeam.onNoDoubleClick {
            JumpUtils.gotoVolunteerTeamList()
        }

        volunteerActivityAdapter.onItemClick = object :VolunteerActivityAdapter.OnItemClickListener{
            override fun onItemClick(id: String, position: Int,status:Boolean) {
                if(status){
                    mModel.collectionCancel(id,position)
                }else{
                    mModel.collection(id,position)
                }
            }

        }

        // 2020/9/2 新增榜单跳转
        mBinding.run {
            vrScoreService.onNoDoubleClick {
                JumpUtils.goToVolunteerRankList(
                    EnumGlobal.RankingTypeEnum.INTEGRAL.value,
                    EnumGlobal.ListClassEnum.VOLUNTEER.value
                )
            }
            vrService.onNoDoubleClick {
                JumpUtils.goToVolunteerRankList(
                    EnumGlobal.RankingTypeEnum.SERVICETIME.value,
                    EnumGlobal.ListClassEnum.VOLUNTEER.value
                )
            }
            vrScoreTeamService.onNoDoubleClick {
                JumpUtils.goToVolunteerRankList(
                    EnumGlobal.RankingTypeEnum.INTEGRAL.value,
                    EnumGlobal.ListClassEnum.VOLUNTEERTEAM.value
                )
            }
            vrTeamService.onNoDoubleClick {
                JumpUtils.goToVolunteerRankList(
                    EnumGlobal.RankingTypeEnum.SERVICETIME.value,
                    EnumGlobal.ListClassEnum.VOLUNTEERTEAM.value
                )
            }
        }
    }



    private fun hideFocus(){
        mBinding.tmFocus.visibility = View.GONE
        mBinding.rvVolunteerFocus.visibility = View.GONE
    }

    private val menuAdapter = object :
        RecyclerViewAdapter<VolunteerMenuItemBinding, VolunteerMenuBean>(R.layout.volunteer_menu_item) {
        override fun setVariable(
            mBinding: VolunteerMenuItemBinding,
            position: Int,
            item: VolunteerMenuBean
        ) {
            mBinding.tvTitle.text = item.title
            Glide.with(this@VolunteerMainActivity).load(item.icon).into(mBinding.avIcon)
            mBinding.root.onNoDoubleClick {
                JumpUtils.menuJump(item.key)
            }
        }

    }
    private val focusAdapter = VolunteerFocusAdapter(this)

    private val newsAdapter = object :RecyclerViewAdapter<VolunteerNewsListItemBinding,HomeContentBean>(R.layout.volunteer_news_list_item){
        override fun setVariable(
            mBinding: VolunteerNewsListItemBinding,
            position: Int,
            item: HomeContentBean
        ) {
            mBinding.tvTitle.text = item.title
            mBinding.tvAddress.text = item.createCompany
            mBinding.tvTime.text = item.publishTime
            Glide.with(this@VolunteerMainActivity).load(item.getContentCoverImageUrl()).placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.aiImage)
            mBinding.root.onNoDoubleClick {
                if (item.contentType.equals("IMAGE")) {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_IMG)
                        .withString("id", item.id.toString())
                        .navigation()
                } else {
                    ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                        .withString("id", item.id.toString())
                        .withString("contentTitle", "资讯详情")
                        .navigation()
                }
            }
        }
    }
}
package com.daqsoft.volunteer.volunteer

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.MineServiceBean
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.databinding.ActivityVolunteerCenterBinding
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.utils.StringUtils
import com.daqsoft.volunteer.volunteer.adapter.VolunteerMenuAdapter
import com.daqsoft.volunteer.volunteer.vm.VolunteerCenterVM
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/28 9:21
 *@author: caihj
 *@des:志愿者中心
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_CENTER)
class VolunteerCenterActivity:TitleBarActivity<ActivityVolunteerCenterBinding,VolunteerCenterVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_center

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_center)

    override fun injectVm(): Class<VolunteerCenterVM> = VolunteerCenterVM::class.java

    override fun initView() {
        mModel.volunteer.observe(this, Observer {
            Glide.with(mBinding.ivBg1)
                .asBitmap()
                .load(it.head)
                .placeholder(R.mipmap.mine_profile_photo_default)
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 1)))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                        var b = GaosiUtils.rsBlur(applicationContext, resource, 25)
                        mBinding.ivBg1.setImageBitmap(b)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
            Glide.with(this).load(it.head).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.avHead)
            mBinding.tvVolunteerName.text = it.name
            mBinding.tvVolunteerAddress.text = it.serviceRegionName
            val timestr =  "${it.serviceTime}小时"
            val span = SpannableString(timestr)
            val ab = AbsoluteSizeSpan(16, true)
            span.setSpan(ab,0,it.serviceTime.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            mBinding.tvServiceTime.text = span
            mBinding.tvServiceCount.text = it.serviceNum.toString()
            mBinding.tvScore.text = it.cumulativeIntegral.toString()
            mBinding.tvRank.text = it.ranking.toString()
            if(it.level != 0){
                mBinding.tvLevelTitle.visibility = View.VISIBLE
                val logos = resources.obtainTypedArray(R.array.volunteer_module_level_logo)
                val bgs = resources.obtainTypedArray(R.array.volunteer_module_level_volunteer_bg)
                mBinding.ivLevelIcon.visibility = View.VISIBLE
                mBinding.tvLevelTitle.text = StringUtils.getVolunteerLevelStr(it.level.toString())
                mBinding.tvLevelTitle.background = resources.getDrawable(bgs.getResourceId(it.level - 1,0))
                mBinding.ivLevelIcon.setImageDrawable(resources.getDrawable(logos.getResourceId(it.level - 1,0)))
            }
        })
        mBinding.clCard.onNoDoubleClick {
            JumpUtils.gotoVolunteerCard()
        }
    }

    override fun initData() {
        initMineServiceData()
        initTeamServiceData()
        mModel.getVolunteer()
    }

    private val mineServiceAdapter by lazy {
        VolunteerMenuAdapter()
    }
    private val teamServiceAdapter by lazy {
        VolunteerMenuAdapter()
    }

    /**
     * 初始化我的服务中的数据
     */
    private fun initMineServiceData() {
        var serviceTitles = resources.getStringArray(R.array.volunteer_module_center_mine_menus)
        var serviceIcons = resources.obtainTypedArray(R.array.volunteer_module_center_mine_menus_icon)
        var serviceTypes = resources.getStringArray(R.array.volunteer_module_center_mine_menus_key)
        var serviceList = mutableListOf<MineServiceBean>()
        mineServiceAdapter.clear()
        for ((index, title) in serviceTitles.withIndex()) {
            var service = MineServiceBean(
                title, serviceIcons.getResourceId(index, 0),
                serviceTypes.get(index)
            )
                serviceList.add(service)
        }
        mBinding.rvMineService.apply {
            layoutManager = GridLayoutManager(this@VolunteerCenterActivity, 4)
            adapter = mineServiceAdapter
        }
        mineServiceAdapter.add(serviceList)
        mineServiceAdapter.notifyDataSetChanged()
    }

    /**
     * 初始化我的服务中的数据
     */
    private fun initTeamServiceData() {
        var serviceTitles = resources.getStringArray(R.array.volunteer_module_center_team_menus)
        var serviceIcons = resources.obtainTypedArray(R.array.volunteer_module_center_team_menus_icon)
        var serviceTypes = resources.getStringArray(R.array.volunteer_module_center_team_menus_key)
        var serviceList = mutableListOf<MineServiceBean>()
        teamServiceAdapter.clear()
        for ((index, title) in serviceTitles.withIndex()) {
            var service = MineServiceBean(
                title, serviceIcons.getResourceId(index, 0),
                serviceTypes.get(index)
            )
            serviceList.add(service)
        }
        mBinding.rvTeamService.apply {
            layoutManager = GridLayoutManager(this@VolunteerCenterActivity, 4)
            adapter = teamServiceAdapter
        }
        teamServiceAdapter.add(serviceList)
        teamServiceAdapter.notifyDataSetChanged()
    }
}
package com.daqsoft.volunteer.volunteer

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerBean
import com.daqsoft.volunteer.bean.VolunteerServiceRecordBean
import com.daqsoft.volunteer.bean.VolunteerTeamListBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerDetailBinding
import com.daqsoft.volunteer.databinding.VolunteerDetailTeamListItemBinding
import com.daqsoft.volunteer.databinding.VolunteerServiceListItemBinding
import com.daqsoft.volunteer.utils.StringUtils
import com.daqsoft.volunteer.volunteer.vm.VolunteerDetailVM
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_content_more_title.view.*
import kotlinx.android.synthetic.main.more_title_bar.view.*
import org.jetbrains.anko.toast

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/4 15:52
 *@author: caihj
 *@des:志愿者详情
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_DETAIL )
class VolunteerDetailActivity :TitleBarActivity<ActivityVolunteerDetailBinding, VolunteerDetailVM>(){

   @Autowired
   @JvmField
   var id:String = ""

    var volunteer :VolunteerBean ? = null
    override fun getLayout(): Int = R.layout.activity_volunteer_detail

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_detail_title)

    override fun injectVm(): Class<VolunteerDetailVM> = VolunteerDetailVM::class.java
    override fun initView() {
//        Glide.with(this).load(R.mipmap.share_icon_wechat)
//            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 1)))
//            .into(mBinding.ivBg1)
        mModel.volunteer.observe(this, Observer {
            dissMissLoadingDialog()
            volunteer = it
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
            mBinding.tvServiceTime.text = it.serviceTime.toString()
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
            if(it.vipResourceStatus.likeStatus){
                changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_selected)
            }else{
                changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_normal)
            }
            if(it.vipResourceStatus.collectionStatus){
                changeTvImage(mBinding.tvCollect,R.mipmap.bottom_icon_collect_selected)
            }else{
                changeTvImage(mBinding.tvCollect,R.mipmap.bottom_icon_collect_normal)
            }

            setLikeAndCollection()
        })
        mModel.services.observe(this, Observer {
            if(it.isNotEmpty()){
                serviceAdapter.add(it)
            }else{
                mBinding.mService.visibility = View.GONE
                mBinding.rvService.visibility = View.GONE
            }
        })
        mBinding.mService.tv_title_label.text = "志愿服务"
        mBinding.mTeam.tv_title_label.text = "所属团队"
        mBinding.rvService.apply {
            layoutManager = LinearLayoutManager(this@VolunteerDetailActivity)
            adapter = serviceAdapter
        }

        mModel.teams.observe(this, Observer {
            if(it.isNotEmpty()){
                teamAdapter.add(it)
            }else{
                mBinding.mTeam.visibility = View.GONE
                mBinding.rvTeams.visibility = View.GONE
            }
        })
        mBinding.rvTeams.apply {
            layoutManager = LinearLayoutManager(this@VolunteerDetailActivity)
            adapter = teamAdapter
        }
        mModel.likeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            volunteer?.vipResourceStatus?.likeStatus  = true
            volunteer?.likeNum =  volunteer?.likeNum!! + 1
            mBinding.tvLike.text = volunteer?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_selected)
        })
        mModel.cancelLikeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            volunteer?.vipResourceStatus?.likeStatus  = false
            volunteer?.likeNum =  volunteer?.likeNum!! - 1
            mBinding.tvLike.text = volunteer?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_normal)
        })
        mModel.collectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            volunteer?.vipResourceStatus?.collectionStatus  = true
            volunteer?.collectionNum =volunteer?.collectionNum!! + 1
            mBinding.tvCollect.text = volunteer?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect,R.mipmap.bottom_icon_collect_selected)
        })
        mModel.cancelCollectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            volunteer?.vipResourceStatus?.collectionStatus  = false
            volunteer?.collectionNum = volunteer?.collectionNum !! - 1
            mBinding.tvCollect.text = volunteer?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect,R.mipmap.bottom_icon_collect_normal)
        })

        initEvent()
    }


    private fun initEvent(){
        mBinding.tvLike.onNoDoubleClick {
            if(gotoLogin()){
                showLoadingDialog()
                if(volunteer!!.vipResourceStatus.likeStatus){
                    mModel.cancelLike()
                }else{
                    mModel.addLike()
                }
            }
        }

        mBinding.tvCollect.onNoDoubleClick {
            if(gotoLogin()){
                showLoadingDialog()
                if(!volunteer!!.vipResourceStatus.collectionStatus){
                    mModel.collect()
                }else{
                    mModel.cancelCollect()
                }
            }
        }
    }

    /**
     * 修改textView顶部图片
     */
    private fun changeTvImage(v: TextView, image:Int){
        val topDrawable = getDrawable(image)
        topDrawable?.setBounds(0,0,topDrawable.minimumWidth,topDrawable.minimumHeight)
        v.setCompoundDrawables(null,topDrawable,null,null)
    }

    private fun gotoLogin():Boolean{
        if(!AppUtils.isLogin()){
            toast("您还没有登录，请先登录!")
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                .navigation()
            return false
        }
        return true
    }


    private fun setLikeAndCollection(){
        mBinding.tvLike.text = volunteer!!.likeNum.toString()
        mBinding.tvCollect.text = volunteer!!.collectionNum.toString()
    }

    private val serviceAdapter = object :RecyclerViewAdapter<VolunteerServiceListItemBinding, VolunteerServiceRecordBean>(R.layout.volunteer_service_list_item){
        override fun setVariable(
            mBinding: VolunteerServiceListItemBinding,
            position: Int,
            item: VolunteerServiceRecordBean
        ) {
            Glide.with(this@VolunteerDetailActivity).load(item.images.getRealImages()).placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.aiImage)
            mBinding.tvTitle.text = item.name
            mBinding.tvAddress.text = "服务时间:${item.address}"
            mBinding.tvTime.text = "服务地址:${item.useStartTime}"
        }

    }

    private val teamAdapter = object :RecyclerViewAdapter<VolunteerDetailTeamListItemBinding, VolunteerTeamListBean>(R.layout.volunteer_detail_team_list_item){
        override fun setVariable(
            mBinding: VolunteerDetailTeamListItemBinding,
            position: Int,
            item: VolunteerTeamListBean
        ) {
            Glide.with(this@VolunteerDetailActivity).load(item.teamIcon).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.aiImage)
            mBinding.tvTitle.text = item.teamName
            var address =""
            if(item.teamAddress.isNotEmpty()){
                address = item.teamAddress
            }else{
                address = item.teamRegionName
            }
            mBinding.tvAddress.text = address
            val numStr = mutableListOf<String>()
            if(item.teamMemberNum != 0){
                numStr.add("团队${item.teamMemberNum}人")
            }
            if(item.teamServiceTime != 0){
                numStr.add("服务${item.teamServiceTime}小时")
            }
            if(item.teamServiceNum != 0){
                numStr.add("服务${item.teamServiceNum}次")
            }
            mBinding.tvNum.text = DividerTextUtils.convertDotString(numStr)
        }

    }


    override fun initData() {
        mModel.id = id
        showLoadingDialog()
        mModel.getVolunteer()
        mModel.getServiceRecord()
        mModel.getVolunteerTeam()
    }

}
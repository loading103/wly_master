package com.daqsoft.volunteer.team

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.*
import com.daqsoft.volunteer.databinding.ActivityVolunteerTeamDetailBinding
import com.daqsoft.volunteer.databinding.VolunteerNewsListItemBinding
import com.daqsoft.volunteer.event.SignApplyEvent
import com.daqsoft.volunteer.main.adapter.VolunteerActivityAdapter
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.team.adapter.VolunteerSignTableAdapter
import com.daqsoft.volunteer.team.adapter.VolunteerTeamMemberAdapter
import com.daqsoft.volunteer.team.adapter.VolunteerTeamServiceAdapter
import com.daqsoft.volunteer.utils.JumpUtils
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_content_more_title.view.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/4 15:52
 *@author: caihj
 *@des:志愿者团队详情
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_TEAM_DETAIL )
class VolunteerTeamDetailActivity :TitleBarActivity<ActivityVolunteerTeamDetailBinding, VolunteerTeamDetailVM>(){

    @Autowired
    @JvmField
    var id = ""

    var teamBean:VolunteerTeamDetailBean? = null
    private val activityAdapter = VolunteerActivityAdapter(this)
    private val memberAdapter = VolunteerTeamMemberAdapter(this)
    private val serviceAdapter = VolunteerTeamServiceAdapter(this)
    override fun getLayout(): Int = R.layout.activity_volunteer_team_detail

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_team_detail_title)


    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null



    override fun injectVm(): Class<VolunteerTeamDetailVM> = VolunteerTeamDetailVM::class.java

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                teamBean?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@VolunteerTeamDetailActivity)
                    }
                    // 设置分享数据
                    var content= Constant.SHARE_DEC
                    sharePopWindow?.setShareContent(
                        it.teamName, content, it.teamIcon.getRealImages(),
                        ShareModel.getSlowDesc(id.toString())
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }
                }
            }

        })
    }
    override fun initView() {

        mModel.volunteerTeam.observe(this, Observer {
            if(it != null){
                teamBean = it
                Glide.with(mBinding.ivBg1)
                    .asBitmap()
                    .load(it.teamIcon)
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
            }
            Glide.with(this).load(it.teamIcon).into(mBinding.avHead)
            mBinding.tvTeamName.text = it.teamName
            mBinding.tvAddress.text = it.teamAddress
            val timestr =  "${it.teamServiceTime}小时"
            val span = SpannableString(timestr)
            val ab = AbsoluteSizeSpan(16, true)
            span.setSpan(ab,0,it.teamServiceTime.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            mBinding.tvServiceTime.text = span
            mBinding.tvServiceCount.text = it.teamServiceNum.toString()
            mBinding.tvTeamPeople.text = it.teamMemberNum.toString()
            mBinding.tvScore.text = it.teamCumulativeIntegral.toString()
            mBinding.tvRank.text = it.ranking.toString()
            if(it.manageUnit.isNullOrEmpty()){
                mBinding.llCompany.visibility = View.GONE
            }else{
                mBinding.tvCompany.text = it.manageUnit
            }
            if(it.teamIntroduce.isNullOrEmpty()){
                mBinding.tvIntroduce.visibility = View.GONE
            }else{
                mBinding.tvIntroduce.text = it.teamIntroduce
            }
            mBinding.tvUser.text = it.teamPrincipalList[0].name
            mBinding.tvPhone.text = it.teamPrincipalList[0].phone
            mBinding.tvTeamAddress.text = it.teamRegionName
            mBinding.tvPhone.onNoDoubleClick {
                if(!it.teamPrincipalList[0].phone.isNullOrEmpty()){
                    SystemHelper.callPhone(this,it.teamPrincipalList[0].phone)
                }
            }
            mBinding.tvTeamAddress.onNoDoubleClick {
                if (!it.latitude.isNullOrEmpty()&&!it.longitude.isNullOrEmpty()) {
                    if (MapNaviUtils.isGdMapInstalled()) {
                        MapNaviUtils.openGaoDeNavi(
                            BaseApplication.context, 0.0, 0.0, null,
                            it.latitude.toDouble(), it.longitude.toDouble(),
                            it.teamRegionName
                        )
                    } else {
                        mModel.toast.postValue("非常抱歉，系统未安装地图软件")
                    }
                } else {
                    mModel.toast.postValue("非常抱歉，暂无位置信息")
                }
            }

            if(it.vipResourceStatus.likeStatus){
                changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_selected)
            }else{
                changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_normal)
            }
            if(it.vipResourceStatus.fansStatus){
                mBinding.tvFocus.text = "已关注"
            }else{
                mBinding.tvFocus.text = "关注"
            }
            setLikeAndCollection()
            parseSignData(it)
        })

        mBinding.rvService.apply {
            layoutManager = GridLayoutManager(this@VolunteerTeamDetailActivity,2)
            adapter = serviceAdapter
        }
        mModel.services.observe(this, Observer {
            if(it.size > 0){
                serviceAdapter.add(it)
            }else{
                mBinding.volunteerServiceFlag.visibility = View.GONE
                mBinding.rvService.visibility = View.GONE
            }
        })

        mModel.likeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            teamBean?.vipResourceStatus?.likeStatus  = true
            teamBean?.likeNum =  teamBean?.likeNum!! + 1
            mBinding.tvLike.text = teamBean?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_selected)
        })
        mModel.cancelLikeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            teamBean?.vipResourceStatus?.likeStatus  = false
            teamBean?.likeNum =  teamBean?.likeNum!! - 1
            mBinding.tvLike.text = teamBean?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_normal)
        })

        mModel.focusStatus.observe(this, Observer {
            if(it){
                mBinding.tvFocus.text = "已关注"
            }
        })

        mModel.cancelFocusStatus.observe(this, Observer {
            if(it){
                mBinding.tvFocus.text = "关注"
            }
        })

        mModel.activitys.observe(this, Observer {
            if(it.size > 0){
                activityAdapter.add(it)
            }else{
                mBinding.activityFlag.visibility = View.GONE
                mBinding.rvActivity.visibility = View.GONE
            }
        })

        mModel.volunteerNews.observe(this, Observer {
            if(it.size > 0){
                newsAdapter.add(it)
            }else{
                mBinding.newsFlag.visibility = View.GONE
                mBinding.rvNews.visibility = View.GONE
            }
        })

        // 评论
        mBinding.rvComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val commentAdapter = CommentAdapter(this)
        mBinding.rvComments.adapter = commentAdapter
        mModel.commentBeans.observe(this, Observer {
            if (it.size > 0) {
                commentAdapter.add(it)
            }else{
                mBinding.commentFlag.visibility = View.GONE
                mBinding.rvComments.visibility = View.GONE
            }
        })

        mBinding.tvComment.onNoDoubleClick {
            JumpUtils.gotoCommentPage(id,ResourceType.CONTENT_TYPE_VOLUNTEER_TEAM)
        }

        mBinding.commentFlag.onNoDoubleClick {
            JumpUtils.gotoCommentList(id,ResourceType.CONTENT_TYPE_VOLUNTEER_TEAM)
        }

        mModel.memebers.observe(this, Observer {
            if(it.size > 0){
                memberAdapter.add(it)
            }else{
                mBinding.memberFlag.visibility = View.GONE
                mBinding.rvMembers.visibility = View.GONE
            }
        })
        mBinding.activityFlag.tv_title_label.text = "志愿招募"
        mBinding.memberFlag.tv_title_label.text = "团队成员"
        mBinding.volunteerServiceFlag.tv_title_label.text = "志愿风采"
        mBinding.newsFlag.tv_title_label.text = "团队资讯"
        mBinding.commentFlag.tv_title_label.text = "点评"

        mBinding.rvActivity.apply {
            layoutManager =  LinearLayoutManager(this@VolunteerTeamDetailActivity)
            adapter = activityAdapter
        }
        mBinding.rvMembers.apply {
            layoutManager =  LinearLayoutManager(this@VolunteerTeamDetailActivity)
            adapter = memberAdapter
        }
        mBinding.rvNews.apply {
            layoutManager =  LinearLayoutManager(this@VolunteerTeamDetailActivity)
            adapter = newsAdapter
        }

        mBinding.activityFlag.onNoDoubleClick {
            JumpUtils.gotoVolunteerActivity()
        }
        mBinding.tvLike.onNoDoubleClick {
            if(gotoLogin()){
                showLoadingDialog()
                if(teamBean!!.vipResourceStatus.likeStatus){
                    mModel.cancelLike(id)
                }else{
                    mModel.addLike(id)
                }
            }
        }
        mBinding.tvFocus.onNoDoubleClick {
            if(gotoLogin()){
                showLoadingDialog()
                if(teamBean!!.vipResourceStatus.fansStatus){
                    mModel.cancelFocus(id)
                }else{
                    mModel.focus(id)
                }
            }
        }
        mBinding.memberFlag.onNoDoubleClick {
            JumpUtils.gotoMembers(id)
        }
        mBinding.tvComment.onNoDoubleClick {
            JumpUtils.gotoCommentPage(id,ResourceType.CONTENT_TYPE_VOLUNTEER_TEAM)
        }
        mBinding.volunteerServiceFlag.onNoDoubleClick {
            JumpUtils.gotoServiceRecordList(id)
        }

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
        mBinding.tvLike.text = teamBean!!.likeNum.toString()
    }

    /**
     * 修改textView顶部图片
     */
    private fun changeTvImage(v: TextView, image:Int){
        val topDrawable = getDrawable(image)
        topDrawable?.setBounds(0,0,topDrawable.minimumWidth,topDrawable.minimumHeight)
        v.setCompoundDrawables(null,topDrawable,null,null)
    }

    override fun initData() {

        volunteerSignTableAdapter= VolunteerSignTableAdapter(this)
        volunteerSignTableAdapter?.signApplyListener = object :VolunteerSignTableAdapter.SignApplyListener{
            override fun onClickSignApply() {
                EventBus.getDefault().postSticky(SignApplyEvent(mModel.volunteerTeam.value!!))
                ARouter.getInstance().build(ARouterPath.VolunteerModule.VOLUNTEER_SERVICE_SIGN)
                    .navigation()
            }

        }
        mBinding.rvSignTable.apply {
            layoutManager = LinearLayoutManager(this@VolunteerTeamDetailActivity)
            adapter = volunteerSignTableAdapter
        }
        mModel.getVolunteerTeamDetail(id)
        mModel.getVolunteerActivity(id)
        mModel.getMembers(id)
        mModel.getVolunteerNews()
        mModel.getActivityCommentList(id)
        mModel.getVolunteerService(id)
    }

    private var volunteerSignTableAdapter :VolunteerSignTableAdapter? = null
    private val newsAdapter = object : RecyclerViewAdapter<VolunteerNewsListItemBinding, HomeContentBean>(R.layout.volunteer_news_list_item){
        override fun setVariable(
            mBinding: VolunteerNewsListItemBinding,
            position: Int,
            item: HomeContentBean
        ) {
            mBinding.tvTitle.text = item.title
            if(item.createCompany.isNullOrEmpty()){
                mBinding.tvAddress.visibility= View.GONE
            }
            mBinding.tvAddress.text = item.createCompany
            mBinding.tvTime.text = item.publishTime
            Glide.with(this@VolunteerTeamDetailActivity).load(item.getContentCoverImageUrl()).placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.aiImage)
        }

    }

    private fun parseSignData(team:VolunteerTeamDetailBean){
        val signTableData = mutableListOf<List<String>>()
        val monday = team.signInDateList.monday
        val tuesday= team.signInDateList.tuesday
        val wednesday = team.signInDateList.wednesday
        val thursday = team.signInDateList.thursday
        val friday = team.signInDateList.friday
        val saturday = team.signInDateList.saturday
        val sunday = team.signInDateList.sunday
        val morningData = mutableListOf(
            monday.indexOf("morning").toString(),
            tuesday.indexOf("morning").toString(),
            wednesday.indexOf("morning").toString(),
            thursday.indexOf("morning").toString(),
            friday.indexOf("morning").toString(),
            saturday.indexOf("morning").toString(),
            sunday.indexOf("morning").toString()
        )
        signTableData.add(morningData)
        val afternoonData = mutableListOf(
            monday.indexOf("afternoon").toString(),
            tuesday.indexOf("afternoon").toString(),
            wednesday.indexOf("afternoon").toString(),
            thursday.indexOf("afternoon").toString(),
            friday.indexOf("afternoon").toString(),
            saturday.indexOf("afternoon").toString(),
            sunday.indexOf("afternoon").toString()
        )
        signTableData.add(afternoonData)
        val nightData = mutableListOf(
            monday.indexOf("night").toString(),
            tuesday.indexOf("night").toString(),
            wednesday.indexOf("night").toString(),
            thursday.indexOf("night").toString(),
            friday.indexOf("night").toString(),
            saturday.indexOf("night").toString(),
            sunday.indexOf("night").toString()
        )
        signTableData.add(nightData)

        val allDayTime = mutableListOf<String>()
        if(!team.signInMorningStarTime.isNullOrEmpty() && !team.signInMorningEndTime.isNullOrEmpty()){
            allDayTime.add("上午：${team.signInMorningStarTime}-${team.signInMorningEndTime}")
        }else{
            allDayTime.add("")
        }
        if(!team.signInAfternoonStarTime.isNullOrEmpty() && !team.signInAfternoonEndTime.isNullOrEmpty()){
            allDayTime.add("下午：${team.signInAfternoonStarTime}-${team.signInAfternoonEndTime}")
        }else{
            allDayTime.add("")
        }
        if(!team.signInNightStarTime.isNullOrEmpty() && !team.signInNightEndTime.isNullOrEmpty()){
            allDayTime.add("晚上：${team.signInNightStarTime}-${team.signInNightEndTime}")
        }else{
            allDayTime.add("")
        }
        if(allDayTime.size == 3){
            allDayTime.add("全天：${team.signInMorningStarTime}-${team.signInNightEndTime}")
        }else{
            allDayTime.add("")
        }
        initTableData(signTableData,allDayTime)
    }

    private fun initTableData(signTableData: List<List<String>>, allDayTime:List<String>){
        val tableData = mutableListOf<VolunteerTeamSignTableBean>()
        var volunteerTeamSignTableBean = VolunteerTeamSignTableBean(
            TableConst.TITLE,
            listOf("周一","周二","周三","周四","周五","周六","周日")
        )
        tableData.add(volunteerTeamSignTableBean)
        volunteerTeamSignTableBean = VolunteerTeamSignTableBean(
            TableConst.CONTENT,
            signTableData[0]
        )
        tableData.add(volunteerTeamSignTableBean)
        volunteerTeamSignTableBean = VolunteerTeamSignTableBean(
            TableConst.CONTENT,
            signTableData[1]
        )
        tableData.add(volunteerTeamSignTableBean)
        volunteerTeamSignTableBean = VolunteerTeamSignTableBean(
            TableConst.CONTENT,
            signTableData[2]
        )
        tableData.add(volunteerTeamSignTableBean)
        volunteerTeamSignTableBean = VolunteerTeamSignTableBean(
            TableConst.TIME,
            allDayTime
        )
        tableData.add(volunteerTeamSignTableBean)
        volunteerTeamSignTableBean = VolunteerTeamSignTableBean(
            TableConst.BOTTOM,
            listOf()
        )
        tableData.add(volunteerTeamSignTableBean)

        for (item in tableData){
            when(item.type){
                TableConst.TITLE ->{
                    volunteerSignTableAdapter!!.addViewType(R.layout.volunteer_team_service_time_table_title)
                }
                TableConst.CONTENT ->{
                    volunteerSignTableAdapter!!.addViewType(R.layout.volunteer_team_service_time_table_content)
                }
                TableConst.TIME ->{
                    volunteerSignTableAdapter!!.addViewType(R.layout.volunteer_team_service_time_table_time)
                }
                else ->
                    volunteerSignTableAdapter!!.addViewType(R.layout.volunteer_team_service_time_table_bottom)
            }
            volunteerSignTableAdapter!!.addItem(item)

        }
        volunteerSignTableAdapter!!.notifyDataSetChanged()
    }

}

class VolunteerTeamDetailVM:BaseViewModel(){

    var volunteerTeam = MutableLiveData<VolunteerTeamDetailBean>()


    fun getVolunteerTeamDetail(id:String){
        VolunteerRepository.service.getVolunteerTeamDetail(id).excute(object :BaseObserver<VolunteerTeamDetailBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerTeamDetailBean>) {
                volunteerTeam.postValue(response.data)
            }

        })
    }

    var activitys = MutableLiveData<MutableList<VolunteerActivityBean>>()
    fun getVolunteerActivity(id:String){
        val params = HashMap<String,String>()
        params["voluntTeamId"] = id
        params["pageSize"] = "2"
        params["currPage"] = "1"
        VolunteerRepository.service.getVolunteerActivityList(params).excute(object :BaseObserver<VolunteerActivityBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerActivityBean>) {
                activitys.postValue(response.datas)
            }

        })
    }

    val memebers = MutableLiveData<MutableList<VolunteerMemberBean>>()

    fun getMembers(id:String){
        val params = HashMap<String,String>()
        params["teamId"] = id
        params["pageSize"] = "5"
        VolunteerRepository.service.getVolunteerTeamMembers(params).excute(object :BaseObserver<VolunteerMemberBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerMemberBean>) {
                memebers.postValue(response.datas)
            }
        })
    }


    // 获取资讯列表
    var volunteerNews = MutableLiveData<MutableList<HomeContentBean>>()

    /**
     * 获取资讯列表
     */
    fun getVolunteerNews() {
        val params = HashMap<String,String>()
//        params["channelCode"] = com.daqsoft.provider.bean.Constant.HOME_CONTENT_TYPE_volunteerNews
        params["pageSize"] = "2"
        mPresenter.value?.loading = false
        VolunteerRepository.service.getVolunteerNewsList(params).excute(object : BaseObserver<HomeContentBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<HomeContentBean>) {
                volunteerNews.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<HomeContentBean>) {
                volunteerNews.postValue(null)
            }

        })
    }


    var likeStatus = MutableLiveData<Boolean>()
    var cancelLikeStatus = MutableLiveData<Boolean>()
    /**
     * 点赞
     */
    fun addLike(id:String){
        CommentRepository.service.postThumbUp(id, ResourceType.CONTENT_TYPE_VOLUNTEER_TEAM).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("感谢您的点赞")
                    likeStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("点赞失败!")
            }

        })
    }

    /**
     * 取消点赞
     */
    fun cancelLike(id:String){
        CommentRepository.service.postThumbCancel(id, ResourceType.CONTENT_TYPE_VOLUNTEER_TEAM).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("成功取消")
                    cancelLikeStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("取消点赞失败")
            }
        })
    }


    var focusStatus = MutableLiveData<Boolean>()
    var cancelFocusStatus = MutableLiveData<Boolean>()
    /**
     * 点赞
     */
    fun focus(id:String){
        CommentRepository.service.attentionResource(id, ResourceType.CONTENT_TYPE_VOLUNTEER_TEAM).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("感谢您的关注")
                    likeStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("关注失败!")
            }

        })
    }

    /**
     * 取消点赞
     */
    fun cancelFocus(id:String){
        CommentRepository.service.attentionResourceCancle(id, ResourceType.CONTENT_TYPE_VOLUNTEER_TEAM).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("成功取消")
                    cancelLikeStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("取消关注失败")
            }
        })
    }


    /**
     * 获取评论列表
     */
    var commentBeans = MutableLiveData<MutableList<CommentBean>>()

    /**
     * 获取评论列表
     */
    fun getActivityCommentList(id:String) {
        mPresenter.value?.loading = true
        val param = HashMap<String, String>()
        param["resourceType"] = ResourceType.CONTENT_TYPE_VOLUNTEER_TEAM
        param["resourceId"] = id
        CommentRepository.service.getCommentList(param)
            .excute(object : BaseObserver<CommentBean>() {
                override fun onSuccess(response: BaseResponse<CommentBean>) {
                    commentBeans.postValue(response.datas)
                }
            })
    }

    var services = MutableLiveData<MutableList<ServiceRecordBean>>()
    fun getVolunteerService(id:String){
        val params = HashMap<String,String>()
        params["teamId"] = id
        VolunteerRepository.service.getVolunteerServiceRecordList(params).excute(object :BaseObserver<ServiceRecordBean>(){
            override fun onSuccess(response: BaseResponse<ServiceRecordBean>) {
                services.postValue(response.datas)
            }

        })
    }
}
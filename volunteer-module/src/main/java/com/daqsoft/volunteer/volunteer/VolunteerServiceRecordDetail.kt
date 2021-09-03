package com.daqsoft.volunteer.volunteer

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.ThumbBean
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.ServiceRecordDetailBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerServiceRecordDetailBinding
import com.daqsoft.volunteer.databinding.VolunteerAvatarViewBinding
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.JumpUtils
import com.daqsoft.volunteer.utils.StringUtils
import me.nereo.multi_image_selector.BigImgActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.util.ArrayList

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/8 11:18
 *@author: caihj
 *@des:服务记录详情
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_SERVICE_RECORD)
class VolunteerServiceRecordDetail :TitleBarActivity<ActivityVolunteerServiceRecordDetailBinding,VolunteerServiceRecordVM>(){

    @Autowired
    @JvmField
    var id = ""
    var serviceRecordDetail:ServiceRecordDetailBean? = null

    override fun getLayout(): Int = R.layout.activity_volunteer_service_record_detail

    override fun setTitle(): String = getString(R.string.volunteer_module_volunteer_service_record_detail_title)

    override fun injectVm(): Class<VolunteerServiceRecordVM> = VolunteerServiceRecordVM::class.java

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mModel.serviceRecordDetailBean.observe(this, Observer {
            dissMissLoadingDialog()
            setTopImg(it)
            serviceRecordDetail = it
            Glide.with(this).load(it.userHead).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivUser)
            mBinding.tvUser.text = it.userName
            mBinding.tvTime.text = "${it.createTime} 发布"

            if(it.userLevel != "0"){
                val colors = resources.getIntArray(R.array.volunteer_module_level_color)
                val logos = resources.obtainTypedArray(R.array.volunteer_module_level_bg)
                val drawable = resources.getDrawable(logos.getResourceId(it.userLevel.toInt(),0))

                mBinding.tvLevelLabel.text = StringUtils.getVolunteerTeamLevelStr(it.userLevel)
                mBinding.tvLevelLabel.textColor = colors[it.userLevel.toInt()]
                mBinding.tvLevelLabel.background = drawable
            }else{
                mBinding.tvLevelLabel.visibility = View.GONE
            }
            mBinding.tvContent.loadDataWithBaseURL(null, it.content, "text/html", "utf-8", null)
            if(it.activity != null){
                mBinding.tvLink.text = it.activity.name
                mBinding.tvLink.onNoDoubleClick {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_VOLUNTEER_ACTIVITY)
                        .withString("id", it.activity.id)
                        .withString("classifyId", "")
                        .navigation()
                }
                mBinding.tvServiceObj.text = it.serviceTarget
                mBinding.tvServiceAddress.text = it.activity.address
                mBinding.tvServiceBegin.text = it.activity.useStartTime
                mBinding.tvServiceEnd.text = it.activity.useEndTime
                mBinding.tvServiceTime.text = "${it.activity.serviceTime}小时"
                mBinding.tvServicePeople.text = "${it.activity.servicePeople}人"
            }else{
                mBinding.llServiceIntro.visibility = View.GONE
                mBinding.tvLink.visibility = View.GONE
            }

            if(it.showNum != 0){
                mBinding.tvViewNumber.text = it.showNum.toString()
            }else{
                mBinding.tvViewNumber.visibility = View.GONE
            }

            mBinding.tvLike.text = it.likeNum.toString()
            mBinding.tvCollect.text = it.collectionNum.toString()
            mBinding.tvCommentNum.text = it.commentNum.toString()
        })

        mBinding.tvAddComment.onNoDoubleClick {
            JumpUtils.gotoCommentPage(id,ResourceType.TEAM_ACTIVITY_SERVICE)
        }

        mBinding.rvThumb.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvThumb.adapter = avatarAdapter

        mModel.thumbList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.rvThumb.visibility = View.VISIBLE
                avatarAdapter.add(it)
            }
        })
        // 评论
        mBinding.rvComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val commentAdapter = CommentAdapter(this)
        mBinding.rvComments.adapter = commentAdapter
        mModel.commentBeans.observe(this, Observer {
            if (it.size > 0) {
                commentAdapter.add(it)
                mBinding.tvReplayNum.text = "(共${it.size}条评论)"
            }
        })

        mModel.likeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            serviceRecordDetail?.vipResourceStatus?.likeStatus  = true
            serviceRecordDetail?.likeNum =  serviceRecordDetail?.likeNum!! + 1
            mBinding.tvLike.text = serviceRecordDetail?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_selected)
        })
        mModel.cancelLikeStatus.observe(this, Observer {
            dissMissLoadingDialog()
            serviceRecordDetail?.vipResourceStatus?.likeStatus  = false
            serviceRecordDetail?.likeNum =  serviceRecordDetail?.likeNum!! - 1
            mBinding.tvLike.text = serviceRecordDetail?.likeNum!!.toString()
            changeTvImage(mBinding.tvLike,R.mipmap.bottom_icon_like_normal)
        })
        mModel.collectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            serviceRecordDetail?.vipResourceStatus?.collectionStatus  = true
            serviceRecordDetail?.collectionNum =serviceRecordDetail?.collectionNum!! + 1
            mBinding.tvCollect.text = serviceRecordDetail?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect,R.mipmap.bottom_icon_collect_selected)
        })
        mModel.cancelCollectStatus.observe(this, Observer {
            dissMissLoadingDialog()
            serviceRecordDetail?.vipResourceStatus?.collectionStatus  = false
            serviceRecordDetail?.collectionNum = serviceRecordDetail?.collectionNum !! - 1
            mBinding.tvCollect.text = serviceRecordDetail?.collectionNum.toString()
            changeTvImage(mBinding.tvCollect,R.mipmap.bottom_icon_collect_normal)
        })

        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })

        initEvent()
    }

    private fun initEvent(){
        mBinding.tvLike.onNoDoubleClick {
            if(gotoLogin()){
                showLoadingDialog()
                if(serviceRecordDetail!!.vipResourceStatus.likeStatus){
                    mModel.cancelLike()
                }else{
                    mModel.addLike()
                }
            }
        }

        mBinding.tvCollect.onNoDoubleClick {
            if(gotoLogin()){
                showLoadingDialog()
                if(!serviceRecordDetail!!.vipResourceStatus.collectionStatus){
                    mModel.collect()
                }else{
                    mModel.cancelCollect()
                }
            }
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

    /**
     * 修改textView顶部图片
     */
    private fun changeTvImage(v: TextView, image:Int){
        val topDrawable = getDrawable(image)
        topDrawable?.setBounds(0,0,topDrawable.minimumWidth,topDrawable.minimumHeight)
        v.setCompoundDrawables(null,topDrawable,null,null)
    }


    /**
     * 点赞人数列表适配器
     */
    private val avatarAdapter = object
        : RecyclerViewAdapter<VolunteerAvatarViewBinding, ThumbBean>(R.layout.volunteer_avatar_view) {
        override fun setVariable(mBinding: VolunteerAvatarViewBinding, position: Int, item: ThumbBean) {
            mBinding.url = item.headUrl
        }

    }

    private fun setTopImg(it: ServiceRecordDetailBean) {
        if (it.images.isNullOrEmpty() && it.coverVideos.isEmpty()) {
            mBinding.cbannerService.visibility = View.GONE
            mBinding.vWorksDetailIndex.visibility = View.GONE
        } else {
            mBinding.vWorksDetailIndex.visibility = View.VISIBLE
            var total = 0
            val images = it.images.split(",")
            var imagesAndVideo = mutableListOf<VideoImageBean>()
            if (it.coverVideos.isNotEmpty()) {
                total += 1
                imagesAndVideo.add(initVideoData(1, it.coverVideos))
            }
            if (images.isNotEmpty()) {
                total += images.size
                for (item in images) {
                    imagesAndVideo.add(initVideoData(0, item))
                }
            }
            mBinding.txtCurrentIndex.text = "1"
            mBinding.txtTotalSize.text = "/${total}"

            mBinding.cbannerService.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return VideoImageHolder(itemView!!, this@VolunteerServiceRecordDetail)
                }

                override fun getLayoutId(): Int {
                    return R.layout.layout_video_image
                }
            }, imagesAndVideo).setCanLoop(false).setPointViewVisible(false).setOnItemClickListener {
                val intent =
                    Intent(this@VolunteerServiceRecordDetail, BigImgActivity::class.java)
                intent.putExtra("position", it)
                intent.putStringArrayListExtra(
                    "imgList",
                    ArrayList(images)
                )
                startActivity(intent)
            }
            mBinding.cbannerService?.onPageChangeListener = object : OnPageChangeListener {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                }

                override fun onPageSelected(index: Int) {
                    mBinding?.txtCurrentIndex.text = ((index + 1).toString())
                    mBinding?.txtTotalSize.text = "/${total}"
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                }

            }
        }
    }

    fun initVideoData(type: Int, path: String): VideoImageBean {
        var video = VideoImageBean()
        video.type = type
        if (type == 1) {
            video.videoUrl = path
        } else {
            video.imageUrl = path
        }
        return video
    }


    override fun initData() {
        mModel.id = id
        showLoadingDialog()
        mModel.getServiceDetail()
        mModel.getThumbList()
        mModel.getActivityCommentList()
    }
}

class VolunteerServiceRecordVM:BaseViewModel(){

    var serviceRecordDetailBean =MutableLiveData<ServiceRecordDetailBean>()
    var id = ""
    fun getServiceDetail(){
        val params = HashMap<String,String>()
        params["id"] = id
        VolunteerRepository.service.getVolunteerServiceRecordDetail(params).excute(object :BaseObserver<ServiceRecordDetailBean>(){
            override fun onSuccess(response: BaseResponse<ServiceRecordDetailBean>) {
                serviceRecordDetailBean.postValue(response.data)
            }

            override fun onFailed(response: BaseResponse<ServiceRecordDetailBean>) {
                super.onFailed(response)
                mError.value = response
            }

        })
    }

    /**
     * 点赞列表
     */
    var thumbList = MutableLiveData<MutableList<ThumbBean>>()


    /**
     * 获取点赞列表
     */
    fun getThumbList() {
        HomeRepository.service.getThumbList(id, ResourceType.TEAM_ACTIVITY_SERVICE)
            .excute(object : BaseObserver<ThumbBean>() {
                override fun onSuccess(response: BaseResponse<ThumbBean>) {
                    thumbList.postValue(response.datas)
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
    fun getActivityCommentList() {
        mPresenter.value?.loading = true
        val param = HashMap<String, String>()
        param["resourceType"] = ResourceType.TEAM_ACTIVITY_SERVICE
        param["resourceId"] = id
        CommentRepository.service.getCommentList(param)
            .excute(object : BaseObserver<CommentBean>() {
                override fun onSuccess(response: BaseResponse<CommentBean>) {
                    commentBeans.postValue(response.datas)
                }
            })
    }


    var likeStatus = MutableLiveData<Boolean>()
    var cancelLikeStatus = MutableLiveData<Boolean>()
    /**
     * 点赞
     */
    fun addLike(){
        CommentRepository.service.postThumbUp(id, ResourceType.TEAM_ACTIVITY_SERVICE).excute(object :BaseObserver<Any>(){
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
    fun cancelLike(){
        CommentRepository.service.postThumbCancel(id, ResourceType.TEAM_ACTIVITY_SERVICE).excute(object :BaseObserver<Any>(){
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


    var collectStatus = MutableLiveData<Boolean>()
    var cancelCollectStatus = MutableLiveData<Boolean>()

    /**
     * 收藏
     */
    fun collect(){
        CommentRepository.service.posClloection(id,ResourceType.TEAM_ACTIVITY_SERVICE).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("感谢您的收藏")
                    collectStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("收藏失败")
            }
        })
    }

    /**
     * 取消收藏
     */
    fun cancelCollect(){
        CommentRepository.service.posCollectionCancel(id,ResourceType.CONTENT_TYPE_VOLUNTEER).excute(object :BaseObserver<Any>(){
            override fun onSuccess(response: BaseResponse<Any>) {
                if(response.code == 0){
                    toast.postValue("成功取消")
                    cancelCollectStatus.postValue(true)
                }else{
                    toast.postValue(response.message)
                }
            }

            override fun onError(e: Throwable) {
                toast.postValue("取消收藏失败")
            }
        })
    }

}
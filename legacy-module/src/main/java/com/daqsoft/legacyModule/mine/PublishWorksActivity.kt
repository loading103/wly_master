package com.daqsoft.legacyModule.mine

import android.content.Intent
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.ActivityPublishWorksBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.home.bean.ItemAddressBean
import com.daqsoft.provider.view.LabelsView
import com.google.gson.Gson
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import me.nereo.multi_image_selector.utils.FileUtils
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import java.util.regex.Pattern

/**
 *@package:com.daqsoft.legacyModule.mine
 *@date:2020/5/19 10:53
 *@author: caihj
 *@des:发布作品
 **/
@Route(path =  ARouterPath.LegacyModule.LEGACY_PUBLISH_WORKS)
class PublishWorksActivity:TitleBarActivity<ActivityPublishWorksBinding,PublishWorksVM>() {

    companion object {
        val ADDLOCATION = 0x0004
        val ADDTAG = 1
    }

    // pk 作品id
    @Autowired
    @JvmField
    var id = ""

    // pk 作品标题
    @Autowired
    @JvmField
    var pkTitle = ""

    // 操作类型 publish 发布，pk pk作品，edit 编辑作品
    @Autowired
    @JvmField
    var type = "publish"


    override fun getLayout(): Int = R.layout.activity_publish_works

    override fun setTitle(): String = getTitleStr()

    private fun getTitleStr(): String = when(type){
        "publish" -> getString(R.string.legacy_publish_works)
        "pk" ->getString(R.string.legacy_publish_pk_works)
        else -> getString(R.string.legacy_edit_works)
    }

    override fun injectVm(): Class<PublishWorksVM> = PublishWorksVM::class.java
    var labelSelected = mutableListOf<String>()

    override fun initView() {
        mModel.topics.observe(this, Observer {
            dissMissLoadingDialog()
            if (it.size > 0) {
                mBinding.tvTopicLabel.visibility = View.VISIBLE
                mBinding.lvLabels.setLabels(it) { label, position, data -> "#${data!!.name}#" }
                // 显示选中的话题
                if (labelSelected.size > 0) {
                    val positions = mutableListOf<Int>()
                    for (index in it.indices) {
                        for (label in labelSelected) {
                            if (it[index].id == label) {
                                positions.add(index)
                            }
                        }
                    }
                    mBinding.lvLabels.setSelects(positions)
                }
            } else {
                mBinding.tvTopicLabel.visibility = View.GONE
            }

        })
        mBinding.rvImages.setPicNumber(20)
        mBinding.rvImages.maxPicVideoNum=1
        mBinding.rvImages.init(this, true,true)
        if(type=="pk"){
            mBinding.etTitle.visibility = View.GONE
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.tvTitle.text = pkTitle
        }
        mModel.worksDetail.observe(this, Observer {
            if(it!=null){
                mModel.id = it.id
                mBinding.etTitle.setText(it.title)
                if(it.tagName.isEmpty()){
                    mBinding.etContent.setText(it.content)
                }else{
                    setContent(it.tagName,it.content)
                    mBinding.tvAddTag.text = "#${it.tagName}#"
                }
                if (it.topicInfo.isNotEmpty()) {
                    for (item in it.topicInfo) {
                        labelSelected.add(item.topicId)
                    }
                }
                if(it.resourceName.isNotEmpty()){
                    mBinding.tvLocationLabel.visibility = View.GONE
                    mBinding.tvLocation.visibility = View.VISIBLE
                    mBinding.tvLocation.text = it.resourceName
                }
                if (it.images.isNotEmpty()) {
                    var images = mutableListOf<Image>()
                    if(!it.video.isNullOrEmpty()){
                        images.add(Image(it.video,"",0))
                    }
                    for (item in it.images){
                        images.add(Image(item,"",0))
                    }
                    mBinding.rvImages.onActivityResult(images as java.util.ArrayList<Image>?)
                }

            }
        })
        initEvent()
    }

    override fun initData() {
        mBinding.vm = mModel

        if(type == "pk"){
            mModel.pkid = id
        }else if(type == "edit"){
            mModel.getMineWorkDetail(id)
        }
        showLoadingDialog()
        mModel.getTopicList()
    }

    /**
     * 检查必须是否已经填完
     */
    private fun isNotAllFill(): Boolean {
        if(mBinding.etTitle.text.isNullOrBlank()&& type != "pk"){
            toast("请输入作品标题!")
            return true
        }

        if (mBinding.etContent.text.isNullOrBlank()) {
            toast("请输入作品内容!")
            return true
        }
        if(mBinding.rvImages.path.isNullOrBlank()){
            toast("至少上传一个图片或视频!")
            return true
        }

        if (!AppUtils.isLogin()) {
            toast("您还没登录,请先登录！")
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            return true
        }
        return false
    }

    private fun initEvent(){
        mBinding.lvLabels.setOnLabelClickListener { _, data, position ->
            data as HomeTopicBean
            if (mBinding.lvLabels.selectLabels.contains(position)) {
                labelSelected.add(data.id)
            } else {
                labelSelected.remove(data.id)
            }
        }

        mBinding.tvLocation.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_ADDRESS)
                .navigation(this, ADDLOCATION)
        }

        mBinding.tvLocationLabel.setOnClickListener {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_ADDRESS)
                .navigation(this, ADDLOCATION)
        }
        mBinding.tvAddTag.setOnClickListener {
            ARouter.getInstance()
                .build(MainARouterPath.ADD_STORY_TAG)
                .navigation(this, ADDTAG)
        }
        mBinding.tvPublish.onNoDoubleClick {
            if(!isNotAllFill()){
               mModel.urls = mBinding.rvImages.path
                if(type == "pk") {
                    mModel.title = pkTitle
                }else{
                    mModel.title = mBinding.etTitle.text.toString()
                }
                mModel.content = mBinding.etContent.text.toString()
                mModel.topicId = labelSelected.joinToString(",")
                mModel.publishWorks()
            }
        }
    }

    private fun setContent(tagName:String,content:String){
        val c = "#$tagName#"
        mBinding.tvAddTag.text = c
        val p = Pattern.compile("#.+?#")
        var ss = if (content.isNotEmpty()) {
            val m = p.matcher(content)
            if (m.matches()) {
                SpannableString(m.replaceAll(c))
            } else {
                SpannableString(c + content)
            }
        } else {
            SpannableString(c)
        }
        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.legacy_module_primary_color)), 0,
            c.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mModel.content =ss.toString()
        mBinding.etContent.setText(ss)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == ADDTAG) {
                if (data.hasExtra("tag")) {
                    val tagName = data.getStringExtra("tag")
                       setContent(tagName,mBinding.etContent.text.toString())
                }
            } else if (requestCode == ADDLOCATION) {
                if (data.hasExtra("address")) {
                    val address = data.getParcelableExtra<ItemAddressBean>("address")
                    mModel.address.set(address)
                    mBinding.tvLocationLabel.visibility = View.GONE
                    mBinding.tvLocation.visibility = View.VISIBLE
                    mBinding.tvLocation.text = address.name
                }
            } else if (requestCode == Constrant.ADD_IMAGE) {
                // 選擇图片视频上传
                if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                    var list: ArrayList<Image> =
                        data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                    mBinding.rvImages.onActivityResult(list)
                }
            } else if (requestCode == Constrant.ADD_VIDEO) {
                // 選擇图片视频上传
                if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                    var list: ArrayList<Image> =
                        data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                    if (list.size > 0) {
                        mBinding.rvImages.insertAtFirst(list[0].apply {
                            type=1
                        })

                    }
                }
            }
        }

    }
}


class PublishWorksVM:BaseViewModel(){

    /**
     * 选中的地点
     */
    var address = ObservableField<ItemAddressBean>()
    /**
     * 参与的话题id
     */
    var topicId = ""

    /**
     * 话题列表
     */
    var topics = MutableLiveData<MutableList<HomeTopicBean>>()

    /**
     * 输入的作品内容
     */
    var content = ""


    /**
     * 三方链接
     */
    var tripartiteLink = MutableLiveData<String>("")

    /**
     * 获取推荐的的热门话题
     */
    fun getTopicList(){
        mPresenter.value?.loading = true
        val map = HashMap<String,String>()
        map["recommend"] = "1"
        map["topicStatus"] = "1" //话题状态 0：未开始 1：进行中 2：已结束
        map["pageSize"] = "10"
        HomeRepository.service.getTopicList(map)
            .excute(object : BaseObserver<HomeTopicBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeTopicBean>) {
                    topics.postValue( response.datas)
                }
            })

    }

    /**
     * 图片文件路径
     */
    var urls = ""

    var title = ""

    var pkid = ""

    var id = ""
    /**
     * 发布作品
     */
    fun publishWorks() {
        mPresenter.value?.loading = true
        val param =HashMap<String,Any>()
        param["content"] = content
        if(id.isNotEmpty()){
            param["id"] = id
        }
        param["status"] = 1
        param["storyType"] ="story"
        if(pkid.isNotEmpty()){
            param["pkId"] = pkid
        }else{
            param["ichWorks"] = true
        }
        if (urls!=""){
            var imags = urls.split(",")
            var imageUrls = ""
            for(item in imags){
                if(FileUtils.isVideo(item)){
                    param["video"] = item
                }else{
                    imageUrls+= "$item,"
                }
            }
            if(!imageUrls.isNullOrEmpty()){
                imageUrls = imageUrls.substring(0,imageUrls.lastIndexOf(","))
                param["images"] = imageUrls
            }
        }

        if (address.get()!=null){
            param["latitude"] = address.get()!!.latitude.toString()
            param["longitude"] = address.get()!!.longitude.toString()
            param["resourceType"] = address.get()!!.resourceType
            param["resourceId"] = address.get()!!.resourceId
        }

        if (!topicId.isNullOrEmpty()){
            param["topicId"] = topicId
        }

        if (!title.isNullOrEmpty()){
            param["title"] = title
        }

        tripartiteLink.value?.let {
            if (it.trim().isNotBlank()){
                param["sourceUrl"] = it
            }
        }

        HomeRepository.service.postStoryStrategy(param)
            .excute(object : BaseObserver<String>(mPresenter) {
                override fun onSuccess(response: BaseResponse<String>) {
                    toast.postValue(response.message)
                    if (response.code==0){
                            val id = response.data
                        ARouter.getInstance()
                            .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
                            .withInt("type",1)
                            .withString("id",id)
                            .navigation()
                        finish.postValue(true)
                     }
                }
            })
    }

    var worksDetail = MutableLiveData<HomeStoryBean>()

    /**
     * 获取我的作品详情
     */
    fun getMineWorkDetail(id: String) {
        HomeRepository.service.getMineStoryDetail(id)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    var homeStoryTagBean = response.data
                    worksDetail.postValue(homeStoryTagBean)
                }
            })
    }

}
package com.daqsoft.travelCultureModule.story.vm

import android.text.SpannableString
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.bean.StrategyDetail
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.network.home.bean.ItemAddressBean
import com.google.gson.Gson
import me.nereo.multi_image_selector.utils.FileUtils

/**
 * @Description 添加故事的viewmodel
 * @ClassName   WriteStoryFragmentViewModel
 * @Author      PuHua
 * @Time        2020/2/19 10:09
 */
class WriteStoryFragmentViewModel : BaseViewModel() {
    /**
     * 发布故事攻略的类型
     */
    var storyType = "story"

    /**
     * 输入的故事内容
     */
    var content = ""

    /**
     * 故事详情
     */
    val storyDetail by lazy { MutableLiveData<HomeStoryBean>() }

    val locationIntroduce by lazy { ObservableField<String>() }

    /**
     * 话题列表
     */
    val homeTopicBean by lazy { MutableLiveData<MutableList<HomeTopicBean>>() }

    /**
     * 选中的地点
     */
    val address by lazy { ObservableField<ItemAddressBean>() }

    /**
     * 参与的话题id
     */
    var topicId = ""

    /**
     * 标题
     */
    val title by lazy { ObservableField<String>() }

    /**
     * 图片文件路径
     */
    var urls = ""

    /**
     * 攻略列表
     */
    lateinit var strategyList: MutableList<StrategyDetail>

    /**
     * 故事封面
     */
    val cover by lazy { ObservableField<String>() }

    var id = ""
    var strategyId = ""

    var sourceUrl: String = ""

    /**
     * 发布故事
     */
    fun publishStory(saveType: Int = 0) {
        mPresenter.value?.loading = true
        val param = HashMap<String, Any>()
        param["content"] = content
        param["storyType"] = storyType

        if (storyType == "strategy") {
            param["strategy"] = Gson().toJson(strategyList)
            param["cover"] = cover.get()!!
        }

        if (urls != "") {
            var imags = urls.split(",")
            var imageUrls = ""
            for (item in imags) {
                if (FileUtils.isVideo(item)) {
                    param["video"] = item
                } else {
                    imageUrls += "$item,"
                }
            }
            if (!imageUrls.isNullOrEmpty()) {
                imageUrls = imageUrls.substring(0, imageUrls.lastIndexOf(","))
                param["images"] = imageUrls
            }
        }

        if (address.get() != null) {
            param["latitude"] = address.get()!!.latitude.toString()
            param["longitude"] = address.get()!!.longitude.toString()
            param["resourceType"] = address.get()!!.resourceType
            param["resourceId"] = address.get()!!.resourceId.toString()
        }

        if (!topicId.isNullOrEmpty()) {
            param["topicId"] = topicId
        }

        if (title.get() != null) {
            param["title"] = title.get()!!
        }
        if (storyType == "story" && !id.isNullOrEmpty()) {
            param["id"] = id
        }
        if (storyType == "strategy" && !strategyId.isNullOrEmpty()) {
            param["id"] = strategyId
        }

        param["status"] = saveType
        if (!sourceUrl.isNullOrEmpty()) {
            param["sourceUrl"] = sourceUrl
        }

        HomeRepository.service.postStoryStrategy(param)
            .excute(object : BaseObserver<String>(mPresenter) {
                override fun onSuccess(response: BaseResponse<String>) {
                    if (response.code == 0) {
                        toast.postValue(response.message)
                        if (saveType != 3) {
                            if (storyType != "strategy") {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STORY_DETAIL)
                                    .withString("id", response.data.toString())
                                    .withInt("type", 2)
                                    .navigation()
                            } else {
                                ARouter.getInstance()
                                    .build(MainARouterPath.MAIN_STRATEGY_DETAIL)
                                    .withString("id", response.data.toString())
                                    .withInt("type", 2)
                                    .navigation()
                            }
                            finish.postValue(true)
                        } else {
                            if (storyType != "strategy") {
                                id = response.data.toString()
                            } else {
                                strategyId = response.data.toString()
                            }
                        }
                    } else {
                        if (response.code == 2) {
                            toast.postValue("您还没有登录，请先登录!")
                        } else {
                            toast.postValue(response.message)
                        }
                    }
                }
            })
    }

    /**
     * 获取推荐的的热门话题
     */
    fun getTopicList() {
        mPresenter.value?.loading = true
        val map = HashMap<String, String>()
        map["recommend"] = "1"
        map["topicStatus"] = "1" //话题状态 0：未开始 1：进行中 2：已结束
        map["pageSize"] = "10"
        HomeRepository.service.getTopicList(map)
            .excute(object : BaseObserver<HomeTopicBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeTopicBean>) {
                    homeTopicBean.postValue(response.datas)
                }
            })

    }

    /**
     * 获取我的故事详情
     */
    fun getMineStoryDetail(id: String) {
        mPresenter.value?.loading = true
        HomeRepository.service.getMineStoryDetail(id)
            .excute(object : BaseObserver<HomeStoryBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeStoryBean>) {
                    storyDetail.postValue(response.data)
//                   locationIntroduce.set(DividerTextUtils.convertDotString(StringBuilder(),response.data.))
                }
            })
    }

}
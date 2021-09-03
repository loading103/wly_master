package com.daqsoft.guidemodule.scenicList

import androidx.lifecycle.MutableLiveData
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.guidemodule.bean.GuideHomeListBean
import com.daqsoft.guidemodule.net.GuideRepository
import com.daqsoft.provider.base.LabelType
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.provider.bean.ResourceTypeLabel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


internal class GuideScenicListViewModel : BaseViewModel() {


    private val param = HashMap<String, Any>()

    /**
     * 条件列表
     */
    val selectLabels = MutableLiveData<MutableList<MutableList<ResourceTypeLabel>>>()

    /**
     * 分页管理器
     */
    val pageManager: PageManager by lazy {
        PageManager(10)
    }

    /**
     * 主题类型
     */
    var theme: String? = ""

    /**
     * 经度
     */
    var lng: String = ""

    /**
     * 纬度
     */
    var lat: String = ""

    /**
     * 适合人群
     */
    var crowd: String? = ""

    /**
     * 等级
     */
    var level: String? = ""

    var isInitRequest: Boolean = true


    /**
     * 景点列表
     */
    val scenicList = MutableLiveData<MutableList<GuideHomeListBean>>()

    /**
     * 获取分类标签
     */
    fun getSelectLabel() {
        mPresenter.value?.loading = false
        val list = mutableListOf<MutableList<ResourceTypeLabel>>()
        val merge = Observable.concat(
            GuideRepository.service.getSelectLabel(
                LabelType.SCENIC_THEME,
                ResourceType.CONTENT_TYPE_SCENERY
            ),
            GuideRepository.service.getSelectLabel(
                LabelType.SUITABLE_FOR_PEOPLE, ResourceType.CONTENT_TYPE_SCENERY
            )
        )
        merge.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponse<ResourceTypeLabel>> {
                override fun onComplete() {
                    selectLabels.postValue(list)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: BaseResponse<ResourceTypeLabel>) {
                    val data = t.datas
                    data?.add(ResourceTypeLabel("", "", "", "", "全部"))
                    data?.let { list.add(it) }
                }

                override fun onError(e: Throwable) {

                }

            })
    }


    /**
     * 获取景区列表信息
     */
    fun getScenicList(useHot: Boolean, useDistance: Boolean) {
        param.clear()
        mPresenter.value?.loading = false
        mPresenter.value?.isNeedRecyleView = isInitRequest
        isInitRequest = false

        // 适应人群
        if (!crowd.isNullOrEmpty()) {
            param["crowd"] = crowd!!
        }
        // 景区主题
        if (!theme.isNullOrEmpty()) {
            param["theme"] = theme!!
        }
        // 等级
        if (!level.isNullOrEmpty()) {
            param["level"] = level!!
        }
        // 经纬度
        if (!lng.isNullOrEmpty() && !lat.isNullOrEmpty()) {
            param["lon"] = lng
            param["lat"] = lat
        }

        param["recommendSort"] = useHot

        param["distanceSort"] = useDistance

        // 页面大小
        param["pageSize"] = pageManager.pageSize.toString()
        // 当前页码
        param["currPage"] = pageManager.pageIndex.toString()

        GuideRepository.service.getGuideList(param).excute(object : BaseObserver<GuideHomeListBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<GuideHomeListBean>) {
                mPresenter.value?.loading = false
                scenicList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<GuideHomeListBean>) {
                super.onFailed(response)
                mPresenter.value?.loading = false
                mError.postValue(response)
            }
        })
    }


}
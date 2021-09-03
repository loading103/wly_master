package com.daqsoft.travelCultureModule.hotActivity.detail

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityCommentListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.businessview.adapter.ProviderCommentNewAdapter
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Description 活动评论列表
 * @ClassName   ActivityCommentListActivity
 * @Author      PuHua
 * @Time        2020/1/15 13:52
 */
@Route(path = MainARouterPath.MAIN_ACTIVITY_COMMENT)
class ActivityCommentListActivity : TitleBarActivity<MainActivityCommentListBinding, ActivityCommentViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var type: String = ""

    var currPage: Int = 1

    override fun getLayout(): Int = R.layout.main_activity_comment_list

    override fun setTitle(): String = getString(R.string.home_activity_comment)

    override fun injectVm(): Class<ActivityCommentViewModel> = ActivityCommentViewModel::class.java

    override fun initView() {

//        val commentAdapter = CommentAdapter(this)
        EventBus.getDefault().register(this)
        var commentAdapter = ProviderCommentNewAdapter(this!!,false,"")
        val tagLayoutManager =
            LinearLayoutManager(
                this@ActivityCommentListActivity, LinearLayoutManager.VERTICAL,
                false
            )
        mBinding.rvComments.layoutManager = tagLayoutManager
        mBinding.rvComments.adapter = commentAdapter
        commentAdapter?.setOnLoadMoreListener {
            currPage += 1
            mModel.getActivityCommentList(currPage, id, type)
        }
        mModel.commentBeans.observe(this, Observer {
            dissMissLoadingDialog()
            if (mBinding.rvComments.visibility == View.GONE) {
                mBinding.rvComments.visibility = View.VISIBLE
            }
            PageDealUtils().pageDeal(currPage,it,commentAdapter)
            if(!it?.datas.isNullOrEmpty()){
                commentAdapter?.add(it!!.datas!!)
            }
        })


    }

    override fun initData() {
        showLoadingDialog()
        mModel.getActivityCommentList(currPage, id, type)
    }

    fun gotoAddComment(v: View) {
        ARouter.getInstance()
            .build(MainARouterPath.MAIN_COMMENT_ADD)
            .withString("id", id)
            .withString("type", type)
            .navigation()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mModel.getActivityCommentList(currPage, id, type)
    }
}

class ActivityCommentViewModel : BaseViewModel() {

    /**
     * 获取活动评论列表
     */
    var commentBeans = MutableLiveData<BaseResponse<CommentBean>>()

    /**
     * 获取评论列表
     */
    fun getActivityCommentList(currPage: Int, id: String, type: String = ResourceType.CONTENT_TYPE_STORY) {

        mPresenter.value?.loading = false
        val param = HashMap<String, String>()
        param["resourceType"] = type
        param["resourceId"] = id
        param["pageSize"] = "10"
        param["currPage"] = currPage.toString()
        CommentRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>() {
            override fun onSuccess(response: BaseResponse<CommentBean>) {
                commentBeans.postValue(response)
            }

            override fun onFailed(response: BaseResponse<CommentBean>) {
                commentBeans.postValue(response)
            }
        })
    }


}
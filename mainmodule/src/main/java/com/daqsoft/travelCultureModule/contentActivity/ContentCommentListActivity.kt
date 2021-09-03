package com.daqsoft.travelCultureModule.contentActivity

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
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityContentCommentListBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.PageManager
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.comment.beans.CommentBean


/**
 * @des     内容详情页的评论列表
 * @author  Wongxd
 * @date    2020-4-28  9:13
 */
@Route(path = MainARouterPath.MAIN_CONTENT_COMMENT_LIST)
internal class ContentCommentListActivity : TitleBarActivity<ActivityContentCommentListBinding, ContentCommentListViewModel>() {

    override fun getLayout(): Int = R.layout.activity_content_comment_list

    override fun setTitle(): String = "评论列表"

    override fun injectVm(): Class<ContentCommentListViewModel> = ContentCommentListViewModel::class.java


    @Autowired
    @JvmField
    var infoId = ""


    private val rvAdapter by lazy { CommentAdapter(this) }

    override fun initView() {


        mBinding.srl.setOnRefreshListener {
            mBinding.srl.finishRefresh()
            refresh()
        }

        mBinding.srl.setOnLoadMoreListener {
            mBinding.srl.finishLoadMore()
            loadMore()
        }


        mBinding.rv.apply {
            layoutManager = LinearLayoutManager(this@ContentCommentListActivity)
            adapter = rvAdapter
        }


        mModel.commentBeans.observe(this, Observer {


            mBinding.rv.visibility = View.VISIBLE

            if (it.isNullOrEmpty()) {
                rvAdapter.loadEnd()
            } else
                rvAdapter.loadComplete()

            if (mModel.pageManager.isFirstIndex) {
                rvAdapter.clearNotify()
            }


            rvAdapter.add(it)


        })

    }

    private fun refresh() {
        mModel.pageManager.initPageIndex()
        mModel.getActivityCommentList(infoId)
    }

    private fun loadMore() {
        mModel.pageManager.nexPageIndex
        mModel.getActivityCommentList(infoId)
    }

    override fun initData() {
        refresh()
    }
}

/**
 * @des     内容详情页的评论列表
 * @author  Wongxd
 * @date    2020-4-28  9:13
 */
internal class ContentCommentListViewModel : BaseViewModel() {


    val pageManager = PageManager(10)


    //获取评论列表
    var commentBeans = MutableLiveData<MutableList<CommentBean>>()

    fun getActivityCommentList(id: String) {
        mPresenter.value?.loading = true
        val param = HashMap<String, String>()
        param["resourceType"] = "CONTENT"
        param["resourceId"] = id
        param["currPage"] = pageManager.pageIndex.toString()
        param["pageSize"] = pageManager.pageSize.toString()

        CommentRepository.service.getCommentList(param).excute(object : BaseObserver<CommentBean>() {
            override fun onSuccess(response: BaseResponse<CommentBean>) {
                commentBeans.postValue(response.datas)
            }
        })
    }
}
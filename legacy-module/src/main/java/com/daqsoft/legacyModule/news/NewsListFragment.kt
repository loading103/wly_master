package com.daqsoft.legacyModule.news

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.BindingViewHolder
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.databinding.ItemLegacyNewsBinding
import com.daqsoft.legacyModule.databinding.LegacyModuleFragmentNewsBinding
import com.daqsoft.legacyModule.dp
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import java.util.HashMap

/**
 *@package:com.daqsoft.legacyModule.news
 *@date:2020/5/14 10:04
 *@author: caihj
 *@des:新闻列表页面
 **/
class NewsListFragment:BaseFragment<LegacyModuleFragmentNewsBinding,NewsViewModel>() {



    companion object{
        const val NEWS_TYPE = "news_type"
        fun newInstance(type:String):NewsListFragment{
            val bundle = Bundle()
            bundle.putString(NEWS_TYPE,type)
            val newsListFragment = NewsListFragment()
            newsListFragment.arguments = bundle
            return newsListFragment
        }
    }

    override fun getLayout(): Int = R.layout.legacy_module_fragment_news

    override fun injectVm(): Class<NewsViewModel> = NewsViewModel::class.java

    override fun initView() {

        mBinding.rvNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
        newsAdapter.setOnLoadMoreListener {
            mModel.currPage++
            mModel.getNewList()
        }
        mModel.news.observe(this, Observer {
            pageDeal(it)
        })
    }

    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(datas: MutableList<HomeContentBean>) {
        if (mModel.currPage == 1) {
            mBinding?.rvNews.smoothScrollToPosition(0)
            mBinding?.rvNews.visibility = View.VISIBLE
            newsAdapter!!.clear()
            newsAdapter!!.emptyViewShow = datas.isNullOrEmpty()

        }
        if (!datas.isNullOrEmpty()) {
            newsAdapter!!.add(datas)
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.pageSize.toInt()) {
            newsAdapter!!.loadEnd()
        } else {
            newsAdapter!!.loadComplete()
        }
        dissMissLoadingDialog()
    }


    override fun initData() {
        mModel.type = arguments?.getString(NEWS_TYPE).toString()
        mModel.getNewList()
    }

    private val newsAdapter = object :RecyclerViewAdapter<ItemLegacyNewsBinding,HomeContentBean>(R.layout.item_legacy_news){
        override fun setVariable(mBinding: ItemLegacyNewsBinding, position: Int, item: HomeContentBean) {
            if(item.imageUrls.isNullOrEmpty()){
                mBinding.avIcon.visibility = View.GONE
            }else{
                Glide.with(context!!)
                    .load(item.imageUrls[0].url)
                    .into(mBinding.avIcon)
            }
            mBinding.tvTitle.text = item.title
            mBinding.tvType.text = item.channelName
            val timestr = DateUtil.formatDateByString("yyyy-MM-dd","yyyy-MM-dd HH:mm",item.publishTime)
            mBinding.tvTime.text = item.publishTime
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id", item.id.toString())
                    .withString("contentTitle", "资讯详情")
                    .navigation()
            }
            if (position == getData().size-1){
                mBinding.line.visibility = View.GONE
            }
        }

    }

}

class NewsViewModel:BaseViewModel(){
    var type = ""
    var news = MutableLiveData<MutableList<HomeContentBean>>()
    var currPage = 1
    val pageSize = "20"
    fun getNewList(){
        val param = HashMap<String, String>()
        param["channelCode"] = type
        param["pageSize"] = pageSize
        param["currPage"] = currPage.toString()
        HomeRepository.service.getHomeContentList(param)
            .excute(object : BaseObserver<HomeContentBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<HomeContentBean>) {
                    news.postValue(response.datas)
                }
            })
    }
}
package com.daqsoft.legacyModule.mine

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.databinding.MineLegacyPopupBinding
import com.daqsoft.legacyModule.databinding.MineWorksListBinding
import com.daqsoft.legacyModule.mine.adpter.MyWorksAdapter
import com.daqsoft.legacyModule.net.LegacyRepository
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.BasePopupWindow
import com.daqsoft.provider.view.ListPopupWindow
import org.jetbrains.anko.toast


/**
 * @Description 我的作品
 * @ClassName   HotActivityDetailActivity
 * @Author      Caihj
 * @Time        2020/05/18 17:28
 */
@Route(path =  ARouterPath.LegacyModule.LEGACY_MINE_WORKS)
class MineWorksListActivity : TitleBarActivity<MineWorksListBinding, MyWorksListActivityViewModel>() {

    //排序弹窗
    var sortPopupWindow:ListPopupWindow<Any>? = null
    val sorts = mModel.sorts
    //标签弹窗
    var tagPopupWindow:ListPopupWindow<Any>? = null
    //删除对话框
    var deleteDialog:BaseDialog ? = null
    var popWindow:BasePopupWindow? = null
    var workList:MutableList<LegacyStoryListBean> ? = null
    var legacyStoryListBean:LegacyStoryListBean? = null

    override fun getLayout(): Int = R.layout.mine_works_list

    override fun setTitle(): String = getString(R.string.legacy_module_mine_works)


    override fun injectVm(): Class<MyWorksListActivityViewModel> = MyWorksListActivityViewModel::class.java
    private var storyAdapter: MyWorksAdapter? = null
    override fun initView() {
        mModel.storyList.observe(this, Observer {
//            mBinding.swRefreshStory.isRefreshing = false
            mBinding.swRefreshStory.finishRefresh()
            dissMissLoadingDialog()
            workList = it
            pageDeal(it)
        })


        // 故事
        storyAdapter = MyWorksAdapter(this)
        val storyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mBinding.rvWorks.layoutManager = storyLayoutManager
        mBinding.rvWorks.adapter = storyAdapter

        mModel.tags.observe(this, Observer {

            it?.add(0, TagBean("","全部"))
            tagPopupWindow = ListPopupWindow.getInstance(mBinding.tvTag,it as List<Any>){item ->
                run {
                        mBinding.tvTag.text = (item as TagBean).name
                    mModel.currentTag = item.id
                   refreshData()
                }
            }

        })

//        mBinding?.swRefreshStory.setProgressViewOffset(true, -20, 100);
        mBinding.swRefreshStory.setOnRefreshListener {
//            mBinding?.swRefreshStory.isRefreshing = true
            mModel.currentPage = 1
            storyAdapter?.clear()
            mModel.getMyStoryList()
        }
        storyAdapter!!.setOnLoadMoreListener {
                mModel.currentPage = 1 + mModel.currentPage
                mModel.getMyStoryList()
            }
        storyAdapter!!.setClickItemListener { item, position ->
            initPopupWindow(item)
            legacyStoryListBean = item
            mModel.deletePosition = position
            popWindow!!.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
        }

        mModel.deleteFinish.observe(this, Observer {
           if(it){
               toast("删除成功!")
               storyAdapter!!.getData().removeAt(mModel.deletePosition)
               storyAdapter!!.notifyItemRemoved(mModel.deletePosition)
           }else{
               toast("删除失败!")
           }
        })
        mModel.topStatus.observe(this, Observer {
            dissMissLoadingDialog()
            if(it){
                doTopData()
            }
        })
        initEvent()
        initDeleteDialog()
    }

    private fun doTopData(){
        if(legacyStoryListBean != null){
            if(legacyStoryListBean!!.top == 0){
                legacyStoryListBean!!.top = 1
                workList?.remove(legacyStoryListBean!!)
                workList?.add(0,legacyStoryListBean!!)
            }else{
                legacyStoryListBean!!.top = 0
                workList?.remove(legacyStoryListBean!!)
                workList?.add(legacyStoryListBean!!)
            }
            storyAdapter!!.clear()
            storyAdapter!!.add(workList!!)
        }
    }

    /**
     * 刷新数据
     */
    private fun refreshData(){
        mModel.currentPage = 1
        storyAdapter?.clear()
        showLoadingDialog()
        mModel.getMyStoryList()
    }


    private fun initSortPopupWindow(){
        sortPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort,sorts){
            item ->
            kotlin.run {
                mBinding.tvSort.text = (item as ValueKeyBean).name
                mModel.currentSort = item.value
                refreshData()
            }
        }
    }

    private fun initDeleteDialog(){
        deleteDialog = BaseDialog(this)
        deleteDialog!!.contentView(R.layout.dialog_delete_works)
            .layoutParams( ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            .gravity(Gravity.CENTER)
            .animType(BaseDialog.AnimInType.BOTTOM)
            .canceledOnTouchOutside(false)
        deleteDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            deleteDialog!!.dismiss()
        }
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
            mModel.deleteWorks(legacyStoryListBean?.id.toString())
            deleteDialog!!.dismiss()
        }
    }

    override fun initData() {
        initSortPopupWindow()
        mModel.getTagList()
    }

    override fun onResume() {
        super.onResume()
        mModel.getMyStoryList()
    }

    private fun initEvent(){
        mBinding.tvSort.onNoDoubleClick {
            sortPopupWindow?.show()
        }
        mBinding.tvTag.onNoDoubleClick {
            tagPopupWindow?.show()
        }
    }

    /**
     * 列表数据页码处理
     *
     * @param page     当前页
     * @param response 返回数据体
     * @param adapter  适配器
     */
    private fun pageDeal(datas: MutableList<LegacyStoryListBean>) {
        if (mModel.currentPage == 1) {
            mBinding?.rvWorks.smoothScrollToPosition(0)
            mBinding?.rvWorks.visibility = View.VISIBLE
            storyAdapter!!.clear()
            storyAdapter!!.emptyViewShow = datas.isNullOrEmpty()

        }
        if (!datas.isNullOrEmpty()) {
            storyAdapter!!.add(datas)
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.pageSize) {
            storyAdapter!!.loadEnd()
        } else {
            storyAdapter!!.loadComplete()
        }
        dissMissLoadingDialog()
    }



    @SuppressLint("WrongConstant")
    fun initPopupWindow(item:LegacyStoryListBean){
           if(popWindow == null){
               val inflater = LayoutInflater.from(BaseApplication.context)
               val addBinding: MineLegacyPopupBinding = DataBindingUtil.inflate(
                   inflater,
                   R.layout.mine_legacy_popup,
                   null,
                   false
               )
               popWindow = BasePopupWindow(
                   addBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
                   LinearLayout.LayoutParams.WRAP_CONTENT, true
               )
               popWindow!!.setOnDismissListener {
                   popWindow = null
               }
               popWindow!!.softInputMode = PopupWindow.INPUT_METHOD_NEEDED
               popWindow!!.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
               // 设置背景
               popWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
               // 设置是否能够响应外部点击事件
               popWindow!!.isOutsideTouchable = true
               // 设置能否响应点击事件
               popWindow!!.isTouchable = true
               popWindow!!.isFocusable = true
               popWindow!!.resetDarkPosition()
               if(item.top == 1){
                   addBinding.tvPopupTop.text = "取消置顶"
               }else{
                   addBinding.tvPopupTop.text = "置顶"
               }
               addBinding.tvPopupCancel.onNoDoubleClick {
                   popWindow!!.dismiss()
                   popWindow = null
               }
//            addBinding.tvPopupEdit.onNoDoubleClick {
//                TODO("编辑")
//            }
               addBinding.tvPopupTop.onNoDoubleClick {
                   popWindow!!.dismiss()
                   popWindow = null
                   if(item.top == 1){
                       showLoadingDialog()
                       mModel.vipTop(item.id,0)
                   }else{
                       showLoadingDialog()
                       mModel.vipTop(item.id,1)
                   }
               }
               addBinding.tvPopupDel.onNoDoubleClick {
                   popWindow!!.dismiss()
                   popWindow = null
                   deleteDialog!!.show()
               }
           }
    }

}

/**
 * @des 活动详情的viewmodel
 * @author PuHua
 * @Date 2019/12/26 18:11
 */
class MyWorksListActivityViewModel : BaseViewModel() {

    /**
     * 故事排序
     */
    val sorts = mutableListOf(
        ValueKeyBean("不限", ""),
        ValueKeyBean("浏览量优先", "showNum"),
        ValueKeyBean("评论量优先", "commentNum"),
        ValueKeyBean("点赞量优先", "likeNum"),
        ValueKeyBean("被跟做优先", "pkNum"))

    /**
     * 故事列表数据
     */
    val storyList = MutableLiveData<MutableList<LegacyStoryListBean>>()

    /**
     * 个人标签
     */
    val tags = MutableLiveData<MutableList<TagBean>>()

    /**
     * 删除状态
     */
    val deleteFinish = MutableLiveData<Boolean>()

    /**
     * 当前标签
     */
    var currentTag = ""
    /**
     * 当前排序
     */
    var currentSort =""

    /**
     * 当前页码
     */
    var currentPage:Int  = 1

    /**
     * 删除位置
     */
    var deletePosition:Int = -1

    var deleteStory:HomeStoryBean ? = null

    val pageSize = 20

    /**
     * 获取标签
     */
    fun getTagList(){
        val param = HashMap<String, String>()
        param["ich"] = "true"
        HomeRepository.service.getVIPTagList(param).excute(object : BaseObserver<TagBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<TagBean>) {
                tags.postValue(response.datas)
            }
        })
    }

    fun getMyStoryList(){
        val param = HashMap<String, String>()
        // homeCover   首页封面	number	【选填】是否首页封面1：是 0：否
//        param["homeCover"] = "1"
        //  pageSize
        param["pageSize"] = "$pageSize"
        param["currPage"] = "$currentPage"
        param["tagId"] = currentTag
        if (currentSort != "") {
            param["orderType"] = currentSort
        }
        param["ich"] = "1"
        param["ichWorks"] = "1"
        mPresenter.value?.loading = false
        LegacyRepository.service.getMineLegacyWorks(param).excute(object : BaseObserver<LegacyStoryListBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<LegacyStoryListBean>) {
                storyList.postValue(response.datas)
            }

            override fun onFailed(response: BaseResponse<LegacyStoryListBean>) {
                super.onFailed(response)
                storyList.postValue(response.datas)
            }
        })

    }
    var topStatus = MutableLiveData<Boolean>()

    fun vipTop(id:Int,top:Int){
        LegacyRepository.service.vipTop(id = id,top = top).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                topStatus.postValue(true)
                if(top == 0){
                    ToastUtils.showMessage("取消置顶成功!")
                }else{
                    ToastUtils.showMessage("置顶成功!")
                }
            }

            override fun onFailed(response: BaseResponse<String>) {
                topStatus.postValue(false)
                if(top == 0){
                    ToastUtils.showMessage("取消置顶失败!")
                }else{
                    ToastUtils.showMessage("置顶失败!")
                }
            }

        })
    }

    /**
     * 删除作品
     */
    fun deleteWorks(id:String){
        val param = HashMap<String, String>()
        param["id"] = id
        LegacyRepository.service.postDeleteWorks(param).excute(object :BaseObserver<String>(){
            override fun onSuccess(response: BaseResponse<String>) {
                if(response.code == 0){
                    deleteFinish.postValue(true)
                }else{
                    deleteFinish.postValue(false)
                }
            }

            override fun onError(e: Throwable) {
                deleteFinish.postValue(false)
            }

        })
    }

}
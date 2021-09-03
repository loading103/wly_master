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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.LegacyStoryListBean
import com.daqsoft.legacyModule.databinding.ActivityMineLegacyBinding
import com.daqsoft.legacyModule.databinding.MineLegacyPopupBinding
import com.daqsoft.legacyModule.mine.adpter.MyWorksAdapter
import com.daqsoft.legacyModule.mine.vm.MineLegacyVM
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.TagBean
import com.daqsoft.provider.bean.ValueKeyBean
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.BasePopupWindow
import com.daqsoft.provider.view.ListPopupWindow
import org.jetbrains.anko.toast

/**
 *@package:com.daqsoft.legacyModule.mine
 *@date:2020/5/18 10:26
 *@author: caihj
 *@des:我的非遗界面
 **/
@Route(path = ARouterPath.LegacyModule.LEGACY_MINE)
class MineLegacyActivity:TitleBarActivity<ActivityMineLegacyBinding,MineLegacyVM>() {
    override fun getLayout(): Int = R.layout.activity_mine_legacy

    override fun setTitle(): String = getString(R.string.legacy_module_my_legacy)

    override fun injectVm(): Class<MineLegacyVM> = MineLegacyVM::class.java

    var adapter:MyWorksAdapter? = null

    var popWindow:BasePopupWindow? = null
    var workList:MutableList<LegacyStoryListBean> ? = mutableListOf()
    var legacyStoryListBean:LegacyStoryListBean? = null

    //排序弹窗
    var sortPopupWindow: ListPopupWindow<Any>? = null
    val sorts = mModel.sorts
    //标签弹窗
    var tagPopupWindow: ListPopupWindow<Any>? = null
    //删除对话框
    var deleteDialog: BaseDialog? = null



    @SuppressLint("SetTextI18n")
    override fun initView() {
        mModel.legacyNum.observe(this, Observer {
            dissMissLoadingDialog()
            if(it!=null){
                mBinding.tvProductCount.text = it.storyNum.toString()
                mBinding.tvFansCount.text = it.fans.toString()
                mBinding.tvPkCount.text = it.pk.toString()
                mBinding.tvFocusCount.text = it.watch.toString()
                Glide.with(this@MineLegacyActivity)
                    .load(it.headUrl)
                    .placeholder(R.mipmap.mine_profile_photo_default)
                    .into(mBinding.rvHead)

//                Glide.with(mBinding.ivBg1)
//                    .asBitmap()
//                    .load(it.headUrl)
//                    .placeholder(R.mipmap.mine_profile_photo_default)
//                    .centerCrop()
//                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 1)))
//                    .into(object : CustomTarget<Bitmap>() {
//                        override fun onResourceReady(
//                            resource: Bitmap,
//                            transition: Transition<in Bitmap>?
//                        ) {
//
//                            var b = GaosiUtils.rsBlur(applicationContext, resource, 25)
//                            mBinding.ivBg1.setImageBitmap(b)
//                        }
//
//                        override fun onLoadCleared(placeholder: Drawable?) {
//
//                        }
//                    })
                mBinding.tvName.text = it.name
            }
        })
        adapter = MyWorksAdapter(this@MineLegacyActivity)
        mBinding.rvList.layoutManager = LinearLayoutManager(this@MineLegacyActivity)
        mBinding.rvList.adapter = adapter
        adapter?.setClickItemListener { item, position ->
            initPopupWindow(item)
            legacyStoryListBean = item
            mModel.deletePosition = position
            popWindow!!.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
        }
        mModel.workList.observe(this, Observer {response ->
            dissMissLoadingDialog()
            response.datas?.let{
                PageDealUtils().pageDeal(mModel.currentPage, response, adapter!!)
                adapter?.add(it)
                adapter?.notifyDataSetChanged()
            }
//            if(it.isNotEmpty()){
//                workList?.addAll(it)
//               pageDel(it)
//            }else{
//                mBinding.llMineCount.visibility = View.GONE
//            }
        })
        mModel.pageData.observe(this, Observer {
//            if(it.total!=0){
                mBinding.tvCount.text = "(${it.total})"
                mBinding.tvTag.text = "${mBinding.tvTag.text}${it.total}"
//            }
        })
        mBinding.tvWatchInfo.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_PEOPLE_DETAIL_ACTIVITY)
                .withString("id", mModel.legacyNum.value?.heritagePeopleId)
                .navigation()
        }
        mBinding.clProduct.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_MINE_WORKS)
                .navigation()
        }
        mBinding.clFans.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_MINE_FANS)
                .withString("type","fans")
                .navigation()
        }
        mBinding.clFocus.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_MINE_FANS)
                .withString("type","watch")
                .navigation()
        }
        mBinding.clPk.onNoDoubleClick {
            ARouter.getInstance().build(ARouterPath.LegacyModule.LEGACY_MINE_FANS)
                .withString("type","pk")
                .navigation()
        }
        mModel.topStatus.observe(this, Observer {
            dissMissLoadingDialog()
            if(it){
                doTopData()
            }
        })

        mModel.tags.observe(this, Observer {

            it?.add(0, TagBean("","全部"))
            tagPopupWindow = ListPopupWindow.getInstance(mBinding.tvTag,it as List<Any>){item ->
                run {
                    mBinding.tvTag.text = (item as TagBean).name
                    mModel.currentTag = item.id
                    mModel.currentPage = 1
                    showLoadingDialog()
                    adapter?.clear()
                    mModel.getWorkList()
                }
            }

        })
        mBinding.tvSort.onNoDoubleClick {
            sortPopupWindow?.show()
        }
        mBinding.tvTag.onNoDoubleClick {
            tagPopupWindow?.show()
        }
        mModel.deleteFinish.observe(this, Observer {
            if(it){
                toast("删除成功!")
                adapter!!.getData().removeAt(mModel.deletePosition)
                adapter!!.notifyItemRemoved(mModel.deletePosition)
            }else{
                toast("删除失败!")
            }
        })
        mBinding.ivPublish.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_PUBLISH_WORKS)
                .withString("type","publish")
                .navigation()
        }
        adapter?.setOnLoadMoreListener {
            mModel.currentPage ++
            mModel.getWorkList()
        }
    }

    /**
     * 置顶数据
     */
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
            adapter!!.clear()
            adapter!!.add(workList!!)
        }
    }

    private fun pageDel(datas :MutableList<LegacyStoryListBean>){
        if(mModel.currentPage == 1 ){
            mBinding.rvList.visibility = View.VISIBLE
            adapter!!.clear()
            adapter!!.emptyViewShow = datas.isNullOrEmpty()
        }
        if (!datas.isNullOrEmpty()) {
            adapter!!.add(datas)
        }
        if (datas.isNullOrEmpty() || datas.size < mModel.pageSize.toInt()) {
            adapter!!.loadEnd()
        } else {
            adapter!!.loadComplete()
        }
        dissMissLoadingDialog()
    }

    private fun initSortPopupWindow(){
        sortPopupWindow = ListPopupWindow.getInstance(mBinding.tvSort,sorts){
                item ->
            kotlin.run {
                mBinding.tvSort.text = (item as ValueKeyBean).name
                mModel.currentSort = item.value
                adapter?.clear()
                mModel.currentPage = 1
                showLoadingDialog()
                mModel.getWorkList()
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
            deleteDialog!!.dismiss()
            mModel.deleteWorks(legacyStoryListBean!!.id.toString())
        }
    }

    @SuppressLint("WrongConstant")
    fun initPopupWindow(item:LegacyStoryListBean){
          if(popWindow == null){
              val inflater = LayoutInflater.from(context)
              val addBinding:MineLegacyPopupBinding = DataBindingUtil.inflate(
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
//                ARouter.getInstance()
//                    .build(ARouterPath.LegacyModule.LEGACY_PUBLISH_WORKS)
//                    .withString("id", item?.id.toString())
//                    .withString("type","edit")
//                    .navigation()
//                popWindow!!.dismiss()
//                popWindow = null
//            }
              addBinding.tvPopupTop.onNoDoubleClick {
                  popWindow!!.dismiss()
                  popWindow = null
                  if(item.top == 1){
                      showLoadingDialog()
                      mModel.vipTop(item.id,0)
                  }else{
                      showLoadingDialog()
                      mModel.vipTop(item.id)
                  }
              }
              addBinding.tvPopupDel.onNoDoubleClick {
                  popWindow!!.dismiss()
                  popWindow = null
                  deleteDialog!!.show()
              }
          }
    }

    override fun initData() {
        initSortPopupWindow()
        initDeleteDialog()
        showLoadingDialog()
        mModel.getWorkList()
        mModel.getTagList()
    }

    override fun onResume() {
        super.onResume()
        mModel.getLegacyNum()
    }
}
package com.daqsoft.provider.businessview.ui

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.businessview.adapter.ProviderCommentNewAdapter
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.viewmodel.ProviderCommentLsViewModel
import com.daqsoft.provider.databinding.ProviderActivityCommentLsBinding
import com.daqsoft.provider.network.comment.beans.CommentBean
import com.daqsoft.provider.network.comment.beans.CommentTagsBean
import com.daqsoft.provider.network.comment.beans.CommtentTagBean
import com.daqsoft.provider.view.LabelsView
import com.google.android.material.appbar.AppBarLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Description 公共评论页码
 * @ClassName   ProviderCommentActivity
 * @Author      luoyi
 * @Time        2020/4/13 10:59
 */
@Route(path = ARouterPath.Provider.PROVIDER_COMMENT_LS)
class ProviderCommentLsActivity : TitleBarActivity<ProviderActivityCommentLsBinding, ProviderCommentLsViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var type: String = ""

    @JvmField
    @Autowired
    var contentTitle: String = ""

    /**
     * 评论组
     */
    var commentTagsMap: HashMap<String, CommentTagsBean> = hashMapOf()

    /**
     * 评论适配器
     */
    var providerCommentAdapter: ProviderCommentNewAdapter? = null

    var commentTags: MutableList<CommtentTagBean> = mutableListOf()

    /**
     * 当前选中类型
     */
    var mSelectTypes = 0

    /**
     * 当前选中tag位置
     */
    var mSelectTagPos = 0

    override fun getLayout(): Int {
        return R.layout.provider_activity_comment_ls
    }

    override fun setTitle(): String {
        return getString(R.string.provider_all_comment)
    }

    override fun injectVm(): Class<ProviderCommentLsViewModel> {
        return ProviderCommentLsViewModel::class.java
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        providerCommentAdapter = ProviderCommentNewAdapter(this@ProviderCommentLsActivity,true,type)
        mBinding.recyComments.layoutManager = LinearLayoutManager(this@ProviderCommentLsActivity, LinearLayoutManager.VERTICAL, false)
        mBinding.recyComments.adapter = providerCommentAdapter
        initViewEvent()
        initViewModel()
    }

    private fun initViewModel() {
        mModel.commentTagsBeans.observe(this, Observer {
            bindCommentTags(it)
        })
        mModel.commentTagsBeans2.observe(this, Observer {
            bindCommentTags2(it)
        })

        mModel.totalStartNum.observe(this, Observer {
            it?.let {
                mBinding.rlRoot.visibility=View.VISIBLE
                mBinding.RatingBar.rating=it.toFloat()
                mBinding.tvScore.text="综合评分：  $it"
            }

        })

        mModel.totalNum.observe(this, Observer {
//            mBinding.lblProviderComment
            if (!commentTags.isNullOrEmpty() && mSelectTypes == -1) {
                commentTags[0].commentNum = it
                mBinding.lblProviderComment.updateFisrtLable("全部(${it})")
            }
        })
        mModel.commentBeans.observe(this, Observer {
            dissMissLoadingDialog()
            pageDealed(it)
        })
        mModel.mError.observe(this, Observer {
//            mBinding.swpRefreshProviderComments.isRefreshing = false
            mBinding.swpRefreshProviderComments.finishRefresh()
            dissMissLoadingDialog()
        })

    }

    private fun bindCommentTags2(datas: MutableList<CommtentTagBean>?) {
        commentTags.clear()
        if (!datas.isNullOrEmpty()) {
            var tags: MutableList<String> = mutableListOf()
            var countNum: Int = 0
            for (tag in datas) {
                tags.add("${tag.name}(${tag.commentNum})")
                countNum = countNum + tag.commentNum
            }
            tags.add(0, "全部(${countNum})")
            mBinding.lblProviderComment.setLabels(tags)
            mBinding.lblProviderComment.setSelects(0)
            mBinding.lblProviderComment.visibility = View.VISIBLE
            commentTags.addAll(datas)
        } else {
            mModel.getCommentLs()
            mBinding.lblProviderComment.visibility = View.GONE
        }
    }

    private fun initViewEvent() {
        mBinding.tvToPostComment.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                var path = if (type == ResourceType.CONTENT_TYPE_SCENERY || type == ResourceType.CONTENT_TYPE_RESTAURANT || type == ResourceType.CONTENT_TYPE_HOTEL
                    || type == ResourceType.CONTENT_TYPE_AGRITAINMENT || type == ResourceType.CONTENT_TYPE_ENTERTRAINMENT
                    || type == ResourceType.CONTENT_TYPE_RESEARCH_BASE   || type == ResourceType.CONTENT_TYPE_SPECIALTY) {
                    ARouterPath.Provider.PROVIDER_POST_COMMENT
                } else {
                    ARouterPath.Provider.PROVIDER_ORDER_COM_ACTIVITY
                }
                ARouter.getInstance().build(path)
                    .withString("id", id)
                    .withString("type", type)
                    .withString("contentTitle", contentTitle)
                    .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }


        }
        mBinding.vProviderGoodComment.onNoDoubleClick {
            if (mSelectTypes != 0) {
                initTagTextStyle()
                mBinding.vLineProviderGoodComment.visibility = View.VISIBLE
                mBinding.txtGoodComment.setTextColor(resources.getColor(R.color.color_333))
                mBinding.txtGoodCommentValue.setTextColor(resources.getColor(R.color.color_333))
                mBinding.txtGoodComment.paint.isFakeBoldText = true
                mSelectTypes = 0
                bindTagsData()
            }
        }
        mBinding.vProviderBadComment.onNoDoubleClick {
            if (mSelectTypes != 1) {
                initTagTextStyle()
                mBinding.vLineProviderBadComment.visibility = View.VISIBLE
                mBinding.txtBadComment.setTextColor(resources.getColor(R.color.color_333))
                mBinding.txtBadCommentValue.setTextColor(resources.getColor(R.color.color_333))
                mBinding.txtCommonlyCommentValue.setTextColor(resources.getColor(R.color.color_333))
                mBinding.txtBadComment.paint.isFakeBoldText = true
                mSelectTypes = 1
                bindTagsData()
            }
        }
        mBinding.vProviderCommonlyeComment.onNoDoubleClick {
            if (mSelectTypes != 2) {
                initTagTextStyle()
                mBinding.vLineProviderCommonlyComment.visibility = View.VISIBLE
                mBinding.txtComonlyComment.setTextColor(resources.getColor(R.color.color_333))
                mBinding.txtComonlyComment.paint.isFakeBoldText = true
                mSelectTypes = 2
                bindTagsData()
            }
        }
        mBinding.lblProviderComment.setOnLabelSelectChangeListener(object : LabelsView.OnLabelSelectChangeListener {
            override fun onLabelSelectChange(label: TextView?, data: Any?, isSelect: Boolean, position: Int) {
                if (isSelect) {
                    mSelectTagPos = position
                    label?.setTextColor(resources.getColor(R.color.color_36cd64))
                    label?.setBackgroundResource(R.drawable.shape_provider_comment_tag_selected)
                    try {
                        mModel.pageIndex = 1
                        if (mSelectTypes != -1) {
                            mModel.commentLevel = mSelectTypes.toString()
                            if (position != 0) {
                                val item = commentTagsMap[mSelectTypes.toString()]?.tagList?.get((position - 1))
                                mModel.commentTagId = item?.id.toString()
                            } else {
                                // 全部
                                mModel.commentTagId = ""
                            }
                        } else {
                            if (position == 0) {
                                // 全部
                                mModel.commentTagId = ""
                            } else {
                                var curPos = position - 1
                                if (curPos in commentTags.indices) {
                                    mModel.commentTagId = commentTags[curPos].id.toString()
                                }
                            }

                        }
                        mModel.getCommentLs()
                    } catch (e: Exception) {
                    }
                } else {
                    label?.setTextColor(resources.getColor(R.color.color_333))
                    label?.setBackgroundResource(R.drawable.shape_provider_comment_tag_normal)
                }
            }
        })
        mBinding.lblProviderComment.setmOnLabelShowMoreListener(object : LabelsView.OnLabelShowMoreListener {
            override fun onLabeShowLoadMore() {
                mBinding.vProviderCommentLabesMore.visibility = View.VISIBLE
            }
        })
        mBinding.vProviderCommentLabesMore.onNoDoubleClick {
            // 显示更多标签
            if (mBinding.lblProviderComment.maxLines == -1) {
                mBinding.lblProviderComment.maxLines = 2
                mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_down)
            } else {
                mBinding.lblProviderComment.maxLines = -1
                mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_up)
            }
        }
        mBinding.swpRefreshProviderComments.setOnRefreshListener {
//            mBinding.swpRefreshProviderComments.isRefreshing = true
            mModel.pageIndex = 1
            mModel.getCommentLs()
        }
        providerCommentAdapter?.setOnLoadMoreListener {
            mModel.pageIndex++
            mModel.getCommentLs()
        }
//        mBinding?.swpRefreshProviderComments.setProgressViewOffset(true, -20, 100);
        mBinding?.swpRefreshProviderComments.isNestedScrollingEnabled = true
        mBinding?.appbarProviderComments.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
                if (p1 >= 0) {
                    mBinding?.swpRefreshProviderComments.setEnabled(true);
                } else {
                    mBinding?.swpRefreshProviderComments.setEnabled(false);
                }
            }

        })


        mBinding?.recyComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var topRowVerticalPosition: Int = 0
                if (recyclerView == null || recyclerView.childCount == 0) {
                    topRowVerticalPosition = 0;
                } else {
                    topRowVerticalPosition = recyclerView.getChildAt(0).getTop()
                }
                mBinding.swpRefreshProviderComments.isEnabled = topRowVerticalPosition >= 0;
            }
        });
    }

    /**
     * 改变tags
     */
    private fun bindTagsData() {
        mSelectTagPos = 0
        if (!commentTagsMap.isNullOrEmpty()) {
            val item = commentTagsMap[mSelectTypes.toString()]
            if (item != null) {
                mBinding.vProviderCommentLabesMore.visibility = View.GONE
                var tags: MutableList<String> = mutableListOf()
                if (item.tagList.isNullOrEmpty()) {
//                    tags.add(0, "全部(${item.commentNum})")
                    mBinding.lblProviderComment.visibility = View.GONE
                    mModel.pageIndex = 1
                    if (mSelectTypes != -1) {
                        mModel.commentLevel = mSelectTypes.toString()
                        mModel.commentTagId = ""
                    }
                    mModel.getCommentLs()
                } else {
                    mBinding.lblProviderComment.visibility = View.VISIBLE
                    tags.add(0, "全部(${item.commentNum})")
                    for (tag in item.tagList) {
                        tags.add("${tag.name}(${tag.commentNum})")
                    }
                }
                mBinding.lblProviderComment.setLabels(tags)
                mBinding.lblProviderComment.setSelects(mSelectTagPos)
                // 重置最大行数为2
                mBinding.lblProviderComment.maxLines = 2
                mBinding.imgProviderCommentLabesDown.setBackgroundResource(R.mipmap.provider_arrow_down)
            }
        }
    }

    /**
     * 处理评论列表分页
     */
    private fun
            pageDealed(it: MutableList<CommentBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.recyComments.isVisible) {
            mBinding.recyComments.visibility = View.VISIBLE
        }
//        mBinding.swpRefreshProviderComments.isRefreshing = false
        mBinding.swpRefreshProviderComments.finishRefresh()
        if (mModel.pageIndex == 1) {
            mBinding.appbarProviderComments.setExpanded(true)
//                mBinding.recyComments.smoothScrollToPosition(0)
            providerCommentAdapter!!.clear()
        }
        if (!it.isNullOrEmpty()) {
            providerCommentAdapter!!.add(it!!)
        }
        if (it.isNullOrEmpty() || it.size < mModel.pageSize) {
            providerCommentAdapter?.loadEndRefresh()
        } else {
            providerCommentAdapter?.loadComplete()
        }

    }

    /**
     * 绑定评论数据
     * @param it 返回数据实体
     *
     */
    private fun bindCommentTags(it: HashMap<String, CommentTagsBean>) {
        if (!it.isNullOrEmpty()) {
            commentTagsMap = it
            if (it["0"] != null) {
                val item = it["0"]
                // 好评
                mBinding.txtGoodCommentValue.text = "(${item!!.commentNum})"
                // 默认显示好评tags
                if (item.tagList.isNullOrEmpty()) {
                    mBinding.lblProviderComment.visibility = View.GONE
                    mModel.pageIndex = 1
                    mModel.commentLevel = item.commentType.toString()
                    mModel.getCommentLs()
                } else {
                    var tags: MutableList<String> = mutableListOf()
                    tags.add(0, "全部(${item.commentNum})")
                    for (tag in item.tagList) {
                        tags.add("${tag.name}(${tag.commentNum})")
                    }
                    mBinding.lblProviderComment.setLabels(tags)
                    mBinding.lblProviderComment.setSelects(0)
                }
            }
            if (it["1"] != null) {
                val item = it["1"]
                // 差评
                mBinding.txtBadCommentValue.text = "(${item!!.commentNum})"
            }
            if (it["2"] != null) {
                val item = it["2"]
                // 一般
                mBinding.txtCommonlyCommentValue.text = "(${item!!.commentNum})"
            }
        } else {
            mModel.pageIndex = 1
            mModel.getCommentLs()
        }
    }

    private fun initTagTextStyle() {
        mBinding.vLineProviderBadComment.visibility = View.GONE
        mBinding.vLineProviderGoodComment.visibility = View.GONE
        mBinding.vLineProviderCommonlyComment.visibility = View.GONE
        mBinding.txtGoodComment.setTextColor(resources.getColor(R.color.color_666))
        mBinding.txtBadComment.setTextColor(resources.getColor(R.color.color_666))
        mBinding.txtComonlyComment.setTextColor(resources.getColor(R.color.color_666))
        mBinding.txtGoodCommentValue.setTextColor(resources.getColor(R.color.color_666))
        mBinding.txtBadCommentValue.setTextColor(resources.getColor(R.color.color_666))
        mBinding.txtCommonlyCommentValue.setTextColor(resources.getColor(R.color.color_666))
        mBinding.txtGoodComment.paint.isFakeBoldText = false
        mBinding.txtBadComment.paint.isFakeBoldText = false
        mBinding.txtComonlyComment.paint.isFakeBoldText = false
    }

    override fun initData() {
//        showLoadingDialog()
        mModel.resourceType = type
        mModel.resourceId = id
        mModel.getCommNumber(id,type)
        //ToastUtils.showMessage(""+type)
        if (type == ResourceType.CONTENT_TYPE_SCENERY || type == ResourceType.CONTENT_TYPE_RESTAURANT
            || type == ResourceType.CONTENT_TYPE_HOTEL || type == ResourceType.CONTENT_TYPE_HOTEL|| type == ResourceType.CONTENT_TYPE_AGRITAINMENT
            || type == ResourceType.CONTENT_TYPE_ENTERTRAINMENT || type == ResourceType.CONTENT_TYPE_RESEARCH_BASE|| type == ResourceType.CONTENT_TYPE_SPECIALTY) {
            mBinding.vProviderCommentTypes.visibility = View.VISIBLE
            mBinding.vProviderActivityLine.visibility = View.VISIBLE
            mSelectTypes = 0
            mModel.getCommentTagCountInfo(id, type)
        } else {
            mBinding.vProviderCommentTypes.visibility = View.GONE
            mBinding.vProviderActivityLine.visibility = View.GONE
            mSelectTypes = -1
            mModel.getCommentTagCountInfo2(id, type)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        mBinding.vProviderGoodComment?.performClick()
        initData()
    }
}
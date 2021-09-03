package com.daqsoft.travelCultureModule.branches

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setImageUrl
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemBrandPalyfunZixunBinding
import com.daqsoft.mainmodule.databinding.MainActivityBranchDetailBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.BranchDetailBean
import com.daqsoft.provider.bean.BrandSiteInfo
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.HomeBranchBean
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.branches.adapter.gvBaseAdapter
import com.daqsoft.travelCultureModule.branches.adapter.gvMDDBaseAdapter
import com.daqsoft.travelCultureModule.branches.adapter.gvMoreBrandBaseAdapter
import com.daqsoft.travelCultureModule.branches.view.ExpandableTextView
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubZixunBean
import com.daqsoft.travelCultureModule.net.MainRepository
import java.util.*

/**
 * @Description 文化旅游品牌详情页面
 * @ClassName   BranchDetailActivity
 * @Author      PuHua
 * @Time        2019/12/23 17:28
 */
@Route(path = MainARouterPath.MAIN_BRANCH_DETAIL)
class BranchDetailActivity :
    TitleBarActivity<MainActivityBranchDetailBinding, BranchDetailActivityViewModel>(),
    ExpandableTextView.OnExpandListener {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var siteId: String = ""
    var next_title: String = ""
    lateinit var cur_bean: BranchDetailBean
    override fun getLayout(): Int = R.layout.main_activity_branch_detail

    override fun setTitle(): String = getString(R.string.main_branch)

    override fun injectVm(): Class<BranchDetailActivityViewModel> =
        BranchDetailActivityViewModel::class.java
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null


    var adapter_palyfun = object : RecyclerViewAdapter<ItemBrandPalyfunZixunBinding, ClubZixunBean>(
        R.layout.item_brand_palyfun_zixun
    ) {
        override fun setVariable(
            mBinding: ItemBrandPalyfunZixunBinding,
            position: Int,
            item: ClubZixunBean
        ) {
            mBinding.ivIbPlayName.text = item.title
            if (!item.imageUrls.isNullOrEmpty()) {
                setImageUrlqwx(
                    mBinding.ivIbPlayLogo, item.imageUrls[0].url,
                    AppCompatResources.getDrawable(
                        BaseApplication.context,
                        R.drawable.placeholder_img_fail_h300
                    ), 5
                )
            } else {
                setImageUrlqwx(
                    mBinding.ivIbPlayLogo, "",
                    AppCompatResources.getDrawable(
                        BaseApplication.context,
                        R.drawable.placeholder_img_fail_h300
                    ), 5
                )
            }
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT_INFO)
                    .withString("id", item.id.toString())
                    .withString("contentTitle", "攻略详情")
                    .navigation()
            }
        }
    }

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                cur_bean?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@BranchDetailActivity)
                    }
                    var content= Constant.SHARE_DEC
                    sharePopWindow?.setShareContent(
                        it.name, content, it.brandImage.getRealImages(),
                        ShareModel.getPinPaiDetailUrl(id)
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }
                }
            }

        })
    }
    override fun initView() {
        // 一下为替换三个fragment
//        var homeBannerAndHappyFragment = HomeBannerAndHappyFragment()
//        var tripBranchNextFragment = TripBranchNextFragment()
//
//        val activityTopicStoryFragment = ActivityTopicStoryFragment()
//        transactFragment(R.id.trip_next, tripBranchNextFragment)
//        transactFragment(R.id.activity_story, activityTopicStoryFragment)

        mModel.branchDetailBean.observe(this, Observer {

            cur_bean = it
            mBinding.clBrand.visibility = View.VISIBLE
            mBinding.tvBiName.text = it.name
            next_title = it.name
            mBinding.tvBiSign.text = it.slogan


            mBinding.scenicCount = it.relationResourceCount ?: "0"
            mBinding.siteCount = it.siteCount
            if ((it.relationResourceCount.isNullOrEmpty() || it.relationResourceCount == "0") && (it.siteCount.isNullOrEmpty() || it.siteCount == "0")) {
                mBinding.llBarandBoom.visibility = View.GONE
            } else {
                mBinding.llBarandBoom.visibility = View.VISIBLE
            }
            mBinding.tvBiCollect.text = it.collectCount
            mBinding.tvBiLike.text = it.thumbCount
            mBinding.like = it.thumbStatus
            checkBoomlike(it.thumbStatus)
            checkBoomColect(it.collectStatus.toBoolean())
            var data_color = it.mainColor.split(",")
            if (data_color.size == 3) {
                var color =
                    Color.rgb(data_color[0].toInt(), data_color[1].toInt(), data_color[2].toInt())
                val colors = intArrayOf(color, -0x00) //分别为开始颜色，中间夜色，结束颜色
                val gd =
                    GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors) //创建drawable
                // gd.alpha=240
                mBinding.llBrandHead.background = gd
                mBinding.llBarandBoom.setBackgroundColor(color)
                mBinding.scrollView.setBackgroundColor(color)
            }
            setImageUrl(mBinding.ivCityImage, it.brandImage, null)
            var content = Html.fromHtml(HtmlFomat.delHTMLTag(it.suggest))
            mBinding.etBrandContent.text = if (!content.isNullOrEmpty()) {
                content.toString().replace("video", "")
            } else {
                ""
            }
            mBinding.etBrandContent.setExpandListener(this)
        })
        //景区玩乐
        mBinding.llJqwl.gvScenicBrand.numColumns = 2
        mModel.branchScenicList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    var adapter = gvBaseAdapter(it, "0")
                    mBinding.llJqwl.gvScenicBrand.adapter = adapter
                } else {
                    mBinding.llJqwl.llBrandJqwlContent.visibility = View.GONE
                }
            } else {
                mBinding.llJqwl.llBrandJqwlContent.visibility = View.GONE
            }
        })
        mModel.scenicTotleNum.observe(this, Observer {
            mBinding.llJqwl.secnicnum = it
            if (!it.isNullOrEmpty()) {
                try {
                    var num = it.toInt()
                    if (num > 4) {
                        val drawable = getDrawable(R.mipmap.brand_card_more)
                        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                        mBinding.llJqwl.tvBrandAllnum.setCompoundDrawables(
                            null,
                            drawable,
                            null,
                            null
                        )
                        mBinding.llJqwl.tvBrandAllnum.isClickable = true
                    } else {
                        mBinding.llJqwl.tvBrandAllnum.isClickable = false
                    }
                } catch (e: Exception) {
                }
            }
        })
        //游玩攻略
        mModel.zixunList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    val tagLayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    mBinding.llYwgl.rvPlayfun.layoutManager = tagLayoutManager
                    mBinding.llYwgl.rvPlayfun.adapter = adapter_palyfun
                    adapter_palyfun.add(it!!)
                } else {
                    mBinding.llYwgl.llBrandPalyfunContent.visibility = View.GONE
                }
            } else {
                mBinding.llYwgl.llBrandPalyfunContent.visibility = View.GONE
            }
        })
        mModel.playfunTotleNum.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llYwgl.playfunnum = it
                try {
                    var num = it.toInt()
                    if (num > 4) {
                        val drawable = getDrawable(R.mipmap.brand_card_more)
                        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                        mBinding.llYwgl.tvBrandAllnum.setCompoundDrawables(
                            null,
                            drawable,
                            null,
                            null
                        )
                        mBinding.llYwgl.tvBrandAllnum.isClickable = true
                    } else {
                        mBinding.llYwgl.tvBrandAllnum.isClickable = false
                    }
                } catch (e: Exception) {
                }
            }
        })
        //目的地
        mBinding.llMdd.gvBrand.numColumns = 3
        mModel.mddList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    var adapter = gvMDDBaseAdapter(it)
                    mBinding.llMdd.gvBrand.adapter = adapter
                } else {
                    mBinding.llMdd.llBrandMddContent.visibility = View.GONE
                }
            } else {
                mBinding.llMdd.llBrandMddContent.visibility = View.GONE
            }
        })
        mModel.mddTotleNum.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.llMdd.mddnum = it
                try {
                    var num = it.toInt()
                    if (num > 6) {
                        val drawable = getDrawable(R.mipmap.brand_card_more)
                        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                        mBinding.llMdd.tvBrandAllnum.setCompoundDrawables(
                            null,
                            drawable,
                            null,
                            null
                        )
                        mBinding.llMdd.tvBrandAllnum.isClickable = true
                    } else {
                        mBinding.llMdd.tvBrandAllnum.isClickable = false
                    }
                } catch (e: Exception) {
                }
            }
        })
        //更多品牌
        mBinding.llMoreBrand.gvBrand.numColumns = 3
        mModel.homeBranchBeanList.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    var adapter = gvMoreBrandBaseAdapter(it)
                    mBinding.llMoreBrand.gvBrand.adapter = adapter
                } else {
                    mBinding.llMoreBrand.llBrandMoreContent.visibility = View.GONE
                }
            } else {
                mBinding.llMoreBrand.llBrandMoreContent.visibility = View.GONE
            }
        })
        // 跳转点击
        mBinding.llJqwl.tvBrandAllnum.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_OTHER)
                .withString("id", id.toString())
                .withString("title", next_title)
                .withString("siteId",siteId)
                .withString("type", "2")
                .navigation()
        }
        mBinding.llMdd.tvBrandAllnum.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_OTHER)
                .withString("id", id.toString())
                .withString("siteId",siteId)
                .withString("title", next_title)
                .withString("type", "3")
                .navigation()
        }

        //游玩攻略跳转
        mBinding.llYwgl.tvBrandAllnum.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_STRATEGY_LIST)
                .withString("id", id)
                .navigation()
        }

        mBinding.tvBiLike.setOnClickListener {
            if (cur_bean != null && cur_bean.thumbStatus) {
                mModel.check_dislike(id, ResourceType.CONTENT_TYPE_BRAND)
                cur_bean.thumbStatus = false
                checkBoomlike(false)
                cur_bean.thumbCount = (cur_bean.thumbCount.toInt() - 1).toString()
                mBinding.tvBiLike.text = cur_bean.thumbCount
            } else if (cur_bean != null && !cur_bean.thumbStatus) {
                mModel.check_like(id, ResourceType.CONTENT_TYPE_BRAND)
                cur_bean.thumbStatus = true
                checkBoomlike(true)
                cur_bean.thumbCount = (cur_bean.thumbCount.toInt() + 1).toString();
                mBinding.tvBiLike.text = cur_bean.thumbCount
            }
        }
        mBinding.tvBiCollect.setOnClickListener {
            if (cur_bean != null && cur_bean!!.collectStatus.toBoolean()) {
                mModel.check_discolect(id, ResourceType.CONTENT_TYPE_BRAND)
                cur_bean.collectStatus = "false"
                checkBoomColect(false)
                cur_bean.collectCount = (cur_bean.collectCount.toInt() - 1).toString();
                mBinding.tvBiCollect.text = cur_bean.collectCount
            } else if (cur_bean != null && !cur_bean!!.collectStatus.toBoolean()) {
                mModel.check_colect(id, ResourceType.CONTENT_TYPE_BRAND)
                cur_bean.collectStatus = "true"
                checkBoomColect(true)
                cur_bean.collectCount = (cur_bean.collectCount.toInt() + 1).toString();
                mBinding.tvBiCollect.text = cur_bean.collectCount
            }
        }
    }

    override fun initData() {
        mModel.getBranchList(id, siteId)
        //      mModel.getBrandScenicList("306")
        mModel.getZixunList(id, ResourceType.CONTENT_TYPE_BRAND, "", "2", "1", "", "ywgl")
        mModel.getBrandScenicList(id, "6")
        mModel.getBrandMDD(id, "6")
        mModel.getBranchMoreList(id)
    }

    override fun onClick() {//字体展开回调
        ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_OTHER)
            .withString("id", id.toString())
            .withString("title", next_title)
            .withString("siteId",siteId)
            .withString("type", "1")
            .navigation()
    }

    fun checkBoomlike(like: Boolean) {
        if (like) {
            mBinding.tvBiLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.brand_card_like_selected), null, null
            )
        } else {
            mBinding.tvBiLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.brand_card_like_normal), null, null
            )
        }
    }

    fun checkBoomColect(colect: Boolean) {
        if (colect) {
            mBinding.tvBiCollect.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.brand_card_collect_selected), null, null
            )
        } else {
            mBinding.tvBiCollect.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.brand_card_collect_normal), null, null
            )
        }

    }

    override fun onShrink(view: ExpandableTextView?) {

    }

    override fun onExpand(view: ExpandableTextView?) {

    }

}

class BranchDetailActivityViewModel : BaseViewModel() {

    /**咨询or攻略总数量*/
    var total = 0

    /**
     * 品牌详情
     */
    var branchDetailBean = MutableLiveData<BranchDetailBean>()

    /**
     * 品牌展播列表
     */
    fun getBranchList(id: String, siteId: String?) {
        mPresenter.value?.loading = true
        var param: HashMap<String, String> = HashMap()
        param["id"] = id
        if (!siteId.isNullOrEmpty()) {
            param["siteId"] = siteId
        }

        MainRepository.service.getHomeBranchDetail(param)
            .excute(object : BaseObserver<BranchDetailBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<BranchDetailBean>) {
                    branchDetailBean.postValue(response.data)
                }
            })
    }

    /**
     * 获取景区玩乐列表
     */
    var branchScenicList = MutableLiveData<MutableList<BrandSiteInfo>>()
    var scenicTotleNum = MutableLiveData<String>()
    fun getBrandScenicList(ids: String, pagesize: String) {
        MainRepository.service.getBrandScenicList(ids, pagesize)
            .excute(object : BaseObserver<BrandSiteInfo>() {
                override fun onSuccess(response: BaseResponse<BrandSiteInfo>) {
                    branchScenicList.postValue(response.datas)
                    scenicTotleNum.postValue(response.page?.total.toString())
                }

            })
    }

    /**
     * 游玩攻略他妈的就是咨询
     */
    var zixunList = MutableLiveData<MutableList<ClubZixunBean>>()
    var playfunTotleNum = MutableLiveData<String>()
    fun getZixunList(
        linksResourceId: String, linksResourceType: String, orderType: String,
        pageSize: String, currPage: String, region: String, channelCode: String
    ) {
        MainRepository.service.getClubZixunList(
            linksResourceId, linksResourceType, orderType,
            pageSize, currPage, region, channelCode
        ).excute(object : BaseObserver<ClubZixunBean>() {
            override fun onSuccess(response: BaseResponse<ClubZixunBean>) {
                zixunList.postValue(response.datas)
                response.page?.let {
                    total = it.total
                    playfunTotleNum.postValue(it.total.toString())
                }
            }

            override fun onFailed(response: BaseResponse<ClubZixunBean>) {
                zixunList.postValue(null)
            }
        })
    }

    /**
     * 目的地
     */
    var mddList = MutableLiveData<MutableList<BrandSiteInfo>>()
    var mddTotleNum = MutableLiveData<String>()
    fun getBrandMDD(brandId: String, pagesize: String) {
        HomeRepository.service.getHomeBranchDetail(pagesize, brandId, "SITE")
            .excute(object : BaseObserver<BrandSiteInfo>() {
                override fun onSuccess(response: BaseResponse<BrandSiteInfo>) {
                    mddList.postValue(response.datas)
                    response.page?.let {
                        mddTotleNum.postValue(it.total.toString())
                    }
                }

            })
    }

    /**
     * 更多品牌
     */
    var homeBranchBeanList = MutableLiveData<MutableList<HomeBranchBean>>()

    fun getBranchMoreList(ids: String) {
        val param = HashMap<String, String>()
        // queryType  【选填】默认为1 1 首页/品牌名片 2 城市名片 3 随机推荐
        param["queryType"] = "3"
        // 活动类型id
        param["pageSize"] = "3"
        param["currPage"] = "1"
        param["excludeBrandId"] = ids
        MainRepository.service.getBranchList(param).excute(object : BaseObserver<HomeBranchBean>() {
            override fun onSuccess(response: BaseResponse<HomeBranchBean>) {
                homeBranchBeanList.postValue(response.datas)
            }
        })
    }

    //点赞
    var like = MutableLiveData<String>()

    fun check_like(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.postThumbUp(resourceId, resourceType)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    like.postValue(response.data.toString())
                }
            })
    }

    //取消点赞
    fun check_dislike(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.postThumbCancel(resourceId, resourceType).excute(object
            : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                Log.e("e", "erroe");
            }

        })
    }

    //收藏
    fun check_colect(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posClloection(resourceId, resourceType).excute(object
            : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                Log.e("e", "erroe");
            }

        })
    }

    //取消收藏
    fun check_discolect(resourceId: String, resourceType: String) {
        mPresenter.value?.loading = true
        CommentRepository.service.posCollectionCancel(resourceId, resourceType).excute(object
            : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                Log.e("e", "erroe");
            }

        })
    }
}
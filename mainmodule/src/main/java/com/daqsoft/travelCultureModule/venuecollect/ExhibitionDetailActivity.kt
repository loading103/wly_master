package com.daqsoft.travelCultureModule.venuecollect
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityShowDetailBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.VenueCollectDetailBean
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.venuecollect.viewmodel.ExhibitionLsViewModel
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 展览详情界面
 */
@Route(path = MainARouterPath.COLLECT_SHOW_DETAIL)
class ExhibitionDetailActivity : TitleBarActivity<ActivityShowDetailBinding, ExhibitionLsViewModel>() {

    @JvmField
    @Autowired
    var id: String = "0"

    @JvmField
    @Autowired
    var name: String = ""
    // 场所经度
    var foodLat: Double = 0.0

    // 场所纬度
    var foodLng: Double = 0.0

    var maxWebViewHeight: Int = 0
    /**
     * 文本内容高度
     */
    var webViewHeight = 0

    // 娱乐场所实体
    var detail: VenueCollectDetailBean? = null

    var imagesList:MutableList<String> = mutableListOf()

    var isHaveVide: Boolean = false

    var isHave720: Boolean = false

    private var   videoImageHolder:VideoImageHolder ?= null

    var sharePopWindow: SharePopWindow? = null

    override fun getLayout(): Int {
        return R.layout.activity_show_detail
    }

    override fun setTitle(): String {
        return "展览详情"
    }

    override fun injectVm(): Class<ExhibitionLsViewModel> {
        return ExhibitionLsViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        initViewEvent()
    }

    override fun initData() {
        mModel.getShowDetails(id)
        // 相关推荐
        mModel.getTjListDatas(id)

    }


    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            detail?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@ExhibitionDetailActivity)
                }
                var content= Constant.SHARE_DEC
                if(!TextUtils.isEmpty(it.briefing)){
                    content=it.briefing
                }
                sharePopWindow?.setShareContent(
                    it.name, content, it.images.getRealImages(),
                    ShareModel.getExhibitionDetail(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }

            }
        })
    }
    private fun initViewModel() {
        mModel.details.observe(this, Observer {
            if(it==null){
                setBindData(it)
                return@Observer
            }
            detail=it
            mBinding.datas=it

            // 所有展品
            mModel.getWwListDatas(it.exhibitionId)

            setBindData(it)
        })
        // 相关推荐
        mModel.tjdatas.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.proXgTj?.visibility = View.VISIBLE
                mBinding.proXgTj.setData(it,false)
            }else{
                mBinding.proXgTj?.visibility = View.GONE
            }
        })
        // 所有展品
        mModel.mWdatas.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.proAllCulture?.visibility = View.VISIBLE
                mBinding.proAllCulture.setData(it,false)
            }else{
                mBinding.proAllCulture?.visibility = View.GONE
            }
        })
    }

    private fun setBindData(it: VenueCollectDetailBean?) {

        // 展览介绍
        if(it?.introduce.isNullOrEmpty()){
            mBinding.ilFoodIntrouduce.hide=true
        }else{
            mBinding.webInfor.settings.defaultTextEncodingName = "utf-8"
            mBinding.webInfor.settings.javaScriptEnabled = true
            mBinding.webInfor.isScrollContainer = false
            mBinding.webInfor.isVerticalScrollBarEnabled = false
            mBinding.webInfor.isHorizontalScrollBarEnabled = false
            mBinding.webInfor.loadDataWithBaseURL(null, it?.introduce?.let { it1 -> StringUtil.getHtml(it1) }, "text/html", "utf-8", null)

            maxWebViewHeight = Utils.dip2px(this, 189f).toInt()
            mBinding.webInfor.setCallBack {
                webViewHeight = it
                if (it > maxWebViewHeight) {
                    val layoutParams = mBinding.webInfor.layoutParams as  ConstraintLayout.LayoutParams
                    layoutParams.width=  ConstraintLayout.LayoutParams.MATCH_PARENT
                    layoutParams.height=  maxWebViewHeight
                    mBinding.webInfor.layoutParams=layoutParams
                    mBinding.ivContentMore.visibility = View.VISIBLE
                } else if (it < maxWebViewHeight) {
                    val layoutParams = mBinding.webInfor.layoutParams as  ConstraintLayout.LayoutParams
                    layoutParams.width=  ConstraintLayout.LayoutParams.MATCH_PARENT
                    layoutParams.height=   ConstraintLayout.LayoutParams.WRAP_CONTENT
                    mBinding.webInfor.layoutParams=layoutParams
                }
            }

            mBinding.ivContentMore.setOnClickListener {
                if (mBinding.ivContentMore.isSelected) {
                    mBinding.ivContentMore.isSelected = false
                    mBinding.ivContentMore.setImageResource(R.mipmap.main_arrow_down)
                    if (webViewHeight >= Utils.dip2px(this, 189f).toInt()) {
                        val layoutParams = mBinding.webInfor.layoutParams as  ConstraintLayout.LayoutParams
                        layoutParams.width=  ConstraintLayout.LayoutParams.MATCH_PARENT
                        layoutParams.height=  maxWebViewHeight
                        mBinding.webInfor.layoutParams=layoutParams
                        webViewHeight = Utils.dip2px(this, 189f).toInt()
                    }
                } else {
                    mBinding.ivContentMore.isSelected = true
                    mBinding.ivContentMore.setImageResource(R.mipmap.main_arrow_up)
                    val layoutParams = mBinding.webInfor.layoutParams as  ConstraintLayout.LayoutParams
                    layoutParams.width=  ConstraintLayout.LayoutParams.MATCH_PARENT
                    layoutParams.height=  ConstraintLayout.LayoutParams.WRAP_CONTENT
                    mBinding.webInfor.layoutParams=layoutParams
                    webViewHeight += 100
                }

            }
        }

        // 顶部视频资源
        var dataVideoImages: MutableList<VideoImageBean> = mutableListOf()
        // 视频
        if (!it?.video.isNullOrEmpty()) {
            dataVideoImages.add(0, VideoImageBean().apply {
                type = 1
                videoUrl = it?.video
            })
            isHaveVide = true
            mBinding.txtFoodDetailVideo.visibility = View.VISIBLE

        }
        it?.images?.split(",")?.let { it1 -> imagesList.addAll(it1) }
        // 720
        if (!it?.panorama.isNullOrEmpty()) {
            dataVideoImages.add(VideoImageBean().apply {
                type = 2
                videoUrl = it?.panorama
                imageUrl = it?.panorama
                name = it?.name
            })
            isHave720 = true
            mBinding.txtFoodDetailPannaor.visibility = View.VISIBLE
        }
        // 图片
        if (!imagesList.isNullOrEmpty()) {
            for (item in imagesList) {
                dataVideoImages.add(VideoImageBean().apply {
                    type = 0
                    imageUrl = item
                })
            }
            mBinding.txtFoodDetailImagesRoot.visibility = View.VISIBLE
            mBinding.txtFoodDetailImages.text = "1/${imagesList.size}"
        }


        if (!dataVideoImages.isNullOrEmpty()) {
            mBinding.cbrFoodDetail.visibility = View.VISIBLE
            mBinding.cbrFoodDetail.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    videoImageHolder= VideoImageHolder(itemView!!, this@ExhibitionDetailActivity)
                    return videoImageHolder as VideoImageHolder
                }

                override fun getLayoutId(): Int {
                    return R.layout.layout_video_image
                }
            }, dataVideoImages).setCanLoop(dataVideoImages.size > 1).setPointViewVisible(false).setOnItemClickListener {
                when (dataVideoImages[it].type) {
                    0 -> {

                        var picPosition=0
                        if(!isHave720 && !isHaveVide ){
                            picPosition=it
                        }else if(isHave720 && isHaveVide ){
                            picPosition=it-2
                        }else{
                            picPosition=it-1
                        }
                        if(picPosition<0){
                            picPosition=0
                        }
                        val intent = Intent(this@ExhibitionDetailActivity, BigImgActivity::class.java)
                        intent.putExtra("position", picPosition)
                        intent.putStringArrayListExtra("imgList", ArrayList(imagesList))
                        startActivity(intent)
                    }
                    1 -> {
                    }
                    2 -> {

                    }
                }
            }
            mBinding.cbrFoodDetail.onPageChangeListener = object : OnPageChangeListener {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                }

                override fun onPageSelected(index: Int) {
                    var pos = index
                    if (dataVideoImages[index].type == 0) {
                        if (isHave720) {
                            pos -= 1
                        }
                        if (isHaveVide) {
                            pos -= 1
                        }
                        if (pos >= 0) {
                            mBinding.txtFoodDetailImages.text = "${pos + 1}/${imagesList?.size}"
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                }

            }
        } else {
            mBinding.cbrFoodDetail.visibility = View.GONE
        }

    }


    private fun initViewEvent() {
        // 点击画册
        mBinding.txtFoodDetailImagesRoot.onNoDoubleClick {
            var pos = 0
            if (isHaveVide) {
                pos += 1
            }
            if (isHave720) {
                pos += 1
            }
            try {
                mBinding.cbrFoodDetail.setCurrentItem(pos, true)
            } catch (e: Exception) {
            }

        }
        // 点击视频按钮
        mBinding.txtFoodDetailVideo.onNoDoubleClick {
            mBinding.cbrFoodDetail?.setCurrentItem(0, true)
            if (imagesList.isNotEmpty()) {
                Handler().postDelayed({   mBinding.txtFoodDetailImages.text = "1/${imagesList.size}"},300)
            }
        }
        // 点击720按钮
        mBinding.txtFoodDetailPannaor.onNoDoubleClick {
            if (isHaveVide) {
                mBinding.cbrFoodDetail.setCurrentItem(1, true)
            } else {
                mBinding.cbrFoodDetail.setCurrentItem(0, true)
            }
            if (imagesList.isNotEmpty()) {
                Handler().postDelayed({   mBinding.txtFoodDetailImages.text = "1/${imagesList.size}"},300)
            }
        }
    }

    /**
     * 播放视频，banner不能自动翻页
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAudioPlayer(event: UpdateAudioStateEvent) {
        // 1 准备播放 2 播放 3暂停/完成
        try {
            when (event.type) {
                1 -> {
                    mBinding?.cbrFoodDetail.stopTurning()
                }
                2 -> {
                    mBinding?.cbrFoodDetail.stopTurning()
                }
                3 -> {
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

            ToastUtils.showMessage(e.message)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
            mBinding?.cbrFoodDetail?.stopVideoPlayer()
            if(videoImageHolder!=null){
                (videoImageHolder as VideoImageHolder).release()
            }
        } catch (e: Exception) {
        }

    }
}
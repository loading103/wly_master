package com.daqsoft.travelCultureModule.venuecollect
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainCultureDetailBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.bean.CultureDetailBean
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.businessview.view.ListenerAudioView
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.venuecollect.adapter.TagCultureAdapter
import com.daqsoft.travelCultureModule.venuecollect.viewmodel.CultureLsViewModel
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

/**
 * 精品文物详情界面
 */
@Route(path = MainARouterPath.COLLECT_CULYURE_DETAIL)
class CulturalRelicDetailActivity : TitleBarActivity<MainCultureDetailBinding, CultureLsViewModel>() {

    @JvmField
    @Autowired
    var id: String = "0"

    // 娱乐场所实体
    var detail: CultureDetailBean? = null

    //标签
    val adapterTag2: TagCultureAdapter by lazy { TagCultureAdapter() }

    var sharePopWindow: SharePopWindow? = null
    override fun getLayout(): Int {
        return R.layout.main_culture_detail
    }

    override fun setTitle(): String {
        return "文物详情"
    }

    override fun injectVm(): Class<CultureLsViewModel> {
        return CultureLsViewModel::class.java
    }

    override fun initView() {
        initViewModel()
        initViewEvent()
    }
    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
            detail?.let {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@CulturalRelicDetailActivity)
                }
                var content= Constant.SHARE_DEC
                if(!TextUtils.isEmpty(it.briefing)){
                    content=it.briefing
                }
                sharePopWindow?.setShareContent(
                    it.name, content, it.images.getRealImages(),
                    ShareModel.getCultureDetail(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
            }
        })
    }
    override fun initData() {
        mModel.getShowDetails(id)
        // 相关推荐
        mModel.getTuiJListDatas(id)
    }

    private fun initViewModel() {

        mModel.details.observe(this, Observer {
            dissMissLoadingDialog()
            if(it==null){
                return@Observer
            }
            detail=it
            mBinding.data=it
            bindDetailData(it)

        })
        // 相关推荐
        mModel.listdatas.observe(this, Observer {
            mBinding.proXgTj.setData(it,false)
        })
    }

    private fun bindDetailData(it: CultureDetailBean) {
        //标签
        var tags= mutableListOf<String>()
        mBinding.rvTag1.adapter = adapterTag2
        tags.add(it.typeName)
        if(!it.years.isNullOrBlank()){
            tags.add(it.years)
        }
        adapterTag2.setNewData(tags)


        // 展览介绍
        if(!TextUtils.isEmpty(it.introduce)){
            mBinding.tvWeb.settings.defaultTextEncodingName = "utf-8"
            mBinding.tvWeb.settings.javaScriptEnabled = true
            mBinding.tvWeb.loadDataWithBaseURL(null, StringUtil.getHtml(it.introduce), "text/html", "utf-8", null)
        }
        // 设置bannner听解说
        var audios: MutableList<AudioInfo> = mutableListOf()
        if (it.audioInfo!=null && it.audioInfo.isNotEmpty()) {
            audios.addAll(it.audioInfo)
        }
        if (audios.isNotEmpty()) {
            mBinding.vDetailAudios.visibility = View.VISIBLE
            mBinding.vDetailAudios.setData(audios)
        } else {
            mBinding.vDetailAudios.visibility = View.GONE
        }
        setCultureHeadImg(it)
        // 720点击
        mBinding.llRoot.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", "文物详情")
                .withString("html", it.threeDimensionalUrl)
                .navigation()
        }
    }

    private fun initViewEvent() {
        mBinding.vDetailAudios?.setTitle("文物讲解")
//        mBinding.vDetailAudios?.onPlayerListener = lobject : ListenerAudioView.OnAudioPlayerListener {
//            override fun onStartPlayer() {
//                mBinding.cbannerStoryDetail.pauseVideoPlayer()
//            }
//        }

    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
            mBinding.vDetailAudios?.stopAudioPlayer()
            mBinding.vDetailAudios?.releaseAudioPlayer()
        } catch (e: java.lang.Exception) {

        }

    }

    private fun setCultureHeadImg(it: CultureDetailBean) {
        if (it.images.isNullOrEmpty() && it.video.isNullOrEmpty()) {
            mBinding.cbannerStoryDetail.visibility = View.GONE
            mBinding.vStoryDetailIndex.visibility = View.GONE
        } else {
            mBinding.vStoryDetailIndex.visibility = View.VISIBLE
            var total = 0
            val images = mutableListOf<String>()
            if(!TextUtils.isEmpty(it.images)){
                val split = it.images.split(",")
                images.addAll(split)
            }
            var imagesAndVideo = mutableListOf<VideoImageBean>()
            // 添加视频
            if (!it.video.isNullOrEmpty()) {
                total += 1
                val videodata = initVideoData(1, it.video)
                videodata.imageUrl=it.videoCover
                imagesAndVideo.add(videodata)
            }
            // 添加图片
            if (!images.isNullOrEmpty()) {
                total += images.size
                for (item in images) {
                    imagesAndVideo.add(initVideoData(0, item))
                }
            }
            mBinding.txtCurrentIndex.text = "1"
            mBinding.txtTotalSize.text = "/${total}"

            mBinding.cbannerStoryDetail.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return VideoImageHolder(itemView!!, this@CulturalRelicDetailActivity)
                }

                override fun getLayoutId(): Int {
                    return R.layout.layout_video_image
                }
            }, imagesAndVideo).setCanLoop(false).setPointViewVisible(false).setOnItemClickListener {

                val intent = Intent(this@CulturalRelicDetailActivity, BigImgActivity::class.java)
                intent.putExtra("position", it)
                intent.putStringArrayListExtra("imgList", ArrayList(images))
                startActivity(intent)
            }
            mBinding.cbannerStoryDetail?.onPageChangeListener = object : OnPageChangeListener {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                }

                override fun onPageSelected(index: Int) {
                    mBinding?.txtCurrentIndex.text = ((index + 1).toString())
                    mBinding?.txtTotalSize.text = "/${total}"
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                }

            }
        }
    }

    fun initVideoData(type: Int, path: String): VideoImageBean {
        var video: VideoImageBean = VideoImageBean()
        video.type = type
        if (type == 1) {
            video.videoUrl = path
        } else {
            video.imageUrl = path
        }
        return video
    }

}
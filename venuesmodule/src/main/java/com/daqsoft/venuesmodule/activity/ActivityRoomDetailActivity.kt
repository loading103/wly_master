package com.daqsoft.venuesmodule.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.DateUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ActivityRoomAudioBean
import com.daqsoft.provider.bean.ActivityRoomBean
import com.daqsoft.provider.bean.ActivityRoomDateBean
import com.daqsoft.provider.bean.ActivityRoomTimeBean
import com.daqsoft.provider.businessview.event.UpdateAudioStateEvent
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.convenientbanner.bean.VideoImageBean
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.holder.VideoImageHolder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ActivityActivityRoomDetailBinding
import com.daqsoft.venuesmodule.databinding.ItemActivityRoomOrderDateBinding
import com.daqsoft.venuesmodule.databinding.ItemActivityRoomOrderTimeBinding
import com.daqsoft.venuesmodule.utils.CommonUtils
import com.daqsoft.venuesmodule.utils.FileUtil
import me.nereo.multi_image_selector.BigImgActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 *@package:com.daqsoft.venuesmodule.activity
 *@date:2020/4/20 9:46
 *@author: caihj
 *@des:活动室详情
 **/
@Route(path = ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL)
class ActivityRoomDetailActivity :
    TitleBarActivity<ActivityActivityRoomDetailBinding, ActivityRoomDetailVM>() {

    @Autowired
    @JvmField
    var id = ""


    var orderDialog: BaseDialog? = null
    var isHaveVide: Boolean = false
    var isHave720: Boolean = false

    var imageSize: Int = 0

    /**
     *  音频播放器
     */
    private var mMediaPlayer: MediaPlayer = MediaPlayer()

    private var isPlaying = false


    override fun getLayout(): Int = R.layout.activity_activity_room_detail

    override fun setTitle(): String = getString(R.string.activity_room_title)

    override fun injectVm(): Class<ActivityRoomDetailVM> = ActivityRoomDetailVM::class.java

    @SuppressLint("SetTextI18n")
    override fun initView() {

        EventBus.getDefault().register(this)
        mMediaPlayer.setOnCompletionListener {
            // 播放完成
            mBinding.ivAudio.setImageResource(R.mipmap.venue_details_banner_pause)
        }
        mMediaPlayer.setOnPreparedListener {
            // 准备完成开始播放
//            mMediaPlayer.start()
            mBinding.cbrActivityRoomDetail.pauseVideoPlayer()
        }


        mBinding.llPlayerAudio.onNoDoubleClick {
            if (isPlaying) {
                mBinding.ivAudio.setImageResource(R.mipmap.venue_details_banner_pause)
                mMediaPlayer.pause()
            } else {
                mBinding.ivAudio.setImageResource(R.mipmap.venue_details_banner_play)
                mMediaPlayer.start()
            }

            isPlaying = !isPlaying
        }

        mBinding.rlActivityFaithauditStatus.onNoDoubleClick {
            // 跳转我的诚信
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_CREDIT_ACTIVITY)
                    .navigation()
            } else {
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mModel.activityRoomAudio.observe(this, Observer {

            if (!it.audio.isNullOrEmpty()) {
                mBinding.llAudio.visibility = View.GONE
                if (!it.audio.isNullOrEmpty()) {

                    val audioUrl = if (it.audio.contains(",")) {
                        it.audio.split(",")[0]
                    } else {
                        it.audio
                    }

                    mMediaPlayer.setDataSource(audioUrl)
                    mMediaPlayer.prepareAsync()

                    mBinding.llAudio.visibility = View.VISIBLE

                    val name = FileUtil.getFileNameNoExtension(it.audio)
                    mBinding.tvAudioName.text = name

                    var time = ""
                    if (!it.audioTime.isNullOrEmpty()) {
                        if (time.contains(":")) {
                            val timeList = it.audioTime.split(":")
                            time = "${timeList[0]}′${timeList[1]}″"
                        } else {
                            time = "${it.audioTime.replace(":", "′")}″"
                        }
                    }
                    mBinding.tvAudioDuration.text = time
                }
            }

        })

        mModel.activityRoomDetail.observe(this, Observer {
            if (it != null) {
                bindTopUi(it)
                mBinding.tvRoomArea.text =
                    String.format(getString(R.string.activity_room_area), it.area)
                mBinding.tvRoomPeople.text =
                    String.format(getString(R.string.activity_room_people, it.galleryful))
                mBinding.tvOpenStatus.visibility = View.GONE
                mBinding.tvHonesty.text = "诚信大于${it.faithAuditValue}分可免审核"
                if (it.openStatus) {
                    roomOrderAdapter.add(it.activityRoom as MutableList<ActivityRoomDateBean>)
                } else {
                    mBinding.tvActivityDetailsRoom.visibility = View.GONE
                    mBinding.tvActivityOrderInfo.visibility = View.GONE
                    mBinding.rvActivityRoomDate.visibility = View.GONE
                }
            }
        })
        initOrderDialog()
    }

    private fun bindTopUi(it: ActivityRoomBean) {
        var dataVideoImages: MutableList<VideoImageBean> = mutableListOf()
        //视频
        if (!it.video.isNullOrEmpty()) {
            dataVideoImages.add(0, VideoImageBean().apply {
                type = 1
                videoUrl = it.video
            })
            isHaveVide = true
            mBinding.vActivityRoomVideo.visibility = View.VISIBLE
        }
        var images = it.images.split(",")
        //720
        if (!it.panoramaUrl.isNullOrEmpty()) {
            dataVideoImages.add(VideoImageBean().apply {
                type = 2
                videoUrl = it.panoramaUrl
                imageUrl = it.panoramaCover
                name = it.name
            })
            isHave720 = true
            mBinding.vActivityRoom720.visibility = View.VISIBLE
        }
        //图片
        if (!images.isNullOrEmpty()) {
            for (item in images) {
                dataVideoImages.add(VideoImageBean().apply {
                    type = 0
                    imageUrl = item
                })
            }
            mBinding.vActivityRoomImages.visibility = View.VISIBLE
            mBinding.txtActivityRoomTopImgIndex.text = "1/${images.size}"
        }
        if (!dataVideoImages.isNullOrEmpty()) {
            mBinding.cbrActivityRoomDetail.visibility = View.VISIBLE
            mBinding.cbrActivityRoomDetail.setPages(object : CBViewHolderCreator {
                override fun createHolder(itemView: View?): Holder<*> {
                    return VideoImageHolder(itemView!!, this@ActivityRoomDetailActivity)
                }

                override fun getLayoutId(): Int {
                    return R.layout.layout_video_image
                }
            }, dataVideoImages).setCanLoop(dataVideoImages.size > 1).setPointViewVisible(false)
                .setOnItemClickListener {
                    when (dataVideoImages[it].type) {
                        0 -> {
                            // 图片点击
                            val intent =
                                Intent(this@ActivityRoomDetailActivity, BigImgActivity::class.java)
                            intent.putExtra("position", it)
                            intent.putStringArrayListExtra(
                                "imgList",
                                ArrayList(images)
                            )
                            startActivity(intent)
                        }
                        1 -> {
                        }
                        2 -> {
                            // 点击720

                        }
                    }
                }.startTurning(3000)
            mBinding.cbrActivityRoomDetail.onPageChangeListener = object : OnPageChangeListener {
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
                            mBinding.txtActivityRoomTopImgIndex.text = "${pos + 1}/${images?.size}"
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                }

            }
        } else {
            mBinding.cbrActivityRoomDetail.visibility = View.GONE
        }
    }

    override fun initData() {
        mBinding.model = mModel
        mModel.getAudioInfo(id)
        mModel.getActivityRoomDetail(id)
        var gridManger = GridLayoutManager(this@ActivityRoomDetailActivity, 4)
        mBinding.rvActivityRoomDate.layoutManager = gridManger
        mBinding.rvActivityRoomDate.adapter = roomOrderAdapter
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        mBinding.cbrActivityRoomDetail.startTurning(3000)
    }

    private fun startTopPageLoop() {
    }

    private fun initEvent() {
        mBinding.tvRoomDetailsPhone.onNoDoubleClick {
            mModel.activityRoomDetail.value?.phone?.let { SystemHelper.callPhone(this, it) }
        }
        mBinding.vActivityRoom720.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", mModel.activityRoomDetail.value?.name)
                .withString("html", mModel.activityRoomDetail.value?.panoramaUrl)
                .navigation()
        }
    }


    @SuppressLint("SetTextI18n")
    fun initOrderDialog() {
        orderDialog = BaseDialog(this)
        orderDialog!!.contentView(R.layout.activity_room_order_dialog)
            .layoutParams(
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            .gravity(Gravity.BOTTOM)
            .animType(BaseDialog.AnimInType.BOTTOM)
            .canceledOnTouchOutside(true)
        orderDialog!!.findViewById<TextView>(R.id.tv_order_btn).setOnClickListener {
            if (currentTime != null) {
                var currentDate =
                    "${currentItem?.date}  ${CommonUtils.dayStrFormat(currentItem!!.week)}"
                val startTime = DateUtil.formatDateByString(
                    "HH:mm",
                    "yyyy-MM-dd HH:mm",
                    currentTime!!.startTime
                )
                val endTime =
                    DateUtil.formatDateByString("HH:mm", "yyyy-MM-dd HH:mm", currentTime!!.endTime)
                var currentTimeStr = "$startTime-$endTime"
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.ACTIVITY_ROOM_ORDER)
                    .withString("id", id)
                    .withString("name", mModel.activityRoomDetail.value?.name)
                    .withString("images", mModel.activityRoomDetail.value?.images)
                    .withString("labelName", mModel.activityRoomDetail.value?.labelName)
                    .withString(
                        "faithAuditStatus",
                        mModel.activityRoomDetail.value?.faithAuditStatus
                    )
                    .withString("address", mModel.activityRoomDetail.value?.address)
                    .withString("orderDate", currentDate)
                    .withString("orderTime", currentTimeStr)
                    .withString("roomOrderTimeId", currentTime!!.id)
                    .withString("venueName", mModel.activityRoomDetail.value?.venueName)
                    .navigation()
            } else {
                toast("请选择时间!")
            }
        }
    }

    fun updateOrderDialog() {
        timeAdapter.clear()
        currentItem?.let {
            val timeStr = DateUtil.formatDateByString("MM月dd日", "yyyy-MM-dd", currentItem!!.date)
            orderDialog!!.findViewById<TextView>(R.id.tv_order_title).text =
                "$timeStr ${CommonUtils.dayStrFormat(currentItem!!.week)}"
            var linearLayoutManager = LinearLayoutManager(this)
            val recyclerView = orderDialog!!.findViewById<RecyclerView>(R.id.rv_order_time)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = timeAdapter
            if (currentItem!!.list.size > 0) {
                for (item in currentItem!!.list) {
                    if (item.status == "0" && !DateUtil.isBeforeNow(item.endTime)) {
                        currentTime = item
                        break
                    }
                }

            }
            timeAdapter.add(currentItem!!.list)
            timeAdapter.notifyDataSetChanged()
        }
    }

    var currentItem: ActivityRoomDateBean? = null

    private var roomOrderAdapter = object :
        RecyclerViewAdapter<ItemActivityRoomOrderDateBinding, ActivityRoomDateBean>(R.layout.item_activity_room_order_date) {
        override fun setVariable(
            mBinding: ItemActivityRoomOrderDateBinding,
            position: Int,
            item: ActivityRoomDateBean
        ) {
            mBinding.tvRoomOrderDate.text =
                DateUtil.formatDateByString("MM-dd", "yyyy-MM-dd", item.date)
            mBinding.root.isSelected = item.select
            mBinding.root.onNoDoubleClick {
                if (currentItem != item) {
                    item.select = true
                    if (currentItem != null) {
                        currentItem!!.select = false
                    }
                    currentItem = item
                    notifyDataSetChanged()
                }
                updateOrderDialog()
                orderDialog?.show()
            }
            var status = ""
            // 是否可预约
            var order = true

            if (item.list.size == 0) {
                status = "不开放"
                order = false
            } else {
                for (day in item.list) {
                    if (day.status == "0" && !DateUtil.isBeforeNow(day.endTime)
                    ) {
                        order = true
                        status = ""
                        break
                    } else {
                        order = false
                        status = "不可订"
                        if (day.status == "1") {
                            status = "已订完"
                        }
                    }
                }
            }
            mBinding.root.isEnabled = order


            val dateToStamp = dateToStamp(item.date).plus(86400000.toLong())
            if(dateToStamp != 86400000.toLong() && (dateToStamp<System.currentTimeMillis() ) ){
                mBinding.ivRoomDateOrder.visibility = View.GONE
                mBinding.root.isEnabled = false
                mBinding.tvRoomOrderDay.text = "不可订"
            }else{
                if (!status.isNullOrEmpty()) {
                    mBinding.tvRoomOrderDay.text = status
                    mBinding.ivRoomDateOrder.visibility = View.GONE
                } else {
                    mBinding.tvRoomOrderDay.text = CommonUtils.dayStrFormat(item.week)
                }

            }
        }

    }

    fun dateToStamp(s: String): Long {
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date: Date = simpleDateFormat.parse(s)
            val ts: Long = date.getTime()
            return ts
        }catch (e :java.lang.Exception){
            return 0
        }
    }

    var currentTime: ActivityRoomTimeBean? = null

    private var timeAdapter = object :
        RecyclerViewAdapter<ItemActivityRoomOrderTimeBinding, ActivityRoomTimeBean>(R.layout.item_activity_room_order_time) {
        override fun setVariable(
            mBinding: ItemActivityRoomOrderTimeBinding,
            position: Int,
            item: ActivityRoomTimeBean
        ) {
            val startTime = DateUtil.formatDateByString("HH:mm", "yyyy-MM-dd HH:mm", item.startTime)
            val endTime = DateUtil.formatDateByString("HH:mm", "yyyy-MM-dd HH:mm", item.endTime)
            mBinding.tvOrderTime.text = "$startTime-$endTime"
            mBinding.tvOrderRemark.text = item.remarks
            if (item.status == "0" && !DateUtil.isBeforeNow(item.endTime)
            ) {
                mBinding.tvOrderTime.textColor = resources.getColor(R.color.txt_black)
                mBinding.tvOrderTime.isEnabled = true
                mBinding.root.onNoDoubleClick {
                    if (currentTime != item) {
                        currentTime = item
                        notifyDataSetChanged()
                    }
                }
            } else {
                mBinding.tvOrderTime.isEnabled = false
                mBinding.tvOrderTime.textColor = resources.getColor(R.color.txt_gray)
                mBinding.tvOrderRemark.text = "暂不可订"
            }
            if (currentTime != null && currentTime!!.id == item.id) {
                mBinding.tvOrderTime.isSelected = true
                mBinding.tvOrderTime.textColor = resources.getColor(R.color.colorPrimary)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mBinding.cbrActivityRoomDetail.stopTurning()
    }

    override fun onDestroy() {
        if (orderDialog != null) {
            orderDialog!!.dismiss()
            orderDialog = null
        }
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop()
                mMediaPlayer.release()
            }
            mBinding.cbrActivityRoomDetail.stopVideoPlayer()
        } catch (e: Exception) {
        }
    EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAudioPlayer(event: UpdateAudioStateEvent) {
        // 1 准备播放 2 播放 3暂停/完成
        try {
            when (event.type) {
                1 -> {
                    mBinding?.cbrActivityRoomDetail.stopTurning()
                }
                2 -> {
                    mBinding?.cbrActivityRoomDetail.stopTurning()
                }
                3 -> {
                    mBinding?.cbrActivityRoomDetail.startTurning(3000)
                }
            }
        } catch (e: java.lang.Exception) {

        }
    }
}

class ActivityRoomDetailVM : BaseViewModel() {

    /**
     * 活动室详情数据
     */
    var activityRoomDetail = MutableLiveData<ActivityRoomBean>()

    fun getActivityRoomDetail(id: String) {
        HomeRepository.service.getActivityRoomDetail(id)
            .excute(object : BaseObserver<ActivityRoomBean>() {
                override fun onSuccess(response: BaseResponse<ActivityRoomBean>) {
                    activityRoomDetail.postValue(response.data)
                }
            })
    }

    var activityRoomAudio = MutableLiveData<ActivityRoomAudioBean>()

    fun getAudioInfo(id: String) {
        HomeRepository.service.getActivityRoomVoice(id)
            .excute(object : BaseObserver<ActivityRoomAudioBean>() {
                override fun onSuccess(response: BaseResponse<ActivityRoomAudioBean>) {
                    activityRoomAudio.postValue(response.data)
                }
            })
    }
}
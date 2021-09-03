package com.daqsoft.usermodule.ui.complaint

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.HtmlUtil
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.MineComplaintListBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMineComplaintBinding
import com.daqsoft.usermodule.databinding.ItemMineComplaintBinding
import com.daqsoft.usermodule.databinding.ItemMineComplaintImgBinding
import me.nereo.multi_image_selector.BigImgActivity
import me.nereo.multi_image_selector.video.PlayerActivity
import java.util.*


/**
 * @Description 我的投诉
 * @ClassName   CollectionActivity
 * @Author      Huangxi
 * @Time        2020/2/28
 */
@Route(path = ARouterPath.UserModule.USER_COMPLAINT_ACTIVITY)
class MineComplaintActivity :
    TitleBarActivity<ActivityMineComplaintBinding, MineComplaintViewModel>(), View.OnClickListener {

    /**
     * 当前码
     */
    var currPage = 1
    /**
     * 状态
     */
    var status = ""

    override fun setTitle(): String = "我的投诉"

    override fun injectVm(): Class<MineComplaintViewModel> = MineComplaintViewModel::class.java

    override fun initView() {
        mBinding.tvComplaintAll.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        mBinding.tvComplaintAll.isSelected = true
        mBinding.tvComplaintAll.setOnClickListener(this)
        mBinding.tvComplaintDeal.setOnClickListener(this)
        mBinding.tvComplaintAccept.setOnClickListener(this)
        mBinding.tvComplaintNoHand.setOnClickListener(this)
        mBinding.ivComplaint.setOnClickListener(this)
//        complaintAdapter.setEmptyContent("您还没有投诉过任何企业！")
        mBinding.recyclerMineComplaint.apply {
            layoutManager = LinearLayoutManager(
                this@MineComplaintActivity, LinearLayoutManager
                    .VERTICAL, false
            )
            adapter = complaintAdapter
        }
        complaintAdapter.setOnLoadMoreListener {
            currPage++
            mModel.getMineComplaintList(status, currPage)
        }
        mBinding.refreshMineComplaint.setOnRefreshListener {
            currPage = 1
            mModel.getMineComplaintList(status, currPage)
        }
    }

    override fun initData() {
    }

    override fun notifyData() {
        super.notifyData()
        mModel.beans.observe(this, Observer {
            PageDealUtils().pageDeal(currPage, it, complaintAdapter)
            complaintAdapter.add(it?.datas!!)
            complaintAdapter.notifyDataSetChanged()
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_complaint_all -> {
                // 全部
                mBinding.tvComplaintAll.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                mBinding.tvComplaintDeal.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintAccept.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintNoHand.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintAll.isSelected = true
                mBinding.tvComplaintDeal.isSelected = false
                mBinding.tvComplaintAccept.isSelected = false
                mBinding.tvComplaintNoHand.isSelected = false
                status = ""
            }
            R.id.tv_complaint_deal -> {
                // 已处理
                mBinding.tvComplaintAll.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintDeal.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                mBinding.tvComplaintAccept.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintNoHand.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintAll.isSelected = false
                mBinding.tvComplaintDeal.isSelected = true
                mBinding.tvComplaintAccept.isSelected = false
                mBinding.tvComplaintNoHand.isSelected = false
                status = "6"
            }
            R.id.tv_complaint_accept -> {
                // 已受理
                mBinding.tvComplaintAll.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintDeal.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintAccept.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                mBinding.tvComplaintNoHand.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintAll.isSelected = false
                mBinding.tvComplaintDeal.isSelected = false
                mBinding.tvComplaintAccept.isSelected = true
                mBinding.tvComplaintNoHand.isSelected = false
                status = "5"
            }
            R.id.tv_complaint_no_hand -> {
                // 未处理
                mBinding.tvComplaintAll.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintDeal.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintAccept.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                mBinding.tvComplaintNoHand.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                mBinding.tvComplaintAll.isSelected = false
                mBinding.tvComplaintDeal.isSelected = false
                mBinding.tvComplaintAccept.isSelected = false
                mBinding.tvComplaintNoHand.isSelected = true
                status = "4"
            }
            R.id.iv_complaint -> {
                // 投诉
                ARouter.getInstance().build(MainARouterPath.MAIN_COMPLAINT_ACTIVITY).navigation()
            }
        }
        currPage = 1
        mModel.getMineComplaintList(status, currPage)

    }

    override fun getLayout(): Int = R.layout.activity_mine_complaint

    /**
     * 投诉的适配器
     */
    var complaintAdapter = object : RecyclerViewAdapter<ItemMineComplaintBinding, MineComplaintListBean>(
        R.layout.item_mine_complaint) {
        override fun setVariable(
            mBinding: ItemMineComplaintBinding,
            position: Int,
            item: MineComplaintListBean
        ) {
            mBinding.item = item
            mBinding.tvComplaintName.text = "${item.incidentTime}${HtmlUtils.html2Str(item.respondent)}"

            mBinding.root.setOnClickListener {
                ARouter.getInstance().build(ARouterPath.UserModule.USER_COMPLAINT_DETAILS_ACTIVITY)
                    .withString("id", item.id.toString())
                    .navigation()
            }
            var statusResult = when (item.status) {
                4 -> "未处理"
                5 -> "已受理"
                6 -> "已处理"
                else -> ""
            }
            if (statusResult.isNotEmpty()) {
                mBinding.tvComplaintStatus.visibility = View.VISIBLE
                mBinding.tvComplaintStatus.text = statusResult
                if (item.status == 4) {
                    mBinding.tvComplaintStatus.setBackgroundResource(R.drawable.shape_orange_3)
                } else {
                    mBinding.tvComplaintStatus.setBackgroundResource(R.drawable.shape_main_3)
                }
            } else {
                mBinding.tvComplaintContent.visibility = View.GONE
            }
            // 视频图片适配器
            var imgAdapter = object : RecyclerViewAdapter<ItemMineComplaintImgBinding, String>(
                R.layout.item_mine_complaint_img
            ) {
                override fun setVariable(
                    mBinding: ItemMineComplaintImgBinding,
                    position: Int,
                    data: String
                ) {
                    if (data.endsWith(".mp4")) {
                        mBinding.ivVideo.visibility = View.VISIBLE
                    } else {
                        mBinding.ivVideo.visibility = View.GONE
                    }
                    // 异步加载视频缩略图
                    Glide.with(this@MineComplaintActivity)
                        .setDefaultRequestOptions(
                            RequestOptions()
                                .frame(1000000)
                                .centerCrop()
                        )
                        .load(data)
                        .placeholder(R.drawable.placeholder_img_fail_h158)
                        .into(mBinding.itemImg)
                    mBinding.itemImg.setOnClickListener {
                        if (data.endsWith(".mp4")) {
                            val intent =
                                Intent(this@MineComplaintActivity, PlayerActivity::class.java)
                            intent.putExtra("title", "视频播放")
                            intent.putExtra("url", data)
                            startActivity(intent)
                        } else {
                            val intent =
                                Intent(this@MineComplaintActivity, BigImgActivity::class.java)
                            intent.putExtra("position", position)
                            intent.putStringArrayListExtra(
                                "imgList",
                                item.evidenceImages as ArrayList<String>?
                            )
                            startActivity(intent)
                        }
                    }
                }
            }
            mBinding.recyclerImg.apply {
                layoutManager = GridLayoutManager(this@MineComplaintActivity, 4)
                adapter = imgAdapter
            }
            var imgList = mutableListOf<String>()
            imgList.addAll(item.evidenceImages as MutableList<String>)
            imgList.addAll(item.evidenceVideo as MutableList<String>)
            if(imgList.size > 0){
                imgAdapter.add(imgList)
                imgAdapter.notifyDataSetChanged()
            }else{
                mBinding.recyclerImg.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mModel.getMineComplaintList(status, currPage)
    }


}

/**
 * @Description 我的投诉列表ViewModel
 * @ClassName   CollectionActivity
 * @Author      Huangxi
 * @Time        2020/2/28
 */
class MineComplaintViewModel : BaseViewModel() {
    /**
     * 我的投诉数据
     */
    var beans = MutableLiveData<BaseResponse<MineComplaintListBean>>()

    /**
     * 获取投诉
     */
    fun getMineComplaintList(status: String, currPage: Int) {
        mPresenter.value?.loading = true
        UserRepository().userService.getMineComplaintList(status, currPage)
            .excute(object : BaseObserver<MineComplaintListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MineComplaintListBean>) {
                    beans.postValue(response)
                }
            })
    }

}

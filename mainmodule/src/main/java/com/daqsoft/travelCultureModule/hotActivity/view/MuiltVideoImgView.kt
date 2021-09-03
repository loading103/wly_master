package com.daqsoft.travelCultureModule.hotActivity.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.ArcImageView
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.LayoutActivityResultBinding
import com.daqsoft.provider.bean.ImageVideoBean
import com.daqsoft.travelCultureModule.hotActivity.adapter.ActivityResultAdapter
import me.nereo.multi_image_selector.BigImgActivity
import me.nereo.multi_image_selector.video.PlayerActivity
import java.util.ArrayList

/**
 * @Description 活动视频和图片 多图
 * @ClassName   MuiltVideoImgView
 * @Author      luoyi
 * @Time        2020/6/12 10:44
 */
class MuiltVideoImgView : FrameLayout {


    private var mContext: Context? = null
    private var mBinding: LayoutActivityResultBinding? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_activity_result,
            this,
            false
        )
        addView(mBinding?.root)
    }

    fun setData(datas: MutableList<ImageVideoBean>) {
        var size = datas.size
        when (size) {
            1 -> {
                dealOneItem(datas)
            }
            2 -> {
                dealTwoItem(datas)
            }
            3 -> {
                dealThreeItem(datas)
            }
            else -> {
                dealDatas(datas)
            }
        }
    }

    private fun dealOneItem(datas: MutableList<ImageVideoBean>) {
        mBinding?.vActivityResultImgOne?.visibility = View.VISIBLE
        mBinding?.rvActResultMoreImages?.visibility = View.GONE
        mBinding?.vActivityResultImgTwo?.visibility = View.GONE
        mBinding?.vActivityResultImgThree?.visibility = View.GONE
        for (item in datas) {
            if (item != null) {
                if (item.type == 1) {
                    mBinding?.imgActTwoVideo?.visibility = View.VISIBLE
                    mBinding?.imgActResultOne?.let {
                        Glide.with(mContext!!)
                            .load(StringUtil.getVideoCoverUrl(item.video!!))
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .into(it)
                    }
                } else {
                    mBinding?.imgActTwoVideo?.visibility = View.GONE
                    mBinding?.imgActResultOne?.let {
                        Glide.with(mContext!!)
                            .load(item.image)
                            .placeholder(R.mipmap.placeholder_img_fail_240_180)
                            .into(it)
                    }
                }
                mBinding?.vActivityResultImgOne?.onNoDoubleClick {
                    dealViewClick(item)
                }
                break
            }
        }

    }


    private fun dealTwoItem(datas: MutableList<ImageVideoBean>) {
        mBinding?.vActivityResultImgOne?.visibility = View.GONE
        mBinding?.vActResultMoreImages?.visibility = View.GONE
        mBinding?.vActivityResultImgTwo?.visibility = View.VISIBLE
        mBinding?.vActivityResultImgThree?.visibility = View.GONE
        for (i in datas.indices) {
            var item = datas[i]
            if (i == 1) {
                dealImageOrVideo(item, mBinding?.imgActTwoFirst, mBinding?.imgActTwoVideo)
                mBinding?.imgActTwoFirst?.onNoDoubleClick {
                    dealViewClick(item)
                }
            } else {
                dealImageOrVideo(item, mBinding?.imgActTwoTwo, mBinding?.imgActTwoTwoVideo)
                mBinding?.imgActTwoTwo?.onNoDoubleClick {
                    dealViewClick(item)
                }
            }
        }
    }


    private fun dealThreeItem(datas: MutableList<ImageVideoBean>) {
        mBinding?.vActivityResultImgOne?.visibility = View.GONE
        mBinding?.vActResultMoreImages?.visibility = View.GONE
        mBinding?.vActivityResultImgTwo?.visibility = View.GONE
        mBinding?.vActivityResultImgThree?.visibility = View.VISIBLE
        for (i in datas.indices) {
            var item = datas[i]
            if (i == 1) {
                dealImageOrVideo(item, mBinding?.imgActThreeOne, mBinding?.imgActThreeVideoOne)
                mBinding?.imgActThreeOne?.onNoDoubleClick {
                    dealViewClick(item)
                }
            } else if (i == 2) {
                dealImageOrVideo(item, mBinding?.imgActThreeTwo, mBinding?.imgActThreeTwoVideo)
                mBinding?.imgActThreeTwo?.onNoDoubleClick {
                    dealViewClick(item)
                }
            } else {
                dealImageOrVideo(item, mBinding?.imgActThreeThree, mBinding?.imgActThreeThreeVideo)
                mBinding?.imgActThreeThree?.onNoDoubleClick {
                    dealViewClick(item)
                }
            }
        }
    }

    private fun dealDatas(datas: MutableList<ImageVideoBean>) {
        mBinding?.vActivityResultImgOne?.visibility = View.GONE
        mBinding?.vActResultMoreImages?.visibility = View.VISIBLE
        mBinding?.vActivityResultImgTwo?.visibility = View.GONE
        mBinding?.vActivityResultImgThree?.visibility = View.GONE
        var adapter = ActivityResultAdapter(mContext!!)
        adapter.emptyViewShow = false
        mBinding?.rvActResultMoreImages?.layoutManager = GridLayoutManager(
            mContext!!, 3,
            GridLayoutManager.VERTICAL, false
        )
        mBinding?.rvActResultMoreImages?.adapter = adapter
        adapter.showMore = datas.size <= 9
        if (adapter.showMore) {
            mBinding?.vMoreActImages?.visibility = View.GONE
        } else {
            mBinding?.vMoreActImages?.visibility = View.GONE
        }
        mBinding?.vMoreActImages?.onNoDoubleClick {
            mBinding?.vActResultMoreImages?.visibility = View.GONE
            adapter.showMore = true
            adapter.notifyDataSetChanged()
        }
        adapter.clear()
        adapter.add(datas)
    }


    private fun dealImageOrVideo(item: ImageVideoBean, imgActTwoFirst: ArcImageView?, imgActTwoVideo: ImageView?) {
        if (item.type == 1) {
            imgActTwoFirst?.let {
                Glide.with(mContext!!)
                    .load(StringUtil.getVideoCoverUrl(item.video!!))
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(it)
            }
            imgActTwoVideo?.visibility = View.VISIBLE
        } else {
            imgActTwoVideo?.let {
                Glide.with(mContext!!)
                    .load(item.image)
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(it)
            }
            imgActTwoVideo?.visibility = View.GONE
        }
        imgActTwoFirst?.onNoDoubleClick {
            dealViewClick(item)
        }
    }

    private fun dealViewClick(item: ImageVideoBean) {
        if (item.type == 1) {
            val intent =
                Intent(mContext, PlayerActivity::class.java)
            intent.putExtra("title", "视频播放")
            intent.putExtra("url", item.video)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext!!.startActivity(intent)
        } else {
            var list: ArrayList<String> = ArrayList()
            list.add(item.image!!)
            val intent =
                Intent(mContext!!, BigImgActivity::class.java)
            intent.putExtra("position", 0)
            intent.putStringArrayListExtra(
                "imgList", list
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext!!.startActivity(intent)
        }
    }
}
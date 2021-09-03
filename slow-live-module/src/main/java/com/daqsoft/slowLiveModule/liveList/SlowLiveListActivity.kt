package com.daqsoft.slowLiveModule.liveList

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.slowLiveModule.BlurBmpUtil
import com.daqsoft.slowLiveModule.R
import com.daqsoft.slowLiveModule.bean.LiveContentTypeBean
import com.daqsoft.slowLiveModule.bean.LiveTopBean
import com.daqsoft.slowLiveModule.bean.loadWithDefault
import com.daqsoft.slowLiveModule.databinding.SlowLiveAtyLiveListBinding
import com.daqsoft.slowLiveModule.databinding.SlowLiveItemContentBinding
import com.daqsoft.slowLiveModule.databinding.SlowLiveItemRvTopBinding
import com.daqsoft.slowLiveModule.net.logI
import com.daqsoft.slowLiveModule.rv.dsl.linear
import com.daqsoft.slowLiveModule.video.VideoActivity
import java.lang.StringBuilder


/**
 * @des     SlowLiveListActivity
 * @class   SlowLiveListActivity
 * @author  Wongxd
 * @date    2020-4-15  15:19
 *
 */
@Route(path = ARouterPath.SlowLiveModule.SLOW_LIVE_LIST_ACTIVITY)
internal class SlowLiveListActivity :
    TitleBarActivity<SlowLiveAtyLiveListBinding, LiveListViewModel>() {

    override fun getLayout(): Int = R.layout.slow_live_aty_live_list

    override fun injectVm(): Class<LiveListViewModel> = LiveListViewModel::class.java

    override fun setTitle(): String = getString(R.string.slow_live_live_list)


    private val mBlurBmpUtil by lazy { BlurBmpUtil() }

    private val mAdapter by lazy { LiveListAdapter() }

    override fun initView() {

        initViewModel()

        PagerSnapHelper().attachToRecyclerView(mBinding.rvTop)

        mAdapter.setOnLoadMoreListener {
            mModel.pageManager.nexPageIndex
            mModel.getLiveList()
        }


        mBinding.rv.apply {
            layoutManager = LinearLayoutManager(this@SlowLiveListActivity)
            adapter = mAdapter
        }

        mBinding.srl.setOnRefreshListener {
//            mBinding.srl.isRefreshing = false
            mModel.getLiveTop()
        }

    }

    private fun initViewModel() {

        mModel.liveList.observe(this, Observer {

            mBinding.rv.visibility = View.VISIBLE

            if (mModel.pageManager.isFirstIndex) {
                mAdapter.loadComplete()
                mAdapter.clearNotify()
            }

            if (it.isNullOrEmpty()) {
                mAdapter.loadEnd()
                if (mAdapter.itemCount > 1){
                    mBinding.rv.visibility = View.VISIBLE
                    mAdapter.notifyItemChanged(mAdapter.itemCount - 1)
                }
                else
                    mBinding.rv.visibility = View.GONE
            } else {
                mAdapter.add(it.toMutableList())
                mAdapter.notifyDataSetChanged()
                mAdapter.loadComplete()
            }

        })


        mModel.liveTop.observe(this, Observer {

            mBinding.srl.finishRefresh()
            if (it.isNullOrEmpty()) return@Observer

            mBinding.rvTop.visibility = View.VISIBLE

            renderRvTop(it)

        })


        mModel.liveContentTypeList.observe(this, Observer {

            if (it.subset.isNullOrEmpty()) return@Observer

            mBinding.rvContentType.visibility = View.VISIBLE

            renderRvContentType(it)
        })
    }

    private fun renderRvContentType(type: LiveContentTypeBean) {

        mBinding.rvContentType.linear {
            orientation = LinearLayoutManager.HORIZONTAL
            type.subset.forEach { typeItem ->

                itemDsl {
                    xml(R.layout.slow_live_item_content_type)
                    render {
                        val iv = it.findViewById<ImageView>(R.id.iv)
                        val tv = it.findViewById<TextView>(R.id.tv)
                        try {
                            tv.text = typeItem.name.substring(0,2)+"\n"+typeItem.name.substring(2,typeItem.name.length)
                        }catch (e :Exception){
                            tv.text = typeItem.name
                        }
                        Glide.with(iv).load(typeItem.backgroundImg).into(iv)

                        it.onNoDoubleClick {

                            ARouter.getInstance()
                                .build(ARouterPath.SlowLiveModule.SLOW_LIVE_CONTENT_ACTIVITY)
                                .withString("titleStr", typeItem.name)
                                .withString("channelCode", typeItem.channelCode)
                                .navigation()
                        }
                    }
                }
            }
        }
    }

    private fun renderRvTop(list: MutableList<LiveTopBean>) {

        mBinding.rvTop.let { rv ->
            rv.linear {
                orientation = LinearLayoutManager.HORIZONTAL

                list.forEach { top ->

                    itemDsl {

                        xml(R.layout.slow_live_item_rv_top)

                        render {
                            val binding = DataBindingUtil.bind<SlowLiveItemRvTopBinding>(it)
                            binding?.top = top
                            var name: StringBuilder = StringBuilder("")
                            if (!top?.scenicName.isNullOrEmpty()) {
                                name.append(top!!.scenicName)
                                name.append("-")
                            }

                            if(top.getType()=="1"){
                                binding?.ivLive?.visibility= View.VISIBLE
                                binding?.tvLive?.visibility= View.VISIBLE
                                binding?.tvLive?.text="Live"
                            }else  if(top.getType()=="2"){
                                binding?.ivLive?.visibility= View.GONE
                                binding?.tvLive?.visibility= View.VISIBLE
                                binding?.tvLive?.text="直播回放"
                            }else{
                                binding?.ivLive?.visibility= View.GONE
                                binding?.tvLive?.visibility= View.GONE
                            }
                            name.append("" + top?.scenicSpotsName)
                            binding?.tvSlowLiveName?.text = name

                            binding?.ivLive?.apply {
                                val drawable = background as AnimationDrawable
                                if (!drawable.isRunning)
                                    drawable.start()
                            }

                            try {
                                binding!!.ivTopLive.apply {
                                    if(top.getType()=="2" && !top.replayCover.isNullOrEmpty()){
                                        GlideModuleUtil.loadDqImage(top.replayCover,this)
                                    }else{
                                        GlideModuleUtil.loadDqImage(top.getImages(), this)
                                    }
                                }
                                if(top.getType()=="2" && !top.replayCover.isNullOrEmpty()) {
                                    mBlurBmpUtil.returnBlurBitmap(this@SlowLiveListActivity, top.replayCover) { bmp ->
                                        binding?.ivLiveListBlur?.setImageBitmap(bmp)
                                    }
                                }else if (!top.getImages().isNullOrEmpty())
                                    mBlurBmpUtil.returnBlurBitmap(this@SlowLiveListActivity, top.getImages()) { bmp ->
                                        binding?.ivLiveListBlur?.setImageBitmap(bmp)
                                    }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            it.onNoDoubleClick {
                                ARouter.getInstance()
                                    .build(ARouterPath.SlowLiveModule.SLOW_LIVE_DETAIL_ACTIVITY)
                                    .withInt("scenicSpotsId", top.scenicSpotsId)
                                    .withString("scenicSpotsName", top.scenicSpotsName)
                                    .navigation()

                            }
                        }
                    }

                }
            }
        }
    }


    override fun initData() {
        mModel.getLiveTop()
    }


}
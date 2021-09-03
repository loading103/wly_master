package com.daqsoft.provider.uiTemplate.titleBar.column

import android.content.Context
import android.graphics.Rect
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.SubChanelChildBean
import com.daqsoft.provider.bean.SubChannelBean
import com.daqsoft.provider.databinding.ScInformationStyleOneBinding
import com.daqsoft.provider.databinding.ScInformationStyleOneRecycleViewItemBinding
import com.daqsoft.thetravelcloudwithculture.home.bean.HomeContentBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.information
 * @date 12/10/2020 上午 10:06
 * @author zp
 * @describe 资讯样式1
 */

class SCColumnStyleOne(context: Context) : SCColumnStyleBase(context){

    private lateinit var recyclerViewAdapter : StyleOneRecyclerViewAdapter

    override fun initView() {
        val mBinding = DataBindingUtil.inflate<ScInformationStyleOneBinding>(
            LayoutInflater.from(context),
            R.layout.sc_information_style_one,
            this,
            false
        )
        bindDataToView(mBinding)
        this.addView(mBinding.root)
    }

    override fun columnDataChanged(data: MutableList<SubChanelChildBean>) {
        recyclerViewAdapter.clear()

        if (data.size > 4){
            recyclerViewAdapter.add(data.subList(0,4))
        }else{
            recyclerViewAdapter.add(data)
        }

        recyclerViewAdapter.notifyDataSetChanged()
        recyclerViewAdapter.endTimer()
        recyclerViewAdapter.startTimer()
    }


    /**
     * 绑定数据到视图
     * @param mBinding ScInformationStyleOneBinding
     */
    fun bindDataToView(viewBinding: ScInformationStyleOneBinding) {

        recyclerViewAdapter = StyleOneRecyclerViewAdapter()
        with(viewBinding.recycleView) {
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val itemCount = state.itemCount - 1
                    if (position != itemCount) {
                        outRect.bottom = 8.dp
                    }
                }
            })
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }

        recyclerViewAdapter.setOnItemChangeListener(object : ItemChangeListener {
            override fun onChange(item: SubChanelChildBean) {
                Timber.e("ItemChangeListener")
                // 图
                Glide.with(viewBinding.root.context)
                    .load(item.backgroundImg)
                    .placeholder(R.mipmap.placeholder_img_fail_240_180)
                    .into(viewBinding.bigImage)
                // 标题
                viewBinding.title.text = item.name
                viewBinding.bigImage.onNoDoubleClick {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_CONTENT)
                        .withString("channelCode", item.channelCode)
                        .withString("titleStr", item.name)
                        .navigation()
                }
            }
        })
    }


    inner class StyleOneRecyclerViewAdapter : RecyclerViewAdapter<ScInformationStyleOneRecycleViewItemBinding, SubChanelChildBean>(
        R.layout.sc_information_style_one_recycle_view_item
    ) {

        private var oldIndex = 0
        private var index = 0
        private var progressBarList = arrayListOf<Pair<ProgressBar, SubChanelChildBean>>()

        override fun setVariable(
            mBinding: ScInformationStyleOneRecycleViewItemBinding,
            position: Int,
            item: SubChanelChildBean
        ) {
            // item点击
            mBinding.root.onNoDoubleClick {
                ARouter.getInstance()
                    .build(MainARouterPath.MAIN_CONTENT)
                    .withString("channelCode", item.channelCode)
                    .withString("titleStr", item.name)
                    .navigation()
            }
            // 图
            Glide.with(mBinding.root.context)
                .load(item.backgroundImg)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.image)
            // 标题
            mBinding.title.text = item.name
            // 进度条
            progressBarList.add(Pair(mBinding.progressBar, item))
            if (progressBarList.size == getData().size){
                endTimer()
                startTimer()
            }
        }

        var disposable: Disposable? = null

        /**
         * 开始计时器
         */
        fun startTimer(initialDelay: Long = 0, period: Long = 5L) {
            if (progressBarList.isEmpty()){
                return
            }

            Observable.interval(initialDelay, period, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<Long> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: Long) {
                        progressBarList[oldIndex].first.progress = 0
                        progressBarList[oldIndex].first.visibility = View.GONE
                        progressBarList[index].first.visibility = View.VISIBLE
                        setProgressRate(progressBarList[index].first, period)
                        itemChangeListenerInterface?.onChange(progressBarList[index].second)
                        oldIndex = index
                        index++
                        if (index > progressBarList.size - 1) {
                            index = 0
                        }
                    }

                    override fun onError(e: Throwable) {
                        endTimer()
                    }

                    override fun onComplete() {
                    }

                })
        }

        /**
         * 结束计时器
         */
        fun endTimer() {
            disposable?.let {
                if (!it.isDisposed) {
                    it.dispose()
                }
            }
        }

        /**
         * 设置进度
         */
        private fun setProgressRate(progressBar: ProgressBar, period: Long){
            object : CountDownTimer(period * 1000, 1000){
                override fun onTick(millisUntilFinished: Long) {
                    progressBar.max = period.toInt()
                    progressBar.progress = period.toInt() - (millisUntilFinished/1000).toInt()
                }
                override fun onFinish() {
                }
            }.start()
        }

        private var itemChangeListenerInterface: ItemChangeListener? = null

        fun setOnItemChangeListener(listener: ItemChangeListener) {
            itemChangeListenerInterface = listener
        }
    }

    interface ItemChangeListener {
        fun onChange(item: SubChanelChildBean)
    }
}


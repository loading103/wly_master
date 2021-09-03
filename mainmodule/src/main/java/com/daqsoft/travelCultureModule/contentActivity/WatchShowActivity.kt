package com.daqsoft.travelCultureModule.contentActivity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.utils.Utils.getValueByName
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityWatchShowBinding
import com.daqsoft.mainmodule.databinding.ItemWatchShowBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.travelCultureModule.contentActivity.bean.ShowBean
import com.daqsoft.travelCultureModule.contentActivity.bean.WatchShowBean
import com.daqsoft.travelCultureModule.contentActivity.view.GalleryTransformer
import com.daqsoft.travelCultureModule.net.MainRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 *@package:com.daqsoft.travelCultureModule.contentActivity
 *@date:2020/4/21 17:58
 *@author: caihj
 *@des:看演出
 **/
@Route(path = MainARouterPath.WATCH_SHOW)
class WatchShowActivity : TitleBarActivity<ActivityWatchShowBinding, WatchShowViewModel>() {

    var citys = mutableListOf<ShowBean>()

    var watchShowAdapter: WatchShowAdapter? = null

    var index = 0
    var isContinue = true

    // 保存订阅者
    lateinit var subscribe: Disposable

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg != null) {
                when (msg.what) {
                    1 -> {
                        index++
                        mBinding.pagerShowCard.currentItem = index
                    }
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.activity_watch_show

    override fun setTitle(): String = getString(R.string.watch_show_title)

    override fun injectVm(): Class<WatchShowViewModel> = WatchShowViewModel::class.java

    override fun initView() {

        // 看演出
        mModel.watchShowList.observe(this, Observer {
            if(!it.subset.isNullOrEmpty()) {
                mBinding.tvShow.visibility= View.VISIBLE
                mBinding.pagerShowCard.visibility=View.VISIBLE
                mBinding.showName.visibility=View.VISIBLE
                mBinding.showName.text = it.subset[0].name
                mBinding.pagerShowCard.offscreenPageLimit = 3
                watchShowAdapter = WatchShowAdapter(it.subset as ArrayList<ShowBean>)
                var width = Utils.getWidthInPx(this)
                var offset =
                    resources.getDimension(R.dimen.dp_180) - (width - resources.getDimension(R.dimen.dp_175)) / 3 * 2
                mBinding.pagerShowCard.setPageTransformer(
                    false,
                    GalleryTransformer(
                        1,
                        offset
                    )
                )
                mBinding.pagerShowCard.adapter = watchShowAdapter
                citys = it.subset.toMutableList()
            }else{
                mBinding.tvShow.visibility= View.GONE
                mBinding.pagerShowCard.visibility=View.GONE
                mBinding.showName.visibility=View.GONE
            }
        })

        mBinding.pagerShowCard.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                index = position
                if (citys.size > 0) {
                    var currPos = position % citys.size
                    if (currPos < citys.size) {
                        var currentItem = citys[currPos]
                        if (currentItem != null) {
                            mBinding.showName.text = currentItem.name
                        }
                    }
                }
            }
        })

        // 艺术门类
        val gridLayoutManager = GridLayoutManager(this, 2)
        mBinding.recyArt.layoutManager = gridLayoutManager
        mBinding.recyArt.adapter = showAdapter
        mModel.artList.observe(this, Observer {
            if (it != null&&!it.subset.isNullOrEmpty()) {
                showAdapter.add(it.subset as MutableList<ShowBean>)
            }else{
                mBinding.tvArt.visibility=View.GONE
                mBinding.recyArt.visibility=View.GONE
            }
        })
    }

    override fun initData() {
        mModel.getWatchShows()
        mModel.getArt()
        StatisticsRepository.instance.statisticsPage(title, 1)
    }

    var showAdapter =
        object : RecyclerViewAdapter<ItemWatchShowBinding, ShowBean>(R.layout.item_watch_show) {
            override fun setVariable(
                mBinding: ItemWatchShowBinding,
                position: Int,
                item: ShowBean
            ) {
                mBinding.tvTitle.text = item.name
                mBinding.tvInfo.text = item.summary
                Glide.with(this@WatchShowActivity)
                    .load(item.backgroundImg)
                    .into(mBinding.avCover)
                mBinding.root.onNoDoubleClick {
                    ARouter.getInstance()
                        .build(ARouterPath.VenuesModule.VENUES_LIST_ACTIVITY)
                        .withString("name", item.name)
                        .withString("channelCode", item.channelCode)
                        .withString("tag", getValueByName(item.url, "tag"))
                        .withString("type", getValueByName(item.url, "type"))
                        .navigation()
                }
            }
        }

    override fun onResume() {
        super.onResume()
        subscribe = Observable.interval(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (isContinue) {
                    handler.sendEmptyMessage(1)
                }
            }
    }

    override fun onPause() {
        super.onPause()
        subscribe.dispose()//取消订阅
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        if (!subscribe.isDisposed) {
            subscribe.dispose()
        }
        super.onDestroy()
        StatisticsRepository.instance.statisticsPage(title, 2)
    }
}

class WatchShowViewModel : BaseViewModel() {

    var watchShowList = MutableLiveData<WatchShowBean>()

    var artList = MutableLiveData<WatchShowBean>()

    /**
     * 获取看演出
     */
    fun getWatchShows() {
        MainRepository.service.getWatchShow("kyc1").excute(object : BaseObserver<WatchShowBean>() {
            override fun onSuccess(response: BaseResponse<WatchShowBean>) {
                watchShowList.postValue(response.data)
            }
        })
    }

    /**
     * 获取艺术门类
     */
    fun getArt() {
        MainRepository.service.getWatchShow("ysml").excute(object : BaseObserver<WatchShowBean>() {
            override fun onSuccess(response: BaseResponse<WatchShowBean>) {
                artList.postValue(response.data)
            }
        })
    }
}
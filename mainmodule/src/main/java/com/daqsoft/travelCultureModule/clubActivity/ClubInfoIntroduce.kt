package com.daqsoft.travelCultureModule.clubActivity

import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.setImageUrl
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.BaseApplication.Companion.context
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityClubInfoIntroduceBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.network.venues.bean.AudioInfo
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.view.web.WebUtils
import com.daqsoft.travelCultureModule.clubActivity.adapter.ClubAudiosAdapter
import com.daqsoft.travelCultureModule.clubActivity.bean.AudioBean
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubInfoBean
import com.daqsoft.travelCultureModule.clubActivity.vm.ClubActicityInfoIntroduceViewModel
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_club_info_introduce.*
import kotlinx.android.synthetic.main.item_list_audio.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit
import androidx.viewpager.widget.PagerAdapter as PagerAdapter1


@Route(path = MainARouterPath.MAIN_CLUB_INFO_INTRODUCE)
class ClubInfoIntroduce :
    TitleBarActivity<ActivityClubInfoIntroduceBinding, ClubActicityInfoIntroduceViewModel>() {

    @JvmField
    @Autowired
    var data: ClubInfoBean? = null

    override fun getLayout(): Int = R.layout.activity_club_info_introduce

    override fun setTitle(): String = "社团介绍"

    override fun injectVm(): Class<ClubActicityInfoIntroduceViewModel> =
        ClubActicityInfoIntroduceViewModel::class.java

    override fun initView() {
        data?.let {
            mBinding.bean = it
            mBinding.tvCiiName.setText(it.name)
            if (it.associaLevel == "") mBinding.tvCiLevel.visibility = View.GONE
            mBinding.tvCiLevel.setText(it.associaLevel)
//            if (it.type == "") mBinding.llCiiType.visibility = View.GONE
            mBinding.tvCiType.setText(it.type)
            mBinding.tvCiiTime.setText(it.startTime)
            mBinding.tvCiiPersonnum.setText("" + it.teamPerson)
            if (it.fansNum == 0) {
                mBinding.tvCillFansLab.visibility = View.GONE
                mBinding.tvCiiFansDivder.visibility = View.GONE
                mBinding.tvCiiFans.visibility = View.GONE
            } else {
                mBinding.tvCiiFans.setText("" + it.fansNum)
                mBinding.tvCillFansLab.visibility = View.VISIBLE
                mBinding.tvCiiFansDivder.visibility = View.VISIBLE
                mBinding.tvCiiFans.visibility = View.VISIBLE
            }

//            mBinding.tvCiiType.setText(it.type)//团队类型
            // 负责人
            if (it.dutyPerson.isNullOrEmpty()) {
                mBinding.llCiiResperson.visibility = View.GONE
            } else {
                mBinding.tvCiiResperson.setText(it.dutyPerson)
                mBinding.llCiiResperson.visibility = View.VISIBLE
            }

            // 主管单位
            if (it.manageUnit.isNullOrEmpty()) {
                mBinding.llCiiDepartment.visibility = View.GONE
            } else {
                mBinding.tvCiiDepartment.setText(it.manageUnit)
                mBinding.llCiiDepartment.visibility = View.VISIBLE
            }

            mBinding.tvCiiAre.setText(it.regionName)//所在地区
            mBinding.tvCiiAddress.setText(it.address)
            mBinding.tvCiiAddress.onNoDoubleClick {

                if (it.latitude != 0.0 && it.longitude != 0.0) {
                    if (MapNaviUtils.isGdMapInstalled()) {
                        MapNaviUtils.openGaoDeNavi(
                            context,
                            0.0,
                            0.0,
                            null,
                            it.latitude,
                            it.longitude,
                            it.address
                        )
                    } else {
                        toast("非常抱歉，系统未安装高德地图")
                    }
                } else {
                    toast("非常抱歉，暂无位置信息")
                }
            }
            if (!it.phone.isNullOrEmpty()) {
                mBinding.tvCiiPhone.text = it.phone
                mBinding.tvCiiPhone.onNoDoubleClick {
                    if (it.phone != null)
                        SystemHelper.callPhone(this,it.phone)
                }
            } else {
                mBinding.llCiiPhone.visibility = View.GONE
            }


//            mBinding.tvCiiContent.setText(Html.fromHtml(it.introduce))
            // 团队介绍
            if (!it.introduce.isNullOrEmpty()) {
                mBinding.llCiiInfo.visibility = View.VISIBLE
                mBinding.webContent.settings.javaScriptEnabled = true
                mBinding.webContent.loadDataWithBaseURL(
                    null,
                    StringUtil.getHtml(it.introduce),
                    "text/html",
                    "utf-8",
                    null
                )
            } else {
                mBinding.llCiiInfo.visibility = View.GONE
            }
            if (!it.images.isNullOrEmpty()) {
                val mList = ArrayList<String>()
                var imges = it.images.split(",")
                for (value in imges.indices) {
                    mList.add(imges[value])
                }
                mBinding.tvCiiCur.text = "1"
                mBinding.tvCiiAll.text = mList.size.toString()

                var viewList = ArrayList<View>()
                mBinding.vpCii.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {
                    }

                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        mBinding.tvCiiCur.text = (position + 1).toString()
                    }

                    override fun onPageSelected(position: Int) {
                    }

                })
                mBinding.vpCii.setAdapter(object : PagerAdapter1() {
                    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                        container.removeView(viewList[position])
                    }

                    override fun instantiateItem(container: ViewGroup, position: Int): Any {
                        val imageView = ImageView(this@ClubInfoIntroduce)
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
                        setImageUrl(
                            imageView, mList.get(position), AppCompatResources.getDrawable(
                                BaseApplication.context, R.drawable
                                    .placeholder_img_fail_h158
                            ), 5
                        )
                        container.addView(imageView)
                        viewList.add(imageView)
                        return imageView
                    }

                    override fun getCount(): Int {
                        return mList.size
                    }

                    override fun isViewFromObject(view: View, `object`: Any): Boolean {
                        return view === `object`
                    }
                })
            }
            var audioStr = it.associaAudio.toString()
            // 团队语音
            mBinding.vClubInfoAudios.visibility = View.GONE
            if (!audioStr.isNullOrEmpty()) {
                var map = Utils.jsonStringToList(audioStr, AudioBean::class.java)
                if (!map.isNullOrEmpty()) {
                    var datas: MutableList<AudioInfo> = mutableListOf()
                    for (position: Int in map.indices) {
                        var audioBean = map[position]
                        if (audioBean != null) {
                            datas.add(AudioInfo().apply {
                                audio = audioBean.url
                                name = audioBean.name
                                time = getTime(audioBean.time.toInt())
                            })
                        }
                    }
                    mBinding.vClubInfoAudios.setTitle("社团语言")
                    mBinding.vClubInfoAudios.setData(datas)
                    mBinding.vClubInfoAudios.visibility = View.VISIBLE
                }
            }
        }

    }


    fun getTime(ms: Int): String {
        var total = ms * 1000
        var s = (total / 1000) % 60
        var m = total / 1000 / 60
        var result = ""
        if (m < 10) {
            result = "0" + m
        } else {
            result = m.toString()
        }
        if (s < 10) {
            result = "$result:0$s"
        } else {
            result = "$result:$s"
        }
        return result
    }

    override fun initData() {


    }

    override fun onPause() {
        super.onPause()
        mBinding.vClubInfoAudios.pauseAudioPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mBinding.vClubInfoAudios.pauseAudioPlayer()
            mBinding.vClubInfoAudios.stopAudioPlayer()
        } catch (e: Exception) {
        }
    }
}
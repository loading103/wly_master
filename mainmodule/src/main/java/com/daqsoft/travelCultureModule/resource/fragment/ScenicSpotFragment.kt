package com.daqsoft.travelCultureModule.resource.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemScenicSpotPagerBinding
import com.daqsoft.provider.bean.*
import com.daqsoft.travelCultureModule.resource.adapter.ScenicSpotAdapter
import java.lang.Exception

/**
 * @Description
 * @ClassName   ScenicSpotFragment
 * @Author      luoyi
 * @Time        2020/4/2 10:36
 */
class ScenicSpotFragment : BaseFragment<ItemScenicSpotPagerBinding, BaseViewModel>() {

    private var datas: MutableList<Spots> = mutableListOf()
    private var adapter: ScenicSpotAdapter? = null
    private var name: String? = null
    private var imageUrl: String? = null
    private var tags: String? = null
    private var scenicTags: ScenicTags? = null
    private var scenicId: Int ?= -1

    companion object {
        const val SPOTS = "spots"
        const val NAME = "name"
        const val IMAGE_URL = "image_url"
        const val TAGS = "tags"
        const val SCENCTAGS = "scenic_tags"
        const val SCENIC_ID = "scenic_id"

        fun newInstance(data: MutableList<Spots>, name: String?, imageUrl: String?, tags: String?, scenicTags: ScenicTags?, scenicId: Int): ScenicSpotFragment {
            var frag = ScenicSpotFragment()
            var bundle = Bundle()
            bundle.putParcelableArrayList(SPOTS, ArrayList(data))
            bundle.putString(NAME, name)
            bundle.putString(IMAGE_URL, imageUrl)
            bundle.putString(TAGS, tags)
            bundle.putInt(SCENIC_ID, scenicId)
            bundle.putParcelable(SCENCTAGS, scenicTags)
            frag.arguments = bundle
            return frag
        }
    }

//    constructor(data: MutableList<Spots>, name: String?, imageUrl: String?, tags: String?, scenicTags: ScenicTags?) {
//        this.datas.clear()
//        this.datas.addAll(data)
//        this.name = name
//        this.imageUrl = imageUrl
//        this.tags = tags
//        this.scenicTags = scenicTags
//    }

    override fun getLayout(): Int {
        return R.layout.item_scenic_spot_pager
    }

    override fun injectVm(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {
        getParams()
        mBinding.recyScenicSpots.layoutManager = GridLayoutManager(context!!, 2)
        adapter = ScenicSpotAdapter()
        mBinding.recyScenicSpots.adapter = adapter
        adapter!!.scenicName = name
        adapter!!.scenicImageUrls = imageUrl
        adapter!!.scenicTags = scenicTags
        adapter!!.scenicId = scenicId
        adapter!!.add(datas)
    }

    private fun getParams() {
        try {
            name = arguments?.getString(NAME)
            imageUrl = arguments?.getString(IMAGE_URL)
            var tempSpots = arguments?.getParcelableArrayList<Spots>(SPOTS)
            if (!tempSpots.isNullOrEmpty()) {
                datas.clear()
                datas.addAll(tempSpots)
            }
            var tempScenicTags = arguments?.getParcelable<ScenicTags>(SCENCTAGS)
            if (tempScenicTags != null) {
                scenicTags = tempScenicTags
            }
            var tags = arguments?.getString(TAGS)
            scenicId  = arguments?.getInt(SCENIC_ID)
        } catch (e: Exception) {

        }
    }

    override fun initData() {
    }

}
package com.daqsoft.travelCultureModule.hotel.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemRouteBinding
import com.daqsoft.mainmodule.databinding.LayoutRouteOrderBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.electronicBeans.ProductBean

/**
 * @Description 订线路
 * @ClassName   RouteOrderView
 * @Author      luoyi
 * @Time        2020/4/16 9:40
 */
class RouteOrderView : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutRouteOrderBinding? = null
    /**
     * 路线适配器
     */
    var routerAdapter: RouterAdapter? = null
    /**
     * 路线产品数据
     */
    var mDatas: MutableList<ProductBean> = mutableListOf()

    var onRouterViewListener: OnRouterViewListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.layout_route_order,
            this,
            false
        )

        addView(binding?.root)


    }

    /**
     * 设置数据信息
     */
    public fun setData(datas: MutableList<ProductBean>) {
        mDatas.clear()
        mDatas.addAll(datas)
        routerAdapter = RouterAdapter(mContext!!)
        binding?.rvRoute?.layoutManager =
            LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
        binding?.rvRoute?.adapter = routerAdapter
        routerAdapter?.isNeedShowMore = mDatas.size > 3
        if (routerAdapter!!.isNeedShowMore) {
            binding?.vRouterOrderShowMore?.visibility = View.VISIBLE
        } else {
            binding?.vRouterOrderShowMore?.visibility = View.GONE
        }
        routerAdapter?.clear()
        routerAdapter?.add(mDatas)
        routerAdapter?.onRouterListener = object : RouterAdapter.OnRouterListener {
            override fun onGetRestinfo(snCode: String, name: String) {
                if (onRouterViewListener != null) {
                    onRouterViewListener?.onGetRouterViewListener(snCode, name)
                }
            }

        }
        binding?.vRouterOrderShowMore?.onNoDoubleClick {
            if (routerAdapter!!.isNeedShowMore) {
                routerAdapter!!.isNeedShowMore = false
                routerAdapter?.notifyDataSetChanged()
                val drawable2 =
                    mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_up)
                drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                binding?.txtRouterOrderTip?.setCompoundDrawables(null, null, drawable2, null)
            } else {
                routerAdapter!!.isNeedShowMore = true
                routerAdapter?.notifyDataSetChanged()
                val drawable2 =
                    mContext!!.getDrawable(com.daqsoft.provider.R.mipmap.provider_arrow_down)
                drawable2.setBounds(0, 0, drawable2.minimumWidth, drawable2.minimumHeight)
                binding?.txtRouterOrderTip?.setCompoundDrawables(null, null, drawable2, null)

            }
        }
    }


    /***
     *
     * 线路适配器
     */
    class RouterAdapter : RecyclerViewAdapter<ItemRouteBinding, ProductBean> {

        var mContext: Context? = null

        var isNeedShowMore: Boolean = false
        var onRouterListener: OnRouterListener? = null

        constructor(context: Context) : super(R.layout.item_route) {
            this.mContext = context
        }


        override fun getItemCount(): Int {
            if (isNeedShowMore) {
                if (getData().size > 3) {
                    return 3
                }
            }
            return super.getItemCount()
        }

        override fun setVariable(mBinding: ItemRouteBinding, position: Int, item: ProductBean) {
            mBinding.product = item
            Glide.with(mContext!!)
                .load("" + item.url + item.thumbImageUrl)
                .placeholder(R.mipmap.placeholder_img_fail_240_180)
                .into(mBinding.ivCover)
            mBinding.tvPrice.text = StringUtil.companreBigDecimal(item.price)
            mBinding.tvUser.onNoDoubleClick {
                if (onRouterListener != null) {
                    onRouterListener?.onGetRestinfo(item.sn, item.name)
                }
            }
            mBinding.root.onNoDoubleClick {
                var shopUrl = SPUtils.getInstance().getString(SPKey.SHOP_URL)
                if (!shopUrl.isNullOrEmpty()) {
                    if (AppUtils.isLogin()) {
                        var uuid = SPUtils.getInstance().getString(SPKey.UUID)
                        var token = SPUtils.getInstance().getString(SPKey.USER_CENTER_TOKEN)
                        var encry = SPUtils.getInstance().getString(SPKey.ENCRYPTION)
                        ARouter.getInstance().build(ARouterPath.Provider.WEB_ACTIVITY)
                            .withString("mTitle", item.name)
                            .withString(
                                "html",
                                "${shopUrl}/line/confirm?lineSn=${item.sn}&unid=${uuid}&token=${token}&encryption=${encry}"
                            )
                            .navigation()
                    } else {
                        ToastUtils.showMessage("非常抱歉，你还未登录~")
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }
            }
        }

        interface OnRouterListener {
            fun onGetRestinfo(snCode: String, name: String)
        }
    }

    interface OnRouterViewListener {
        fun onGetRouterViewListener(snCode: String, name: String)
    }

    fun setVisible(isShow: Boolean) {
        visibility = if (isShow) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
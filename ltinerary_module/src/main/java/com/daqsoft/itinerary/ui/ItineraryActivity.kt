package com.daqsoft.itinerary.ui

import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.col.sln3.di
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.adapter.ItineraryListAdapter
import com.daqsoft.itinerary.databinding.ActivityItineraryBinding
import com.daqsoft.itinerary.interfa.OnItemSelectedListener
import com.daqsoft.provider.ItineraryRouter
import com.daqsoft.itinerary.vm.ItineraryViewModel
import com.daqsoft.provider.ARouterPath
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.itinerary_dialog_bottom.*
import kotlinx.android.synthetic.main.itinerary_dialog_bottom.view.*
import kotlinx.android.synthetic.main.itinerary_dialog_renamed.view.*

/**
 * @Author:     邓益千
 * @Create by   2020/4/20 20:14
 * @Description 智能行程主界面
 */
@Route(path = ItineraryRouter.ITINERARY_LIST)
class ItineraryActivity : TitleBarActivity<ActivityItineraryBinding, ItineraryViewModel>() {


    companion object {
        /**系统推荐*/
        const val SYSTEM = "SYSTEM"

        /**我的定制行程*/
        const val CLIENT = "CLIENT"
    }

    private val adapter: ItineraryListAdapter by lazy {
        ItineraryListAdapter(SYSTEM)
    }

    private val myAdapter: ItineraryListAdapter by lazy {
        ItineraryListAdapter(CLIENT)
    }

    private var dialogView: View? = null

    private var dialog: AlertDialog? = null

    override fun getLayout(): Int = R.layout.activity_itinerary

    override fun setTitle(): String = getString(R.string.smart_itinerary)

    override fun injectVm(): Class<ItineraryViewModel> = ItineraryViewModel::class.java

    override fun initView() {

        mBinding.SmartRefreshLayout.setOnRefreshListener {
            mModel.currPage = 1
            mModel.getItineraryList(CLIENT)
        }


        //加载更多监听
        adapter.setOnItemListener(itemListener)
        adapter.setOnLoadMoreListener {
            mModel.currPage++
            mModel.getItineraryList(SYSTEM)
        }

        mBinding.mRecyclerView.adapter = adapter
        myAdapter.emptyViewShow = false
        mBinding.myItineraryView.adapter = myAdapter

        myAdapter.setOnItemListener(itemListener)
    }

    override fun initData() {

        //删除行程之后刷新操作
        mModel.refresh.observe(this, Observer {
            dissMissLoadingDialog()
            myAdapter.clearNotify()
            mModel.getItineraryList(CLIENT)
        })

        mModel.itineraryList.observe(this, Observer {
            PageDealUtils().pageDeal(mModel.currPage, it, adapter!!)

            mBinding.SmartRefreshLayout.finishRefresh()
            dissMissLoadingDialog()

            if (it.datas.isNullOrEmpty()) {
                adapter?.emptyViewShow = true
            } else {
                if (mModel.currPage <= 1) {
                    adapter?.clear()
                }
                adapter?.add(it.datas!!)
            }
        })

        mModel.myItineraryList.observe(this, Observer {
            mBinding.SmartRefreshLayout.finishRefresh()
            dissMissLoadingDialog()
            myAdapter.clear()
            if (AppUtils.isLogin()) {
                if (!it.isNullOrEmpty()) {
                    mBinding.emptyLayout.visibility = View.GONE
                    mBinding.labelText.visibility = View.VISIBLE
                    mBinding.myItineraryView.visibility = View.VISIBLE
                    myAdapter.add(it)
                } else {
                    mBinding.myItineraryView.visibility = View.GONE
                    mBinding.labelText.visibility = View.GONE
                    mBinding.emptyLayout.visibility = View.VISIBLE
                }
                if (mModel.myTotal == myAdapter.itemCount - 1) {
                    myAdapter.loadEnd()
                } else {
                    myAdapter.loadComplete()
                }
            } else {
                ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        })

        //系统推荐
        mModel.getItineraryList(SYSTEM)
    }

    private val itemListener = object : ItineraryListAdapter.OnItemClick<Map<String, String>> {
        override fun onSelected(index: Int, map: Map<String, String>) {
            ARouter.getInstance()
                .build(ItineraryRouter.ITINERARY_DETAIL)
                .withString("itineraryId", map["id"])
                .withString("tagType", map["tag"])
                .navigation()
        }

        override fun onRenamed(position: Int, id: String) {
            showBottomDialog(position, id)
        }
    }

    fun onViewClick(view: View) {
        if (view.id == R.id.customize) {
            ARouter.getInstance()
                .build(ItineraryRouter.ITINERARY_CUSTOM)
                .navigation()
        } else {
            ARouter.getInstance()
                .build(ItineraryRouter.SYST_RECOMM_LIST)
                .navigation()
        }
    }

    private fun showBottomDialog(position: Int, id: String) {
        val bottomView = LayoutInflater.from(this).inflate(R.layout.itinerary_dialog_bottom, null)
        val bottomDialog = BottomSheetDialog(this@ItineraryActivity)
        bottomDialog.setContentView(bottomView)
        bottomDialog.show()

        bottomView.edit_view.setOnClickListener {
            bottomDialog.dismiss()
            renamedDialog(position)
        }

        bottomView.cance_view.setOnClickListener { bottomDialog.dismiss() }

        bottomView.delete_view.setOnClickListener {
            bottomDialog.dismiss()
            val json = ArrayMap<String, Any>()
            json["journeyId"] = id
            json["operateType"] = "0"

            val params = ArrayMap<String, String>()
            params["id"] = id
            params["type"] = "JOURNEY"

            json["params"] = arrayListOf(params)
            mModel.operateSource(json)
        }
    }

    private fun renamedDialog(position: Int) {
        val bean = myAdapter.getData()[position]
        if (dialog == null) {
            dialogView = LayoutInflater.from(this).inflate(R.layout.itinerary_dialog_renamed, null)
            dialogView!!.sure_view.setOnClickListener {
                dialog?.dismiss()
                val name = dialogView!!.name_view.text.toString()
                if (name.length >= 50) {
                    ToastUtils.showMessage("行程名称过长")
                    return@setOnClickListener
                }
                if (name.isNullOrEmpty()) {
                    ToastUtils.showMessage("行程名称不能为空")
                    return@setOnClickListener
                }

                showLoadingDialog()
                mModel.itineraryUpdate(bean.id.toString(), name)
            }

            dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

        }
        dialogView!!.name_view.setText(bean.name)
        dialog?.show()
    }

    override fun onResume() {
        super.onResume()
        showLoadingDialog()
        // 定制行程
        mModel.getItineraryList(CLIENT)
    }

}

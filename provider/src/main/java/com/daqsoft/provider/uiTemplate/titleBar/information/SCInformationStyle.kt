package com.daqsoft.provider.uiTemplate.titleBar.information

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.databinding.ScInformationStyleBinding
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.titleBar.view.TitleBarFactoryView

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.information
 * @date 10/10/2020 下午 5:48
 * @author zp
 * @describe
 */
class SCInformationStyle(
    private val layoutHelper: LayoutHelper,
    val commonTemlate:CommonTemlate
) : DelegateAdapter.Adapter<SCInformationStyle.RecyclerViewItemHolder>() {

    val informationStyleView by lazy {
        SCInformationStyleFactory.getStyleView(commonTemlate.moduleCode?:"1").create(BaseApplication.context)
    }

    /**
     * 标题栏
     */
    val titleBarView by lazy {
        TitleBarFactoryView(BaseApplication.context).apply {
            setData(commonTemlate)
            onTitileBarClickListener =
                object : TitleBarFactoryView.OnTitleBarClickListener {
                    override fun toMoreInfo(commonTemlate: CommonTemlate) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                            .withString("channelCode", "systemChannel")
                            .navigation()
                    }
                }
        }
    }

    inner class RecyclerViewItemHolder(val binding: ScInformationStyleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItemHolder {
        val mBinding = DataBindingUtil.inflate<ScInformationStyleBinding>(
            LayoutInflater.from(parent.context),
            R.layout.sc_information_style,
            parent,
            false
        )
        return RecyclerViewItemHolder(mBinding)
    }

    override fun onBindViewHolder(holder: RecyclerViewItemHolder, position: Int) {
        holder.binding.apply {
            // 标题栏
            this.titleBar.removeAllViews()
            this.titleBar.addView(titleBarView)
            // 内容
            this.content.removeAllViews()
            this.content.addView(informationStyleView)
            // 换一批
            this.changeTheBatch.onNoDoubleClick {
                changeTheBatchInterface?.changeTheBatch()
            }
            if (commonTemlate.moduleCode == "2"){
                changeTheBatch.helper.backgroundColorNormal = content.resources.getColor(R.color.color_f5f5f5)
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return layoutHelper
    }


    fun setChangeTheBatch(changeTheBatchInterface:ChangeTheBatch){
        this.changeTheBatchInterface = changeTheBatchInterface
    }

    private var changeTheBatchInterface:ChangeTheBatch? = null

    interface ChangeTheBatch{
        fun changeTheBatch()
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.INFORMATION + (commonTemlate.moduleCode?:"1").toInt()
    }
}
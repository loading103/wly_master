package com.daqsoft.itinerary.ui.fragment

import android.content.Context
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.itinerary.R
import com.daqsoft.itinerary.bean.LabelBean
import com.daqsoft.itinerary.databinding.FragmentSettingScenicBinding
import com.daqsoft.itinerary.ui.ItineraryCustomActivity
import com.daqsoft.itinerary.vm.ItineraryCustomViewModel

/**
 * @Author：      邓益千
 * @Create by：   2020/4/23 20:28
 * @Description： 步骤二，景点设置
 */
class ScenicSettingFragment : BaseFragment<FragmentSettingScenicBinding, ItineraryCustomViewModel>(){

    /**适合景区的人群*/
    private val sceneryCrowd: MutableList<LabelBean> by lazy {
        ArrayList<LabelBean>()
    }

    /**景区的主题*/
    private val sceneryTheme: MutableList<LabelBean> by lazy {
        ArrayList<LabelBean>()
    }

    /**适合场馆的人群*/
    private val venueCrowd: MutableList<LabelBean> by lazy {
        ArrayList<LabelBean>()
    }

    /**场馆的主题*/
    private val venueTheme: MutableList<LabelBean> by lazy {
        ArrayList<LabelBean>()
    }

    /**保存选中的标签 — 景区*/
    val sceneryLabel: ArrayMap<String,LabelBean> by lazy {
        ArrayMap<String,LabelBean>()
    }

    /**保存选中的标签 — 场馆*/
    val venueLabel: ArrayMap<String,LabelBean> by lazy {
        ArrayMap<String,LabelBean>()
    }

    private lateinit var parentActivity: ItineraryCustomActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as ItineraryCustomActivity
    }

    override fun getLayout(): Int = R.layout.fragment_setting_scenic

    override fun injectVm(): Class<ItineraryCustomViewModel> = ItineraryCustomViewModel::class.java

    override fun initView() {
        temp = mBinding.viewSingle.apply {
            setOnClickListener(onRadioClick)
        }
        mBinding.viewLovers.setOnClickListener(onRadioClick)
        mBinding.viewParents.setOnClickListener(onRadioClick)
        mBinding.viewFamily.setOnClickListener(onRadioClick)

        mBinding.viewLandscape.setOnCheckedChangeListener(multiselectClick)
        mBinding.viewHistory.setOnCheckedChangeListener(multiselectClick)
        mBinding.viewUncharted.setOnCheckedChangeListener(multiselectClick)
        mBinding.viewFood.setOnCheckedChangeListener(multiselectClick)

        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==mBinding.viewHotDaka.id){
                mBinding.vectorExplore.compoundDrawables[1].setTint(resources.getColor(R.color.color_D4D4D4))
                mBinding.viewHotDaka.compoundDrawables[1].setTint(resources.getColor(R.color.color_FF9E05))
            } else {
                mBinding.viewHotDaka.compoundDrawables[1].setTint(resources.getColor(R.color.color_D4D4D4))
                mBinding.vectorExplore.compoundDrawables[1].setTint(resources.getColor(R.color.color_FF9E05))
            }
            for (index in 0 until group.childCount){
                val radioView = group.getChildAt(index) as AppCompatRadioButton
                if (radioView.isChecked){
                    val textName = radioView.text.toString()
                    for (theme in sceneryTheme){
                        if (textName == theme.labelName){
                            sceneryLabel["hot"] = theme
                            break
                        }
                    }
                    for (theme in venueTheme){
                        if (textName == theme.labelName){
                            venueLabel["hot"] = theme
                            break
                        }
                    }
                    break
                }
            }
        }
    }

    override fun initData() {
        parentActivity.showLoadingDialog()

        //景区标签数据
        mModel.scenicLabel.observe(this, Observer {
            for (theme in sceneryTheme){
                //默认选中网红打卡
                if (mBinding.viewHotDaka.text.toString() == theme.labelName){
                    sceneryLabel["hot"] = theme
                }
                //默认选中自然风光
                if (mBinding.viewLandscape.text.toString() == theme.labelName){
                    sceneryLabel["${mBinding.viewLandscape.id}"] = theme
                }
            }

            for (crowd in sceneryCrowd){
                //默认选中人群
                if (mBinding.viewSingle.text.toString() == crowd.labelName){
                    parentActivity.customLabel.value!!.crowdName = crowd.labelName!!
                    parentActivity.customLabel.value!!.sceneryCrowd = crowd.id.toString()
                    break
                }
            }
            mModel.getVenueLabel(venueTheme,venueCrowd)
        })

        //场馆标签数据
        mModel.venuesLabel.observe(this, Observer {
            for (theme in venueTheme){
                //默认选中网红打卡
                if (mBinding.viewHotDaka.text.toString() == theme.labelName){
                    venueLabel["hot"] = theme
                }
                //默认选中自然风光
                if (mBinding.viewLandscape.text.toString() == theme.labelName){
                    venueLabel["${mBinding.viewLandscape.id}"] = theme
                }
            }

            for (crowd in venueCrowd){
                //默认选中人群
                if (mBinding.viewSingle.text.toString() == crowd.labelName){
                    parentActivity.customLabel.value!!.venueCrowd = crowd.id.toString()
                    break
                }
            }
            parentActivity.dissMissLoadingDialog()
        })

        mModel.getScenicLabels(sceneryTheme,sceneryCrowd)
    }


    /**团队类型单选监听*/
    private var temp: AppCompatRadioButton? = null
    private val onRadioClick = View.OnClickListener { view->
        temp?.apply{
            isChecked = false
            compoundDrawables[1].setTint(resources.getColor(R.color.color_D4D4D4))
        }
        temp = view as AppCompatRadioButton
        temp!!.apply {
            isChecked = true
            compoundDrawables[1].setTint(resources.getColor(R.color.color_FF9E05))
            val crowdName = text.toString()

            //根据选中的人群，在【场馆】人群集合里面找出id
            for (crowd in venueCrowd){
                if (crowdName == crowd.labelName){
                    parentActivity.customLabel.value!!.venueCrowd = crowd.id.toString()
                    break
                }
            }
            //根据选中的人群，在【景区】人群集合里面找出id
            for (crowd in sceneryCrowd){
                if (crowdName == crowd.labelName){
                    parentActivity.customLabel.value!!.sceneryCrowd = crowd.id.toString()
                    break
                }
            }
        }
    }


    /**个性化标签多选*/
    private val multiselectClick = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        buttonView.apply {
            if (isChecked){
                val textName = text.toString()
                for (theme in sceneryTheme){
                    if (textName == theme.labelName){
                        sceneryLabel["$id"] = theme
                        break
                    }
                }
                for (theme in venueTheme){
                    if (textName == theme.labelName){
                        venueLabel["$id"] = theme
                        break
                    }
                }
                compoundDrawables[1].setTint(resources.getColor(R.color.color_FF9E05))
            } else {
                venueLabel.remove("$id")
                sceneryLabel.remove("$id")
                compoundDrawables[1].setTint(resources.getColor(R.color.color_D4D4D4))
            }
        }
    }

}
package com.daqsoft.venuesmodule.activity.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.widgets.FullyLinearLayoutManager
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.view.LabelsView
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.adapter.VenueResPersonAdapter
import com.daqsoft.venuesmodule.databinding.LayoutTeamAppointMentInfoBinding
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

/**
 * @Description 多人预约
 * @ClassName   ManyPeopleResInfoView
 * @Author      luoyi
 * @Time        2020/8/3 16:39
 */
class ManyPeopleResInfoView : FrameLayout {

    private var mContext: Context? = null

    private var binding: LayoutTeamAppointMentInfoBinding? = null

    /**
     * 多人预约信息适配器
     */
    private var adpater: VenueResPersonAdapter? = null

    /**
     * 数据集
     */
    private var persons: MutableList<Contact> = mutableListOf()

    /**
     * 已选择人信息
     */
    private var mdatasSelectPersons: MutableList<Contact> = mutableListOf()

    var onManyPeopleViewListener: OnManyPeopleViewListener? = null

    /**
     * 预约类型
     */
    var reservationType: Int = 0

    /**
     * 预约信息
     */
    var data: VenueOrderViewInfo? = null

    /**
     * 当前选择数据集
     */
    var currentNumbers: MutableList<String> = mutableListOf()

    /**
     * 使用人数
     */
    var userNum: String = ""

    var isChangeSelectLabel: Boolean = true

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
            R.layout.layout_team_appoint_ment_info,
            this,
            false
        )
        addView(binding!!.root)
        initViewEvent()
    }

    private fun initViewEvent() {
        mContext?.let {
            adpater = VenueResPersonAdapter(mContext!!)
            adpater?.onVenueResPersonListener =
                object : VenueResPersonAdapter.OnVenueResPersonListener {
                    override fun toInputView(person: Contact, position: Int) {
                        onManyPeopleViewListener?.showEditInfo(person, position)
                    }

                    override fun toPhotoIdCard(postion: Int) {
                        onManyPeopleViewListener?.toPhotoIdCard(postion)
                    }

                    override fun toSelectContact(position: Int) {
                        onManyPeopleViewListener?.toSelectContact(position)
                    }

                    override fun clearData(position: Int, id: Int) {
                        if (position in mdatasSelectPersons.indices) {
                            mdatasSelectPersons.set(
                                position, Contact(
                                    "", "", "", 0, "", "", 0, 3, 0,
                                    "", null
                                    , ""
                                )
                            )
                            adpater?.updateItem(position, mdatasSelectPersons[position])
                        }
                        clearSelectLabel(id)
                    }

                    override fun toEditContact(position: Int, contact: Contact) {
                        onManyPeopleViewListener?.toEditContact(contact, position)
                    }


                }
            adpater?.emptyViewShow = false
            binding?.rvReservationPersons?.adapter = adpater
            binding?.rvReservationPersons?.layoutManager =
                FullyLinearLayoutManager(it, FullyLinearLayoutManager.VERTICAL, false)
        }
        binding?.llvReservationPersons?.setOnLabelSelectChangeListener { label, data, isSelect, position ->
            var item = persons[position]
            if (isSelect) {
                if (item.type == 2) {
                    // 添加联系人
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_ADD_CONTACT)
                        .navigation()
                    binding?.llvReservationPersons?.setUnSelect(position)
                } else {
                    if (item.type == 3) {
                        label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.c_36cd64))
                        label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_selected_r3)
//                        selectPersons(persons[position])
                    } else {
                        onManyPeopleViewListener?.toSelectContact(item)
                        binding?.llvReservationPersons?.setUnSelect(position)
                    }
                }
            } else {
                if (item.type != 2) {
                    label?.setTextColor(resources.getColor(com.daqsoft.provider.R.color.color_333))
                    label?.setBackgroundResource(com.daqsoft.provider.R.drawable.shape_venue_default_r3)
                    persons[position].type = 0
                    unSelectPersons(persons[position])
                }
            }

        }
        binding?.tvVenuePersonNumValue?.onNoDoubleClick {
            showSelectNumber()
        }
    }

    /**
     * 清除常用联系人弹窗选择
     * @param id 常用联系人id
     */
    private fun clearSelectLabel(id: Int) {
        if (!persons.isNullOrEmpty()) {
            for (i in persons.indices) {
                var item = persons[i]
                if (item.id == id && id != -1) {
                    persons[i].type = 0
                    binding?.llvReservationPersons?.setUnSelect(i)
                    break
                }
            }
        }
    }

    /**
     * 取消选择常用联系人
     * @param userContact 取消选择常用联系人信息
     */
    private fun unSelectPersons(userContact: Contact) {
        for (i in mdatasSelectPersons.indices) {
            var item = mdatasSelectPersons[i]
            if (item.id == userContact.id && item.id != -1 && item.id != 0) {
                var person = Contact(
                    "", "", "", 0, "", "", 0, 3, 0,
                    "", null
                    , ""
                )
                mdatasSelectPersons[i] = person
                adpater?.selectPos = -1
                adpater?.updateItemAll(i, person)
                break
            }
        }
    }

    /**
     * 选中联系人
     */
    fun selectPersons(person: Contact) {
        if (!isSelectPerson(person)) {
            for (i in mdatasSelectPersons.indices) {
                var item = mdatasSelectPersons[i]
                if (item.type == 3) {
                    mdatasSelectPersons[i] = person
                    adpater?.selectPos = i
                    adpater?.updateItemAll(i, person)
                    break
                }
            }
        } else {
            ToastUtils.showMessage("已选择的联系人")
        }

        for (i in persons.indices) {
            var item = persons[i]
            if (item.id == person.id && item.id != -1 && item.id != 0) {
                persons[i].type = 3
                binding?.llvReservationPersons?.setSelect(i)
                break
            }
        }
    }

    /**
     * 是否已选择联系人
     */
    fun isSelectPerson(person: Contact): Boolean {
        for (i in mdatasSelectPersons.indices) {
            var item = mdatasSelectPersons[i]
            if (item.id == person.id && item.id != -1 && item.id != 0) {
                return true
            }
        }
        return false
    }

    /**
     * 设置多人预约联系人信息
     */
    public fun setData(data: MutableList<Contact>?) {
        persons.clear()
        if (!data.isNullOrEmpty()) {
            for (i in data.indices) {
                var item = data[i]
                item.type = 1
                persons.add(item)
            }
        }
        persons.add(Contact("", "", "", 0, "添加", "", 0, 2, 0, "", null, ""))
        binding?.llvReservationPersons?.minSelect = 0
        binding?.llvReservationPersons?.setLabels(getPersonLables())

    }

    private fun getPersonLables(): MutableList<String>? {
        var temp: MutableList<String> = mutableListOf()
        for (item in persons) {
            item?.let {
                temp.add("" + item.name)
            }
        }
        return temp
    }

    /**
     * 改变预约方式
     * @param reservationType 预约类型
     */
    fun changeVenueTipPersonNum(reservationType: Int) {
        this.reservationType = reservationType
        if (reservationType == 2) {
            binding?.edtVenueRtnInCompanyNameVlaue?.visibility = View.VISIBLE
            binding?.tvVenueRtnCompanyName?.visibility = View.VISIBLE
        } else {
            binding?.edtVenueRtnInCompanyNameVlaue?.visibility = View.GONE
            binding?.tvVenueRtnCompanyName?.visibility = View.GONE
        }
        data?.let {
            if (reservationType == 1) {
                binding?.tvVenuePersonNumValue?.text =
                    "" + it.personNumMix
                if (it.personNumMax == 1) {
                    binding?.imgVenuePesonNum?.visibility = View.GONE
                    binding?.tvVenuePersonTip?.text = context.getString(
                        R.string.venue_reservation_only_tip_num
                    )
                } else {
                    binding?.imgVenuePesonNum?.visibility = View.VISIBLE
                    binding?.tvVenuePersonTip?.text = context.getString(
                        R.string.venue_reservation_max_tip_num,
                        "" + it.personNumMax
                    )
                }
                changeReserationUserNum(it.personNumMix)
            } else {
                binding?.tvVenuePersonNumValue?.text =
                    "" + it.teamNumMin
                if (it.teamNumMax == 1) {
                    binding?.imgVenuePesonNum?.visibility = View.GONE
                    binding?.tvVenuePersonTip?.text = context.getString(
                        R.string.venue_reservation_only_tip_num
                    )
                } else {
                    binding?.imgVenuePesonNum?.visibility = View.VISIBLE
                    binding?.tvVenuePersonTip?.text = context.getString(
                        R.string.venue_reservation_tip_num,
                        "" + it.teamNumMin, "" + it.teamNumMax
                    )
                }
                changeReserationUserNum(it.teamNumMin)
            }
        }

    }

    /**
     * 选择数量适配器
     */
    private fun showSelectNumber() {

        if (binding?.tvVenuePersonNumValue != null) {
            UIHelperUtils.hideKeyboard(binding?.tvVenuePersonNumValue!!)
        }
        if (data != null) {
            when (reservationType) {
                1 -> {
                    // 个人预约
                    numberPv?.setPicker(getPersonRervationNum())
                    currentNumbers.clear()
                    currentNumbers.addAll(getPersonRervationNum())
                }
                2 -> {
                    // 团队预约
                    currentNumbers.clear()
                    currentNumbers.addAll(getTempResevationNum())
                }
            }
            numberPv?.setPicker(currentNumbers)
            var numStr: String? =
                binding?.tvVenuePersonNumValue?.text.toString()
            if (!numStr.isNullOrEmpty() && !currentNumbers.isNullOrEmpty()) {
                var index = currentNumbers.indexOf(numStr)
                if (index >= 0) {
                    numberPv.setSelectOptions(index)
                }
            }
            numberPv?.show()
        }
    }

    /**
     * 改变选择人数
     */
    @Synchronized
    private fun changeReserationUserNum(useNum: Int) {
        io.reactivex.Observable.just("")
            .map {
                if (mdatasSelectPersons.isNullOrEmpty()) {
                    for (i in 1..useNum) {
                        mdatasSelectPersons.add(
                            Contact(
                                "",
                                "",
                                "",
                                0,
                                "",
                                "",
                                0,
                                3,
                                0,
                                "",
                                null,
                                ""
                            )
                        )
                    }
                } else {
                    var size = mdatasSelectPersons.size
                    var preSize = size + 1
                    if (useNum > size) {
                        var temp: MutableList<Contact> = mutableListOf()
                        for (i in preSize..useNum) {
                            temp.add(Contact("", "", "", 0, "", "", 0, 3, 0, "", null, ""))
                        }
                        mdatasSelectPersons.addAll(temp)
                    } else if (useNum < size) {
                        var temps: MutableList<Contact> = mutableListOf()
                        for (i in 0 until useNum) {
                            temps.add(mdatasSelectPersons[i])
                        }
                        mdatasSelectPersons.clear()
                        mdatasSelectPersons.addAll(temps)
                    } else {

                    }
                }
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                adpater?.clear()
                adpater?.add(mdatasSelectPersons)
                binding?.tvManyPeopleTip?.text =
                    mContext?.getString(R.string.venue_reservation_more_person, "" + useNum)
            }


    }

    /**
     * 数量选择器
     */
    private val numberPv by lazy {

        val pV = OptionsPickerBuilder(context, OnOptionsSelectListener { s1, s2, s3, v ->
            // 1 个人 2 团队
            var num = currentNumbers.get(s1)
            when (reservationType) {
                1 -> {
                    binding?.tvVenuePersonNumValue?.text = "${num}"
                    userNum = num
                    changeReserationUserNum(num.toInt())
                }
                2 -> {
                    binding?.tvVenuePersonNumValue?.text = "${num}"
                    userNum = num.toString()
                    changeReserationUserNum(num.toInt())
                }
            }
        }).build<String>()

        pV
    }

    /**
     * 获取团队预约到场人数
     */
    private fun getTempResevationNum(): MutableList<String> {
        var numbers: MutableList<String> = mutableListOf()
        if (data != null) {
            var max = data!!.teamNumMax
            var min = data!!.teamNumMin
            for (i in min..max) {
                numbers.add(i.toString())
            }
        }
        return numbers
    }

    /**
     * 获取个人预约到场人数
     */
    private fun getPersonRervationNum(): MutableList<String> {
        var numbers: MutableList<String> = mutableListOf()
        if (data != null) {
            var max = data!!.personNumMax
            var min = data!!.personNumMix
            for (i in min..max) {
                numbers.add(i.toString())
            }
        }
        return numbers
    }

    fun getManyPeopleReravtionInfo(): MutableList<ReseartionContact> {
        var companyName: String? = binding?.edtVenueRtnInCompanyNameVlaue?.text.toString()
        var temps: MutableList<ReseartionContact> = mutableListOf()
        for (i in mdatasSelectPersons.indices) {
            var item = mdatasSelectPersons[i]
            if (item.type != 2 && item.type != 3) {
                temps.add(
                    ReseartionContact(
                        item.certType,
                        item.phone,
                        item.name,
                        item.certNumber,
                        0,
                        item?.healthInfoBean?.region
                        , companyName
                    )
                )
            }
        }
        if (!userNum.isNullOrEmpty() && !temps.isNullOrEmpty()) {
            try {
                if (temps.size == userNum.toInt()) {
                    temps[0].leader = 1
                }
            } catch (e: Exception) {
            }

        }
        return temps

    }

    /**
     * 编辑模式返回，回填信息
     */
    fun updateSelectPersons(contact: Contact, position: Int) {
        try {
            if (position in mdatasSelectPersons.indices) {
                mdatasSelectPersons[position] = contact
                adpater?.updateItem(position, contact)
            }
        } catch (e: Exception) {
        }

    }

    fun isContainCertNum(certNum: String): Boolean {
        var isContain = false
        for (item in mdatasSelectPersons) {
            if (!item.certNumber.isNullOrEmpty() && item.certNumber == certNum) {
                isContain = true
            }
        }
        return isContain
    }

    /**
     * 返回是否已有预约人
     *  0 没有 1有
     */
    fun getIsResationPerson(): Int {
        if (!mdatasSelectPersons.isNullOrEmpty()) {
            var item = mdatasSelectPersons[0]
            if (item.name.isNullOrEmpty() && item.certNumber.isNullOrEmpty() && item.phone.isNullOrEmpty()) {
                return 0
            }
        }
        return 1
    }


    interface OnManyPeopleViewListener {
        /**
         * 选择数量
         */
        fun showNumPick()

        /**
         * 显示编辑信息
         */
        fun showEditInfo(person: Contact, position: Int)

        /**
         * 拍摄身份证
         */
        fun toPhotoIdCard(postion: Int)

        /**
         * 选择联系人
         */
        fun toSelectContact(postion: Int)

        /**
         * 选择联系人去修改
         */
        fun toSelectContact(contact: Contact)

        /**
         * 编辑联系人
         */
        fun toEditContact(contact: Contact, position: Int)

    }
}
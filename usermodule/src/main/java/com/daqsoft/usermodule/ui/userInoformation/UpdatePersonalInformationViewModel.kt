package com.daqsoft.usermodule.ui.userInoformation


import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.bean.UserBean
import com.daqsoft.provider.network.net.UserRepository

/**
 * @Description 修改个人信息的model
 * @ClassName   UpdatePersonalInformationViewModel
 * @Author      PuHua
 * @Time        2019/11/4 18:16
 */
class UpdatePersonalInformationViewModel : BaseViewModel() {
    /**
     * 修改的参数名
     */
    companion object {
        /**
         * 邮箱
         */
        const val email = "email"
        /**
         * 	公司名称	string	非必填
         */
        const val workplace = "workplace"
        /**
         * 学校
         */
        const val school = "school"
        /**
         * 生日	string	非必填，格式 yyyy-MM-dd
         */
        const val birthday = "birthday"
        /**
         * 星座	string	星座是枚举字段，需要调用星座接口获取数据
         */
        const val constellation = "constellation"
        /**
         * 家庭住址	string	非必填；长度限制50
         */
        const val address = "address"
        /**
         * 身份证	string	非必填，注意身份证做了强校验，需要填写正确
         */
        const val idCard = "idCard"
        /**
         * 个性签名	string	非必填(长度限制200)
         */
        const val signature = "signature"
        /**
         * 地区	string	非必填，传地区最低级region，如四川省成都市，只传成都市一个region
         */
        const val placeLocation = "placeLocation"
        /**
         * 性别(数字)	number	非必填；1 男，2 女，0 保密
         */
        const val sex = "sex"
        /**
         * 昵称	string	非必填
         */
        const val nickName = "nickName"
        /**
         * 头像
         */
        const val headUrl = "headUrl"
    }

    /**
     * 页面类型
     */
    var type: String = ""
    /**
     * 个人信息
     */
    var userBean: UserBean? = null

    /**
     * 修改个人信息
     */
    fun updatePsersonalInformation(key: String, value: String) {
        mPresenter.value?.loading = true
        val map: HashMap<String, String> = HashMap()
        map[key] = value
        UserRepository().userService.updateUserInformation(map)
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    if (response.code == 0) {
                        toast.postValue("修改成功!")
                        finish.postValue(true)
                    } else {
                        toast.postValue(response.message.toString())
                    }
                }
            })
    }


}
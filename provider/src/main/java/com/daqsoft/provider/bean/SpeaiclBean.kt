package com.daqsoft.provider.bean

import android.view.View
import com.daqsoft.provider.getRealImageUrl
import com.daqsoft.provider.network.venues.bean.AudioInfo
import kotlinx.android.parcel.Parcelize

/**
 * 景区实体
 */
data class SpeaiclBean(
    val activity: List<ActivityBean>?,
    val audio: String?,
    val audioTime: String?,
    val authDepartment: String?,
    val authTime: String?,
    val briefing: String?,
    val code: String?,
    val contentDataList: List<Any>?,
    val externalIsOpen: Boolean?,
    val highLight: String?,
    val id: Int?,
    val images: String?,
    val indexImage: String?,
    val introduce: String?,
    val isOpen: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val name: String?,
    val officialDocNum: String?,
    val orderStatus: String?,
    val panoramaUrl: String?,
    val phone: String?,
    val recommendHomePage: Int?,
    val region: String?,
    val regionName: String?,
    val resourceCode: String?,
    val signId: String?,
    val signType: List<String>?,
    val srs: Int?,
    var tag: MutableList<String>?= mutableListOf(),
    val type: String?,
    val video: String?,
    val vipResourceStatus: VipResourceStatus?,
    val websiteUrl: String
) {
    fun getheadImages():String?{
        return indexImage?.getRealImageUrl()
    }
    fun getYchandi():String?{
        if(regionName.isNullOrBlank()){
            return ""
        }
        return "原产地：$regionName"
    }

    fun getProTime():String?{
        if(authTime.isNullOrBlank()){
            return ""
        }
        return "受保护时间：$authTime"
    }

    fun getBiaozhi():String{
        if(signType.isNullOrEmpty()){
            return  ""
        }
        return  signType[0]
    }

    fun getBiaoTag(): MutableList<String>?{
        if(type.isNullOrBlank()){
            return tag
        }else{
            if(tag.isNullOrEmpty()){
                tag?.add(0,type)
            }else{
                if( tag?.get(0)!=type){
                    tag?.add(0,type)
                }
            }
            return tag
        }
    }
}
data class SpeaiclDetailBean(
    val activity: MutableList<ActivityBean>,
    val audio: String,
    val audioInfo: List<AudioInfo>,
    val audioTime: String,
    val authDepartment: String,
    val authTime: String,
    val briefing: String,
    val categoryId: String,
    val categoryName: String,
    val code: String,
    var collectionNum: String,
    val commentNum: String,
    val createCompany: String,
    val createTime: String,
    val createUser: Int,
    val cutRegionName: String,
    val elevation: Any,
    val highLight: String,
    val id: Int,
    val imageList: String,
    val images: String,
    val indexImage: String,
    val introduce: String?=null,
    val isOpen: Int,
    val latitude: Double,
    var likeNum: String,
    val longitude: Double,
    val name: String,
    val officialDocNum: String,
    val officialUrl: String,
    val orderAddressType: String,
    val orderStatus: String,
    val orderSysUrl: String,
    val orgId: String,
    val panoramaCover: String,
    val panoramaUrl: String,
    val phone: String,
    val region: String,
    val regionName: String,
    val resourceCode: String,
    val signId: String,
    val signType: List<String>,
    val signTypeName: String,
    val siteId: Int,
    val sort: String,
    val status: String,
    val tag: List<String>,
    val type: String,
    val typeName: String,
    val updateTime: String,
    val updateUser: Int,
    val video: String,
    val videoCover: String,
    val vipResourceStatus: VipResourceStatus,
    val websiteUrl: String
){

    fun getBzVisible():Int{
        if(signType.isNullOrEmpty()){
            return  View.GONE
        }
        return  View.VISIBLE
    }

    fun getTypes1():Boolean{
        if(signType.isNullOrEmpty()){
            return  false
        }
        return signType.contains("农产品地理标志")
    }
    fun getTypes2():Boolean{
        if(signType.isNullOrEmpty()){
            return  false
        }
        return signType.contains("地理标志保护产品")
    }


    fun getYchandi():String?{
        if(regionName.isNullOrBlank()){
            return ""
        }
        return "原产地：$regionName"
    }

    fun getProTime():String?{
        if(authTime.isNullOrBlank()){
            return ""
        }
        return "受保护时间：$authTime"
    }

    fun getProGwen():String?{
        if(officialDocNum.isNullOrBlank()){
            return ""
        }
        return "保护公文号：$officialDocNum"
    }
}




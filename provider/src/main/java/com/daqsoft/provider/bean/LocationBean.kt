package com.daqsoft.provider.bean

/**
 *@package:com.daqsoft.provider.bean
 *@date:2020/4/11 14:09
 *@author: caihj
 *@des:TODO
 **/
data class LocationBean(  val region: String,
                          val address: String,
                          val latitude: Double,
                          val longitude: Double,
                          var city: String)
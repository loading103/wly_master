package com.daqsoft.provider.uiTemplate.titleBar.cityCard

import android.content.Context

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.cityCard
 * @date 12/10/2020 下午 3:02
 * @author zp
 * @describe 故事样式工厂
 */
class SCCityCardStyleFactory {

    companion object{
        fun getStyleView(type:String) : SCCityCardStyleView {
            return when(type){
                "1" ->{
                    SCCityCardStyleViewOne()
                }
                "2" ->{
                    SCCityCardStyleViewTwo()
                }
                "3" ->{
                    SCCityCardStyleViewThree()
                }
                "4" ->{
                    SCCityCardStyleViewFour()
                }
                else ->{
                    SCCityCardStyleViewOne()
                }
            }
        }
    }
}

interface SCCityCardStyleView{
    fun create(context: Context): SCCityCardStyleBase
}


class SCCityCardStyleViewOne : SCCityCardStyleView{
    override fun create(context: Context): SCCityCardStyleBase {
        return SCCityCardStyleOne(context)
    }
}

class SCCityCardStyleViewTwo : SCCityCardStyleView{
    override fun create(context: Context): SCCityCardStyleBase {
        return SCCityCardStyleTwo(context)
    }
}

class SCCityCardStyleViewThree : SCCityCardStyleView{
    override fun create(context: Context): SCCityCardStyleBase {
        return SCCityCardStyleThree(context)
    }
}

class SCCityCardStyleViewFour : SCCityCardStyleView{
    override fun create(context: Context): SCCityCardStyleBase {
        return SCCityCardStyleFour(context)
    }
}
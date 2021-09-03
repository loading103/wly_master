package com.daqsoft.provider.uiTemplate.titleBar.column

import android.content.Context

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.information
 * @date 12/10/2020 下午 3:02
 * @author zp
 * @describe 咨询样式工厂
 */
class SCColumnStyleFactory {

    companion object{
        fun getStyleView(type:String) : SCColumnStyleView {
            return when(type){
                "1" ->{
                    SCColumnStyleViewOne()
                }
                "2" ->{
                    SCColumnStyleViewTwo()
                }
                "3" ->{
                    SCColumnStyleViewThree()
                }
                else ->{
                    SCColumnStyleViewOne()
                }
            }
        }
    }
}

interface SCColumnStyleView{
    fun create(context: Context): SCColumnStyleBase
}


class SCColumnStyleViewOne : SCColumnStyleView{
    override fun create(context: Context): SCColumnStyleBase {
        return SCColumnStyleOne(context)
    }
}

class SCColumnStyleViewTwo : SCColumnStyleView{
    override fun create(context: Context): SCColumnStyleBase {
        return SCColumnStyleTwo(context)
    }
}

class SCColumnStyleViewThree : SCColumnStyleView{
    override fun create(context: Context): SCColumnStyleBase {
        return SCColumnStyleThree(context)
    }
}
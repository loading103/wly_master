package com.daqsoft.provider.uiTemplate.titleBar.information

import android.content.Context

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.information
 * @date 12/10/2020 下午 3:02
 * @author zp
 * @describe 咨询样式工厂
 */
class SCInformationStyleFactory {

    companion object{
        fun getStyleView(type:String) : SCInformationStyleView {
            return when(type){
                "1" ->{
                    SCInformationStyleViewOne()
                }
                "2" ->{
                    SCInformationStyleViewTwo()
                }
                "3" ->{
                    SCInformationStyleViewThree()
                }
                "4" ->{
                    SCInformationStyleViewFour()
                }
                "5" ->{
                    SCInformationStyleViewFive()
                }
                else ->{
                    SCInformationStyleViewOne()
                }
            }
        }
    }
}

interface SCInformationStyleView{
    fun create(context: Context): SCInformationStyleBase
}


class SCInformationStyleViewOne : SCInformationStyleView{
    override fun create(context: Context): SCInformationStyleBase {
        return SCInformationStyleOne(context)
    }
}

class SCInformationStyleViewTwo : SCInformationStyleView{
    override fun create(context: Context): SCInformationStyleBase {
        return SCInformationStyleTwo(context)
    }
}

class SCInformationStyleViewThree : SCInformationStyleView{
    override fun create(context: Context): SCInformationStyleBase {
        return SCInformationStyleThree(context)
    }
}

class SCInformationStyleViewFour : SCInformationStyleView{
    override fun create(context: Context): SCInformationStyleBase {
        return SCInformationStyleFour(context)
    }
}

class SCInformationStyleViewFive : SCInformationStyleView{
    override fun create(context: Context): SCInformationStyleBase {
        return SCInformationStyleFive(context)
    }
}
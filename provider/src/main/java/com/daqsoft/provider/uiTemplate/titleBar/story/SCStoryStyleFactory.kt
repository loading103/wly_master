package com.daqsoft.provider.uiTemplate.titleBar.story

import android.content.Context

/**
 * @package name：com.daqsoft.provider.uiTemplate.titleBar.story
 * @date 12/10/2020 下午 3:02
 * @author zp
 * @describe 故事样式工厂
 */
class SCStoryStyleFactory {

    companion object{
        fun getStyleView(type:String) : SCStoryStyleView {
            return when(type){
                "1" ->{
                    SCStoryStyleViewOne()
                }
                "2" ->{
                    SCStoryStyleViewTwo()
                }
                "3" ->{
                    SCStoryStyleViewThree()
                }
                else ->{
                    SCStoryStyleViewOne()
                }
            }
        }
    }
}

interface SCStoryStyleView{
    fun create(context: Context): SCStoryStyleBase
}


class SCStoryStyleViewOne : SCStoryStyleView{
    override fun create(context: Context): SCStoryStyleBase {
        return SCStoryStyleOne(context)
    }
}

class SCStoryStyleViewTwo : SCStoryStyleView{
    override fun create(context: Context): SCStoryStyleBase {
        return SCStoryStyleTwo(context)
    }
}

class SCStoryStyleViewThree : SCStoryStyleView{
    override fun create(context: Context): SCStoryStyleBase {
        return SCStoryStyleThree(context)
    }
}
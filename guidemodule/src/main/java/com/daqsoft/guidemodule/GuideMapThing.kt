package com.daqsoft.guidemodule

object GuideMapShowType {

    /**
     * CONTENT_TYPE_SCENERY 景区，CONTENT_TYPE_SCENIC_SPOTS 景点，CONTENT_TYPE_TOILET  厕所，CONTENT_TYPE_PARKING  停车场，
     * CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE 非遗（包含CONTENT_TYPE_HERITAGE_TEACHING_BASE，CONTENT_TYPE_HERITAGE_PROTECT_BASE）
     */
    const val GUIDE_SHOW_TYPE_SCENIC = "CONTENT_TYPE_SCENERY"
    const val GUIDE_SHOW_TYPE_LEGACY = "CONTENT_TYPE_HERITAGE_EXPERIENCE_BASE"
    const val GUIDE_SHOW_TYPE_SPOT = "CONTENT_TYPE_SCENIC_SPOTS"
    const val GUIDE_SHOW_TYPE_LINE = "line"
    const val GUIDE_SHOW_TYPE_LINE_TOUR = "tour"
    const val GUIDE_SHOW_TYPE_TOILET = "CONTENT_TYPE_TOILET"
    const val GUIDE_SHOW_TYPE_PARK = "CONTENT_TYPE_PARKING"
}


object GuideResType {
    const val CONTENT_TYPE_GUIDED_TOUR_ROUTE = "CONTENT_TYPE_GUIDED_TOUR_ROUTE"
}


/**
 * ViewPager 切换 index
 */
internal data class GuideVpChangePosEvent(val tag: String, val pos: Int)


/**
 * 讲解 播放状态改变
 */
internal data class GuideSpeakStatusEvent(val tag: String, val isPlaying: Boolean = false, val pos: Int = 0, val name: String = "", val audioUrl: String = "")

/**
 * 导览 、预览  中 下一站 按钮点击
 */
internal data class GuideTourNextSpotEvent(val tag: String, val pos: Int, val oriInPointsIndex: Int)
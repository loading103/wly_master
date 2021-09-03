package com.daqsoft.thetravelcloudwithculture.ui.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.provider.bean.MenuCode
import com.daqsoft.provider.utils.MenuJumpUtils
import com.daqsoft.thetravelcloudwithculture.sc.SCHomeFragment
import com.daqsoft.thetravelcloudwithculture.sc.fragment.ScMineFragment
import com.daqsoft.thetravelcloudwithculture.sc.fragment.ScServiceFragment
import com.daqsoft.thetravelcloudwithculture.ui.*
import com.daqsoft.thetravelcloudwithculture.ws.WSHomeFragment
import com.daqsoft.thetravelcloudwithculture.xj.ui.XJHomeFragment
import com.daqsoft.travelCultureModule.hotActivity.ActivityIndexFragment
import com.daqsoft.travelCultureModule.hotActivity.ActivityIndexFragmentSC
import com.daqsoft.travelCultureModule.story.TimeFragment
import com.daqsoft.venuesmodule.fragment.VenuesFragment


/**
 * 跳转加载工具类
 * @author 黄熙
 * @date 2020/3/25 0025
 * @version 1.0.0
 * @since JDK 1.8
 * @update luoyi 调整该类为静态工具类
 */
object JumpUtils {

    /**
     * 根据首页返回的menuCode类型判断，加载对应的Fragment
     * @param element 元素
     */
    fun homeFragmentUtils(element: HomeMenu): Fragment {
        return when (element.menuCode) {
            // 首页
            MenuCode.INDEX -> {
                if (BaseApplication.appArea == "sc") {
                    return SCHomeFragment()
                } else if (BaseApplication.appArea == "xj") {
                    return XJHomeFragment()
                } else if (BaseApplication.appArea == "ws") {
                    return WSHomeFragment()
                }
                return HomeFragment()
            }
            // 活动
            MenuCode.ACTIVITY -> {
                if (BaseApplication.appArea == "sc") {
                    return ActivityIndexFragmentSC()
                } else if (BaseApplication.appArea == "xj" || BaseApplication.appArea == "ws") {
                    return ActivityIndexFragment()
                }
                return ActivityIndexFragmentSC()
            }
            // 商城
            MenuCode.MALL, MenuCode.EXTERNAL -> {
                ShopFragment.newInstance(element.externalLink)
            }
            // 时光
            MenuCode.TIME -> TimeFragment()
            // 场馆
            MenuCode.VENUE -> VenuesFragment()
            // 公共服务
            MenuCode.COMMON ->
                if (BaseApplication.appArea == "sc") {
                    ScServiceFragment()
                } else if (BaseApplication.appArea == "ws") {
                    WsServiceFragment()
                } else
                    ServiceFragment()

            // 新服务页
            MenuCode.NEW_SERVICE -> {
                ServiceTemplateFragment.newInstance(true)
            }
            // 我的
            MenuCode.USER -> {
                if (BaseApplication.appArea == "sc") {
                    ScMineFragment()
                } else
                    MineFragment()
            }
            else -> DevelopingFragment()
        }
    }

    /**
     * 功能菜单页面跳转工具类
     */
    fun menuPageJumpUtils(item: HomeMenu, childFragmentManager: FragmentManager? = null) {
        MenuJumpUtils.menuPageJumpUtils(item, "", "", childFragmentManager)
    }

    /**
     * 根据首页返回的menuCode类型判断，加载对应的Fragment
     * @param element 元素
     *
     * {
    "_j_business" = 1;
    "_j_msgid" = 2252004293732051;
    "_j_uid" = 50321037664;
    aps =     {
    alert = "明凯推送消息测试";
    badge = 11;
    sound = "";
    };
    classify = 1;
    content = "自定义内容";
    relationId = 555;
    resourceType = "CONTENT_TYPE_STORY";
    title = "自定义标题";
    type = 1;
    }
     */
    fun homeFragmentUtils(element: CommonTemlate): Fragment {
        return when (element.menuCode) {
            // 首页
            MenuCode.INDEX -> {
                return HomeFragment()
            }
            // 活动
            MenuCode.ACTIVITY -> {
                if (BaseApplication.appArea == "sc") {
                    return ActivityIndexFragmentSC()
                } else if (BaseApplication.appArea == "xj") {
                    return ActivityIndexFragment()
                }
                return ActivityIndexFragmentSC()
            }
            // 商城
            MenuCode.MALL, MenuCode.EXTERNAL -> {
                ShopFragment.newInstance(element.externalLink)
            }
            // 时光
            MenuCode.TIME -> TimeFragment()
            // 场馆
            MenuCode.VENUE -> VenuesFragment()
            // 公共服务
            MenuCode.COMMON -> {
                if (BaseApplication.appArea == "sc") {
                    ScServiceFragment()
                } else if (BaseApplication.appArea == "ws") {
                    WsServiceFragment()
                } else
                    ServiceFragment()
            }
            // 新服务页
            MenuCode.NEW_SERVICE -> {
                ServiceTemplateFragment.newInstance(true)
            }
            // 新个人中心
            MenuCode.NEW_MINE_CENTER -> {
                MineFragmentNew()
            }

            // 我的
            MenuCode.USER -> {
                if (BaseApplication.appArea == "sc") {
                    MineFragmentNew()
                } else
                    MineFragment()
            }
            else -> DevelopingFragment()
        }
    }
}
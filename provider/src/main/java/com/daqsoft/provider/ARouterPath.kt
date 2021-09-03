package com.daqsoft.provider

import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils

/**
 * 保存activity的路径
 */
object ARouterPath {
    /**
     * 用户中心模块
     */
    class UserModule {
        companion object {
            /**
             * 个人资料PersonalInformationActivity
             */
            const val USER_INFORMATION_ACTIVITY = "/userModule/PersonalInformationActivity"

            /**
             * 登录页
             */
            const val USER_LOGIN_ACTIVITY = "/userModule/LoginActivity"

            /**
             * 注册页
             */
            const val USER_REGISTER_ACTIVITY = "/userModule/RegisterActivity"

            /**
             * 绑定手机号
             */
            const val USER_BIND_PHONE = "/userModule/BindPhoneActivity"

            /**
             * 修改个人信息页
             */
            const val USER_UPDATE_INFORMATION = "/userModule/UpdatePersonalInformationActivity"

            /**
             * 个人更多信息
             */
            const val USER_UPDATE_MORE = "/userModule/MoreInformationActivity"

            /**
             * 收货地址列表页
             */
            const val USER_RECEIVE_ADDRESS = "/userModule/ReceiverAddressManagementActivity"

            /**
             * 新增收货地址
             */
            const val USER_ADD_RECEIVE_ADDRESS = "/userModule/AddReceiverAddressActivity"

            /**
             * 新增联系人
             */
            const val USER_ADD_CONTACT = "/userModule/AddContactActivity"

            /**
             * 收货地址列表页
             */
            const val USER_CONTACT_MANAGEMENT = "/userModule/ContactManagementActivity"

            /**
             * 修改重置密码
             */
            const val USER_UPDATE_RESET_PASSWORD = "/userModule/UpdateAndResetPasswordActivity"

            /**
             * 预订列表页面
             */
            const val USER_ORDER_LIST = "/userModule/OrderListActivity"

            /**
             * 我的预约列表
             */
            const val USER_APPOINTMENT_LIST = "/userModule/MineAppointmentActivity"

            /**
             * 我的预约列表 四川
             */
            const val USER_APPOINTMENT_SC_LIST = "/UserMoudle/MineScAppointmentActivity"

            /**
             * 我的预约列表 改版四川
             */
            const val USER_MY_ORDERS = "/UserMoudle/NewMyOrdersList"

            /**
             * 小电商订单列表页面
             */
            const val USER_ELECTRONIC_ORDERS = "/userModule/ElectronicListActivity"

            /**
             * 订单详情
             */
            const val USER_ORDER_DETAIL = "/userModel/OrderDetailActivity"

            /**
             * 订单详情 改版
             */
            const val NEW_ORDER_DETAIL = "/userModel/NewOrderDetailActivity"

            /**
             * 核销详情
             */
            const val WRITE_OFF_DETAIL = "/userModel/WriteOffDetailActivity"

            /**
             *  部分核销详情
             */
            const val PART_WRITER_OFF_SUCCESS = "/userModel/PartWritterSuccessActivity"

            /**
             * 实名认证提交
             */
            const val AUTHENTICATE_COMMIT_ACTIVITY = "/userModel/AuthenticateCommitActivity"

            /**
             * 实名认证完成
             */
            const val AUTHENTICATE_COMPLETE_ACTIVITY = "/userModel/AuthenticateCompleteActivity"

            /**
             * 实名认证不通过
             */
            const val AUTHENTICATE_NOT_PASS_ACTIVITY = "/userModel/AuthenticateNotPassActivity"

            /**
             * 实名认证审核中
             */
            const val AUTHENTICATE_REVIEW_ACTIVITY = "/userModel/AuthenticateReviewActivity"

            /**
             * 消费码列表页面
             */
            const val USER_CONSUME_LIST_ACTIVITY = "/userModule/ConsumeListActivity"

            /**
             * 消费码详情页面
             */
            const val USER_CONSUME_DETAIL = "/userModule/ConsumeDetailActivity"

            /**
             * 购物退款
             */
            const val MINE_ORDER_REFUND = "/userModule/OrderRefundListActivity"

            /**
             * 购物退款详情
             */
            const val MINE_ORDER_REFUND_DETAIL = "/userModule/OrderRefundDetailActivity"

            /**
             * 预约消费页面--小电商
             */
            const val USER_CONSUME_ELECTRONIC_BOOKING = "/userModule/ElectronicBookingActivity"

            /**
             * 预约消费页面--小电商
             */
            const val USER_CONSUME_ELECTRONIC_BOOKING_WEB =
                "/userModule/ElectronicBookingWebActivity"

            /**
             * 消费码详情--小电商
             */
            const val USER_ELECTRONIC_TICKET_DETAIL = "/userModule/ElectronicTicketDetailActivity"

            /**
             * 小电商订单详情（实物）
             */
            const val USER_ELECTRONIC_IN_KIND_DETAIL_ACTIVITY =
                "/userModule/ElectronicOrderInKindDetailActivity"

            /**
             * 小电商订单详情(虚拟商品)
             */
            const val USER_ELECTRONIC_VULTURE_DETAIL_ACTIVITY =
                "/userModule/ElectronicOrderDetailActivity"

            /**
             * 小电商订单详情（酒店）
             */
            const val USER_ELECTRONIC_HOTEL_DETAIL_ACTIVITY =
                "/userModule/ElectronicOrderHotelDetailActivity"

            /**
             * 小电商订单详情（门票）
             */
            const val USER_ELECTRONIC_TICKET_DETAIL_ACTIVITY =
                "/userModule/ElectronicOrderTicketDetailActivity"

            /**
             * 小电商订单详情（路线）
             */
            const val USER_ELECTRONIC_LINE_DETAIL_ACTIVITY =
                "/userModule/ElectronicOrderLineDetailActivity"

            /**
             * 小电商评价晒单
             */
            const val USER_ELECTRONIC_COMMENT_ACTIVITY = "/userModule/OrderCommentActivity"

            /**
             * 小电商退款申请
             */
            const val USER_ELECTRONIC_REBACK_ACTIVITY = "/userModule/ElectronicOrderReBackActivity"

            /**
             * 我的收藏
             */
            const val USER_COLLECTION_ACTIVITY = "/userModule/CollectionActivity"

            /**
             * 我的关注
             */
            const val USER_FOCUS_ACTIVITY = "/userModule/MineFocusActivity"

            /**
             * 我的投诉
             */
            const val USER_COMPLAINT_ACTIVITY = "/userModule/MineComplaintActivity"

            /**
             * 我的投诉详情
             */
            const val USER_COMPLAINT_DETAILS_ACTIVITY = "/userModule/MineComplaintDetailsActivity"


            /**
             * 我的信用
             */
            const val USER_CREDIT_ACTIVITY = "/userModule/MineCreditActivity"

            /**
             * 我的信用记录
             */
            const val USER_CREDIT_RECORD_ACTIVITY = "/userModule/MineCreditHistoryActivity"

            /**
             * 意见反馈
             */
            const val USER_FEED_BACK_ACTIVITY = "/userModule/FeedBackActivity"

            /**
             * 意见反馈列表
             */
            const val USER_FEED_BACK_LS_ACTIVITY = "/userModule/FeedBackLsActivity"

            const val USER_APPOINT_CANCE_ORDER_ACITIVTY = "/userModule/UserCanceOrderActivity"

            /**
             * 邀请有礼
             */
            const val USER_INVITE_ACTIVITY = "/userModule/InviteActivity"

            /**
             * 邀请列表
             */
            const val USER_INVITE_LS_ACTIVITY = "/userModule/InviteLsActivity"

            /**
             * 接收邀请页面
             */
            const val USER_INIVITE_RECEIVE_ACTIVITY = "/userModule/ReceiveInviteActivity"

            /**
             * 输入邀请码页面
             */
            const val USER_INIVITE_INPUT_CODE_ACITIVITY = "/userModule/InputInviteCodeActivity"

            const val USER_INVITE_SUCCESS_ACTIVITY = "/userModule/InviteSuccessActivity"

            /**
             * 消息中心
             */
            const val USER_MEASSAGE_CENTER_ACTIVITY = "/userModule/MessageCenterActivity"
            /**
             * 消息列表（系统消息）
             */
            const val USER_MEASSAGE_LIST_DETAIL = "/userModule/MessageListActivity"
            /**
             * 消息列表（互动消息）
             */
            const val USER_MEASSAGE_HD_LIST_DETAIL = "/userModule/MessageHdListActivity"
            /**
             * 消息列表（其它消息）
             */
            const val USER_MEASSAGE_QT_LIST_DETAIL = "/userModule/MessageOtherListActivity"
            /**
             * 消息中心
             */
            const val USER_MEASSAGE_COURSE_ACTIVITY = "/userModule/MessageCourseActivity"

            /**
             * 通知详情
             */
            const val USER_MEASSAGE_NOTICE_DETAIL = "/userModule/NoticeDetailActivity"

            /**
             * 我的邀请页面
             */
            fun toMineInvitePage() {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(USER_INVITE_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showUnLoginMsg()
                    ARouter.getInstance().build(USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }

            /**
             * 我的邀请列表页面
             */
            fun toMineInviteLsPage() {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(USER_INVITE_LS_ACTIVITY)
                        .navigation()
                } else {
                    ToastUtils.showUnLoginMsg()
                    ARouter.getInstance().build(USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }

            /**
             * 输入邀请码页面
             */
            fun toInPutInviteCodePage() {
                if (AppUtils.isLogin()) {
                    ARouter.getInstance().build(USER_INIVITE_INPUT_CODE_ACITIVITY)
                        .navigation()
                } else {
                    ToastUtils.showUnLoginMsg()
                    ARouter.getInstance().build(USER_LOGIN_ACTIVITY)
                        .navigation()
                }
            }

            /**
             * 接收邀请界面
             */
            fun toReceiveInvitePage(code: String) {
                ARouter.getInstance().build(USER_INIVITE_RECEIVE_ACTIVITY)
                    .withString("code", code)
                    .navigation()
            }

            fun toLoginPage() {
                ARouter.getInstance().build(USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }


    class MainModule {
        companion object {
            const val MAIN_ACTIVITY = "/mainModule/MainActivity"
        }

    }

    class Provider {
        companion object {
            /**
             * WEB界面
             */
            const val WEB_ACTIVITY = "/provider/WebActivity"
            /**
             * 评论列表
             */
            const val PROVIDER_COMMENT_LS = "/provider/providerCommentLs"

            /**
             * 提交评论
             */
            const val PROVIDER_POST_COMMENT = "/provider/providerPostComment"

            /**
             * 评论结果
             */
            const val PROVIDER_COMMENT_RESULT = "/provider/providerCommentResult"

            /**
             *  活动列表
             */
            const val PROVIDER_ACTIVITIES = "/provider/providerActivities"

            /**
             * 扫一扫界面
             */
            const val PROVIDER_SCAN_ACTIVITY = "/provider/CaptureActivity"

            /**
             * 订单评论界面
             */
            const val PROVIDER_ORDER_COM_ACTIVITY = "/provider/orderCommentActivity"

            /**
             * 拍摄身份证
             */
            const val PROVIDER_PHOTO_IDCARD_ACTIVITY = "/provider/photoIdCardActivity"
        }

    }

    class IntegralModule {
        companion object {
            /**
             * 积分会员商城首页
             */
            const val MEMBER_HOME_ACTIVITY = "/integralModule/MemberHomeActivity"

            /**
             * 用户积分详情
             */
            const val INTEGRAL_DETAIL_ACTIVITY = "/integralModule/IntegralDetailActivity"
        }
    }

    /**
     * 文化场馆module
     */
    class VenuesModule {

        companion object {
            /**
             * 文化场馆列表
             */
            const val VENUES_LIST_ACTIVITY = "/venuesModule/VenuesActivity"

            /**
             * 文化场馆详情
             */
            const val VENUES_DETAILS_ACTIVITY = "/venuesModule/VenuesDetailsActivity"

            /**
             * 场馆预约
             */
            const val VENUES_RESERVATION_ACTIVITY = "/venuesModule/VenueReservationActivity"

            /**
             * 场馆预约详情
             */
            const val VENUES_RESERVATION_V1_ACTIVITY = "/venuesModule/VenueReservationV1Activity"

            /**
             * 景区预约详情
             */
            const val SCENIC_RESERVATION_ACTIVITY = "/venuesModule/ScenicReservationActivity"

            /**
             * 智游天府码详情
             */
            const val VENUES_ZYTF_CODE_INFO_ACTIVITY = "/venuesModule/VneueZytfCodeInfoActivity"

            /**
             * 智游天府讲解员预约
             */
            const val VENUES_RESERVATION_COM_ACTIVITY = "/venuesModule/VenueCommentatorResActivity"

            /**
             * 智游天府选择时间
             */
            const val VENUE_RES_SELECT_TIME_ACTIVITY = "/venueModule/VenueResSelectTimeActivity"

            /**
             * 智游天府选择时间
             */
            const val SCENIC_RES_SELECT_TIME_ACTIVITY = "/venueModule/ScenicResSelectTimeActivity"

            /**
             * 场馆预约详情
             */
            const val VENUES_RESERVATION_INFO_ACTIVITY =
                "/venuesModule/VenueReservationInfoActivity"

            /**
             * 场馆图片
             */
            const val VENUES_IMAGES_ACTIVITY = "/venuesModule/VenuesImagesActivity"

            /**
             * 场馆图片浏览
             */
            const val VENUES_BIG_IAMGE_ACTIVITY = "/venuesModule/VenuesBigImageActivity"

            /**
             * 活动室详情
             */
            const val ACTIVITY_ROOM_DETAIL = "/venuesModule/ActivityRoomDetailActivity"


            /**
             * 活动室预订
             */
            const val ACTIVITY_ROOM_ORDER = "/venuesModule/ActivityRoomOrderActivity"

            /**
             * 核销列表
             */
            const val VENUE_WRITER_OFF_ACTIIVTY = "/venuesModule/VenueWriterOffActivity"

            /**
             * 选择不核销的用户页面
             */
            const val NO_WRITER_OFF_USER_ACTIVITY = "/venuesModule/NoWriterOffUserActivity"

            const val VENUE_APPOINT_USER_ACTIVITY = "/venuesModule/AppointUserActivity"
        }
    }

    /**
     *  service 服务module
     */
    class ServiceModule {
        companion object {
            /**
             * 一键报警
             */
            const val SERVICE_SOS_ACTIVITY = "/serviceModule/ServiceSosActivity"

            /**
             *导游查询
             */
            const val SERVICE_TOUR_GUIDE_ACTIVITY = "/serviceModule/ServiceTourGuideActivity"

            /**
             *导游列表
             */
            const val SERVICE_TOUR_GUIDE_LIST_ACTIVITY = "/serviceModule/GuideTourListActivity"

            /**
             *旅行社
             */
            const val SERVICE_TRAVEL_AGENCY_LIST_ACTIVITY =
                "/serviceModule/TravelAgencyListActivity"

            /**
             *公交查询
             */
            const val SERVICE_QUERY_BUS_ACTIVITY = "/serviceModule/QueryBusActivity"

            /**
             *附近公交
             */
            const val SERVICE_NEAR_BUS_ACTIVITY = "/serviceModule/NearBusActivity"

            /**
             *公交线路列表
             */
            const val SERVICE_BUS_LINE_ACTIVITY = "/serviceModule/BusLineActivity"

            /**
             *公交线路详情列表
             */
            const val SERVICE_BUS_LINE_DETAIL_ACTIVITY = "/serviceModule/BusLineDetailActivity"

            /**
             *火车查询
             */
            const val SERVICE_QUERY_TRAIN_ACTIVITY = "/serviceModule/QueryTrainActivity"

            /**
             *城市选择
             */
            const val SERVICE_CHOOSE_CITY_ACTIVITY = "/serviceModule/ChooseCityActivity"

            /**
             *火车列表
             */
            const val SERVICE_TRAIN_LIST_ACTIVITY = "/serviceModule/TrainListActivity"

            /**
             *火车列表详情
             */
            const val SERVICE_TRAIN_DETAIL_ACTIVITY = "/serviceModule/TrainDetailActivity"

            /**
             *汽车列表
             */
            const val SERVICE_SUBWAY_LIST_ACTIVITY = "/serviceModule/SubwayListActivity"

            /**
             *机票列表
             */
            const val SERVICE_PLANE_LIST_ACTIVITY = "/serviceModule/PlaneListActivity"

            /**
             *天气查询和查艺考
             */
            const val SERVICE_WEATHER_QUERY_ACTIVITY = "/serviceModule/WeatherQueryActivity"

            /**
             *艺术基金
             */
            const val SERVICE_ART_FOUND_ACTIVITY = "/serviceModule/ArtFoundActivity"

            /**
             * 文章子栏目列表
             */
            const val CONTENT_SUB_ACITIVTY = "/serviceModule/ContentSubActivity"

            /**
             *艺术基金详情
             *
             */
            const val SERVICE_ART_DETAIL_ACTIVITY = "/serviceModule/ArtFoundDetailActivity"

            /**
             *  服务
             *
             */
            const val SERVICE_ACTIVITY = "/serviceModule/ServiceActivity"
            const val SERVICE_XJ_ACTIVITY = "/serviceModule/XinJiangServiceActivity"
        }
    }

    /**
     * 导游导览
     */
    class GuideModule {
        companion object {

            /**
             *  景区列表
             *
             */
            const val GUIDE_SCENIC_LIST_ACTIVITY = "/guideModule/GuideScenicListActivity"

            /**
             *  导览地图
             *
             */
            const val GUIDE_TOUR_ACTIVITY = "/guideModule/GuideTourMapActivity"

            /**
             *  预览及导览模式
             *
             */
            const val GUIDE_TOUR_MODE_ACTIVITY = "/guideModule/GuideTourModeActivity"

            /**
             *  搜索
             *
             */
            const val GUIDE_SEARCH_ACTIVITY = "/guideModule/GuideSearchActivity"

        }
    }


    /**
     * 慢直播
     */
    class SlowLiveModule {
        companion object {

            /**
             *  慢直播列表
             *
             */
            const val SLOW_LIVE_LIST_ACTIVITY = "/slowLiveModule/slowLiveListActivity"

            /**
             *  慢直播详情
             *
             */
            const val SLOW_LIVE_DETAIL_ACTIVITY = "/slowLiveModule/slowLiveDetailActivity"


            /**
             *  慢直播内容
             *
             */
            const val SLOW_LIVE_CONTENT_ACTIVITY = "/slowLiveModule/slowLiveContentActivity"
            /**
             *  慢直播播放
             *
             */
            const val SLOW_LIVE_PLAY_ACTIVITY = "/slowLiveModule/slowLivePlayActivity"
        }
    }


    /**
     * 品非遗
     */
    class LegacyModule {
        companion object {

            /**
             *  品非遗首页
             *
             */
            const val LEGACY_HOME_ACTIVITY = "/legacyModule/legacyHomeActivity"

            /**
             *  非遗传承
             */
            const val LEGACY_Smrity_ACTIVITY = "/legacyModule/LegacySmritiActivity"

            /**
             * 非遗美食
             */
            const val LEGACY_FOOD_LIST_ACTIVITY = "/legacyModule/LegacyFoodListActivity"

            /**
             * 非遗商品
             */
            const val LEGACY_PRODUCT_LIST_ACTIVITY = "/legacyModule/LegacyProductListActivity"


            /**
             * 非遗商品
             */
            const val LEGACY_WORK_LIST_ACTIVITY = "/legacyModule/LegacyWorksListActivity"

            /**
             *  视听非遗
             *
             */
            const val LEGACY_MEDIA_ACTIVITY = "/legacyModule/legacyMediaActivity"


            /**
             *  视听资讯
             *
             */
            const val LEGACY_NEWS_ACTIVITY = "/legacyModule/LegacyHomeActivity"

            /**
             *  非遗故事
             *
             */
            const val LEGACY_STORY_ACTIVITY = "/legacyModule/legacyStoryActivity"

            /**
             *  非遗项目详情
             *
             */
            const val LEGACY_Smrity_DETAIL_ACTIVITY = "/legacyModule/LegacySmritiDetailActivity"

            /**
             *  非遗传承人详情
             *
             */
            const val LEGACY_PEOPLE_DETAIL_ACTIVITY = "/legacyModule/LegacyPeopleDetailActivity"

            /**
             *  非遗体验基地详情
             *
             */
            const val LEGACY_EXPERIENCE_DETAIL_ACTIVITY =
                "/legacyModule/LegacyExperienceBaseDetailActivity"

            /**
             * 我的非遗
             */
            const val LEGACY_MINE = "/legacyModule/MineLegacyActivity"

            /**
             * 我的非遗
             */
            const val LEGACY_MINE_WORKS = "/legacyModule/MineWorksListActivity"

            /**
             * 我的粉丝
             */
            const val LEGACY_MINE_FANS = "/legacyModule/MineFansListActivity"

            /**
             * 发布作品
             */
            const val LEGACY_PUBLISH_WORKS = "/legacyModule/PublishWorksActivity"

            /**
             * 作品详情
             */
            const val LEGACY_WORKS_DETAIL = "/legacyModule/WorksDetailActivity"

            /**
             * 非遗项目对应故事列表
             */
            const val LEGACY_PROJECT_STORY = "/legacyModule/AllIntangibleHeritageStories"
        }
    }

    /**
     *乡村游
     */
    class CountryModule {
        companion object {
            /**
             * 乡村游主页
             */
            const val COUNTRY_TOUR_LIST = "/country/CountryTourActivity"

            /**
             * 农家乐列表
             */
            const val COUNTRY_HAPPINESS_LIST = "/country/CountryHappinessActivity"

            /**
             * 农家乐详情
             */
            const val COUNTRY_HAPPINESS_DETAIL = "/country/CountryHapDetailActivity"

            /**
             *住宿列表
             */
            const val COUNTRY_HOTEL_LIST = "/country/CountryHotelMoreActivity"

            /**
             *资讯列表
             */
            const val COUNTRY_CONTENT_ACTIVITY = "/country/CountryContentActivity"

            /**
             *乡村旅游名片
             */
            const val COUNTRY_CITY_LIST_ACTIVITY = "/country/CountryCityListActivity"

            /**
             * 全部乡村
             */
            const val COUNTRY_ALL_MORE_ACTIVITY = "/country/CountryAllMoreActivity"

            /**
             * 乡村详情
             */
            const val COUNTRY_COUNTRY_DETAIL_ACTIVITY = "/country/CountryDetailActivity"

            /**
             * 乡村介绍
             */
            const val COUNTRY_DETAIL_MORE_ACTIVITY = "/country/CountryDetailMoreActivity"

            /**
             * 景观点详情
             */
            const val COUNTRY_SCENIC_SPOT_ACTIVITY = "/homeModule/CountryScenicSpotActivity"
        }
    }

    /**
     *网红打卡
     */
    class OnLineClickModule {
        companion object {
            /**
             * 网红打卡列表
             */
            const val ONLINE_CLICK_LIST = "/onLineClick/OnLineClickListActivity"


        }
    }

    /**
     * 志愿者
     */
    class VolunteerModule {
        companion object {
            /**
             * 志愿者首页
             */
            const val VOLUNTEER_INDEX = "/volunteerModule/VolunteerMainActivity"

            /**
             * 志愿者注册
             */
            const val VOLUNTEER_REGISTER = "/volunteerModule/VolunteerRegisterActivity"

            /**
             * 志愿者团队注册
             */
            const val VOLUNTEER_TEAM_REGISTER = "/volunteerModule/VolunteerTeamRegisterActivity"

            /**
             * 志愿者列表
             */
            const val VOLUNTEER_LIST = "/volunteerModule/VolunteerListActivity"

            /**
             * 志愿者排行榜
             */
            const val VOLUNTEER_RANK_LIST = "/volunteerModule/VolunteerRankListActivity"

            /**
             * 志愿招募
             */
            const val VOLUNTEER_ACTIVITY_LIST = "/volunteerModule/VolunteerActivityListActivity"

            /**
             * 志愿者详情
             */
            const val VOLUNTEER_DETAIL = "/volunteerModule/VolunteerDetailActivity"

            /**
             * 志愿者团队详情
             */
            const val VOLUNTEER_TEAM_DETAIL = "/volunteerModule/VolunteerTeamDetailActivity"

            /**
             * 志愿者风采列表
             */
            const val VOLUNTEER_SERVICE_RECORD_LIST = "/volunteerModule/VolunteerServiceRecordListActivity"

            /**
             * 志愿者风采列表 首页进入
             */
            const val VOLUNTEER_FENG_CAI_LIST = "/volunteerModule/VolunteerFengCaiListActivity"

            /**
             * 志愿品牌
             */
            const val VOLUNTEER_BRAND_LIST = "/volunteerModule/VolunteerBrandListActivity"

            /**
             * 志愿者品牌详情
             */
            const val VOLUNTEER_BRAND_DETAIL = "/volunteerModule/VolunteerBrandDetailActivity"

            /**
             * 我的关注
             */
            const val VOLUNTEER_FOCUS_LIST = "/volunteerModule/VolunteerFocusListActivity"

            /**
             * 团队成员
             */
            const val VOLUNTEER_TEAM_MEMBERS = "/volunteerModule/VolunteerTeamMemberActivity"

            /**
             * 团队成员
             */
            const val VOLUNTEER_TEAM_LIST = "/volunteerModule/VolunteerTeamListActivity"

            /**
             * 志愿签到
             */
            const val VOLUNTEER_SIGN_LIST = "/volunteerModule/VolunteerTeamSignListActivity"

            /**
             * 搜索地点
             */
            const val ADDRESS_SEARCH = "/volunteerModule/AddressActivity"

            /**
             * 志愿者注册结果界面
             */
            const val APPLY_SUCCESS_RESULT = "/volunteerModule/VolunteerRegisterSuccessActivity"

            /**
             * 志愿者申请详情
             */
            const val VOLUNTEER_APPLY_DETAIL = "/volunteerModule/VolunteerApplyDetailActivity"

            /**
             * 志愿者团队申请详情
             */
            const val VOLUNTEER_TEAM_APPLY_DETAIL = "/volunteerModule/VolunteerTeamApplyDetailActivity"

            /**
             * 志愿服务记录详情
             */
            const val VOLUNTEER_SERVICE_RECORD = "/volunteerModule/VolunteerServiceRecordDetail"

            /**
             * 服务签到
             */
            const val VOLUNTEER_SERVICE_SIGN = "/volunteerModule/VolunteerTeamSignActivity"

            /**
             * 志愿者中心
             */
            const val VOLUNTEER_CENTER = "/volunteerModule/VolunteerCenterActivity"

            /**
             * 志愿者基本信息
             */
            const val VOLUNTEER_INFORMATION = "/volunteerModule/VolunteerInformationActivity"

            /**
             * 修改志愿者紧急联系人
             */
            const val VOLUNTEER_UPDATE_CONTACT = "/volunteerModule/VolunteerUpdateContactActivity"

            /**
             * 修改信息
             */
            const val VOLUNTEER_UPDATE_INFORMATION = "/volunteerModule/VolunteerUpdateInformationActivity"

            /**
             * 志愿者证
             */
            const val VOLUNTEER_CARD = "/volunteerModule/VolunteerCardActivity"

            /**
             * 志愿服务入口
             */
            const val VOLUNTEER_SERVICES_IN = "/volunteerModule/VolunteerServicesInActivity"

            /**
             * 审核记录
             */
            const val VOLUNTEER_AUDIT_LOG = "/volunteerModule/VolunteerAuditLogActivity"
        }
    }

    /**
     * 投票
     */
    class VoteModule {

        companion object {
            /**
             * 投票列表
             */
            const val VOTE_LS = "/voteModule/VoteLsActivity"

            /**
             * 投票详情
             */
            const val VOTE_DETAIL = "/voteModule/VoteDetailActiivty"

            /**
             * 投票详情1
             */
            const val VOTE_DETAIL_NEW = "/voteModule/VoteDetailNoStartActiivty"
            /**
             * 投票搜索
             */
            const val VOTE_SEARCH = "/voteModule/VoteSearchActivity"

            /**
             * 我的参与
             */
            const val VOTE_INPART = "/voteModule/VoteInpartActivity"

            /**
             * 我的参与详情
             */
            const val VOTE_MINE_WORK_DETAIL = "/voteModule/MineVoteDetailActivity"

            /**
             * 我的投票
             */
            const val MINE_VOTE = "/voteModule/MineVoteLsActivity"

            /**
             * 我的参与 作品列表
             */
            const val MINE_VOTE_WORK_LIST = "/voteModule/MineVoteWorkListActivity"
        }

    }

}
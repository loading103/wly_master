package com.daqsoft.provider.network.vote

import retrofit2.http.DELETE

/**
 * @Description 投票常量
 * @ClassName   VoteConstant
 * @Author      luoyi
 * @Time        2020/11/12 17:09
 */
object VoteConstant {

    /**
     * 投票状态
     */
    object STATUS {
        /**
         * 未开始
         */
        const val UN_START = 0

        /**
         * 进行中
         */
        const val PROGRESS_ING = 1

        /**
         * 已结束
         */
        const val END = 2
    }

    /**
     * 操作状态
     */
    object OPERATION_STATUS {
        /**
         * 删除
         */
        const val DELETE = "0"

        /**
         * 撤回
         */
        const val BACK = "8"
    }

    /**
     * 投票类型
     */
    object TYPE {
        /**
         * 用户上传
         */
        const val USER = "USER"

        /**
         * 平台发布
         */
        const val MANAGE = "MANAGE"
    }

    /**
     * 投票按钮状态
     */
    object VoteButtonStatus {
        /**
         * 未开始
         */
        const val UN_START = 0

        /**
         * 可以投票
         */
        const val CAN_VOTE = 1

        /**
         * 已投票
         */
        const val HAVED_VOTE = 2

        /**
         * 已结束
         */
        const val END = 3

        /**
         * 已达到限制
         */
        const val HAVED_limit = 4

    }

    /**
     * 是否必填
     */
    object RequiedItem {
        /**
         * 名称
         */
        const val PRO_NAME: String = "PRO_NAME"

        /**
         * 作者
         */
        const val PRO_AUTHOR: String = "PRO_AUTHOR"

        /**
         * 简介
         */
        const val PRO_INTRO: String = "PRO_INTRO"

        /**
         * 身份证
         */
        const val PRO_ID_CARD: String = "PRO_IDCARD"

        /**
         * 手机号
         */
        const val PRO_PHONE: String = "PRO_PHONE"
    }
}
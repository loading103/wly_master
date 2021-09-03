package com.daqsoft.provider.bean

/**
 * 文件上传实体类
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-7-29
 * @since JDK 1.8.0_191
 */
data class UploadBean(
        /**
         * 消息
         */
        val messages: String?,
        /**
         * 图片
         */
        val fileUrl: String?,
        /**
         * 错误消息
         */
        val error: String,
        /**
         * 路径
         */
        val url: String
)
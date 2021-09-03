package com.daqsoft.provider.bean

/**
 * @ClassName    QRCodeBean
 * @Description  二维码
 * @Author       yuxc
 * @CreateDate   2020/12/4
 */
data class QRCodeBean(
    var isWechatAuth: Boolean?,
    var name: Any?,
    var paramQrcode: Any?,
    var qrcode: Any?,
    var state: Any?
)
package com.daqsoft.legacyModule.media.bean

import cc.shinichi.library.tool.file.FileUtil
import com.daqsoft.legacyModule.net.logI
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken


data class LegacyMediaListBean(
    var startTime: String = "00:00",
    var endTime: String = "00:00",
    var playStatus: Boolean = false,
    var progress: Int = 0,
    var audioInfo: JsonElement? = null,
    var videoInfo: JsonElement? = null,
    var name: String = "", // 体验基地
    var resourceId: String = "", // 3
    var state: Int = 0,
    var duration: Int = 0,
    var resourceType: String = ""// content_type_heritage_experience_base

) {

    fun getAudioInfo(): List<AudioInfo>? {
        logI("getAudioInfo type  ${audioInfo?.javaClass}  $audioInfo")
        return try {
            return Gson().fromJson(audioInfo, object : TypeToken<List<AudioInfo>>() {}.type)
        } catch (e: Exception) {
           null
        }
    }


    fun getVideoInfo(): VideoInfo {
        logI("getVideoInfo type  ${videoInfo?.javaClass}   $videoInfo")
        return try {
            Gson().fromJson(videoInfo, VideoInfo::class.java)
        } catch (e: Exception) {
            VideoInfo()
        }
    }


    data class AudioInfo(
        var audio: String = "", // http://file.geeker.com.cn/uploadfile/cultural-tourism-cloud/1565231187966/金莎 - 被风吹过的夏天.mp3
        var time: String = "" // 10:00
    ) {

        fun getAudioUrl(): String {
            if (!audio.isNullOrEmpty()) {
                if (audio.contains(",")) {
                    val audioList = audio.split(",")
                    return audioList[0]
                }
            }

            return audio
        }

        fun getFileName(): String {
            return FileUtil.getFileNameNoExtension(audio)
        }
    }

    data class VideoInfo(
        var cover: String = "",
        var url: String = "",
        var time: String = "" // 10:00
    ) {
        fun getFileName(): String {
            return FileUtil.getFileNameNoExtension(url)
        }
    }
}

data class LegacyAudioBean(
    var startTime: String = "00:00",
    var endTime: String = "00:00",
    var playStatus: Boolean = false,
    var progress: Int = 0,
    var name: String = "", // 体验基地
    var resourceId: String = "", // 3
    var state: Int = 0,
    var duration: Int = 0,
    var resourceType: String = "",
    var audioInfo:LegacyMediaListBean.AudioInfo? = null
)


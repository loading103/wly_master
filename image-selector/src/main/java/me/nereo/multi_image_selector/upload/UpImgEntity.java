package me.nereo.multi_image_selector.upload;

/**
 * 上传图片
 *
 * @author 严博
 * @version 1.0.0
 * @date 2018-7-27.12:30
 * @since JDK 1.8
 */

public class UpImgEntity {

    /**
     * messages : 上传成功！
     * fileUrl : http://file.geeker.com.cn/uploadfile/SmartScenicmanas/1532663417898/.png
     * error : 0
     * message : 200
     * url : http://file.geeker.com.cn/uploadfile/SmartScenicmanas/1532663417898/.png
     */
    /**
     * 消息
     */
    private String messages;
    /**
     * 图片
     */
    private String fileUrl;
    /**
     * 错误消息
     */
    private int error;
    /**
     * 消息
     */
    private String message;
    /**
     * 图片
     */
    private String url;

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

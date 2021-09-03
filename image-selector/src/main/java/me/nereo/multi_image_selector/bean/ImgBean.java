package me.nereo.multi_image_selector.bean;

/**
 * 图片上传成功之后的返回实体类
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2018/10/27 0027
 * @since JDK 1.8
 */
public class ImgBean {
    private String fileUrl;
    private String url;
    private String messages;
    private int error;
    private String message;
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
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
}

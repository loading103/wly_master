package me.nereo.multi_image_selector.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片实体（文件实体）
 * Created by Nereo on 2015/4/7.
 */
public class Image implements Parcelable{
    /**
     * 为本地地址
     */
    public String path;
    /**
     * 为网络地址
     */
    public String url;
    public String name;
    public long time;
    public long id;
    public int mediaType;
    public int size;
    public int type;
    /**
     * 200 为上传成功
     */
    public int status;

    protected Image(Parcel in) {
        path = in.readString();
        url = in.readString();
        name = in.readString();
        time = in.readLong();
        id = in.readLong();
        thumPath = in.readString();
        mediaType = in.readInt();
        size = in.readInt();
        status = in.readInt();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeLong(time);
        dest.writeLong(id);
        dest.writeString(thumPath);
        dest.writeInt(mediaType);
        dest.writeInt(size);
        dest.writeInt(status);
        dest.writeInt(type);
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public String getThumPath() {
        return thumPath;
    }

    public void setThumPath(String thumPath) {
        this.thumPath = thumPath;
    }

    public String thumPath;

    public Image(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }



    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}

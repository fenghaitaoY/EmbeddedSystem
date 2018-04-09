package com.android.blue.smarthomefunc.model;

import com.google.gson.annotations.SerializedName;

/**
 * 歌手歌曲列表
 * Created by fht on 4/7/18.
 */

public class SingerArtistMusic {
    @SerializedName("publishtime")
    private String publishtime;
    @SerializedName("pic_big")
    private String pic_big;
    @SerializedName("country")
    private String country;
    @SerializedName("lrclink")
    private String lrclink;
    @SerializedName("file_duration")
    private String file_duration;
    @SerializedName("song_id")
    private String song_id;
    @SerializedName("title")
    private String title;
    @SerializedName("ting_uid")
    private String ting_uid;
    @SerializedName("author")
    private String author;
    @SerializedName("album_id")
    private String album_id;
    @SerializedName("album_title")
    private String album_title;
    @SerializedName("pic_radio")
    private String pic_radio;
    @SerializedName("pic_s500")
    private String pic_s500;
    @SerializedName("pic_huge")
    private String pic_huge;
    @SerializedName("album_500_500")
    private String album_500_500;
    @SerializedName("album_1000_1000")
    private String album_1000_1000;

    public String getPublishtime() {
        return publishtime;
    }

    public String getPic_big() {
        return pic_big;
    }

    public String getCountry() {
        return country;
    }

    public String getLrclink() {
        return lrclink;
    }

    public String getFile_duration() {
        return file_duration;
    }

    public String getSong_id() {
        return song_id;
    }

    public String getTitle() {
        return title;
    }

    public String getTing_uid() {
        return ting_uid;
    }

    public String getAuthor() {
        return author;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public String getPic_radio() {
        return pic_radio;
    }

    public String getPic_s500() {
        return pic_s500;
    }

    public String getPic_huge() {
        return pic_huge;
    }

    public String getAlbum_500_500() {
        return album_500_500;
    }

    public String getAlbum_1000_1000() {
        return album_1000_1000;
    }
}

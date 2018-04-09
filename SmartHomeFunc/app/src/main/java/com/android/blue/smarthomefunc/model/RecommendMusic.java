package com.android.blue.smarthomefunc.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fht on 4/5/18.
 */

public class RecommendMusic {

    @SerializedName("title")
    private String title;
    @SerializedName("album_title")
    private String albumTitle;
    @SerializedName("album_id")
    private String albumId;
    @SerializedName("pic_small")
    private String pic_small;
    @SerializedName("pic_big")
    private String pic_big;
    @SerializedName("artist_id")
    private String artistId;
    @SerializedName("publishtime")
    private String publishTime;
    @SerializedName("author")
    private String author;
    @SerializedName("ting_uid")
    private String ting_uid;

    public String getTitle() {
        return title;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getPic_small() {
        return pic_small;
    }

    public String getPic_big() {
        return pic_big;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getAuthor() {
        return author;
    }

    public String getTing_uid() {
        return ting_uid;
    }
}

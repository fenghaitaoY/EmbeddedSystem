package com.android.blue.smarthomefunc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 歌手列表 List SINGER
 * Created by fht on 4/7/18.
 */

public class SingerLIst {
    @SerializedName("artist")
    private List<Singer> singers;

    public List<Singer> getSingers() {
        return singers;
    }

    public static class Singer implements Serializable{
        @SerializedName("ting_uid")
        private String ting_uid;
        @SerializedName("name")
        private String name;
        @SerializedName("firstchar")
        private String firstchar;
        @SerializedName("country")
        private String country;
        @SerializedName("avatar_big")
        private String avatar_big;
        @SerializedName("avatar_middle")
        private String avatar_middle;
        @SerializedName("avatar_small")
        private String avatar_small;
        @SerializedName("albums_total")
        private String albums_total;
        @SerializedName("songs_total")
        private String songs_total;

        public String getTing_uid() {
            return ting_uid;
        }

        public String getName() {
            return name;
        }

        public String getFirstchar() {
            return firstchar;
        }

        public String getCountry() {
            return country;
        }

        public String getAvatar_big() {
            return avatar_big;
        }

        public String getAvatar_middle() {
            return avatar_middle;
        }

        public String getAvatar_small() {
            return avatar_small;
        }

        public String getAlbums_total() {
            return albums_total;
        }

        public String getSongs_total() {
            return songs_total;
        }
    }

}

package com.android.blue.smarthomefunc.model;


import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 1/25/18.
 */

public class DownloadInfo {
    @SerializedName("bitrate")
    private Bitrate bitrate;
    @SerializedName("songinfo")
    private Songinfo songinfo;

    public Bitrate getBitrate() {
        return bitrate;
    }

    public Songinfo getSonginfo(){return songinfo;}

    public void setBitrate(Bitrate bitrate) {
        this.bitrate = bitrate;
    }

    public static class Bitrate {
        @SerializedName("file_duration")
        private int file_duration;
        @SerializedName("file_link")
        private String file_link;

        public int getFile_duration() {
            return file_duration;
        }

        public void setFile_duration(int file_duration) {
            this.file_duration = file_duration;
        }

        public String getFile_link() {
            return file_link;
        }

        public void setFile_link(String file_link) {
            this.file_link = file_link;
        }
    }


    public static class Songinfo{
        @SerializedName("title")
        private String title;

        @SerializedName("lrclink")
        private String lrc;

        @SerializedName("author")
        private String album;

        @SerializedName("pic_big")
        private String pic_big;

        @SerializedName("pic_small")
        private String pic_small;

        public String getTitle() {
            return title;
        }

        public String getLrc() {
            return lrc;
        }

        public String getAlbum() {
            return album;
        }

        public String getPic_big() {
            return pic_big;
        }

        public String getPic_small() {
            return pic_small;
        }
    }
}

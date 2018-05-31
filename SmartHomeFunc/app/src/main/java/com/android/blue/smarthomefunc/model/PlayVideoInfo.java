package com.android.blue.smarthomefunc.model;

import java.util.List;

/**
 * 解析无敌网播放页信息
 * Created by fht on 5/24/18.
 */

public class PlayVideoInfo {
    //正在播放使用的链接
    public String playVideoLink;
    public List<PlayVideoList> playVideoLists;

    public String getPlayVideoLink() {
        return playVideoLink;
    }

    public void setPlayVideoLink(String playVideoLink) {
        this.playVideoLink = playVideoLink;
    }

    public List<PlayVideoList> getPlayVideoLists() {
        return playVideoLists;
    }

    public void setPlayVideoLists(List<PlayVideoList> playVideoLists) {
        this.playVideoLists = playVideoLists;
    }

    public static class PlayVideoList {
        public String videoTitle;
        public String videoLink;

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        public String getVideoLink() {
            return videoLink;
        }

        public void setVideoLink(String videoLink) {
            this.videoLink = videoLink;
        }
    }
}

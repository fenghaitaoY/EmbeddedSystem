package com.android.blue.smarthomefunc.model;

import java.util.List;

/**
 * 解析无敌网 选中网页信息
 * Created by fht on 5/24/18.
 */

public class VideoSelectInfo {
    public String selectVideoName;
    public String selectVideoLink;
    public String selectVideoImage;
    public String selectVideoUpdateToast;
    public List<SelectVideos> selectVideoLists;

    public String getSelectVideoName() {
        return selectVideoName;
    }

    public void setSelectVideoName(String selectVideoName) {
        this.selectVideoName = selectVideoName;
    }

    public String getSelectVideoLink() {
        return selectVideoLink;
    }

    public void setSelectVideoLink(String selectVideoLink) {
        this.selectVideoLink = selectVideoLink;
    }

    public String getSelectVideoImage() {
        return selectVideoImage;
    }

    public void setSelectVideoImage(String selectVideoImage) {
        this.selectVideoImage = selectVideoImage;
    }

    public String getSelectVideoUpdateToast() {
        return selectVideoUpdateToast;
    }

    public void setSelectVideoUpdateToast(String selectVideoUpdateToast) {
        this.selectVideoUpdateToast = selectVideoUpdateToast;
    }

    public List<SelectVideos> getSelectVideoLists() {
        return selectVideoLists;
    }

    public void setSelectVideoLists(List<SelectVideos> selectVideoLists) {
        this.selectVideoLists = selectVideoLists;
    }

    public static class SelectVideos {
        public String videoListTitle;
        public String videoListLink;

        public String getVideoListTitle() {
            return videoListTitle;
        }

        public void setVideoListTitle(String videoListTitle) {
            this.videoListTitle = videoListTitle;
        }

        public String getVideoListLink() {
            return videoListLink;
        }

        public void setVideoListLink(String videoListLink) {
            this.videoListLink = videoListLink;
        }
    }


}

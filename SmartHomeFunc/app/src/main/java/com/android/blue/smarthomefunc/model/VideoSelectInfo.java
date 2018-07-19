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

    public String videoYears;
    public String videoArea;
    public String videoType;
    public String videoIntro;

    public List<SelectVideos> selectVideoLists;

    public List<RecommendVideo> getRecommendVideoList() {
        return recommendVideoList;
    }

    public void setRecommendVideoList(List<RecommendVideo> recommendVideoList) {
        this.recommendVideoList = recommendVideoList;
    }

    public List<RecommendVideo> recommendVideoList;


    public String getVideoIntro() {
        return videoIntro;
    }

    public void setVideoIntro(String videoIntro) {
        this.videoIntro = videoIntro;
    }

    public String getVideoYears() {
        return videoYears;
    }

    public void setVideoYears(String videoYears) {
        this.videoYears = videoYears;
    }

    public String getVideoArea() {
        return videoArea;
    }

    public void setVideoArea(String videoArea) {
        this.videoArea = videoArea;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }



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

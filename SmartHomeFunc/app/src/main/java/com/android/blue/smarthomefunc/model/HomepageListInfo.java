package com.android.blue.smarthomefunc.model;

import com.android.blue.smarthomefunc.enums.HomepageTypeEnum;

import java.util.List;

/**
 * 解析无敌网主页 列表部分
 * Created by fht on 5/24/18.
 */

public class HomepageListInfo {
    public String heardTitle;
    public String more;
    public String moreLink;

    public String videoName;
    public String videoTag;
    public String videoImage;
    public String videoLink;

    public int type;

    public String getHeardTitle() {
        return heardTitle;
    }

    public void setHeardTitle(String heardTitle) {
        this.heardTitle = heardTitle;
    }

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public String getMoreLink() {
        return moreLink;
    }

    public void setMoreLink(String moreLink) {
        this.moreLink = moreLink;
    }


    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoTag() {
        return videoTag;
    }

    public void setVideoTag(String videoTag) {
        this.videoTag = videoTag;
    }

    public String getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public int getType(){
        return type;
    }

    public void setType(int type){
        this.type = type;
    }
}

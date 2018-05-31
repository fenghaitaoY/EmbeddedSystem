package com.android.blue.smarthomefunc.model;

import java.util.List;

/**
 * 解析无敌网主页 列表部分
 * Created by fht on 5/24/18.
 */

public class HomepageListInfo {
    public String heardTitle;
    public String more;
    public String moreLink;
    public List<HomepageVideoList> videoLists;

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

    public List<HomepageVideoList> getVideoLists() {
        return videoLists;
    }

    public void setVideoLists(List<HomepageVideoList> videoLists) {
        this.videoLists = videoLists;
    }




    public class HomepageVideoList{
        public String videoName;
        public String videoTag;
        public String videoImage;
        public String videoLink;

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
    }


}

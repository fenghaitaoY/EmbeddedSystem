package com.android.blue.smarthomefunc.model;

/**
 * Created by fht on 7/19/18.
 */

public class RecommendVideo {
    public String recVideoTitle;
    public String recVideoLink;
    public String recVideoCover;
    public String recVideoScore;

    public String getRecVideoTitle() {
        return recVideoTitle;
    }

    public void setRecVideoTitle(String recVideoTitle) {
        this.recVideoTitle = recVideoTitle;
    }

    public String getRecVideoLink() {
        return recVideoLink;
    }

    public void setRecVideoLink(String recVideoLink) {
        this.recVideoLink = recVideoLink;
    }

    public String getRecVideoCover() {
        return recVideoCover;
    }

    public void setRecVideoCover(String recVideoCover) {
        this.recVideoCover = recVideoCover;
    }

    public String getRecVideoScore() {
        return recVideoScore;
    }

    public void setRecVideoScore(String recVideoScore) {
        this.recVideoScore = recVideoScore;
    }

    @Override
    public String toString() {
        return "title :"+recVideoTitle+" , cover : "+recVideoCover+" , link :"+recVideoLink+",  score :"+ recVideoScore;
    }
}

package com.android.blue.smarthomefunc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 解析获得推荐列表
 * 根据传入的song_id 返回列表
 * Created by fht on 4/5/18.
 */

public class ResultRecommendList {
    @SerializedName("result")
    private RecommendList list;

    public RecommendList getList() {
        return list;
    }

    public static class RecommendList{
        @SerializedName("list")
        private List<RecommendMusic> recList;

        public List<RecommendMusic> getRecommendList() {
            return recList;
        }
    }
}

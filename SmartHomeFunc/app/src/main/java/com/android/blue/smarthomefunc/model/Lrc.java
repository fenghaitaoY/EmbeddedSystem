package com.android.blue.smarthomefunc.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 1/26/18.
 */

public class Lrc {
    @SerializedName("lrcContent")
    private String lrcContent;

    public String getLrcContent(){
        return lrcContent;
    }

    public void setLrcContent(String lrcContent){
        this.lrcContent=lrcContent;
    }
}

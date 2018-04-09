package com.android.blue.smarthomefunc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 歌手歌曲列表List
 * Created by fht on 4/7/18.
 */

public class SingerArtistMusicList {
    @SerializedName("songlist")
    private List<SingerArtistMusic> list;

    public List<SingerArtistMusic> getSingerArtistMusicList() {
        return list;
    }
}

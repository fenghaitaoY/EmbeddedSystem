package com.android.blue.smarthomefunc.http;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.ArtistInfo;
import com.android.blue.smarthomefunc.model.DownloadInfo;
import com.android.blue.smarthomefunc.model.Lrc;
import com.android.blue.smarthomefunc.model.OnlineMusicList;
import com.android.blue.smarthomefunc.model.ResultRecommendList;
import com.android.blue.smarthomefunc.model.SearchMusic;
import com.android.blue.smarthomefunc.model.SingerArtistMusicList;
import com.android.blue.smarthomefunc.model.SingerLIst;
import com.android.blue.smarthomefunc.model.Splash;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 *
 * Created by root on 1/29/18.
 */

public class HttpClient {
    private static final String SPLASH_URL = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
    private static final String BASE_URL ="http://tingapi.ting.baidu.com/v1/restserver/ting";
    private static final String METHOD_GET_MUSIC_LIST = "baidu.ting.billboard.billList";
    private static final String METHOD_DOWNLOAD_MUSIC ="baidu.ting.song.play"; //播放 playAAC
    private static final String METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo"; //获取歌手信息
    private static final String METHOD_SEARCH_MUSIC ="baidu.ting.search.catalogSug";//搜索
    private static final String METHOD_LRC = "baidu.ting.song.lry"; //lrc歌词
    private static final String METHOD_RECOMMAND_SONG_LIST ="baidu.ting.song.getRecommandSongList"; //推荐列表 song_id && num
    private static final String METHOD_ARTIST_MUSIC_LIST ="baidu.ting.artist.getSongList"; //获取歌手歌曲列表 tinguid－歌手ting id, limits-返回数量
    private static final String METHOD_HOT_ARTIST = "baidu.ting.artist.get72HotArtist"; //获得歌手列表
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_SONG_ID = "songid";
    private static final String PARAM_TING_UID = "tinguid";
    private static final String PARAM_QUERY = "query";
    private static final String PARAM_REC_SONG_ID ="song_id";
    private static final String PARAM_REC_NUM ="num";
    private static final String PARAM_LIMIT="limit";

    public static void getSplash(@NonNull final HttpCallback<Splash> callback){
        OkHttpUtils.get().url(SPLASH_URL).build().execute(new JsonCallback<Splash>(Splash.class) {
            @Override
            public void onError(Call call, Exception e, int id) {
                callback.onFail(e);
            }

            @Override
            public void onResponse(Splash response, int id) {
                callback.onSuccess(response);
            }

            @Override
            public void onAfter(int id) {
                callback.onFinish();
            }
        });
    }

    public static void downloadFile(String url, String destFileDir, String destFileName, @Nullable final HttpCallback<File> callback){
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(destFileDir, destFileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        LogUtils.i("downloadFile progress:"+progress+" , total="+total);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null){
                            callback.onFail(e);
                        }
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        if (callback != null){
                            callback.onSuccess(response);
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        if (callback != null){
                            callback.onFinish();
                        }
                    }
                });
    }

    public static void getSongListInfo(String type, int size, int offset, @NonNull final HttpCallback<OnlineMusicList> callback){
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_GET_MUSIC_LIST)
                .addParams(PARAM_TYPE, type)
                .addParams(PARAM_SIZE, String.valueOf(size))
                .addParams(PARAM_OFFSET, String.valueOf(offset))
                .build()
                .execute(new JsonCallback<OnlineMusicList>(OnlineMusicList.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(OnlineMusicList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }


    public static void getMusicDownloadInfo(String songId, @NonNull final HttpCallback<DownloadInfo> callback){
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_DOWNLOAD_MUSIC)
                .addParams(PARAM_SONG_ID, songId)
                .build()
                .execute(new JsonCallback<DownloadInfo>(DownloadInfo.class){
                    @Override
                    public void onResponse(DownloadInfo response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getBitmap(String url, @NonNull final HttpCallback<Bitmap> callback){
        OkHttpUtils.get().url(url).build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getLrc(String songId, @NonNull final HttpCallback<Lrc> callback){
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_LRC)
                .addParams(PARAM_SONG_ID, songId)
                .build()
                .execute(new JsonCallback<Lrc>(Lrc.class){
                    @Override
                    public void onResponse(Lrc response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void searchMusic(String keyword, @NonNull final HttpCallback<SearchMusic> callback){
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                .addParams(PARAM_QUERY, keyword)
                .build()
                .execute(new JsonCallback<SearchMusic>(SearchMusic.class){
                    @Override
                    public void onResponse(SearchMusic response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getArtistInfo(String tingUid, @NonNull final HttpCallback<ArtistInfo> callback){
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_ARTIST_INFO)
                .addParams(PARAM_TING_UID, tingUid)
                .build()
                .execute(new JsonCallback<ArtistInfo>(ArtistInfo.class){
                    @Override
                    public void onResponse(ArtistInfo response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    /**
     * 获得推荐列表
     * @param songid
     * @param num
     * @param callback
     */
    public static void getRecommendSongList(String songid, int num, @NonNull final HttpCallback<ResultRecommendList> callback){
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_RECOMMAND_SONG_LIST)
                .addParams(PARAM_REC_SONG_ID, songid)
                .addParams(PARAM_REC_NUM, String.valueOf(num))
                .build()
                .execute(new JsonCallback<ResultRecommendList>(ResultRecommendList.class){

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(ResultRecommendList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    /**
     * 获得歌手列表
     * @param offset
     * @param limit
     * @param callback
     */
    public static void getSingerList(int offset, int limit, @NonNull final HttpCallback<SingerLIst> callback){
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_HOT_ARTIST)
                .addParams(PARAM_OFFSET, String.valueOf(offset))
                .addParams(PARAM_LIMIT, String.valueOf(limit))
                .build()
                .execute(new JsonCallback<SingerLIst>(SingerLIst.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(SingerLIst response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    /**
     * 歌手歌曲列表
     * @param tingUid 7994
     * @param offset 0
     * @param limit 50
     * @param callback
     */
    public static void getSingerArtistMusicList(String tingUid, int offset, int limit, @NonNull final HttpCallback<SingerArtistMusicList> callback){
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_ARTIST_MUSIC_LIST)
                .addParams(PARAM_TING_UID, tingUid)
                .addParams(PARAM_OFFSET, String.valueOf(offset))
                .addParams(PARAM_LIMIT, String.valueOf(limit))
                .build()
                .execute(new JsonCallback<SingerArtistMusicList>(SingerArtistMusicList.class){

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(SingerArtistMusicList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
}

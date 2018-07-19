package com.android.blue.smarthomefunc.executor;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.enums.HomepageTypeEnum;
import com.android.blue.smarthomefunc.model.HomepageListInfo;
import com.android.blue.smarthomefunc.model.HomepageSlideInfo;
import com.android.blue.smarthomefunc.model.PlayVideoInfo;
import com.android.blue.smarthomefunc.model.RecommendVideo;
import com.android.blue.smarthomefunc.model.VideoSelectInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析网页类
 * Created by fht on 5/24/18.
 */

public class ParseWebPages {
    public static final String WUDI="http://m.wudiseo.com";
    public static final int HOMEPAGE_SLIDE=0;
    public static final int HOMEPAGE_VIDEOS=1;
    public static final int SELECT_VIDEO=2;
    public static final int PLAYING_VIDEO=3;


    public List<HomepageSlideInfo> slides = new ArrayList<>();
    public List<HomepageListInfo> homepageListInfos = new ArrayList<>();
    public VideoSelectInfo selectInfo;
    public PlayVideoInfo playVideoInfo;

    IParseWebPageNotify listener;
    private Handler mHandler;

    private ParseWebPages(){}
    private static ParseWebPages instance;
    public static ParseWebPages getInstance(){
        if (instance == null){
            synchronized (ParseWebPages.class){
                if (instance==null){
                    instance = new ParseWebPages();
                }
            }
        }
        return instance;
    }


    public void doParseSlideVideo(String url){
        LogUtils.i("url ="+url);
        doParseWebPageWork(HOMEPAGE_SLIDE, url);
        doParseWebPageWork(HOMEPAGE_VIDEOS, url);
    }

    public List<HomepageSlideInfo> getHomePageSlideList(){
        return slides;
    }

    public void doParseHomepageListVideo(String url){
        LogUtils.i("url ="+url);
        doParseWebPageWork(HOMEPAGE_VIDEOS, url);
    }

    public List<HomepageListInfo> getHomepageListInfos(){
        return homepageListInfos;
    }


    public void doParseSelectVideo(String url){
        LogUtils.i("url ="+url);
        doParseWebPageWork(SELECT_VIDEO, url);
    }

    public VideoSelectInfo getSelectVideoInfo(){
        return selectInfo;
    }

    public void doParsePlayVideo(String url){
        LogUtils.i("url ="+url);
        doParseWebPageWork(PLAYING_VIDEO, url);
    }

    public PlayVideoInfo getPlayVideoInfo(){
        return playVideoInfo;
    }



    public void setParseWebPagesCompleted(IParseWebPageNotify listener){
        mHandler = new Handler();
        this.listener = listener;
    }




    private void doParseWebPageWork(final int tag, final String url){

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(url).get();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    if (doc == null) return;

                    switch (tag){
                        case HOMEPAGE_SLIDE:
                            Elements slideElements = doc.select("div.item");
                            slides.clear();
                            for (Element e : slideElements){
                                LogUtils.i( "thread  alt ="+e.select("img").attr("alt")+" , href ="
                                        +e.select("a").attr("href")+" , src = "+e.select("img").attr("src"));
                                HomepageSlideInfo slideInfo = new HomepageSlideInfo();
                                slideInfo.setVideoName(e.select("img").attr("alt"));
                                slideInfo.setVideoImage(e.select("img").attr("src"));
                                slideInfo.setVideoLink(e.select("a").attr("href"));
                                if (!slides.contains(slideInfo)) {
                                    LogUtils.i(" -----------addd -----");
                                    slides.add(slideInfo);
                                }
                            }

                            break;
                        case HOMEPAGE_VIDEOS:

                            Elements homepageElements = doc.getElementsByClass("box-a");
                            homepageListInfos.clear();
                            for (Element a:homepageElements){
                                LogUtils.i( "title h3="+a.select("div.title-a").select("h3.title-h3").text()+", " +
                                        ", href="+a.select("div.title-a").select("a").attr("href")+" , more="+
                                        a.select("div.title-a").select("a").text());
                                HomepageListInfo listInfo = new HomepageListInfo();
                                listInfo.setHeardTitle(a.select("div.title-a").select("h3.title-h3").text());
                                listInfo.setMore(a.select("div.title-a").select("a").text());
                                listInfo.setMoreLink(a.select("div.title-a").select("a").attr("href"));


                                listInfo.type = 1;

                                if (!TextUtils.isEmpty(listInfo.getHeardTitle())) {
                                    LogUtils.i("   homepage list info add ");
                                    homepageListInfos.add(listInfo);
                                }


                                LogUtils.i( "-- size="+a.select("div.box-a-c").select("div.con").size());
                                int count = a.select("div.box-a-c").select("div.con").size();
                                for (int i=0; i<count; i++){
                                    LogUtils.i( "href="+a.select("div.box-a-c").select("div.con").get(i).select("a").attr("href")+
                                            " , src="+a.select("div.box-a-c").select("div.con").get(i).select("img").attr("data-src")+
                                            " , num="+a.select("div.box-a-c").select("div.con").get(i).select("span.sNum").text()
                                            +" , sTit="+a.select("div.box-a-c").select("div.con").get(i).select("span.sTit").text());
                                    HomepageListInfo contentList = new HomepageListInfo();
                                    contentList.setVideoName(a.select("div.box-a-c").select("div.con").get(i).select("span.sTit").text());
                                    contentList.setVideoImage(a.select("div.box-a-c").select("div.con").get(i).select("img").attr("data-src"));
                                    contentList.setVideoTag(a.select("div.box-a-c").select("div.con").get(i).select("span.sNum").text());
                                    contentList.setVideoLink(a.select("div.box-a-c").select("div.con").get(i).select("a").attr("href"));

                                    contentList.type=2;

                                    LogUtils.i("   homepage list info add ");
                                    homepageListInfos.add(contentList);
                                }
                            }

                            break;
                        case SELECT_VIDEO:
                            Elements videoElements = doc.select("div.posterPic");
                            LogUtils.i( " video href = "+videoElements.select("a").attr("href")+
                                    " , img ="+videoElements.select("img").attr("src")+
                                    " , alt="+videoElements.select("img").attr("alt"));

                            String updateStr = doc.select("div.detailPosterIntro").select("div.introTxt").select("span.sDes").text();
                            LogUtils.i( "update totast = "+updateStr);
                            selectInfo = new VideoSelectInfo();
                            selectInfo.setSelectVideoName(videoElements.select("img").attr("alt"));
                            selectInfo.setSelectVideoImage(videoElements.select("img").attr("src"));
                            selectInfo.setSelectVideoLink(videoElements.select("a").attr("href"));
                            selectInfo.setSelectVideoUpdateToast(doc.select("div.detailPosterIntro").select("div.introTxt").select("span.sDes").text());
                            selectInfo.selectVideoLists = new ArrayList<>();

                            Elements preListVideo = doc.select("div.tabCon").select("ul.ulNumList").select("a");
                            for (int i=0; i< preListVideo.size();i++){
                                LogUtils.i("pre list title="+preListVideo.get(i).attr("title")+" , href="+preListVideo.get(i).attr("href"));
                                VideoSelectInfo.SelectVideos videos = new VideoSelectInfo.SelectVideos();
                                videos.setVideoListTitle(preListVideo.get(i).attr("title"));
                                videos.setVideoListLink(preListVideo.get(i).attr("href"));
                                selectInfo.selectVideoLists.add(videos);
                            }

                            //年份 地区 类型
                            Elements years = doc.select("div.tabCon").select("ul.ulTxt");
                            for (int j=0; j< years.size();j++){
                                if (years.get(j).text().contains("年份")){
                                    Elements yearsAreaTypes = years.get(j).select("li");
                                    for (int k=0; k< yearsAreaTypes.size(); k++){
                                        LogUtils.i("--------li ----"+yearsAreaTypes.get(k).text());
                                        if (yearsAreaTypes.get(k).text().contains("年份")){
                                            selectInfo.setVideoYears(yearsAreaTypes.get(k).text());
                                        }else if (yearsAreaTypes.get(k).text().contains("地区")){
                                            selectInfo.setVideoArea(yearsAreaTypes.get(k).text());
                                        }else {
                                            selectInfo.setVideoType(yearsAreaTypes.get(k).text());
                                        }
                                    }
                                }
                            }

                            //简介
                            Elements intro = doc.select("div.tabCon");
                            String strIntro=null;
                            strIntro = intro.get(intro.size()-1).text();
                            strIntro= strIntro.split(">>")[1];
                            LogUtils.i("intro : "+strIntro.split("收起")[0]);
                            strIntro = strIntro.split("收起")[0];
                            selectInfo.setVideoIntro(strIntro);

                            //推荐视频
                            Elements recEles = doc.select("div.mod_b").select("div.tb_b").select("div.con");
                            selectInfo.recommendVideoList = new ArrayList<>();
                            for (int i=0;i<recEles.size();i++){
                                RecommendVideo recVideo = new RecommendVideo();
                                recVideo.setRecVideoTitle(recEles.get(i).select("span.sTit").text());
                                recVideo.setRecVideoLink(recEles.get(i).select("a").attr("href"));
                                recVideo.setRecVideoCover(recEles.get(i).select("img").attr("data-src"));
                                recVideo.setRecVideoScore(recEles.get(i).select("span.sNum").text());
                                LogUtils.i(" ----- rec video = "+recVideo.toString());
                                selectInfo.recommendVideoList.add(recVideo);
                            }


                            break;
                        case PLAYING_VIDEO:
                            Elements scriptEle= doc.select("script");
                            for (Element cc: scriptEle) {
                                if (cc.data().contains("VideoInfoList")) {
                                    String[] htmls = cc.html().split("\"");

                                    playVideoInfo = new PlayVideoInfo();
                                    playVideoInfo.playVideoLists = new ArrayList<>();
                                    PlayVideoInfo.PlayVideoList videoList = new PlayVideoInfo.PlayVideoList();

                                    LogUtils.i(" replace =" + htmls[1]);
                                    String[] arr = htmls[1].split("\\$");
                                    LogUtils.i(" video length = " + arr.length);
                                    int j = 0;
                                    boolean isMgtv = false;

                                    for (int i = 0; i < arr.length; i++) {

                                        if (arr[0].equals("芒果视频")) {
                                            isMgtv = true;
                                        }

                                        String regex = "^[0-9]*$";
                                        if (isMgtv && !TextUtils.isEmpty(arr[i]) && arr[i].matches(regex)) {
                                            LogUtils.i("https://v6.wudiseo.com/playurl/?type=mgtv&id=" + arr[i]);
                                            arr[i] = "https://v6.wudiseo.com/playurl/?type=mgtv&id=" + arr[i];
                                        }
                                        //开始集数或高清名称
                                        if (arr[i].startsWith("第") && arr[i].endsWith("集")) {
                                            LogUtils.i(" video 集数:" + arr[i]);
                                            videoList.setVideoTitle(arr[i]);
                                        }else if (arr[i].contains("高清")) {
                                            LogUtils.i( " video 集数:"+arr[i]);
                                            videoList.setVideoTitle(arr[i]);
                                        }

                                        if (arr[i].contains("#第")) {
                                            String[] setsVideo = arr[i].split("#");
                                            for (int k = 0; k < setsVideo.length; k++) {
                                                if (setsVideo[k].startsWith("第") && setsVideo[k].endsWith("集")) {
                                                    LogUtils.i("ddddddd video 集数:" + setsVideo[k]);
                                                    videoList.setVideoTitle(setsVideo[k]);
                                                }else if (setsVideo[k].equals("备用线路")){
                                                    LogUtils.i( "ddddddd video 集数:" + setsVideo[k]);
                                                    videoList.setVideoTitle(setsVideo[k]);
                                                }
                                            }

                                        }
                                        if (arr[i].startsWith("http") || arr[i].startsWith("https")) {
                                            LogUtils.i(" arr[" + j + "]=" + arr[i]);
                                            videoList.setVideoLink(arr[i]);
                                            playVideoInfo.playVideoLists.add(videoList);
                                            j++;
                                        }
                                    }
                                }
                            }

                            break;
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null){
                                listener.resolutionCompletedNotification();
                            }
                        }
                    });

                }

            }
        }).start();
    }

}

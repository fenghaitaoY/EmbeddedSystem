package com.android.blue.smarthomefunc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.VideoSelectInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * 视频集数列表
 *
 * Created by fht on 7/2/18.
 */

public class VideoItemParseRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<VideoSelectInfo.SelectVideos> selectVideos;

    public VideoItemParseRecycleAdapter(List<VideoSelectInfo.SelectVideos> videos){
        selectVideos = videos;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.i("----------video item adapter start ");
        VideoItemViewHolder holder = new VideoItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_recycle_layout, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LogUtils.i("----------video item adapter start ");
        VideoItemViewHolder videoHolder = (VideoItemViewHolder) holder;

        if (selectVideos.get(position).getVideoListTitle().startsWith("第") &&
                selectVideos.get(position).getVideoListTitle().endsWith("集")){
            //动态改变组件大小
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoHolder.listItemVideoBt.getLayoutParams();
            lp.width = 80;
            lp.height = 80;
            videoHolder.listItemVideoBt.setLayoutParams(lp);

            videoHolder.listItemVideoBt.setText(String.valueOf(position+1));
            videoHolder.listItemVideoBt.setTextSize(12);
            videoHolder.listItemVideoBt.setBackgroundResource(R.drawable.choice_video_item_background_drawable);
        }else{
            videoHolder.listItemVideoBt.setText(selectVideos.get(position).getVideoListTitle());
        }
    }

    @Override
    public int getItemCount() {
        return selectVideos.size();
    }




    class VideoItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.video_item_recycle)
        Button listItemVideoBt;

        public VideoItemViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }



    }
}

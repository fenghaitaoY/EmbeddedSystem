package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.HomepageListInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fht on 5/28/18.
 */

public class VideoHomepageRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<HomepageListInfo> mHomepageLists;
    private Context mContext;

    public VideoHomepageRecycleAdapter(List<HomepageListInfo> list){
        mHomepageLists = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.i("viewType="+viewType);
        mContext = parent.getContext();
        //VideoHomepageHolder holder = new VideoHomepageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_list_videos_item, parent, false));
        VideoHomepageTitleHolder holder = new VideoHomepageTitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.scrolling_list_video_title, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LogUtils.i("position ="+position);
        VideoHomepageTitleHolder videoTitleHolder = (VideoHomepageTitleHolder) holder;

        videoTitleHolder.titleName.setText(mHomepageLists.get(position).getHeardTitle());
        videoTitleHolder.more.setText(mHomepageLists.get(position).getMore());
    }

    @Override
    public int getItemCount() {
        return mHomepageLists.size();
    }


    //实现多布局,根据返回的不同的viewtype, creatViewHolder加载不同的布局
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class VideoHomepageHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.video_list_iv)
        ImageView videoListItemImage;
        @BindView(R.id.video_list_introduce_tv)
        TextView introduceTv;
        @BindView(R.id.video_list_item_name)
        TextView videoListName;

        public VideoHomepageHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    class VideoHomepageTitleHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.homepage_title_name)
        TextView titleName;
        @BindView(R.id.homepage_title_more)
        TextView more;

        public VideoHomepageTitleHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}

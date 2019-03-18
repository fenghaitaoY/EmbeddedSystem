package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.HomepageListInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fht on 5/28/18.
 */

public class VideoHomepageRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<HomepageListInfo> mHomepageLists;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public static final int HOMEPAGE_TITLE = 1;
    public static final int HOMEPAGE_CONTENT = 2;


    public VideoHomepageRecycleAdapter(List<HomepageListInfo> list){
        mHomepageLists = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LogUtils.i("viewType="+viewType);
        mContext = parent.getContext();

        if (viewType == HOMEPAGE_TITLE) {
            VideoHomepageTitleHolder holder = new VideoHomepageTitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.scrolling_list_video_title, parent, false));
            return holder;
        }else if (viewType == HOMEPAGE_CONTENT){
            VideoHomepageHolder holder = new VideoHomepageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_list_videos_item, parent, false));
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int type = getItemViewType(position);
        //LogUtils.i("position ="+position+" , type = "+type);
        switch (type){
            case HOMEPAGE_TITLE:
                final VideoHomepageTitleHolder videoTitleHolder = (VideoHomepageTitleHolder) holder;

                videoTitleHolder.titleName.setText(mHomepageLists.get(position).getHeardTitle());
                videoTitleHolder.more.setText(mHomepageLists.get(position).getMore());

                videoTitleHolder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogUtils.i("--点击 更多-----");
                        mOnItemClickListener.onItemClick(videoTitleHolder.more, position);
                    }
                });
                break;
            case HOMEPAGE_CONTENT:
                final VideoHomepageHolder contentHolder = (VideoHomepageHolder) holder;

                contentHolder.videoListName.setText(mHomepageLists.get(position).getVideoName());
                contentHolder.introduceTv.setText(mHomepageLists.get(position).getVideoTag());
                Glide.with(mContext).load(mHomepageLists.get(position).getVideoImage())
                        .apply(new RequestOptions().placeholder(R.drawable.default_video).error(R.drawable.default_video))
                        .into(contentHolder.videoListItemImage);
                contentHolder.mRelatvieLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogUtils.i(" ------点击 电影条目----");
                        mOnItemClickListener.onItemClick(contentHolder.mRelatvieLayout, position);
                    }
                });
                break;
        }


    }

    @Override
    public int getItemCount() {
        //LogUtils.i("size ="+mHomepageLists.size());
        return mHomepageLists.size();
    }


    //实现多布局,根据返回的不同的viewtype, creatViewHolder加载不同的布局
    @Override
    public int getItemViewType(int position) {
        if (mHomepageLists.get(position).getType() == 1){
            return HOMEPAGE_TITLE;
        }else if (mHomepageLists.get(position).getType() == 2){
            return HOMEPAGE_CONTENT;
        }
        return 0;
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }


    class VideoHomepageHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.video_list_iv)
        ImageView videoListItemImage;
        @BindView(R.id.video_list_introduce_tv)
        TextView introduceTv;
        @BindView(R.id.video_list_item_name)
        TextView videoListName;
        @BindView(R.id.homepage_videos_item)
        RelativeLayout mRelatvieLayout;

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

package com.android.blue.smarthomefunc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.RecommendVideo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fht on 8/3/18.
 */

public class RecommendVideoBaseAdapter extends BaseAdapter {
    private List<RecommendVideo> mRecommedVideos;

    public RecommendVideoBaseAdapter(List<RecommendVideo> videos){
        mRecommedVideos = videos;
    }


    @Override
    public int getCount() {
        return mRecommedVideos.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecommedVideos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View parentView, ViewGroup viewGroup) {
        RecommedHolder holder;
        if (parentView == null){
            parentView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recommed_video_item_layout, viewGroup, false);
            holder = new RecommedHolder(parentView);
            parentView.setTag(holder);
        }else{
            holder = (RecommedHolder) parentView.getTag();

        }

        LogUtils.i("position ="+position);
        Glide.with(viewGroup.getContext())
                .load(mRecommedVideos.get(position).getRecVideoCover())
                .apply(new RequestOptions().placeholder(R.drawable.default_cover))
                .into(holder.recommendImage);
        holder.recommendTitle.setText(mRecommedVideos.get(position).getRecVideoTitle());


        return parentView;
    }


    class RecommedHolder{
        @BindView(R.id.recommend_item_iv)
        ImageView recommendImage;
        @BindView(R.id.recommend_item_title)
        TextView recommendTitle;

        public RecommedHolder(View view){
            ButterKnife.bind(this, view);
        }

    }
}

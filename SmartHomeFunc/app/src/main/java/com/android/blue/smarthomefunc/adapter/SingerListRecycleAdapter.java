package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.model.SingerLIst;
import com.android.blue.smarthomefunc.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fht on 4/7/18.
 */

public class SingerListRecycleAdapter extends RecyclerView.Adapter<SingerListRecycleAdapter.SingerViewHolder> {
    private List<SingerLIst.Singer> singers;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public SingerListRecycleAdapter(List<SingerLIst.Singer> data){
        singers = data;
    }

    @Override
    public SingerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SingerViewHolder holder = new SingerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.online_singer_list_recycle_layout, parent, false));
        mContext = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(final SingerViewHolder holder, final int position) {
        holder.singerName.setText(singers.get(position).getName());
        holder.otherSub.setText(singers.get(position).getCountry());
        Glide.with(mContext).load(singers.get(position).getAvatar_big())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                })
                .apply(new RequestOptions().error(R.drawable.default_cover).placeholder(R.drawable.default_cover))
                .into(holder.singerCover);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(holder.layout, position);
                }
            }
        });



    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return singers.size();
    }



    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }



    class SingerViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.singer_recycler_cover)
        CircleImageView singerCover;
        @BindView(R.id.singer_name)
        TextView singerName;
        @BindView(R.id.singer_other)
        TextView otherSub;
        @BindView(R.id.singer_relative)
        RelativeLayout layout;

        public SingerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

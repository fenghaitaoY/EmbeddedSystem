package com.android.blue.smarthomefunc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.application.AppCache;
import com.android.blue.smarthomefunc.entity.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Detail　display
 * Created by fht on 3/26/18.
 */

public class OnlineMoreGridAdapter extends BaseAdapter {
    private int[] images = {R.drawable.selector_online_more_download, R.drawable.selector_online_more_share,
            R.drawable.selector_online_more_add, R.drawable.selector_online_more_music_info,
            R.drawable.selector_online_more_ring, R.drawable.selector_online_more_delete};

    private int[] names = {R.string.music_more_download, R.string.music_more_share,
            R.string.music_more_add, R.string.music_more_info,
            R.string.music_more_ring, R.string.music_more_delete};

    private List<Integer> imageList = new ArrayList<>();
    private List<Integer> nameList = new ArrayList<>();

    public static int LOCAL_TYPE = 0;
    public static int ONLINE_TYPE = 1;
    private int type;
    private Context context;
    private boolean isExist = false;

    public OnlineMoreGridAdapter(Context context, int type) {
        this.type = type;
        this.context = context;
        initShow();
    }

    private void initShow() {
        imageList.clear();
        nameList.clear();

        if (type == ONLINE_TYPE) {
            //remove delete remove download
            if (isExist) {
                for (int i = 0; i < images.length; i++) {
                    if (!context.getString(names[i]).equals(context.getString(R.string.music_more_delete))
                            && !context.getString(names[i]).equals(context.getString(R.string.music_more_download))) {
                        imageList.add(Integer.valueOf(images[i]));
                        nameList.add(Integer.valueOf(names[i]));
                    }
                }
            } else {
                for (int i = 0; i < images.length; i++) {
                    if (!context.getString(names[i]).equals(context.getString(R.string.music_more_delete))) {
                        imageList.add(Integer.valueOf(images[i]));
                        nameList.add(Integer.valueOf(names[i]));
                    }
                }
            }

        } else if (type == LOCAL_TYPE) {
            //remove download
            for (int i = 0; i < images.length; i++) {
                if (!context.getString(names[i]).equals(context.getString(R.string.music_more_download))) {
                    imageList.add(Integer.valueOf(images[i]));
                    nameList.add(Integer.valueOf(names[i]));
                }
            }
        }
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        GridHolder holder;

        if (view == null) {
            view = LayoutInflater.from(AppCache.get().getContext()).inflate(R.layout.grid_item_layout, null);
            holder = new GridHolder(view);
            view.setTag(holder);
        } else {
            holder = (GridHolder) view.getTag();
        }

        holder.icon.setImageResource(imageList.get(position));
        holder.name.setText(nameList.get(position));

        return view;
    }

    /**
     * 更新是否显示下载菜单选项
     * @param isExist
     */
    public void updateDownloadSatus(boolean isExist) {
        LogUtils.i(" isExist = " + isExist);
        this.isExist = isExist;
        initShow();

    }


    class GridHolder {
        @BindView(R.id.grid_item_iv)
        ImageView icon;
        @BindView(R.id.grid_item_tv)
        TextView name;

        public GridHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

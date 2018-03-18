package com.android.blue.smarthomefunc.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.utils.RecycleWrapperUtils;

/**
 * RecyclerView 装饰模式 添加　头布局，尾布局
 * 参考网上实例
 * 感谢　https://github.com/hongyangAndroid/baseAdapter　作者
 * Created by fht on 3/16/18.
 */

public class HeaderAndFooterWrapper<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 10000;
    private static final int BASE_ITEM_TYPE_FOOTER = 20000;

    /**
     * SparseArrayCompat
     *优点节省最高50%缓存;
     *SparseArrayCompat()其实是一个map容器,它使用了一套算法优化了hashMap,可以节省至少50%的缓存.
     *缺点但是有局限性只针对下面类型.
     *key: Integer; value: object
     */
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter mInnerAdapter;

    public HeaderAndFooterWrapper(RecyclerView.Adapter adapter){
        mInnerAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null){
            RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(mHeaderViews.get(viewType)) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };
            return holder;
        }else if (mFootViews.get(viewType) != null){
            RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(mFootViews.get(viewType)) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };
            return holder;
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPosition(position)){
            return mHeaderViews.keyAt(position);
        }else if (isFooterViewPosition(position)){
            return mFootViews.keyAt(position-getHeadersCount()-getRealItemCount());
        }
        return mInnerAdapter.getItemViewType(position-getHeadersCount());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPosition(position)){
            return;
        }
        if (isFooterViewPosition(position)){
            return;
        }
        mInnerAdapter.onBindViewHolder(holder,position-getHeadersCount());
    }

    @Override
    public int getItemCount() {
        LogUtils.i(" getHeaderCount = "+getHeadersCount()+" , getFootersCount="+getFootersCount()+", getRealItemCount="+getRealItemCount());
        return getHeadersCount()+getFootersCount()+getRealItemCount();
    }


    private boolean isHeaderViewPosition(int position){
        return position < getHeadersCount();
    }

    private boolean isFooterViewPosition(int position){
        return position>= getHeadersCount()+getRealItemCount();
    }

    public int getHeadersCount(){
        return mHeaderViews.size();
    }

    private int getRealItemCount(){
        return mInnerAdapter.getItemCount();
    }

    public int getFootersCount(){
        return mFootViews.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        RecycleWrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new RecycleWrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager manager, GridLayoutManager.SpanSizeLookup spanSizeLookup, int position) {
                int viewType = getItemViewType(position);
                if (mHeaderViews.get(viewType) != null){
                    return manager.getSpanCount();
                }else if (mFootViews.get(viewType) != null){
                    return manager.getSpanCount();
                }
                if (spanSizeLookup != null){
                    return spanSizeLookup.getSpanSize(position);
                }
                return 1;

            }
        });

    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();

        if (isHeaderViewPosition(position) || isFooterViewPosition(position)){
            RecycleWrapperUtils.setFullSpan(holder);
        }
    }

    public void addHeaderView(View view){
        mHeaderViews.put(mHeaderViews.size()+BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFootView(View view){
        mFootViews.put(mFootViews.size()+BASE_ITEM_TYPE_FOOTER, view);
    }

    public void removeHeaderView(View view){
        mHeaderViews.remove(mHeaderViews.indexOfValue(view));
    }

    public void removeFootView(View view){
        mFootViews.remove(mFootViews.indexOfValue(view));
    }

    public void removeAllHeader(){
        mHeaderViews.clear();
    }

    public void removeAllFoot(){
        mFootViews.clear();
    }
}

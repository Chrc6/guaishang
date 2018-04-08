package com.houwei.guaishang.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import com.houwei.guaishang.R;

/**
 * 使用PagedListView 要保证PAGE_SIZE个item 一定可以充满屏幕 
 */
public class PagedListView extends ListView {

	public interface OnLoadMoreListener {
		void onLoadMoreItems();
	}

	private boolean isLoading;
	private boolean hasMoreItems;
	private OnLoadMoreListener onLoadMoreListener;
	private View loadinView;
	private View mEmptyLayout;
    private OnScrollListener onScrollListener;
    
    private boolean loadingFootViewHadRemoved; //底部的loadinView被移除了
    
	public PagedListView(Context context) {
		super(context);
		initView(context);
	}

	public PagedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PagedListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public boolean isLoading() {
		return this.isLoading;
	}

	public void setIsLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}

	public OnLoadMoreListener getOnLoadMoreListener(){
		return onLoadMoreListener;
	}
	
	
	private void setHasMoreItems(boolean hasMoreItems) {
		this.hasMoreItems = hasMoreItems;
		if (hasMoreItems && loadingFootViewHadRemoved) {
			//还有更多，但是footview被移除了
			addFooterView(loadinView);
		}
		loadinView.setVisibility(hasMoreItems ? View.VISIBLE:View.GONE);
	}

	public boolean hasMoreItems() {
		return this.hasMoreItems;
	}


	/**
	 * false表示没有下一页了
	 */
	public void onFinishLoading(boolean hasMoreItems) {
		setHasMoreItems(hasMoreItems);
		setIsLoading(false);
	}


	private void initView(Context context) {
		setFooterDividersEnabled(false);
		isLoading = false;
		loadinView =  LayoutInflater.from(context).inflate( R.layout.footer_loading_view,null);
		addFooterView(loadinView);
		super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //Dispatch to child OnScrollListener
                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //Dispatch to child OnScrollListener
                if (onScrollListener != null) {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
                if (totalItemCount > 0) {
                    int lastVisibleItem = firstVisibleItem + visibleItemCount;
                    if (!isLoading && hasMoreItems && (lastVisibleItem == totalItemCount)) {
                        if (onLoadMoreListener != null) {
                            isLoading = true;
                            onLoadMoreListener.onLoadMoreItems();
                        }
                    }
                }
            }
        });
	}

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }
    
    //直接触发一次翻页
    public void startToGetMore(){
    	 if (!isLoading) {
    		 if(!hasMoreItems){
    			 hasMoreItems = true;
    			 loadinView.setVisibility(hasMoreItems ? View.VISIBLE:View.GONE);
    		 }
             if (onLoadMoreListener != null) {
                 isLoading = true;
                 onLoadMoreListener.onLoadMoreItems();
             }
         }
    }
    
    public void showEmptyFooter(Context context,String message){
    	if (mEmptyLayout!=null) {
    		//已经有empty footer了
    		return ;
		}
		mEmptyLayout =  LayoutInflater.from(context).inflate(R.layout.listitem_empty_padding, null);
		TextView iv = (TextView)mEmptyLayout.findViewById(R.id.textViewMessage);
		iv.setText(message == null ? "还没有人评论":message);
		removeLoadingFooterView();
		addFooterView(mEmptyLayout);
    }
    
    public void removeEmptyFooter(){
    	if (mEmptyLayout!=null) {
    		removeFooterView(mEmptyLayout);
    		mEmptyLayout = null;
    	}
    }
    
    public void removeLoadingFooterView(){
    	removeFooterView(loadinView);
    	loadingFootViewHadRemoved = true;
    }
}

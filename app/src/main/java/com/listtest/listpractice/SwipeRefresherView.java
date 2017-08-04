package com.listtest.listpractice;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

public class SwipeRefresherView extends SwipeRefreshLayout {

    private final int myScaledTouchSlop;
    private final View myFooterView;
    private ListView myListView;
    private OnLoadMoreListener myListener;
    private int myItemCount;
    private boolean isLoading;

    public SwipeRefresherView(Context context, AttributeSet attrs){
        super(context, attrs);
        myFooterView = View.inflate(context, R.layout.view_footer, null);
        myScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (myListView == null) {
            if (getChildCount() > 0) {
                if (getChildAt(0) instanceof ListView) {
                    myListView = (ListView) getChildAt(0);
                    setListViewOnScroll();
                }
            }
        }
    }

    private float myDownY, myUpY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                myDownY=ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(canLoadMore()){
                    loadData();
                }
                break;
            case MotionEvent.ACTION_UP:
                myUpY=ev.getY();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean canLoadMore() {
        boolean condition1 = (myDownY - myUpY) >= myScaledTouchSlop;
        boolean condition2 = false;
        if (myListView != null && myListView.getAdapter() != null) {
            if (myItemCount > 0) {
                if (myListView.getAdapter().getCount() < myItemCount) {
                    condition2 = false;
                }else {
                    condition2 = myListView.getLastVisiblePosition() == (myListView.getAdapter().getCount() - 1);
                }
            } else {
                condition2 = myListView.getLastVisiblePosition() == (myListView.getAdapter().getCount() - 1);
            }

        }
        boolean condition3 = !isLoading;
        boolean condition4 = (myUpY>0);
        return condition1 && condition2 && condition3 && condition4;
    }

    private void loadData() {
        if (myListener != null) {
            setLoading(true);
            myListener.onLoadMore();
        }

    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            myListView.addFooterView(myFooterView);
        } else {
            myListView.removeFooterView(myFooterView);
            myDownY = 0;
            myUpY = 0;
        }
    }

    private void setListViewOnScroll() {

        myListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (canLoadMore()) {
                    loadData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    public void setItemCount(int itemCount) {
        this.myItemCount = itemCount;
    }

    interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.myListener = listener;
    }
}



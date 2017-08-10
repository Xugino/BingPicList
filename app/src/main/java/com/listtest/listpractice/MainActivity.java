package com.listtest.listpractice;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private List<Map<String,Object>> mList;
    private NewListView mylist;
    private MyAdapter myadapter;
    private RefreshLayout myrefresher;
    private DataResource ds;
    private DataAsyncTask myTask;
    private int listCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mylist);

        mList= new ArrayList<>();
        mylist=(NewListView)this.findViewById(R.id.list);
        myrefresher=(RefreshLayout) this.findViewById(R.id.refresher);
        myrefresher.setRefreshHeader(new TaurusHeader(this));
        myrefresher.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        myrefresher.setEnableAutoLoadmore(false);
        ds=new DataResource();
        myadapter=new MyAdapter(MainActivity.this);
        mylist.setAdapter(myadapter);

        initEvent();
        initData();
    }

    private void initEvent(){
        myrefresher.setOnRefreshListener(new OnRefreshListener(){
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                initData();
                refreshLayout.finishRefresh(3000);
            }
        });
        myrefresher.setOnLoadmoreListener(new OnLoadmoreListener(){
            @Override
            public void onLoadmore(RefreshLayout refreshLayout) {
                loadMoreData();
                refreshLayout.finishLoadmore(3000);
            }
        });
    }

    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.clear();
                mList.addAll(ds.getMoreData());
                myadapter.notifyDataSetChanged();
                if(listCount<mList.size()){
                    Toast.makeText(MainActivity.this, "加载完成", Toast.LENGTH_SHORT).show();
                    listCount=mList.size();
                }else{
                    Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                }
            }
        },3000);

    }

    private void initData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.clear();
                mList.addAll(ds.getData());
                myadapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
            }
        },3000);
    }

    private class DataResource {

        private List<Map<String,Object>> list;
        private int page;

        private DataResource(){
            list=new ArrayList<>();
        }

        private List<Map<String,Object>> getData()
        {
            list.clear();
            page=1;
            try{
                myTask = new DataAsyncTask();
                list.addAll(myTask.execute(page).get());
            }catch (Exception e){
                e.printStackTrace();
            }
            listCount=list.size();
            return list;
        }
        private List<Map<String,Object>> getMoreData()
        {
            page=page+1;
            try{
                myTask = new DataAsyncTask();
                list.addAll(myTask.execute(page).get());
            }catch (Exception e){
                e.printStackTrace();
            }
            return list;
        }
    }


    private class MyAdapter extends BaseAdapter
    {
        Context mContext;
        DownloadButton btn;
        private LayoutInflater mInflater;

        private MyAdapter(Context mContext){
            this.mContext=mContext;
            mInflater=LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                viewHolder = new ViewHolder();
                convertView=mInflater.inflate(R.layout.item,null);
                viewHolder.pic=convertView.findViewById(R.id.item_pic);
                viewHolder.text=convertView.findViewById(R.id.item_text);
                viewHolder.time=convertView.findViewById(R.id.item_time);
                viewHolder.btn=convertView.findViewById(R.id.download_btn);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }
            Uri uri = Uri.parse((String)ds.list.get(position).get("pic"));
            viewHolder.pic.setImageURI(uri);
            viewHolder.text.setText((CharSequence)ds.list.get(position).get("text"));
            viewHolder.time.setText((CharSequence)ds.list.get(position).get("time"));
            viewHolder.btn.setUri(uri);
            viewHolder.btn.setTime((String)ds.list.get(position).get("time"));
            viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = ((DownloadButton)view).getUri();
                    String serviceString = Context.DOWNLOAD_SERVICE;
                    DownloadManager downloadManager;
                    downloadManager = (DownloadManager) getSystemService(serviceString);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDestinationInExternalPublicDir("sdcard/Download", "BingPic"+((DownloadButton)view).getTime()+".jpg");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    long reference = downloadManager.enqueue(request);
                }
            });

            return convertView;
        }

        class ViewHolder{
            SimpleDraweeView pic;
            TextView text;
            TextView time;
            DownloadButton btn;
        }

    }
}

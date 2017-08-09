package com.listtest.listpractice;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private List<Map<String,Object>> mList;
    private NewListView mylist;
    private MyAdapter myadapter;
    private SwipeRefresherView myrefresher;
    private DataResource ds;
    private DataAsyncTask myTask;
    private int listCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mylist);
        setContentView(R.layout.mylist_easy);

        mList= new ArrayList<>();
        mylist=(NewListView)this.findViewById(R.id.list);
        myrefresher=(SwipeRefresherView) this.findViewById(R.id.refresher);
        ds=new DataResource();
        myadapter=new MyAdapter(MainActivity.this);
        mylist.setAdapter(myadapter);
        myrefresher.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
        myrefresher.setColorSchemeResources(R.color.colorAccent);

        myrefresher.setItemCount(5);
        myrefresher.measure(0,0);
        myrefresher.setRefreshing(true);
        initEvent();
        initData();
    }

    private void initEvent(){
        myrefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                initData();
            }
        });
        myrefresher.setOnLoadMoreListener(new SwipeRefresherView.OnLoadMoreListener(){
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
    }

    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.clear();
                mList.addAll(ds.getMoreData());
                if(listCount<mList.size()){
                    Toast.makeText(MainActivity.this, "加载完成", Toast.LENGTH_SHORT).show();
                    listCount=mList.size();
                }else{
                    Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                }
                myrefresher.setLoading(false);
            }
        }, 3000);
    }

    private void initData(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                mList.clear();
                mList.addAll(ds.getData());
                myadapter=new MyAdapter(MainActivity.this);
                mylist.setAdapter(myadapter);
                myadapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                if (myrefresher.isRefreshing()) {
                    myrefresher.setRefreshing(false);
                }
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


    private class MyAdapter extends BaseAdapter implements View.OnClickListener
    {
        Context mContext;
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
                viewHolder.more=convertView.findViewById(R.id.btn_more);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }
            Uri uri = Uri.parse((String)ds.list.get(position).get("pic"));
            viewHolder.pic.setImageURI(uri);
            viewHolder.text.setText((CharSequence)ds.list.get(position).get("text"));
            viewHolder.time.setText((CharSequence)ds.list.get(position).get("time"));
            viewHolder.more.setOnClickListener(this);

            return convertView;
        }

        class ViewHolder{
            SimpleDraweeView pic;
            TextView text;
            TextView time;
            Button more;
        }

        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.btn_more:
                    showMore();
                    break;
            }
        }

        private void showMore(){
            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示");
            builder.setMessage("这里是详情页（当然并没有做Orz）");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            android.app.AlertDialog dialog=builder.create();
            dialog.show();
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(R.color.sure);
        }
    }
}

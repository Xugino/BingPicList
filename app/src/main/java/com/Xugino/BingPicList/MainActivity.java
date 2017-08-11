package com.Xugino.BingPicList;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private List<Map<String,Object>> mList;
    NewListView mylist;
    private MyAdapter myadapter;
    private RefreshLayout myrefresher;
    private DataResource ds;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private LoadingDialog loadingDialog;
    private EmailInterface emailInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList= new ArrayList<>();
        mylist=(NewListView)this.findViewById(R.id.list);
        myrefresher=(RefreshLayout) this.findViewById(R.id.refresher);
        myrefresher.setRefreshHeader(new TaurusHeader(this));
        myrefresher.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        myrefresher.setEnableAutoLoadmore(false);
        ds=new DataResource();
        myadapter=new MyAdapter(MainActivity.this);
        mylist.setAdapter(myadapter);
        initToolbar();
        initDrawerLayout();
        initEvent();
        loadingDialog=new LoadingDialog(this);
        loadingDialog.show();
        initData();
    }

    private void initToolbar(){
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xffffffff);
        emailInterface=new EmailInterface() {
            @Override
            public void sendEmail(Bundle bundle) {
                boolean result=true;
                Intent intent=new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:huangyiming@buaa.edu.cn"));
                intent.putExtra(Intent.EXTRA_SUBJECT,bundle.getCharSequence("title"));
                intent.putExtra(Intent.EXTRA_TEXT,bundle.getCharSequence("content"));
                try{
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                    result=false;
                }finally {
                    if(!result){
                        Toast.makeText(MainActivity.this, "反馈失败，请重新尝试", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.test:
                        new UpdateManager(MainActivity.this).checkUpdateInfo();
                        break;
                    case R.id.email:
                        new EmailDialog(MainActivity.this,emailInterface).setDisplay();
                        break;
                    case R.id.exit:
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
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

    private void initDrawerLayout(){
        drawerLayout=(DrawerLayout)this.findViewById(R.id.drawerLayout_left);
        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START);//打开侧滑菜单
            return true ;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.clear();
                mList.addAll(ds.getMoreData());
                myadapter.notifyDataSetChanged();
                if(ds.listCount<mList.size()){
                    Toast.makeText(MainActivity.this, "加载完成", Toast.LENGTH_SHORT).show();
                    ds.listCount=mList.size();
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
                loadingDialog.dismiss();
                if(mList.size()>0){
                    Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "刷新失败，请检查你的网络连接！", Toast.LENGTH_LONG).show();
                }

            }
        },3000);
    }




    private class MyAdapter extends BaseAdapter
    {
        Context mContext;
        private LayoutInflater mInflater;
        private boolean isDownloading;

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
            viewHolder.time.setText(getTime((String)ds.list.get(position).get("time")));
            isDownloading=false;
            CompleteReceiver completeReceiver = new CompleteReceiver();
            registerReceiver(completeReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            viewHolder.btn.setUri(uri);
            viewHolder.btn.setTime((String)ds.list.get(position).get("time"));
            viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isDownloading){
                        Toast.makeText(MainActivity.this, "当前已在进行下载，请等待下载完成", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, "开始下载...", Toast.LENGTH_SHORT).show();
                        isDownloading=true;
                        Uri uri = ((DownloadButton)view).getUri();
                        DownloadManager downloadManager;
                        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "BingPic"+((DownloadButton)view).getTime()+".jpg");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        long reference = downloadManager.enqueue(request);
                    }

                }
            });
            return convertView;
        }

        private String getTime(String time){
            StringBuilder stringBuilder=new StringBuilder(time);
            stringBuilder.insert(8,"日");
            if(stringBuilder.charAt(6)=='0') stringBuilder.deleteCharAt(6);
            stringBuilder.insert(6,"月");
            if(stringBuilder.charAt(4)=='0') stringBuilder.deleteCharAt(4);
            stringBuilder.insert(4,"年");
            return stringBuilder.toString();
        }

        class ViewHolder{
            SimpleDraweeView pic;
            TextView text;
            TextView time;
            DownloadButton btn;
        }

        class CompleteReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(isDownloading){
                    Toast.makeText(MainActivity.this, "图片下载完成", Toast.LENGTH_SHORT).show();
                    isDownloading=false;
                }
            }
        }
    }

    interface EmailInterface{
        void sendEmail(Bundle bundle);
    }
}

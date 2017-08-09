package com.listtest.listpractice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

class DataAsyncTask extends AsyncTask<Integer,Void,List<Map<String,Object>>> {

    private List<Map<String,Object>> list;
    private int length;

    DataAsyncTask(){
        super();
        list = new ArrayList<>();
    }

    @Override
    protected List<Map<String,Object>> doInBackground(Integer... page) {
        try{
            int n=page[0]*5;
            String path = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n="+n+"&mkt=zh-CN";
            URL url = new URL(path);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(5000);
            urlConn.setRequestProperty("Accept-Encoding", "identity");
            urlConn.connect();
            if (urlConn.getResponseCode() == 200) {
                String data = readStream(urlConn.getInputStream());
                length = urlConn.getContentLength();
                Log.i(TAG, "请求成功,长度为:"+length);
                Log.i(TAG, data);
                int i;
                Map<String,Object> map;
                Type type= new TypeToken<BingPic>(){}.getType();
                Gson gson = new Gson();
                BingPic bingPic = gson.fromJson(data,type);
                if(n>bingPic.getImages().size()){
                    for(i=n-5;i<bingPic.getImages().size();i++){
                        map=new HashMap<>();
                        map.put("pic","http://cn.bing.com"+bingPic.getImages().get(i).getUrl());
                        map.put("text",bingPic.getImages().get(i).getCopyright());
                        map.put("time",bingPic.getImages().get(i).getStartdate());
                        list.add(map);
                    }
                }else{
                    for(i=n-5;i<n;i++){
                        map=new HashMap<>();
                        map.put("pic","http://cn.bing.com"+bingPic.getImages().get(i).getUrl());
                        map.put("text",bingPic.getImages().get(i).getCopyright());
                        map.put("time",bingPic.getImages().get(i).getStartdate());
                        list.add(map);
                    }
                }

            } else {
                Log.i(TAG, "请求失败");
            }
            urlConn.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return list;
        }
    }

    private static Bitmap getBitmap(String path) throws IOException {

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200){
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream),150,150,true);
            return bitmap;
        }
        return null;
    }

    private String readStream(InputStream inputStream) throws IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[102400];
        int len;
        while((len=inputStream.read(buffer))!=-1){
            outputStream.write(buffer,0,len);
            outputStream.flush();
        }
        outputStream.close();
        inputStream.close();
        return  outputStream.toString();
    }
}

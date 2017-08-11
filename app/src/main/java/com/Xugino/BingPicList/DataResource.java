package com.Xugino.BingPicList;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataResource {

    private DataAsyncTask myTask;
    public int listCount;
    public List<Map<String,Object>> list;
    private int page;

    public DataResource(){
        list=new ArrayList<>();
    }

    public List<Map<String,Object>> getData()
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
    public List<Map<String,Object>> getMoreData()
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
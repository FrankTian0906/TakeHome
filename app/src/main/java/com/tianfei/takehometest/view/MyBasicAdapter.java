package com.tianfei.takehometest.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class MyBasicAdapter extends BaseAdapter{
    private List<Map<String,String>> list;

    public MyBasicAdapter(List<Map<String,String>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    public List<Map<String,String>> getList(){
        return list;
    }
}

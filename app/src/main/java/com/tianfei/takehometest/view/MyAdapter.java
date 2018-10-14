package com.tianfei.takehometest.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianfei.takehometest.R;
import com.tianfei.takehometest.view.MyBasicAdapter;

import java.util.List;
import java.util.Map;

public class MyAdapter extends MyBasicAdapter {
    public MyAdapter(List<Map<String, String>> list) {
        super(list);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Map<String,String > item =getList().get(i);
        ViewHolder viewHolder = null;
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list,null);
            viewHolder = new ViewHolder();
            viewHolder.message = (TextView)view.findViewById(R.id.message);
            viewHolder.imageView = (ImageView)view.findViewById(R.id.imageView);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.message.setText(item.get("message"));

        //return super.getView(i, view, viewGroup);
        return view;
    }

    static class ViewHolder{
        TextView message;
        ImageView imageView;
    }
}

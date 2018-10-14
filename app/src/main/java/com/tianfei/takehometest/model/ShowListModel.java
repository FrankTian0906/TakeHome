package com.tianfei.takehometest.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowListModel{
    private int spotMode = 0;
    private List<Map<String,String>> listData = new ArrayList<>();
    private List<Airports> airports;

    public void showResults(String ori, String des, Context context, ShowListListener showListListener){
        MyMatabaseHelper myMatabaseHelper = new MyMatabaseHelper(context);
        Cursor data = myMatabaseHelper.getData_trans_0(ori,des);
        if(!data.moveToNext()) {
            data = myMatabaseHelper.getData_trans_1(ori, des);
            spotMode = 1;
            if(!data.moveToNext()) {
                data = myMatabaseHelper.getData_trans_2(ori, des);
                spotMode = 2;
            }
            else {
                spotMode = 1;
                data.moveToPrevious();
            }
        }
        else {
            spotMode = 0;
            data.moveToPrevious();
        }
        showListListener.getData_model(spotMode);
        //init results
        //for show on the list
        ArrayList<String> results = new ArrayList<>();
        //for intent adding extra instance
        final List<List<String>> spots_str = new ArrayList<>();
        spots_str.clear();
        while(data.moveToNext()){
            //get IATAs of every list item and put it into List<List<String>> spots_str
            List<String> spots = new ArrayList<>();
            //set the form of list item
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while(!data.isNull(i)) {
                if((i+1)%3 == 1) {
                    sb.append("\n").append("AirLine: ").append(data.getString(i));
                }
                if((i+1)%3 == 2) {
                    sb.append(" Origin: ").append(data.getString(i));
                    spots.add(data.getString(i));
                }
                if((i+1)%3 == 0 ) {
                    sb.append(" ->  Destination: ").append(data.getString(i));
                    spots.add(data.getString(i));
                }
                i++;
            }
            String res = sb.toString();
            if(res.startsWith("\n"))
                res = res.replaceFirst("\n", "");
            results.add(res);

            spots = removeDuplication(spots);
            Log.d("SPOTS-after",spots.toString());
            spots_str.add(spots);
        }
        //is empty
        if(results.isEmpty()) {
            Toast.makeText(context, "Sorry, there is no any lines between two cities!", Toast.LENGTH_LONG).show();
            return;
        }
        //set listView
        listData.clear();
        for(String data_result: results){
            Map<String , String> map = new HashMap<String, String>();
            map.put("message",data_result);
            listData.add(map);
        }
        showListListener.getData_list(listData);
        showListListener.getData_intent(spots_str);
    }

    public List<String> removeDuplication(List<String> list){
        for(int i = 0; i< list.size()-1;i++){
            for(int j = i+1; j< list.size();j++){
                if(list.get(i).equals(list.get(j)))
                    list.remove(j);
            }
        }
        return list;
    }

    public interface ShowListListener {

        public void getData_list(List<Map<String, String>> data);


        public void getData_intent(List<List<String>> airports);


        public void getData_model(int mode);
    }
}

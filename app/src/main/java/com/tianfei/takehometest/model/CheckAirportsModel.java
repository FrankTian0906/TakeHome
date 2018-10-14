package com.tianfei.takehometest.model;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class CheckAirportsModel {

    public void checkExisted(String origin, String destination, Context context, CheckResults checkResults){

        MyMatabaseHelper myMatabaseHelper = new MyMatabaseHelper(context);
        Cursor data_ori = myMatabaseHelper.getAirport(origin);
        Cursor data_des = myMatabaseHelper.getAirport(destination);

        ArrayList<String> results_o = new ArrayList<>();
        ArrayList<String> results_d = new ArrayList<>();
        while(data_ori.moveToNext() && data_des.moveToNext()){
            results_o.add(data_ori.getString(3));
            results_d.add(data_des.getString(3));
        }
        data_ori.close();
        data_des.close();
        // is existed
        if(results_o.isEmpty() || results_d.isEmpty()){
            checkResults.airportsNotExist();
        }
        else
            checkResults.airportsExist();
    }

    public interface CheckResults{

        void airportsExist();

        void airportsNotExist();
    }
}
